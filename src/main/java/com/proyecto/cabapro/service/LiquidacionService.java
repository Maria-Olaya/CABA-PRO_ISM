
package com.proyecto.cabapro.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.proyecto.cabapro.enums.EstadoLiquidacion;
import com.proyecto.cabapro.model.Arbitro;
import com.proyecto.cabapro.model.Liquidacion;
import com.proyecto.cabapro.model.Pago;
import com.proyecto.cabapro.model.Partido;
import com.proyecto.cabapro.model.Tarifa;
import com.proyecto.cabapro.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

@Service
@Transactional
public class LiquidacionService {

    public static class DuplicateLiquidacionException extends RuntimeException {
        public DuplicateLiquidacionException(String message) { super(message); }
    }

    private final ArbitroRepository arbitroRepo;
    private final PartidoRepository partidoRepo;
    private final TarifaRepository tarifaRepo;
    private final LiquidacionRepository liquidacionRepo;
    private final PagoRepository pagoRepo;
    private final TarifaService tarifaService;

    @Autowired
    private MessageSource messageSource;

    public LiquidacionService(ArbitroRepository arbitroRepo,
                              PartidoRepository partidoRepo,
                              TarifaRepository tarifaRepo,
                              LiquidacionRepository liquidacionRepo,
                              PagoRepository pagoRepo,
                              TarifaService tarifaService) {
        this.arbitroRepo = arbitroRepo;
        this.partidoRepo = partidoRepo;
        this.tarifaRepo = tarifaRepo;
        this.liquidacionRepo = liquidacionRepo;
        this.pagoRepo = pagoRepo;
        this.tarifaService = tarifaService;
    }

    @Transactional(readOnly = true)
    public List<Liquidacion> listarPorArbitro(Integer arbitroId) {
        Locale locale = LocaleContextHolder.getLocale();
        Arbitro a = arbitroRepo.findById(arbitroId)
                .orElseThrow(() -> new IllegalArgumentException(
                        messageSource.getMessage("admin.arbitros.noEncontrado", null, locale)
                ));
        return liquidacionRepo.findByArbitroOrderByFechaGeneradaDesc(a);
    }

    public Liquidacion generarParaArbitro(Integer arbitroId) {
        Locale locale = LocaleContextHolder.getLocale();

        Arbitro a = arbitroRepo.findById(arbitroId)
                .orElseThrow(() -> new IllegalArgumentException(
                        messageSource.getMessage("admin.arbitros.noEncontrado", null, locale)
                ));

        autoGenerarTarifasSiFaltan(a);

        List<Tarifa> pendientes = tarifaRepo.findByArbitroAndLiquidacionIsNullOrderByGeneradoEnAsc(a);
        if (pendientes.isEmpty()) {
            throw new IllegalStateException(
                    messageSource.getMessage("error.noPendingRates", null, locale)
            );
        }

        String firma = firmaDeTarifas(pendientes);
        if (liquidacionRepo.existsByArbitroAndFirma(a, firma)) {
            throw new DuplicateLiquidacionException(
                    messageSource.getMessage("error.duplicateSettlement", null, locale)
            );
        }

        BigDecimal total = pendientes.stream()
                .map(Tarifa::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        byte[] pdf = generarPdf(a, pendientes, total);

        Liquidacion liq = new Liquidacion();
        liq.setArbitro(a);
        liq.setFechaGenerada(LocalDateTime.now());
        liq.setEstado(EstadoLiquidacion.PENDIENTE);
        liq.setTotal(total);
        liq.setFirma(firma);
        liq.setPdf(pdf);
        liq = liquidacionRepo.save(liq);

        for (Tarifa t : pendientes) {
            t.setLiquidacion(liq);
        }
        tarifaRepo.saveAll(pendientes);

        return liq;
    }

    public void pagar(Long liquidacionId) {
        Locale locale = LocaleContextHolder.getLocale();

        Liquidacion l = liquidacionRepo.findById(liquidacionId)
                .orElseThrow(() -> new IllegalArgumentException(
                        messageSource.getMessage("error.settlement.notFound", null, locale)
                ));

        if (l.getEstado() == EstadoLiquidacion.PAGADA) return;

        Pago p = new Pago();
        p.setLiquidacion(l);
        p.setFecha(LocalDateTime.now());
        p.setMonto(l.getTotal());
        pagoRepo.save(p);

        l.setEstado(EstadoLiquidacion.PAGADA);
        l.setPagadoEn(LocalDateTime.now());
        liquidacionRepo.save(l);
    }

    public byte[] obtenerPdf(Long id) {
        Locale locale = LocaleContextHolder.getLocale();
        Liquidacion l = liquidacionRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        messageSource.getMessage("error.settlement.notFound", null, locale)
                ));
        return l.getPdf();
    }

    // ==================== Helpers =====================
    private void autoGenerarTarifasSiFaltan(Arbitro a) {
        List<Partido> partidos = partidoRepo.findByArbitros_Id(a.getId());
        for (Partido p : partidos) {
            tarifaService.ensureTarifaIfEligible(p, a);
        }
    }

    private String firmaDeTarifas(List<Tarifa> tarifas) {
        StringBuilder sb = new StringBuilder();
        tarifas.stream()
                .sorted(Comparator.comparing(Tarifa::getId))
                .forEach(t -> {
                    Long tarifaId = t.getId() == null ? -1L : t.getId();
                    Integer partidoId = t.getPartido() != null ? t.getPartido().getIdPartido() : -1;
                    String monto = t.getMonto() != null ? t.getMonto().toPlainString() : "0";
                    sb.append(tarifaId).append('|').append(partidoId).append('|').append(monto).append(';');
                });
        return sha256Hex(sb.toString());
    }

    private String sha256Hex(String data) {
        try {
            var md = java.security.MessageDigest.getInstance("SHA-256");
            byte[] dig = md.digest(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : dig) {
                String h = Integer.toHexString(0xff & b);
                if (h.length() == 1) hex.append('0');
                hex.append(h);
            }
            return hex.toString();
        } catch (Exception e) {
            return Integer.toHexString(data.hashCode());
        }
    }

    // ==================== PDF =====================
    private byte[] generarPdf(Arbitro a, List<Tarifa> filas, BigDecimal total) {
        Locale locale = LocaleContextHolder.getLocale();
        if (locale == null) {
            locale = Locale.getDefault(); 
        }

        System.out.println("🗣 Generando PDF en idioma: " + locale);

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document doc = new Document(PageSize.A4);
            PdfWriter.getInstance(doc, baos);
            doc.open();

            Font h1 = new Font(Font.HELVETICA, 16, Font.BOLD);
            Font normal = new Font(Font.HELVETICA, 10);

            
            doc.add(new Paragraph(messageSource.getMessage("pdf.title", null, locale), h1));
            doc.add(new Paragraph(messageSource.getMessage("pdf.referee", null, locale) + ": "
                    + a.getNombre() + " " + a.getApellido(), normal));
            doc.add(new Paragraph(messageSource.getMessage("pdf.email", null, locale) + ": "
                    + a.getCorreo(), normal));
            doc.add(new Paragraph(messageSource.getMessage("pdf.date", null, locale) + ": "
                    + LocalDateTime.now(), normal));
            doc.add(Chunk.NEWLINE);

            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);
            table.addCell(messageSource.getMessage("pdf.table.date", null, locale));
            table.addCell(messageSource.getMessage("pdf.table.tournament", null, locale));
            table.addCell(messageSource.getMessage("pdf.table.match", null, locale));
            table.addCell(messageSource.getMessage("pdf.table.venue", null, locale));
            table.addCell(messageSource.getMessage("pdf.table.amount", null, locale));

            filas.forEach(t -> {
                table.addCell(t.getPartido().getFecha().toString());
                table.addCell(t.getTorneo().getNombre());
                table.addCell(t.getPartido().getEquipoLocal() + " vs " + t.getPartido().getEquipoVisitante());
                table.addCell(t.getPartido().getLugar());
                table.addCell(t.getMonto().toPlainString());
            });

            doc.add(table);
            doc.add(Chunk.NEWLINE);

            Paragraph tot = new Paragraph(
                    messageSource.getMessage("pdf.total", null, locale) + ": " + total.toPlainString(),
                    new Font(Font.HELVETICA, 12, Font.BOLD)
            );
            doc.add(tot);

            doc.close();
            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException(
                    messageSource.getMessage("error.pdf.generation", null, locale) + ": " + e.getMessage(), e
            );
        }
    }
}

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
import com.proyecto.cabapro.repository.ArbitroRepository;
import com.proyecto.cabapro.repository.LiquidacionRepository;
import com.proyecto.cabapro.repository.PagoRepository;
import com.proyecto.cabapro.repository.PartidoRepository;
import com.proyecto.cabapro.repository.TarifaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

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
        Arbitro a = arbitroRepo.findById(arbitroId)
                .orElseThrow(() -> new IllegalArgumentException("Árbitro no encontrado"));
        return liquidacionRepo.findByArbitroOrderByFechaGeneradaDesc(a);
    }

    /** Genera liquidación para todas las tarifas pendientes del árbitro. */
    public Liquidacion generarParaArbitro(Integer arbitroId) {
        Arbitro a = arbitroRepo.findById(arbitroId)
                .orElseThrow(() -> new IllegalArgumentException("Árbitro no encontrado"));

        // Asegura tarifas solo para partidos elegibles (FINALIZADOS) usando la misma regla del TarifaService
        autoGenerarTarifasSiFaltan(a);

        // Toma pendientes (no liquidadas)
        List<Tarifa> pendientes = tarifaRepo.findByArbitroAndLiquidacionIsNullOrderByGeneradoEnAsc(a);
        if (pendientes.isEmpty()) {
            throw new IllegalStateException("El árbitro no tiene tarifas pendientes de liquidar.");
        }

      
        String firma = firmaDeTarifas(pendientes);
        if (liquidacionRepo.existsByArbitroAndFirma(a, firma)) {
            throw new DuplicateLiquidacionException(
                "Ya existe una liquidación idéntica para este árbitro (evitamos duplicados)."
            );
        }

        // Total
        BigDecimal total = pendientes.stream()
                .map(Tarifa::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // PDF
        byte[] pdf = generarPdf(a, pendientes, total);

       
        Liquidacion liq = new Liquidacion();
        liq.setArbitro(a);
        liq.setFechaGenerada(LocalDateTime.now());
        liq.setEstado(EstadoLiquidacion.PENDIENTE);
        liq.setTotal(total);
        liq.setFirma(firma);
        liq.setPdf(pdf);
        liq = liquidacionRepo.save(liq);

        // Marcar tarifas como liquidadas
        for (Tarifa t : pendientes) {
            t.setLiquidacion(liq);
        }
        tarifaRepo.saveAll(pendientes);

        return liq;
    }

    public void pagar(Long liquidacionId) {
        Liquidacion l = liquidacionRepo.findById(liquidacionId)
                .orElseThrow(() -> new IllegalArgumentException("Liquidación no encontrada"));

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
        Liquidacion l = liquidacionRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Liquidación no encontrada"));
        return l.getPdf();
    }

    // ===== helpers =====
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

    private byte[] generarPdf(Arbitro a, List<Tarifa> filas, BigDecimal total) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document doc = new Document(PageSize.A4);
            PdfWriter.getInstance(doc, baos);
            doc.open();

            Font h1 = new Font(Font.HELVETICA, 16, Font.BOLD);
            Font normal = new Font(Font.HELVETICA, 10);

            doc.add(new Paragraph("Liquidación de árbitro", h1));
            doc.add(new Paragraph("Árbitro: " + a.getNombre() + " " + a.getApellido(), normal));
            doc.add(new Paragraph("Correo: " + a.getCorreo(), normal));
            doc.add(new Paragraph("Fecha: " + LocalDateTime.now(), normal));
            doc.add(Chunk.NEWLINE);

            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);
            table.addCell("Fecha");
            table.addCell("Torneo");
            table.addCell("Partido");
            table.addCell("Lugar");
            table.addCell("Monto");

            filas.forEach(t -> {
                table.addCell(t.getPartido().getFecha().toString());
                table.addCell(t.getTorneo().getNombre());
                table.addCell(t.getPartido().getEquipoLocal() + " vs " + t.getPartido().getEquipoVisitante());
                table.addCell(t.getPartido().getLugar());
                table.addCell(t.getMonto().toPlainString());
            });

            doc.add(table);
            doc.add(Chunk.NEWLINE);
            Paragraph tot = new Paragraph("TOTAL: " + total.toPlainString(), new Font(Font.HELVETICA, 12, Font.BOLD));
            doc.add(tot);

            doc.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generando PDF: " + e.getMessage(), e);
        }
    }
}

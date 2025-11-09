package com.proyecto.cabapro.service;

import com.proyecto.cabapro.model.Asignacion;
import com.proyecto.cabapro.model.Partido;
import com.proyecto.cabapro.model.Arbitro;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.from:Cabapro <no-reply@cabapro.local>}")
    private String from;

    public MailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

public void notificarNuevaAsignacion(Asignacion asg) {
    Arbitro a = asg.getArbitro();
    Partido p = asg.getPartido();
    if (a == null || p == null || a.getCorreo() == null || a.getCorreo().isBlank()) return;

    String subject = "Tienes una nueva asignación";

    String body = "Hola " + a.getNombre() + ",\n\n"
            + "Tienes una nueva asignación PENDIENTE:\n"
            + "- Partido: " + p.getEquipoLocal() + " vs " + p.getEquipoVisitante() + "\n"
            + "- Fecha: " + p.getFecha() + "\n"
            + "- Torneo: " + (p.getTorneo() != null ? p.getTorneo().getNombre() : "-") + "\n"
            + "- Monto: $" + (asg.getMonto() != null ? asg.getMonto().setScale(2) : "0.00") + "\n\n"
            + "Ingresa a Cabapro para ACEPTAR o RECHAZAR la asignación.\n\n"
            + "Saludos.";

    enviar(a.getCorreo(), subject, body);
}


    private void enviar(String to, String subject, String body) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(from);
        msg.setTo(to);
        msg.setSubject(subject);
        msg.setText(body);
        mailSender.send(msg);
    }
}

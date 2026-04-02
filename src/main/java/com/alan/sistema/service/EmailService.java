package com.alan.sistema.service;

import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.Attachment;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;

@Service
public class EmailService {

    private final Resend resend;
    private final String token;

    public EmailService(@Value("${resend.token}") String token){
        this.resend = new Resend(token);
        this.token = token;
    }

    public void enviarEmail(String emailDestino, byte[] pdfContent, String fileName){
        String base64Content = Base64.getEncoder().encodeToString(pdfContent);

        Attachment att = Attachment.builder()
            .fileName(fileName)
            .content(base64Content)
            .build();

        CreateEmailOptions params = CreateEmailOptions.builder()
            .from("Tech Noor <onboarding@resend.dev>")
            .to(emailDestino)
            .subject("Boleto da associação do plano de assinatura")
            .html("<strong>Segue em anexo o boleto gerado.</strong>")
            .attachments(att)
            .build();

        try {
            CreateEmailResponse data = resend.emails().send(params);
            System.out.println("email enviado: "+data.getId());
        } catch (ResendException e) {
            e.printStackTrace();
            System.out.println("Erro ao enviar email: "+e.getMessage());
        }
    }
}

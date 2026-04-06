package com.alan.sistema.util;
import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.io.ByteArrayOutputStream;
import java.util.Base64;

import org.springframework.stereotype.Component;

@Component
public class ZapSignUtil {

    public String converterPdfParaBase64(String caminhoDoArquivo) {
        try {
            // 1. Localiza o arquivo no seu computador
            Path path = Paths.get(caminhoDoArquivo);
            
            // 2. Lê todos os bytes do arquivo de uma vez
            byte[] pdfBytes = Files.readAllBytes(path);
            
            // 3. Converte os bytes para a String Base64
            return Base64.getEncoder().encodeToString(pdfBytes);
            
        } catch (Exception e) {
            throw new RuntimeException("Erro ao ler o arquivo PDF: " + caminhoDoArquivo, e);
        }
    }

    public String gerarContratoBase64(String nomeEmpresa) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document();
            PdfWriter.getInstance(document, baos);
            
            document.open();
            document.add(new Paragraph("CONTRATO DE PRESTAÇÃO DE SERVIÇOS"));
            document.add(new Paragraph(" "));
            document.add(new Paragraph("Contratante: " + nomeEmpresa));
            document.add(new Paragraph("Data de Emissão: " + java.time.LocalDate.now()));
            document.add(new Paragraph(" "));
            document.add(new Paragraph("Este é um documento de teste para integração com ZapSign."));
            document.close();
    
            // Converte o array de bytes resultante para uma String Base64
            return Base64.getEncoder().encodeToString(baos.toByteArray());
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar PDF temporário", e);
        }
    }

}

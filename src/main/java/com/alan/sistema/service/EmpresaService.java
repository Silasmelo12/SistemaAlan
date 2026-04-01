package com.alan.sistema.service;

import org.springframework.stereotype.Service;

import com.alan.sistema.client.AsaasClient;
import com.alan.sistema.dto.AsaasCobrancaCreateRequestDTO;
import com.alan.sistema.dto.AsaasCobrancaCreateResponseDTO;
import com.alan.sistema.dto.AsaasCustomerCreateRequestDTO;
import com.alan.sistema.dto.AsaasCustomerCreateResponseDTO;
import com.alan.sistema.enumeration.BillingType;
import com.alan.sistema.requests.EmpresaPostRequestBody;
import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.Attachment;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;

@Service
public class EmpresaService {

    private final AsaasClient asaasClient;
    private final String ASAAS_TOKEN = "$aact_hmlg_000MzkwODA2MWY2OGM3MWRlMDU2NWM3MzJlNzZmNGZhZGY6OjAyZTAzYTlhLTBlMDMtNDJjOS05OTZjLWY0OTliZjYyYmJhODo6JGFhY2hfYThiZjkwMjYtZjg3ZS00M2M4LWFhNmUtNDlkYzA2NDQ0MGM1"; // Use seu token aqui

    public EmpresaService(AsaasClient asaasClient) {
        this.asaasClient = asaasClient;
    }

    /**
     * Cria um cliente na plataforma Asaas a partir dos dados de uma empresa.
     *
     * @param empresaPostRequestBody dados da empresa.
     * @return o ID do cliente criado no Asaas.
     */
    public String criarClienteAsaas(EmpresaPostRequestBody empresaPostRequestBody) {
        AsaasCustomerCreateRequestDTO asaasCustomerCreateRequestDTO = new AsaasCustomerCreateRequestDTO(
                empresaPostRequestBody.getName(),
                empresaPostRequestBody.getCpfCnpj(),
                empresaPostRequestBody.getEmail()
        );

        AsaasCustomerCreateResponseDTO response = asaasClient.criarCliente(ASAAS_TOKEN, asaasCustomerCreateRequestDTO);

        AsaasCobrancaCreateRequestDTO asaasCobrancaCreateRequestDTO = new AsaasCobrancaCreateRequestDTO(
                response.getId(),
                BillingType.BOLETO,
                100.0,
                "2026-12-12"
        );

        // Gera a cobrança e verifica se o boleto foi criado corretamente
        AsaasCobrancaCreateResponseDTO cobrancaResponse = criarCobranca(asaasCobrancaCreateRequestDTO);
        if (cobrancaResponse != null && cobrancaResponse.getId() != null) {
            System.out.println("Boleto gerado com sucesso! ID do boleto: " + cobrancaResponse.getBankSlipUrl());
        } else {
            System.out.println("Falha ao gerar o boleto.");
        }

        try {
            String bankSlipUrl = cobrancaResponse.getBankSlipUrl();
            if (bankSlipUrl != null && !bankSlipUrl.isEmpty()) {
                java.net.URL url = new java.net.URL(bankSlipUrl);
                java.net.HttpURLConnection connection = (java.net.HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                int responseCode = connection.getResponseCode();
                if (responseCode == java.net.HttpURLConnection.HTTP_OK) {
                    java.io.InputStream inputStream = connection.getInputStream();
                    java.io.File outputFile = new java.io.File("boleto_" + cobrancaResponse.getId() + ".pdf");
                    java.io.FileOutputStream outputStream = new java.io.FileOutputStream(outputFile);

                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }

                    outputStream.close();
                    inputStream.close();

                    System.out.println("Boleto baixado com sucesso em: " + outputFile.getAbsolutePath());

                    //envio do email
                    Resend resend = new Resend("re_PHcKKNd5_G2ex4AQPpcqzdQeHFr2KfukP");

                    // Lê o arquivo PDF para bytes base64
                    java.io.File pdfFile = new java.io.File("boleto_" + cobrancaResponse.getId() + ".pdf");
                    byte[] pdfBytes = null;
                    try (java.io.FileInputStream fis = new java.io.FileInputStream(pdfFile)) {
                        pdfBytes = fis.readAllBytes();
                    } catch (Exception e) {
                        System.out.println("Erro ao ler o arquivo PDF: " + e.getMessage());
                        pdfBytes = null;
                    }

                    if (pdfBytes != null) {
                        // Codifica o PDF em base64
                        String base64Pdf = java.util.Base64.getEncoder().encodeToString(pdfBytes);

                        // Monta o anexo usando o modelo do Resend
                        // o Resend aceita anexos via CreateEmailOptions.Attachment
                        String attachment = "boleto_" + cobrancaResponse.getId() + ".pdf";
                        String attachmentContent = java.util.Base64.getEncoder().encodeToString(pdfBytes);

                        Attachment att = Attachment.builder()
                            .fileName("boleto.pdf")
                            .content(attachmentContent)
                            .build();
                        
                        CreateEmailOptions params = CreateEmailOptions.builder()
                                .from("Acme <onboarding@resend.dev>")
                                .to("silasmelo12@gmail.com")
                                .subject("Boleto da associação Alan 01/04/2026")
                                .html("<strong>Segue em anexo o boleto gerado.</strong>")
                                .attachments(att)
                                .build();

                        try {
                            CreateEmailResponse data = resend.emails().send(params);
                            System.out.println("email enviado: "+data.getId());
                        } catch (ResendException e) {
                            e.printStackTrace();
                        }
                    } else {
                        System.out.println("O arquivo PDF não foi anexado porque não pôde ser lido.");
                    }
                } else {
                    System.out.println("Falha ao baixar o boleto. Código de resposta HTTP: " + responseCode);
                }
                connection.disconnect();
            } else {
                System.out.println("URL do boleto não encontrada.");
            }
        } catch (Exception e) {
            System.out.println("Erro ao baixar o boleto: " + e.getMessage());
        }

        return response.getId();
    }

    public AsaasCobrancaCreateResponseDTO criarCobranca(AsaasCobrancaCreateRequestDTO dadosCobranca) {
        // Chama o método do Feign Client que cria uma cobrança no Asaas
        AsaasCobrancaCreateResponseDTO response = asaasClient.criarCobranca(ASAAS_TOKEN, dadosCobranca);

        // Retorna o ID da cobrança criada (ajuste conforme o DTO de resposta de cobrança!)
        return response;
    }
}

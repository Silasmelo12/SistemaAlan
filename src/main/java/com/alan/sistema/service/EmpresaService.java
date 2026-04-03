package com.alan.sistema.service;

import org.springframework.stereotype.Service;

import com.alan.sistema.dto.AsaasCobrancaCreateRequestDTO;
import com.alan.sistema.dto.AsaasCobrancaCreateResponseDTO;
import com.alan.sistema.dto.AsaasCustomerCreateRequestDTO;
import com.alan.sistema.dto.AsaasCustomerCreateResponseDTO;
import com.alan.sistema.enumeration.BillingType;
import com.alan.sistema.requests.EmpresaPostRequestBody;

@Service
public class EmpresaService {

    //private final AsaasClient asaasClient;
    private final AsaasService asaasService;
    private final EmailService emailService;
    //private final String ASAAS_TOKEN = "$aact_hmlg_000MzkwODA2MWY2OGM3MWRlMDU2NWM3MzJlNzZmNGZhZGY6OjAyZTAzYTlhLTBlMDMtNDJjOS05OTZjLWY0OTliZjYyYmJhODo6JGFhY2hfYThiZjkwMjYtZjg3ZS00M2M4LWFhNmUtNDlkYzA2NDQ0MGM1"; // Use seu token aqui

    public EmpresaService(AsaasService asaasService, EmailService emailService) {
        //this.asaasClient = asaasClient;
        this.asaasService = asaasService;
        this.emailService = emailService;
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

        AsaasCustomerCreateResponseDTO response = asaasService.criarCliente(asaasCustomerCreateRequestDTO);

        return response.getId();
    }

    public AsaasCobrancaCreateResponseDTO criarCobranca(AsaasCobrancaCreateRequestDTO dadosCobranca) {

        // Chama o método do Feign Client que cria uma cobrança no Asaas
        AsaasCobrancaCreateResponseDTO response = asaasService.criarCobranca(dadosCobranca);

        // Retorna o ID da cobrança criada (ajuste conforme o DTO de resposta de cobrança!)
        return response;
    }

    public String processarAdesao(EmpresaPostRequestBody empresaPostRequestBody){
        // 1. Cria Cliente
        AsaasCustomerCreateRequestDTO asaasCustomerCreateRequestDTO = new AsaasCustomerCreateRequestDTO(
            empresaPostRequestBody.getName(),
            empresaPostRequestBody.getCpfCnpj(),
            empresaPostRequestBody.getEmail()
        );
        AsaasCustomerCreateResponseDTO asaasCustomerCreateResponseDTO = asaasService.criarCliente(asaasCustomerCreateRequestDTO);
        
        // 2. Cria Cobrança
        AsaasCobrancaCreateRequestDTO asaasCobrancaCreateRequestDTO = new AsaasCobrancaCreateRequestDTO(
            asaasCustomerCreateResponseDTO.getId(),
                BillingType.BOLETO,
                100.0,
                "2026-12-12" // Vencimento para daqui a 5 dias
        );
        AsaasCobrancaCreateResponseDTO asaasCobrancaCreateResponseDTO = asaasService.criarCobranca(asaasCobrancaCreateRequestDTO);

        if (asaasCobrancaCreateResponseDTO != null && asaasCobrancaCreateResponseDTO.getId() != null ){
            System.out.println("Boleto gerado com sucesso! ID do boleto: " + asaasCobrancaCreateResponseDTO.getBankSlipUrl());

            // 3. Processa PDF e Envio (Opcional: Pode ser Assíncrono @Async)
            if(asaasCobrancaCreateResponseDTO.getBankSlipUrl() != null){
                byte[] pdfContent = asaasService.baixarBoletoPdf(asaasCobrancaCreateResponseDTO.getBankSlipUrl());
                String emailDestino = asaasCustomerCreateRequestDTO.getEmail();
                String fileName = "boleto.pdf";
                emailService.enviarEmail(emailDestino, pdfContent, fileName);
            }
        }
        return asaasCustomerCreateResponseDTO.getId();
    }
}

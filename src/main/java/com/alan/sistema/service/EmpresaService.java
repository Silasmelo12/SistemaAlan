package com.alan.sistema.service;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alan.sistema.client.ZapSignClient;
import com.alan.sistema.dto.AsaasCobrancaCreateRequestDTO;
import com.alan.sistema.dto.AsaasCobrancaCreateResponseDTO;
import com.alan.sistema.dto.AsaasCustomerCreateRequestDTO;
import com.alan.sistema.dto.AsaasCustomerCreateResponseDTO;
import com.alan.sistema.dto.EmpresaResponseDTO;
import com.alan.sistema.dto.ZapSignDocumentRequestDTO;
import com.alan.sistema.dto.ZapSignDocumentResponseDTO;
import com.alan.sistema.dto.ZapSignSignerDTO;
import com.alan.sistema.enumeration.BillingType;
import com.alan.sistema.enumeration.EmpresaStatus;
import com.alan.sistema.model.AsaasData;
import com.alan.sistema.model.Empresa;
import com.alan.sistema.model.ZapSignData;
import com.alan.sistema.repository.EmpresaRepository;
import com.alan.sistema.requests.EmpresaPostRequestBody;
import com.alan.sistema.util.ZapSignUtil;

@Service
public class EmpresaService {

    private final AsaasService asaasService;
    private final EmailService emailService;
    private final EmpresaRepository empresaRepository;
    private final ZapSignClient zapSignClient;
    private final ZapSignUtil zapSignService;
    private static final Logger log = LoggerFactory.getLogger(EmpresaService.class);
    private String zapSignToken;
    
    public EmpresaService(AsaasService asaasService,
            EmailService emailService,
            EmpresaRepository empresaRepository,
            ZapSignClient zapSignClient,
            ZapSignUtil zapSignService,
        @Value("${zapsign.token}") String zapSignToken) {
        this.asaasService = asaasService;
        this.emailService = emailService;
        this.empresaRepository = empresaRepository;
        this.zapSignClient = zapSignClient;
        this.zapSignService = zapSignService;
        this.zapSignToken= zapSignToken;

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

    public String processarAdesao(EmpresaPostRequestBody empresaPostRequestBody) {

        if (empresaRepository.existsByCpfCnpj(empresaPostRequestBody.getCpfCnpj())) {
            throw new RuntimeException("Já existe uma empresa cadastrada com este CNPJ.");
        }
        Empresa empresa = empresaRepository.findByCpfCnpj(empresaPostRequestBody.getCpfCnpj()).orElse(null);
        log.info("Token Asaas carregado: {}...", zapSignToken.substring(0, 5));
        if (empresa == null) {
            empresa = new Empresa();
            empresa.setName(empresaPostRequestBody.getName());
            empresa.setCpfCnpj(empresaPostRequestBody.getCpfCnpj());
            empresa.setEmail(empresaPostRequestBody.getEmail());
            empresa.setTelefone(empresaPostRequestBody.getTelefone());
            empresa.setDataCriacao(Instant.now());
            empresa.setStatus(EmpresaStatus.PROCESSANDO);
            empresa.setId(empresaRepository.save(empresa).getId());
        } else {
            empresa.setName(empresaPostRequestBody.getName());
            empresa.setEmail(empresaPostRequestBody.getEmail());
            empresa.setTelefone(empresaPostRequestBody.getTelefone());
            empresaRepository.save(empresa);

            if (empresa.getZapsignData() != null && empresa.getZapsignData().getSignUrl() != null) {
                if (empresa.getStatus() != EmpresaStatus.AGUARDANDO_ASSINATURA) {
                    empresa.setStatus(EmpresaStatus.AGUARDANDO_ASSINATURA);
                    empresaRepository.save(empresa);
                }
                log.info("Documento ZapSign já existente para o CPF/CNPJ. Retornando id.");
                return empresa.getId();
            }
            if (jaFluxoAdesaoJaAvancado(empresa.getStatus())) {
                log.info(
                        "Empresa já existe com adesão em etapa final ou concluída (status={}). Retornando id sem reprocessar integrações.",
                        empresa.getStatus());
                return empresa.getId();
            }
        }

        boolean precisaCriarClienteAsaas = empresa.getAsaasData() == null
                || empresa.getAsaasData().getCustomerId() == null
                || empresa.getAsaasData().getCustomerId().isBlank();

        if (precisaCriarClienteAsaas) {
            AsaasCustomerCreateRequestDTO asaasCustomerCreateRequestDTO = new AsaasCustomerCreateRequestDTO(
                    empresaPostRequestBody.getName(),
                    empresaPostRequestBody.getCpfCnpj(),
                    empresaPostRequestBody.getEmail()
            );
            AsaasCustomerCreateResponseDTO asaasCustomerCreateResponseDTO = asaasService.criarCliente(asaasCustomerCreateRequestDTO);

            if (asaasCustomerCreateResponseDTO.getId() == null) {
                log.info("Cliente não foi criado no Asaas, mas as informações estão salvas no banco para posterior processamento.");
                return empresa.getId();
            }

            AsaasData asaasData = new AsaasData();
            asaasData.setCustomerId(asaasCustomerCreateResponseDTO.getId());

            empresa.setStatus(EmpresaStatus.CLIENTE_CRIADO);
            empresa.setAsaasData(asaasData);
            empresaRepository.save(empresa);
        } else {
            log.info("Cliente Asaas já vinculado (customerId={}). Pulando criação e retomando fluxo.",
                    empresa.getAsaasData().getCustomerId());
        }

        // No EmpresaService
        //String pdfBase64 = zapSignService.gerarContratoBase64("empresa.getName()");
        //String caminho = "C:/Users/silas/OneDrive/Documentos/contrato_modelo.pdf";
        //String pdfbase64 = zapSignService.converterPdfParaBase64(caminho);
        ZapSignDocumentRequestDTO zapRequest = new ZapSignDocumentRequestDTO();
        try {
            // 1. Coloque o arquivo em: src/main/resources/contrato_modelo.pdf
            org.springframework.core.io.Resource resource = new org.springframework.core.io.ClassPathResource("contrato_modelo.pdf");
            byte[] pdfBytes = resource.getInputStream().readAllBytes();
            String pdfbase64 = java.util.Base64.getEncoder().encodeToString(pdfBytes);
            
            log.info("PDF carregado com sucesso do classpath para a empresa: {}", empresa.getName());
            zapRequest.setBase64_pdf(pdfbase64);
        } catch (IOException e) {
            log.error("Erro ao carger o modelo de contrato: {}", e.getMessage());
            throw new RuntimeException("Falha interna: Modelo de contrato não encontrado no servidor.");
        }

        //log.info("Base 64: " + pdfbase64.getBytes());
        //ZapSignDocumentRequestDTO zapRequest = new ZapSignDocumentRequestDTO();
        //zapRequest.setBase64(pdfBase64); // <--- O ZapSign vai receber isso e transformar em PDF lá
        //zapRequest.setName("Contrato_" + empresa.getName() + ".pdf");
        // ... restante do código de signers

        // só posso criar a cobrança depois do contrato assinado.
        // 1. Prepara o Signer (o dono da empresa)
        ZapSignSignerDTO signer = new ZapSignSignerDTO();
        signer.setName(empresa.getName());
        signer.setEmail(empresa.getEmail());

        // 2. Monta a requisição
        
        zapRequest.setName("Contrato de Prestação de Serviços - " + empresa.getName());
        //zapRequest.setUrl("https://eppge.fgv.br/sites/default/files/teste.pdf"); // O PDF que você gerou
        //zapRequest.setBase64_pdf(pdfbase64);
        zapRequest.setSigners(Collections.singletonList(signer));

        // 3. Chama a API
        ZapSignDocumentResponseDTO zapRes = zapSignClient.criarDocumento("Bearer " + zapSignToken, zapRequest);

        // 4. Salva o link de assinatura no seu MongoDB
        ZapSignData zapData = new ZapSignData();
        zapData.setExternalId(zapRes.getToken()); // ID do documento no ZapSign
        zapData.setSignUrl(zapRes.getSigners().get(0).getSign_url()); // Link para o cliente clicar

        empresa.setZapsignData(zapData);
        empresaRepository.save(empresa);

        // 2. Cria Cobrança
        empresa.setStatus(EmpresaStatus.AGUARDANDO_ASSINATURA);
        empresaRepository.save(empresa);
        
        return empresa.getId();
    }

    public String processarAssinaturaZapSign(String tokenDoc) {
        // Busca a empresa pelo token que você salvou na adesão
        Empresa empresa = empresaRepository.findByZapsignDataExternalId(tokenDoc)
                .orElseThrow(() -> new RuntimeException("Empresa não encontrada para o token: " + tokenDoc));

        log.info("Contrato assinado para a empresa: {}", empresa.getName());

        // Atualiza o status
        if (empresa.getStatus() == EmpresaStatus.CLIENTE_CRIADO) {
            return empresa.getAsaasData().getCustomerId();
        }
        if (empresa.getStatus() == EmpresaStatus.AGUARDANDO_ASSINATURA) {
        // criar Cobrança
            AsaasCobrancaCreateRequestDTO dadosCobranca = new AsaasCobrancaCreateRequestDTO(
                    empresa.getAsaasData().getCustomerId(),
                    BillingType.BOLETO,
                    100.0,
                    LocalDate.now().plusDays(5).toString() // Vencimento para daqui a 5 dias
            );

            AsaasCobrancaCreateResponseDTO asaasCobrancaCreateResponseDTO = asaasService.criarCobranca(dadosCobranca);
            if (asaasCobrancaCreateResponseDTO.getId() == null) {
                log.info("Cliente foi criado no Asaas, mas não foi possível gerar o boleto.");
                return empresa.getId();
            }

            empresa.setStatus(EmpresaStatus.AGUARDANDO_PAGAMENTO);
            AsaasData asaasData = new AsaasData();
            asaasData.setPaymentId(asaasCobrancaCreateResponseDTO.getId());
            asaasData.setBillingType(asaasCobrancaCreateResponseDTO.getBillingType());
            asaasData.setInvoiceUrl(asaasCobrancaCreateResponseDTO.getInvoiceUrl());
            asaasData.setCustomerId(empresa.getAsaasData().getCustomerId());
            empresa.setAsaasData(asaasData);
            empresaRepository.save(empresa);

// 3. Processa PDF e Envio (Opcional: Pode ser Assíncrono @Async)
            if (asaasCobrancaCreateResponseDTO.getBankSlipUrl() != null) {
                byte[] pdfContent = asaasService.baixarBoletoPdf(asaasCobrancaCreateResponseDTO.getBankSlipUrl());
                String emailDestino = empresa.getEmail();
                String fileName = "boleto.pdf";
                emailService.enviarEmail(emailDestino, pdfContent, fileName);
            }

// Lógica de Negócio: Agora que assinou, podemos confirmar a cobrança ou liberar algo
            log.info("Fluxo de adesão avançando para próxima etapa...");
            return empresa.getAsaasData().getInvoiceUrl();
        }

        return empresa.getId();
    }

    public EmpresaResponseDTO consultarProgresso(String id) {

        return empresaRepository.findById(id)
                .map(empresa -> new EmpresaResponseDTO(
                id,
                empresa.getName(),
                empresa.getStatus().name(),
                empresa.getZapsignData().getSignUrl(),
                empresa.getAsaasData().getInvoiceUrl(),
                empresa.getCpfCnpj()
        )).orElse(null);
    }

    public List<Empresa> listarTudo() {
        return empresaRepository.findAll();
    }

    public void deletarTodosClientes() {
        // TODO Auto-generated method stub
        asaasService.deletarTodosClientes();
    }

    /**
     * Idempotência: após gerar link de assinatura ou evoluir no funil de cobrança, não recria cliente/contrato.
     */
    private boolean jaFluxoAdesaoJaAvancado(EmpresaStatus status) {
        if (status == null) {
            return false;
        }
        return status == EmpresaStatus.AGUARDANDO_ASSINATURA
                || status == EmpresaStatus.AGUARDANDO_PAGAMENTO
                || status == EmpresaStatus.ATIVO
                || status == EmpresaStatus.CONTRATO_ASSINADO
                || status == EmpresaStatus.INADIMPLENTE
                || status == EmpresaStatus.BLOQUEADO;
    }

    public void limparTudoGeral() {
        log.info("Token Asaas carregado: {}...", zapSignToken.substring(0, 50));
        log.warn("⚠️ INICIANDO LIMPEZA TOTAL: Asaas + MongoDB");

        try {
            // 1. Limpa o Asaas (usando o método que acabamos de ajustar)
            asaasService.deletarTodosClientes();
            log.info("✅ Todos os clientes foram removidos do Asaas.");

            // 2. Limpa o MongoDB
            empresaRepository.deleteAll();
            log.info("✅ Todas as empresas foram removidas do MongoDB.");

        } catch (Exception e) {
            log.error("❌ Erro durante a limpeza geral: {}", e.getMessage());
            throw new RuntimeException("Falha ao sincronizar a limpeza: " + e.getMessage());
        }
    }

    // private String criarCobranca(AsaasCustomerCreateResponseDTO asaasCustomerCreateResponseDTO, Empresa empresa, AsaasData asaasData, AsaasCustomerCreateRequestDTO asaasCustomerCreateRequestDTO) {
    //     AsaasCobrancaCreateRequestDTO asaasCobrancaCreateRequestDTO = new AsaasCobrancaCreateRequestDTO(
    //             asaasCustomerCreateResponseDTO.getId(),
    //             BillingType.BOLETO,
    //             100.0,
    //             LocalDate.now().plusDays(5).toString() // Vencimento para daqui a 5 dias
    //     );

    //     AsaasCobrancaCreateResponseDTO asaasCobrancaCreateResponseDTO = asaasService.criarCobranca(asaasCobrancaCreateRequestDTO);
    //     if (asaasCobrancaCreateResponseDTO.getId() == null) {
    //         log.info("Cliente foi criado no Asaas, mas não foi possível gerar o boleto.");
    //         return empresa.getId();
    //     }

    //     empresa.setStatus(EmpresaStatus.AGUARDANDO_PAGAMENTO);
    //     asaasData.setPaymentId(asaasCobrancaCreateResponseDTO.getId());
    //     asaasData.setBillingType(asaasCobrancaCreateResponseDTO.getBillingType());
    //     asaasData.setInvoiceUrl(asaasCobrancaCreateResponseDTO.getInvoiceUrl());
    //     asaasData.setCustomerId(asaasCustomerCreateResponseDTO.getId());
    //     empresa.setAsaasData(asaasData);
    //     empresaRepository.save(empresa);

    //     // 3. Processa PDF e Envio (Opcional: Pode ser Assíncrono @Async)
    //     if (asaasCobrancaCreateResponseDTO.getBankSlipUrl() != null) {
    //         byte[] pdfContent = asaasService.baixarBoletoPdf(asaasCobrancaCreateResponseDTO.getBankSlipUrl());
    //         String emailDestino = asaasCustomerCreateRequestDTO.getEmail();
    //         String fileName = "boleto.pdf";
    //         emailService.enviarEmail(emailDestino, pdfContent, fileName);
    //     }

    //     //salvar no banco
    //     //empresa.setAsaasData(asaasData);
    //     //empresa.setCpfCnpj(empresaPostRequestBody.getCpfCnpj());
    //     //empresa.setEmail(empresaPostRequestBody.getEmail());
    //     //empresaRepository.save(empresa);
    //     return asaasCustomerCreateResponseDTO.getId();
    // }

}

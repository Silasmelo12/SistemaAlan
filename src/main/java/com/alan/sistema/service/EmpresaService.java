package com.alan.sistema.service;

import java.time.LocalDate;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import com.alan.sistema.client.ZapSignClient;
import com.alan.sistema.dto.integration.asaas.AsaasCobrancaCreateRequestDTO;
import com.alan.sistema.dto.integration.asaas.AsaasCobrancaCreateResponseDTO;
import com.alan.sistema.dto.integration.asaas.AsaasCustomerCreateRequestDTO;
import com.alan.sistema.dto.integration.asaas.AsaasCustomerCreateResponseDTO;
import com.alan.sistema.dto.integration.asaas.AsaasWebhookDTO;
import com.alan.sistema.dto.integration.zapsign.ZapSignDocumentRequestDTO;
import com.alan.sistema.dto.integration.zapsign.ZapSignDocumentResponseDTO;
import com.alan.sistema.dto.integration.zapsign.ZapSignSignerDTO;
import com.alan.sistema.dto.integration.zapsign.ZapSignWebhookDTO;
import com.alan.sistema.dto.requests.EmpresaRequestDTO;
import com.alan.sistema.dto.response.EmpresaResponseDTO;
import com.alan.sistema.enumeration.BillingType;
import com.alan.sistema.enumeration.EmpresaStatus;
import com.alan.sistema.mapper.EmpresaMapper;
import com.alan.sistema.model.AsaasData;
import com.alan.sistema.model.Empresa;
import com.alan.sistema.model.ZapSignData;
import com.alan.sistema.repository.EmpresaRepository;


@Service
public class EmpresaService {

    private final AsaasService asaasService;
    private final EmailService emailService;
    private final EmpresaRepository empresaRepository;
    private final ZapSignClient zapSignClient;
    private final EmpresaMapper empresaMapper;
    private static final Logger log = LoggerFactory.getLogger(EmpresaService.class);
    private final String zapSignToken;
    
    public EmpresaService(AsaasService asaasService,
            EmailService emailService,
            EmpresaRepository empresaRepository,
            ZapSignClient zapSignClient,
            EmpresaMapper empresaMapper,
        @Value("${zapsign.token}") String zapSignToken) {
        this.asaasService = asaasService;
        this.emailService = emailService;
        this.empresaRepository = empresaRepository;
        this.zapSignClient = zapSignClient;
        this.zapSignToken= zapSignToken;
        this.empresaMapper = empresaMapper;

    }
    /**
     * Cria um cliente na plataforma Asaas a partir dos dados de uma empresa.
     *
     * @param empresaPostRequestBody dados da empresa.
     * @return o ID do cliente criado no Asaas.
     */
    public String criarClienteAsaas(EmpresaRequestDTO empresaPostRequestBody) {
        AsaasCustomerCreateRequestDTO asaasCustomerCreateRequestDTO = new AsaasCustomerCreateRequestDTO(
                empresaPostRequestBody.getName(),
                empresaPostRequestBody.getCpfCnpj(),
                empresaPostRequestBody.getEmail()
        );

        AsaasCustomerCreateResponseDTO response = asaasService.criarCliente(asaasCustomerCreateRequestDTO);

        return response.getId();
    }

    private boolean precisaCriarClienteAsaas(Empresa empresa) {
        return empresa.getAsaasData() == null
                || empresa.getAsaasData().getCustomerId() == null
                || empresa.getAsaasData().getCustomerId().isBlank();
    }

    private void executarIntegracaoAsaas(Empresa empresa, EmpresaRequestDTO dto){
        log.info("Iniciando integração com Asaas para a empresa: {}",empresa.getName());

        // Monta o DTO de envio para o Asaas
        AsaasCustomerCreateRequestDTO asaasReq = new AsaasCustomerCreateRequestDTO(
            dto.getName(),
            dto.getCpfCnpj(),
            dto.getEmail()
        );

        // Chama o serviço que usa o FeignClient
        AsaasCustomerCreateResponseDTO asaasRes = asaasService.criarCliente(asaasReq);

        if(asaasRes != null && asaasRes.getId() != null){
                AsaasData asaasData = new AsaasData();
                asaasData.setCustomerId(asaasRes.getId());

                empresa.setAsaasData(asaasData);
                log.info("Cliente criado no Asaas com sucesso? {}",asaasRes.getId());
        } else{
            log.error("Falha ao obter ID do Asaas para a empresa: {}",empresa.getName());
        }
    }

    public AsaasCobrancaCreateResponseDTO criarCobranca(AsaasCobrancaCreateRequestDTO dadosCobranca) {

        // Chama o método do Feign Client que cria uma cobrança no Asaas
        AsaasCobrancaCreateResponseDTO response = asaasService.criarCobranca(dadosCobranca);

        // Retorna o ID da cobrança criada (ajuste conforme o DTO de resposta de cobrança!)
        return response;
    }

    private boolean temContratoAtivo(Empresa empresa){
        return empresa.getZapsignData()!=null &&
        empresa.getZapsignData().getSignUrl() != null;
    }

    private void executarIntegracaoZapSign(Empresa empresa){
        try {
            log.info("Gerando contrato ZapSign para: {}",empresa.getName());

            // carrega modelo de contrato da pasta resources
            Resource resource = new ClassPathResource("contrato_modelo.pdf");
            byte[] pdfBytes = resource.getInputStream().readAllBytes();
            String pdfBase64 = Base64.getEncoder().encodeToString(pdfBytes);

            ZapSignSignerDTO signer = new ZapSignSignerDTO();
            signer.setName(empresa.getName());
            signer.setEmail(empresa.getEmail());

            ZapSignDocumentRequestDTO zapRequest = new ZapSignDocumentRequestDTO();
            zapRequest.setBase64_pdf(pdfBase64);
            zapRequest.setName("Contrato de prestação de serviços - "+empresa.getName());
            zapRequest.setSigners(Collections.singletonList(signer));

            ZapSignDocumentResponseDTO zapRes = zapSignClient.criarDocumento("Bearer " + zapSignToken, zapRequest);

            ZapSignData zapData = new ZapSignData();
            zapData.setExternalId(zapRes.getToken());
            zapData.setSignUrl(zapRes.getSigners().get(0).getSign_url());

            empresa.setZapsignData(zapData);
            empresa.setStatus(EmpresaStatus.AGUARDANDO_ASSINATURA);

            empresaRepository.save(empresa);

            log.info("Contrato gerado com sucesso. Link: {}",zapData.getSignUrl());
        } catch (Exception e) {
            log.error("Erro fatal ao carregar o PDF do contrato: {}",e.getMessage());
            throw new RuntimeException("Falha ao processar o contrato de adesão");
        }
    }
    
    public String processarAdesaoAtualizado(EmpresaRequestDTO empresaRequestDTO){

        Empresa empresa = empresaRepository.findByCpfCnpj(empresaRequestDTO.getCpfCnpj())
        .orElseGet(()->empresaMapper.toEmpresa(empresaRequestDTO));

        empresaMapper.updateEmpresaFromDto(empresaRequestDTO, empresa);

        empresa = empresaRepository.save(empresa);
        log.info("Fase 1 concluída: Dados sincronizados para o CNPJ {}", empresa.getCpfCnpj());

        if(precisaCriarClienteAsaas(empresa)){
            executarIntegracaoAsaas(empresa, empresaRequestDTO);
        } else{
            log.info("EMpresa {} já possui Cadastro no Asaas. Pulando criação",empresa.getName());
        }

        if(!temContratoAtivo(empresa)){
            executarIntegracaoZapSign(empresa);
        }else{
            log.info("Contrato já existente para o CNPJ {}: ",empresa.getCpfCnpj());
        }
        return empresa.getId();
    }

    public void processarAssinaturaZapSignAtualizado(ZapSignWebhookDTO webhookData){
        // só processamos se o documento foi totalmente assinado
        
        if (webhookData.getEvent_type() == null) {
            log.error("ERRO: Webhook recebido com eventType NULO. Verifique o mapeamento do DTO. JSON: {}", webhookData);
            return; // Sai do método sem fazer nada
        }
        
        if ("doc_viewed".equals(webhookData.getEvent_type())) {
            log.info("O cliente visualizou o contrato, mas ainda não assinou.");
            return; 
        }
    
        if (!"doc_signed".equals(webhookData.getEvent_type())) {
            log.warn("Evento desconhecido ou não mapeado: {}", webhookData.getEvent_type());
            return;
        }



        Empresa empresa = empresaRepository.findByZapsignDataExternalId(webhookData.getToken())
        .orElseThrow(()-> new RuntimeException("Empresa não encontrada para o token: {}"+webhookData.getToken()));

        log.info("Contrato assinado confirmado para a empresa: {}",empresa.getName());
        empresa.setStatus(EmpresaStatus.CONTRATO_ASSINADO);
        empresaRepository.save(empresa);

        gerarCobrancaInicialAsaas(empresa);
    }

    private void gerarCobrancaInicialAsaas(Empresa empresa) {
        double valorPorFuncionário = 50.0;
        int qtd = empresa.getQuantidadeFuncionarios() == null ? 1 : Math.max(1, empresa.getQuantidadeFuncionarios());
        double valorTotal = qtd*valorPorFuncionário;

        log.info("Gerando cobrança para {}: {} funcionários x R$ {} = R$ {}",
            empresa.getName(),qtd,valorPorFuncionário,valorTotal);
        
            AsaasCobrancaCreateRequestDTO cobrancaReq = new AsaasCobrancaCreateRequestDTO();
            cobrancaReq.setCustomer(empresa.getAsaasData().getCustomerId());
            cobrancaReq.setBillingType(BillingType.BOLETO);
            cobrancaReq.setValue(valorTotal);
            cobrancaReq.setDueDate(LocalDate.now().plusDays(5).toString());
            cobrancaReq.setDescription("Adesã ao Sistema Alan - Plano para "+qtd+" funcionários");

            try{
                AsaasCobrancaCreateResponseDTO res = asaasService.criarCobranca(cobrancaReq);

                empresa.getAsaasData().setUltimoPaymentId(res.getId());
                empresa.getAsaasData().setInvoiceUrl(res.getInvoiceUrl());
                empresa.setStatus(EmpresaStatus.AGUARDANDO_PAGAMENTO);

                empresaRepository.save(empresa);

                log.info("Cobrança gerada com sucesso! Link: {}",res.getInvoiceUrl());
            }catch(Exception e){
                log.error("Erro ao gerar cobrança no Asaas para {}: {}",empresa.getName(),e.getMessage());
            }

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

    public void limparTudoGeral() {
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

    public void processarPagamentoAsaas(AsaasWebhookDTO dto) {
        boolean pago = "PAYMENT_CONFIRMED".equals(dto.getEvent()) || 
        "PAYMENT_RECEIVED".equals(dto.getEvent()) ||
        "PAYMENT_UPDATED".equals(dto.getEvent());

        if(!pago){
            log.info("Evento Asaas ignorado (Não é confirmação de pagamento): {}",dto.getEvent());
            return;
        }

        Empresa empresa = empresaRepository.findByAsaasDataUltimoPaymentId(dto.getPayment().getId())
        .orElseThrow(()->new RuntimeException("Cobrança não encontrada: " + dto.getPayment().getId()));

        if(empresa.getStatus() != EmpresaStatus.ATIVO){
            empresa.setStatus(EmpresaStatus.ATIVO);
            empresaRepository.save(empresa);

            log.info("SUCESSO: Empresa {} agora está ATIVA!", empresa.getName());
        
            // 4. Próximo passo opcional: Enviar e-mail de boas-vindas com acesso ao sistema
            //enviarEmailBoasVindas(empresa);
        }
    }
}

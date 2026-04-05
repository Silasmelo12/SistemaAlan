package com.alan.sistema.service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;

import com.alan.sistema.dto.AsaasCobrancaCreateRequestDTO;
import com.alan.sistema.dto.AsaasCobrancaCreateResponseDTO;
import com.alan.sistema.dto.AsaasCustomerCreateRequestDTO;
import com.alan.sistema.dto.AsaasCustomerCreateResponseDTO;
import com.alan.sistema.enumeration.BillingType;
import com.alan.sistema.enumeration.EmpresaStatus;
import com.alan.sistema.model.AsaasData;
import com.alan.sistema.model.Empresa;
import com.alan.sistema.repository.EmpresaRepository;
import com.alan.sistema.requests.EmpresaPostRequestBody;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EmpresaService {

    private final AsaasService asaasService;
    private final EmailService emailService;
    private final EmpresaRepository empresaRepository;
    //private final String ASAAS_TOKEN = "$aact_hmlg_000MzkwODA2MWY2OGM3MWRlMDU2NWM3MzJlNzZmNGZhZGY6OjAyZTAzYTlhLTBlMDMtNDJjOS05OTZjLWY0OTliZjYyYmJhODo6JGFhY2hfYThiZjkwMjYtZjg3ZS00M2M4LWFhNmUtNDlkYzA2NDQ0MGM1"; // Use seu token aqui

    public EmpresaService(AsaasService asaasService, 
        EmailService emailService,
        EmpresaRepository empresaRepository) {
        this.asaasService = asaasService;
        this.emailService = emailService;
        this.empresaRepository = empresaRepository;

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

        Empresa empresa = empresaRepository.findByCpfCnpj(empresaPostRequestBody.getCpfCnpj()).orElse(new Empresa());

        // SÓ ATUALIZA SE FOR NOVO
        if (empresa.getId() == null) {
            empresa.setName(empresaPostRequestBody.getName());
            empresa.setCpfCnpj(empresaPostRequestBody.getCpfCnpj());
            empresa.setEmail(empresaPostRequestBody.getEmail());
            empresa.setTelefone(empresaPostRequestBody.getTelefone());
            empresa.setDataCriacao(Instant.now());
            empresa.setStatus(EmpresaStatus.PROCESSANDO);
            empresa.setId(empresaRepository.save(empresa).getId()); 
        } else {
            log.info("Empresa já existe no banco. Seguindo apenas para conferência no Asaas.");
            return empresa.getId();
        }
        // Salva a Empresa com status PENDENTE no MongoDB.
        
        // 1. Cria Cliente
        AsaasCustomerCreateRequestDTO asaasCustomerCreateRequestDTO = new AsaasCustomerCreateRequestDTO(
            empresaPostRequestBody.getName(),
            empresaPostRequestBody.getCpfCnpj(),
            empresaPostRequestBody.getEmail()
        );
        AsaasCustomerCreateResponseDTO asaasCustomerCreateResponseDTO = asaasService.criarCliente(asaasCustomerCreateRequestDTO);
        
        // Atualiza a Empresa no banco com o customerId retornado.
        
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

        //salvar no banco
        AsaasData asaasData = new AsaasData();
        asaasData.setCustomerId(asaasCustomerCreateResponseDTO.getId());

        empresa.setAsaasData(asaasData);
        //empresa.setCpfCnpj(empresaPostRequestBody.getCpfCnpj());
        //empresa.setEmail(empresaPostRequestBody.getEmail());
        
        empresaRepository.save(empresa);

        return asaasCustomerCreateResponseDTO.getId();
    }

    public EmpresaPostRequestBody consultarProgresso(String id) {
        
        return empresaRepository.findById(id)
        .map(empresa -> new EmpresaPostRequestBody(
            empresa.getName(),
            empresa.getCpfCnpj(),
            empresa.getEmail(),
            empresa.getTelefone()
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

    
}

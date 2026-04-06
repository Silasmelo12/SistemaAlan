package com.alan.sistema.service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

import com.alan.sistema.client.AsaasClient;
import com.alan.sistema.dto.AsaasCobrancaCreateRequestDTO;
import com.alan.sistema.dto.AsaasCobrancaCreateResponseDTO;
import com.alan.sistema.dto.AsaasCustomerCreateRequestDTO;
import com.alan.sistema.dto.AsaasCustomerCreateResponseDTO;
import com.alan.sistema.dto.AsaasCustomerListResponseDTO;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AsaasService {
    
    private final AsaasClient asaasClient;
    private final String token;

    /* public AsaasService(AsaasClient asaasClient, @Value("${asaas.token}") String token) {
        this.asaasClient = asaasClient;
        this.token = token;
    } */

        public AsaasService(AsaasClient asaasClient) {
            this.asaasClient = asaasClient;
            this.token = "$aact_hmlg_000MzkwODA2MWY2OGM3MWRlMDU2NWM3MzJlNzZmNGZhZGY6OjcwNDhlZDRlLWUwOWQtNDdhZS05YmQ4LWUyNGU4ODQyN2M3Yjo6JGFhY2hfNmIyYTA5ZmItYzhmNC00MzgxLTlkNjMtNmM1OWFmZDY1OTBm";
        }

    public AsaasCustomerCreateResponseDTO criarCliente(AsaasCustomerCreateRequestDTO asaasCustomerCreateRequestDTO) {
        return asaasClient.criarCliente(token, asaasCustomerCreateRequestDTO);
    }

    public AsaasCobrancaCreateResponseDTO criarCobranca(AsaasCobrancaCreateRequestDTO asaasCobrancaCreateRequestDTO) {
        return asaasClient.criarCobranca(token, asaasCobrancaCreateRequestDTO);
    }

    //Baixar boleto pdf
    public byte[] baixarBoletoPdf(String urlBoleto) {
        try (InputStream in = URI.create(urlBoleto).toURL().openStream()) {
            return in.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException("Erro ao baixar PDF do boleto", e);
        }
        //return asaasClient.baixarBoletoPdf(token, id);
    }

    public void deletarTodosClientes() {
        log.warn("Iniciando processo de exclusão de todos os clientes no Asaas...");
        
        // 1. Busca a lista de clientes (ajuste o limite conforme necessário)
        AsaasCustomerListResponseDTO lista = asaasClient.listarClientes(token,100);
    
        if (lista != null && lista.getData() != null) {
            lista.getData().forEach(cliente -> {
                try {
                    asaasClient.deletarCliente(token, cliente.getId());
                    log.info("Cliente deletado no Asaas: {}", cliente.getId());
                } catch (Exception e) {
                    log.error("Erro ao deletar cliente {}: {}", cliente.getId(), e.getMessage());
                }
            });
        }
        
        log.info("Processo de limpeza concluído.");
    }

}

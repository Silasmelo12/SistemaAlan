package com.alan.sistema.service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alan.sistema.client.AsaasClient;
import com.alan.sistema.dto.integration.asaas.AsaasCobrancaCreateRequestDTO;
import com.alan.sistema.dto.integration.asaas.AsaasCobrancaCreateResponseDTO;
import com.alan.sistema.dto.integration.asaas.AsaasCustomerCreateRequestDTO;
import com.alan.sistema.dto.integration.asaas.AsaasCustomerCreateResponseDTO;
import com.alan.sistema.dto.integration.asaas.AsaasCustomerListResponseDTO;

@Service
public class AsaasService {

    private final AsaasClient asaasClient;
    private final String token;
    private static final Logger log = LoggerFactory.getLogger(AsaasService.class);

    public AsaasService(AsaasClient asaasClient, @Value("${asaas.token:TOKEN_ASAAS_NAO_CONFIGURADO}") String token) {
        this.asaasClient = asaasClient;
        this.token = token;
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
        AsaasCustomerListResponseDTO lista = asaasClient.listarClientes(token, 100);

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

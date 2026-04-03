package com.alan.sistema.service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import org.springframework.stereotype.Service;

import com.alan.sistema.client.AsaasClient;
import com.alan.sistema.dto.AsaasCobrancaCreateRequestDTO;
import com.alan.sistema.dto.AsaasCobrancaCreateResponseDTO;
import com.alan.sistema.dto.AsaasCustomerCreateRequestDTO;
import com.alan.sistema.dto.AsaasCustomerCreateResponseDTO;

@Service
public class AsaasService {
    
    private final AsaasClient asaasClient;
    private final String token;

    /* public AsaasService(AsaasClient asaasClient, @Value("${asaas.token}") String token) {
        this.asaasClient = asaasClient;
        this.token = token;
    } */

        public AsaasService(AsaasClient asaasClient) {
            this.asaasClient = asaasClient;
            this.token = "$aact_hmlg_000MzkwODA2MWY2OGM3MWRlMDU2NWM3MzJlNzZmNGZhZGY6OjAyZTAzYTlhLTBlMDMtNDJjOS05OTZjLWY0OTliZjYyYmJhODo6JGFhY2hfYThiZjkwMjYtZjg3ZS00M2M4LWFhNmUtNDlkYzA2NDQ0MGM1";
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

}

package com.alan.sistema.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import com.alan.sistema.dto.AsaasCobrancaCreateRequestDTO;
import com.alan.sistema.dto.AsaasCobrancaCreateResponseDTO;
import com.alan.sistema.dto.AsaasCustomerCreateRequestDTO;
import com.alan.sistema.dto.AsaasCustomerCreateResponseDTO;

@FeignClient(name = "asaasClient", url = "https://api-sandbox.asaas.com/v3")
public interface  AsaasClient {

    @PostMapping(value = "/customers", consumes = "application/json")
    AsaasCustomerCreateResponseDTO criarCliente(
        @RequestHeader("access_token") String token, 
        @RequestBody AsaasCustomerCreateRequestDTO dados
    );

    @PostMapping(value = "/payments", consumes = "application/json")
    AsaasCobrancaCreateResponseDTO criarCobranca(
        @RequestHeader("access_token") String token,
        @RequestBody AsaasCobrancaCreateRequestDTO dados
    );
}

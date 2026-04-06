package com.alan.sistema.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import com.alan.sistema.dto.AsaasCobrancaCreateRequestDTO;
import com.alan.sistema.dto.AsaasCobrancaCreateResponseDTO;
import com.alan.sistema.dto.AsaasCustomerCreateRequestDTO;
import com.alan.sistema.dto.AsaasCustomerCreateResponseDTO;
import com.alan.sistema.dto.AsaasCustomerListResponseDTO;

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

    @GetMapping("/customers")
    AsaasCustomerListResponseDTO listarClientes(
        @RequestHeader("access_token") String token,
        @RequestParam("limit") int limit);

    @DeleteMapping("/customers/{id}")
    void deletarCliente(
        @RequestHeader("access_token") String token,
        @PathVariable("id") String id);
}

package com.alan.sistema.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import com.alan.sistema.dto.integration.zapsign.ZapSignDocumentRequestDTO;
import com.alan.sistema.dto.integration.zapsign.ZapSignDocumentResponseDTO;

@FeignClient(name = "ZapSignClient", url="https://sandbox.api.zapsign.com.br/api/v1/")
public interface  ZapSignClient {

    @PostMapping("/docs")
    ZapSignDocumentResponseDTO criarDocumento(
        @RequestHeader("Authorization") String token,
        @RequestBody ZapSignDocumentRequestDTO request
    );

}

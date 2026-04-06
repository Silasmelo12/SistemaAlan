package com.alan.sistema.webhooks;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alan.sistema.dto.ZapSignWebhookDTO;
import com.alan.sistema.service.EmpresaService;

@RestController
@RequestMapping("/webookZapSign")
public class WebhooksZapSignController {

    private final EmpresaService empresaService;

    public WebhooksZapSignController(EmpresaService empresaService) {
        this.empresaService = empresaService;
    }
    
    @PostMapping()
    public ResponseEntity<Void> postMethodName(@RequestBody ZapSignWebhookDTO entity) {
        //TODO: process POST request

        System.out.println("o documento foi assinado com sucesso: " + entity);
        empresaService.processarAssinaturaZapSign(entity.getToken());
        return ResponseEntity.ok().build();
    }
    
}

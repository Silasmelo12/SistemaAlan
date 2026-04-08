package com.alan.sistema.webhooks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alan.sistema.dto.integration.asaas.AsaasWebhookDTO;
import com.alan.sistema.service.EmpresaService;

@RestController
@RequestMapping("/webhooks/asaas")
public class AsaasWebhookController {

    private final EmpresaService empresaService;
    private static final Logger log = LoggerFactory.getLogger(EmpresaService.class);
    
    
    public AsaasWebhookController(EmpresaService empresaService){
        this.empresaService = empresaService;
    }

    public ResponseEntity<Void> handleAsaasWebhook(@RequestBody AsaasWebhookDTO dto){
            log.info("Webhook Asaas recebido1/ Evento={}, PaymentID{}",
                dto.getEvent(),dto.getPayment().getId()
            );

            empresaService.processarPagamentoAsaas(dto);

            return ResponseEntity.ok().build();
    }

}

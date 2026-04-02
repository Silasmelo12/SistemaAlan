package com.alan.sistema.controller;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alan.sistema.requests.EmpresaPostRequestBody;
import com.alan.sistema.service.EmpresaService;

@RestController
@RequestMapping("/empresas")
@EnableFeignClients(basePackages = "com.alan.sistema.client")
public class EmpresaController {

    private final EmpresaService empresaService;

    // O Spring injeta o Service aqui automaticamente
    public EmpresaController(EmpresaService empresaService) {
        this.empresaService = empresaService;
    }


    @GetMapping("/listar")
    public String listarEmpresas() {
        
        
        return "Olá Adam";
    }

    
    @PostMapping("/cadastrar")
    public ResponseEntity<String> cadastrarEmpresa(@RequestBody EmpresaPostRequestBody empresaPostRequestBody) {
        String asaasId = empresaService.processarAdesao(empresaPostRequestBody);
        return ResponseEntity.status(HttpStatus.CREATED)
                            .body("Empresa cadastrada e integrada! ID Asaas: " + asaasId);
    }

    @PostMapping("/adesao")
    public ResponseEntity<String> processarAdesao(@RequestBody EmpresaPostRequestBody empresaPostRequestBody) {
        String asaasId = empresaService.processarAdesao(empresaPostRequestBody);
        return ResponseEntity.status(HttpStatus.CREATED)
                            .body("Adesao da empresa. Id: " + asaasId);
    }

}


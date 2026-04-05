package com.alan.sistema.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alan.sistema.model.Empresa;
import com.alan.sistema.requests.EmpresaPostRequestBody;
import com.alan.sistema.service.EmpresaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/AlanSystem")
public class EmpresaController {

    private final EmpresaService empresaService;

    // O Spring injeta o Service aqui automaticamente
    public EmpresaController(EmpresaService empresaService) {
        this.empresaService = empresaService;
    }

    @GetMapping()
    public ResponseEntity<List<Empresa>> listarTudo() {
        List<Empresa> empresaPostRequestBody = empresaService.listarTudo();
        return ResponseEntity.ok(empresaPostRequestBody);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmpresaPostRequestBody> consultarProgresso(@PathVariable String id) {
        EmpresaPostRequestBody empresaPostRequestBody = empresaService.consultarProgresso(id);
        
        return ResponseEntity.ok(empresaPostRequestBody);
    }

    @PostMapping()
    public ResponseEntity<String> processarAdesao(@RequestBody @Valid EmpresaPostRequestBody empresaPostRequestBody) {
        String asaasId = empresaService.processarAdesao(empresaPostRequestBody);
        return ResponseEntity.status(HttpStatus.CREATED)
                            .body("Adesao da empresa. Id: " + asaasId);
    }

     // No EmpresaController.java
     @DeleteMapping("/limpar-asaas-hmlg")
     public ResponseEntity<Void> limparBaseHmlg() {
         empresaService.limparTudoGeral();
         return ResponseEntity.noContent().build();
     }
}


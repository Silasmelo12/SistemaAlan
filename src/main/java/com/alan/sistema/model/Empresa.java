package com.alan.sistema.model;

import java.time.Instant;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;  
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import com.alan.sistema.enumeration.EmpresaStatus;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Document
public class Empresa {

    @Id
    private String id; // O MongoDB usa String para o ObjectId
    
    @NotBlank(message = "O nome é obrigatório")
    private String name;
    private String cpfCnpj;
    @Email(message = "E-mail inválido")
    private String email;
    private String telefone;
    private EmpresaStatus status;
    
    @CreatedDate
    private Instant dataCriacao;

    @LastModifiedDate
    private Instant dataUltimaAtualizacao;

    // Objeto aninhado com dados do Asaas
    private AsaasData asaasData;

    // Objeto aninhado com dados do ZapSign
    private ZapSignData zapsignData;


    public Empresa() {
    }

    

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCpfCnpj() {
        return cpfCnpj;
    }

    public void setCpfCnpj(String cpfCnpj) {
        this.cpfCnpj = cpfCnpj;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public EmpresaStatus getStatus() {
        return status;
    }

    public void setStatus(EmpresaStatus status) {
        this.status = status;
    }

    public Instant getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(Instant dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public Instant getDataUltimaAtualizacao() {
        return dataUltimaAtualizacao;
    }

    public void setDataUltimaAtualizacao(Instant dataUltimaAtualizacao) {
        this.dataUltimaAtualizacao = dataUltimaAtualizacao;
    }

    public AsaasData getAsaasData() {
        return asaasData;
    }

    public void setAsaasData(AsaasData asaasData) {
        this.asaasData = asaasData;
    }

    public ZapSignData getZapsignData() {
        return zapsignData;
    }

    public void setZapsignData(ZapSignData zapsignData) {
        this.zapsignData = zapsignData;
    } 
}

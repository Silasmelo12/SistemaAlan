package com.alan.sistema.dto.requests;

import org.hibernate.validator.constraints.br.CNPJ;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class EmpresaRequestDTO {

    private String name;
    @CNPJ
    @NotBlank
    private String cpfCnpj;
    @Email
    @NotBlank
    private String email;

    private String telefone;
    private Integer quantidadeFuncionarios;

    public EmpresaRequestDTO(String name, String cpfCnpj, String email, String telefone) {
        this.name = name;
        this.cpfCnpj = cpfCnpj;
        this.email = email;
        this.telefone = telefone;

    }

    public EmpresaRequestDTO() {
    }

    public String getName() {
        return name;
    }

    public String getCpfCnpj() {
        return cpfCnpj;
    }

    public String getEmail() {
        return email;
    }


    public void setName(String name) {
        this.name = name;
    }

    public void setCpfCnpj(String cpfCnpj) {
        this.cpfCnpj = cpfCnpj;
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

    public Integer getQuantidadeFuncionarios() {
        return quantidadeFuncionarios;
    }

    public void setQuantidadeFuncionarios(Integer quantidadeFuncionarios) {
        this.quantidadeFuncionarios = quantidadeFuncionarios;
    }
}




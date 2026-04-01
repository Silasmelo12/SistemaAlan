package com.alan.sistema.dto;

public class AsaasCustomerCreateRequestDTO {

    private String name; // Ex: "cus_000005049321"
    private String cpfCnpj;
    private String email;

    // Getters e Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCpfCnpj() { return cpfCnpj; }
    public void setCpfCnpj(String cpfCnpj) { this.cpfCnpj = cpfCnpj; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public AsaasCustomerCreateRequestDTO(String name, String cpfCnpj, String email) {
        this.name = name;
        this.cpfCnpj = cpfCnpj;
        this.email = email;
    }
    public AsaasCustomerCreateRequestDTO() {
    }
}

package com.alan.sistema.dto.response;

public class EmpresaResponseDTO {
    private String id;
    private String name;
    private String status;
    private String signUrl;    // Link do ZapSign
    private String invoiceUrl; // Link do Boleto/Fatura Asaas
    private String cpfCnpj;

    // Construtor completo
    public EmpresaResponseDTO(String id, String name, String status, String signUrl, String invoiceUrl, String cpfCnpj) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.signUrl = signUrl;
        this.invoiceUrl = invoiceUrl;
        this.cpfCnpj = cpfCnpj;
    }

    // Getters (Obrigatórios para o Spring converter para JSON)
    public String getId() { return id; }
    public String getName() { return name; }
    public String getStatus() { return status; }
    public String getSignUrl() { return signUrl; }
    public String getInvoiceUrl() { return invoiceUrl; }
    public String getCpfCnpj() { return cpfCnpj; }
}

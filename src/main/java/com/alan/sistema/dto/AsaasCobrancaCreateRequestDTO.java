package com.alan.sistema.dto;

import com.alan.sistema.enumeration.BillingType;

public class AsaasCobrancaCreateRequestDTO {

    private String customer; // Identificador único do cliente no Asaas
    private BillingType billingType ; // Forma de pagamento
    private Double value; // Valor da cobrança
    private String dueDate; // Data de vencimento da cobrança (formato: yyyy-MM-dd)

    public AsaasCobrancaCreateRequestDTO() {}

    public AsaasCobrancaCreateRequestDTO(String customer, BillingType billingType, Double value, String dueDate) {
        this.customer = customer;
        this.billingType = billingType;
        this.value = value;
        this.dueDate = dueDate;
    }

    public String getCustomer() {
        return customer;
    }
    public void setCustomer(String customer) {
        this.customer = customer;
    }
    public BillingType getBillingType() {
        return billingType;
    }
    public void setBillingType(BillingType billingType) {
        this.billingType = billingType;
    }
    public Double getValue() {
        return value;
    }
    public void setValue(Double value) {
        this.value = value;
    }
    public String getDueDate() {
        return dueDate;
    }
    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }
    


}

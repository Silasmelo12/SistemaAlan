package com.alan.sistema.model;

public class AsaasData {

    private String customerId;   // cus_xxxx
    private String ultimoPaymentId;    // pay_xxxx (ID do boleto atual)
    private String billingType;  // BOLETO, PIX ou CREDIT_CARD
    private String invoiceUrl;   // Link para o cliente pagar
    private String nfeId;

    public String getCustomerId() {
        return customerId;
    }
    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }
    public String getPaymentId() {
        return ultimoPaymentId;
    }
    public void setPaymentId(String paymentId) {
        this.ultimoPaymentId = paymentId;
    }
    public String getBillingType() {
        return billingType;
    }
    public void setBillingType(String billingType) {
        this.billingType = billingType;
    }
    public String getInvoiceUrl() {
        return invoiceUrl;
    }
    public void setInvoiceUrl(String invoiceUrl) {
        this.invoiceUrl = invoiceUrl;
    }
    public AsaasData(String customerId, String paymentId, String billingType, String invoiceUrl, String nfeId) {
        this.customerId = customerId;
        this.ultimoPaymentId = paymentId;
        this.billingType = billingType;
        this.invoiceUrl = invoiceUrl;
        this.nfeId = nfeId;
    }
    public AsaasData() {
    }

    public String getUltimoPaymentId() {
        return ultimoPaymentId;
    }

    public void setUltimoPaymentId(String ultimoPaymentId) {
        this.ultimoPaymentId = ultimoPaymentId;
    }

    public String getNfeId() {
        return nfeId;
    }

    public void setNfeId(String nfeId) {
        this.nfeId = nfeId;
    }
}

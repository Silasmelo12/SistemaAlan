package com.alan.sistema.model;

public class ZapSignData {

    private String docToken;      // Token do contrato
    private String externalId;    // ID de controle
    private String signUrl;       // Link para o cliente assinar
    private boolean assinado = false;

    public ZapSignData() {
    }

    public String getDocToken() {
        return docToken;
    }

    public void setDocToken(String docToken) {
        this.docToken = docToken;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public String getSignUrl() {
        return signUrl;
    }

    public void setSignUrl(String signUrl) {
        this.signUrl = signUrl;
    }

    public boolean isAssinado() {
        return assinado;
    }

    public void setAssinado(boolean assinado) {
        this.assinado = assinado;
    }
}

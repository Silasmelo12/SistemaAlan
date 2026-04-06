package com.alan.sistema.dto;

import java.util.List;

public class ZapSignDocumentRequestDTO {
    private String name; // Nome do documento (ex: "Contrato - Clínica Exemplo")
    private String url; // Se o PDF já estiver online
    private String base64_pdf; // OU o PDF em string base64 (mais comum)
    private List<ZapSignSignerDTO> signers;
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public String getBase64_pdf() {
        return base64_pdf;
    }
    public void setBase64_pdf(String base64_pdf) {
        this.base64_pdf = base64_pdf;
    }
    public List<ZapSignSignerDTO> getSigners() {
        return signers;
    }
    public void setSigners(List<ZapSignSignerDTO> signers) {
        this.signers = signers;
    }

    

}

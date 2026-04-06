package com.alan.sistema.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ZapSignDocumentResponseDTO {

    private String token; // Este é o ID do documento no ZapSign
    private String name;
    private String status; // ex: "pending"
    private List<SignerResponse> signers;

    // Getters e Setters
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<SignerResponse> getSigners() {
        return signers;
    }

    public void setSigners(List<SignerResponse> signers) {
        this.signers = signers;
    }

    // Classe interna para os Signatários (onde fica o link da assinatura)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SignerResponse {
        private String token;
        private String name;
        private String email;
        private String sign_url; // ESTE É O LINK QUE O CLIENTE CLICA PARA ASSINAR

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getSign_url() {
            return sign_url;
        }

        public void setSign_url(String sign_url) {
            this.sign_url = sign_url;
        }
    }
}
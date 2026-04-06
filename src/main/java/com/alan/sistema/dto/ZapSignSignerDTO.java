package com.alan.sistema.dto;

public class ZapSignSignerDTO {
    private String name;
    private String email;
    private String auth_mode = "SIGN_ENVELOPE"; // Padrão de assinatura por e-mail
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
    public String getAuth_mode() {
        return auth_mode;
    }
    public void setAuth_mode(String auth_mode) {
        this.auth_mode = auth_mode;
    }

    

}

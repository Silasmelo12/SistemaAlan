package com.alan.sistema.dto.integration.zapsign;

public class ZapSignWebhookDTO {
    private String token;
    private String event_type;

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public String getEvent_type() {
        return event_type;
    }
    public void setEvent_type(String event_type) {
        this.event_type = event_type;
    }
    
}
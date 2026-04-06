package com.alan.sistema.dto;

public class ZapSignWebhookDTO {
    private String token;
    private String event;

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public String getEvent() { return event; }
    public void setEvent(String event) { this.event = event; }
}
package com.legalswami.dto;

public class GoogleLoginRequest {
    private String name;
    private String email;
    private String idToken; // optional if you verify token

    public GoogleLoginRequest() {}
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getIdToken() { return idToken; }
    public void setIdToken(String idToken) { this.idToken = idToken; }
}

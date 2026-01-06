package com.bazinga.bazingabe.dto;

public class AuthResponse {
    private String token;
    private Long userId;
    private String username;
    private String email;
    private String role;
    private String subscriptionType;
    private String subscriptionExpiration;

    public AuthResponse(String token, Long userId, String username, String email, String role, String subscriptionType,
            String subscriptionExpiration) {
        this.token = token;
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.role = role;
        this.subscriptionType = subscriptionType;
        this.subscriptionExpiration = subscriptionExpiration;
    }

    public String getToken() {
        return token;
    }

    public Long getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    public String getSubscriptionType() {
        return subscriptionType;
    }

    public String getSubscriptionExpiration() {
        return subscriptionExpiration;
    }
}

package com.bazinga.bazingabe.dto;

public class SubscriptionResponse {
    private String subscriptionType;
    private String subscriptionExpiration;

    public SubscriptionResponse(String subscriptionType, String subscriptionExpiration) {
        this.subscriptionType = subscriptionType;
        this.subscriptionExpiration = subscriptionExpiration;
    }

    public String getSubscriptionType() {
        return subscriptionType;
    }

    public String getSubscriptionExpiration() {
        return subscriptionExpiration;
    }
}

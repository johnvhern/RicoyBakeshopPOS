package com.pos.ricoybakeshop;

public class SupabaseSession {
    private String accessToken;
    private String userId;

    public SupabaseSession(String accessToken, String userId) {
        this.accessToken = accessToken;
        this.userId = userId;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getUserId() {
        return userId;
    }
}

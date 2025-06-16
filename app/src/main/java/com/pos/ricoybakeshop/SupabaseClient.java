package com.pos.ricoybakeshop;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

public class SupabaseClient {
    private static final String SUPABASE_URL = "https://ewvaowsmayxgkvyatavb.supabase.co";
    private static final String API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImV3dmFvd3NtYXl4Z2t2eWF0YXZiIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDk5ODA2NjEsImV4cCI6MjA2NTU1NjY2MX0.lHURuXCaDh8VSMzx4Tf92BBATFvrVjDQ2FtxmkYDi1w";
    private static final String AUTH_URL = SUPABASE_URL + "/auth/v1/token?grant_type=password";
    private static final String USERS_TABLE_URL = SUPABASE_URL + "/rest/v1/users";

    private final OkHttpClient client;

    public SupabaseClient() {
        client = new OkHttpClient();
    }

    // Login with email and password
    public SupabaseSession loginWithEmail(String email, String password) throws Exception {
        JSONObject body = new JSONObject();
        body.put("email", email);
        body.put("password", password);

        Request request = new Request.Builder()
                .url(AUTH_URL)
                .post(RequestBody.create(body.toString(), MediaType.get("application/json")))
                .addHeader("apikey", API_KEY)
                .addHeader("Content-Type", "application/json")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) return null;

            String responseBody = response.body().string();
            JSONObject json = new JSONObject(responseBody);

            String userId = json.getJSONObject("user").getString("id");
            String token = json.getString("access_token");

            return new SupabaseSession(token, userId);
        }
    }

    // Fetch user data using username (to get their email, role)
    public JSONObject fetchUserProfileByUsername(String username) throws Exception {
        HttpUrl url = HttpUrl.parse(USERS_TABLE_URL)
                .newBuilder()
                .addQueryParameter("username", "eq." + username)
                .addQueryParameter("select", "*")
                .build();

        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("apikey", API_KEY)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .addHeader("Accept", "application/json")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) return null;

            String responseBody = response.body().string();
            JSONArray arr = new JSONArray(responseBody);

            if (arr.length() > 0) {
                return arr.getJSONObject(0); // return first matching user
            }
        }
        return null;
    }

    // Optional: fetch profile by email
    public JSONObject fetchUserProfileByEmail(String email) throws Exception {
        HttpUrl url = HttpUrl.parse(USERS_TABLE_URL)
                .newBuilder()
                .addQueryParameter("email", "eq." + email)
                .addQueryParameter("select", "*")
                .build();

        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("apikey", API_KEY)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .addHeader("Accept", "application/json")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) return null;

            String responseBody = response.body().string();
            JSONArray arr = new JSONArray(responseBody);

            if (arr.length() > 0) {
                return arr.getJSONObject(0);
            }
        }
        return null;
    }
}


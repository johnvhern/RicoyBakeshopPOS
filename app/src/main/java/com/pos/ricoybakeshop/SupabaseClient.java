package com.pos.ricoybakeshop;

import android.os.Build;

import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

public class SupabaseClient {
    private static final String SUPABASE_URL = BuildConfig.SUPABASE_URL;
    private static final String API_KEY = BuildConfig.SUPABASE_ANON_KEY;
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
}


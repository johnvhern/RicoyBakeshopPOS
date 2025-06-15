package com.pos.ricoybakeshop;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SupabaseClient {
    private static final String SUPABASE_URL = "https://ewvaowsmayxgkvyatavb.supabase.co";
    private static final String API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImV3dmFvd3NtYXl4Z2t2eWF0YXZiIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDk5ODA2NjEsImV4cCI6MjA2NTU1NjY2MX0.lHURuXCaDh8VSMzx4Tf92BBATFvrVjDQ2FtxmkYDi1w";

    private final OkHttpClient client = new OkHttpClient();

    public JSONObject fetchUser(String username, String branch) throws IOException, JSONException {
        HttpUrl url = HttpUrl.parse(SUPABASE_URL + "/rest/v1/users")
                .newBuilder()
                .addQueryParameter("username", "eq." + username)
                .addQueryParameter("branch", "eq." + branch)
                .addQueryParameter("select", "*")
                .build();

        Request request = new Request.Builder()
                .url(url)
                .addHeader("apikey", API_KEY)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .addHeader("Accept", "application/json")
                .build();

        Response response = client.newCall(request).execute();
        String body = response.body().string();

        JSONArray arr = new JSONArray(body);
        return arr.length() > 0 ? arr.getJSONObject(0) : null;
    }

    public AuthResponse login(String email, String password) throws IOException, JSONException {
        JSONObject body = new JSONObject();
        body.put("email", email);
        body.put("password", password);

        RequestBody requestBody = RequestBody.create(
                body.toString(),
                MediaType.get("application/json")
        );

        Request request = new Request.Builder()
                .url(SUPABASE_URL + "/auth/v1/token?grant_type=password")
                .post(requestBody)
                .addHeader("apikey", API_KEY)
                .addHeader("Content-Type", "application/json")
                .build();

        Response response = client.newCall(request).execute();
        String responseBody = response.body().string();

        if (response.isSuccessful()) {
            JSONObject json = new JSONObject(responseBody);
            return new AuthResponse(json.getString("access_token"), json.getString("refresh_token"));
        } else {
            return null;
        }
    }

    public class AuthResponse {
        public String accessToken;
        public String refreshToken;

        public AuthResponse(String accessToken, String refreshToken) {
            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
        }
    }

}


package com.pos.ricoybakeshop;

import androidx.annotation.NonNull;
import androidx.room.Entity;

@Entity(tableName = "user_session", primaryKeys = {"username", "branch"})
public class LocalUserSession {
    @NonNull public String username;
    @NonNull
    public String branch;
    public String hashedPassword;
    public String role;
    public String userId;
    public String accessToken;
    public String refreshToken;
}

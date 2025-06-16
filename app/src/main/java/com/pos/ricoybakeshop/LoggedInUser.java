package com.pos.ricoybakeshop;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "logged_in_user")
public class LoggedInUser {
    @PrimaryKey
    @NonNull
    public String username;

    public String userId;
    public String role;
    public String branch;
    public String password; // this will store the HASHED password
}


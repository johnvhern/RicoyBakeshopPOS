package com.pos.ricoybakeshop;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface LoggedInUserDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertUser(LoggedInUser user);

    @Query("SELECT * FROM logged_in_user WHERE username = :username LIMIT 1")
    LoggedInUser getUser(String username);

    @Query("DELETE FROM logged_in_user")
    void clearUser();
}

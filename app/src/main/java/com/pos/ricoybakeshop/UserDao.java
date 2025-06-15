package com.pos.ricoybakeshop;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface UserDao {
    @Query("SELECT * FROM user_session WHERE username = :username AND branch = :branch LIMIT 1")
    LocalUserSession getUser(String username, String branch);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(LocalUserSession session);
}

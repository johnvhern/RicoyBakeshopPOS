package com.pos.ricoybakeshop;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {LoggedInUser.class, ProductCategory.class, Product.class}, version = 5)
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase INSTANCE;

    public abstract LoggedInUserDAO loggedInUserDao();
    public abstract ProductDao productDao();
    public abstract CategoryDao categoryDao();

    public static synchronized AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "pos-db")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return INSTANCE;
    }
}



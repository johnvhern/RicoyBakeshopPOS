package com.pos.ricoybakeshop;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "categories")
public class ProductCategory {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @NonNull
    public String name;
}

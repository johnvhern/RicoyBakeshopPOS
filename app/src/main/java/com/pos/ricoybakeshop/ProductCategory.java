package com.pos.ricoybakeshop;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "categories")
public class ProductCategory {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @NonNull
    public String name;

    @NonNull
    public String iconName;

    public int productCount;

    public ProductCategory(@NonNull String name, @NonNull String iconName, int productCount) {
        this.name = name;
        this.iconName = iconName;
        this.productCount = productCount;
    }

    @Ignore
    public ProductCategory(@NonNull String name, @NonNull String iconName) {
        this.name = name;
        this.iconName = iconName;
    }
}

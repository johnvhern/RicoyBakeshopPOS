package com.pos.ricoybakeshop;

import static androidx.room.ForeignKey.CASCADE;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "products",
foreignKeys = @ForeignKey(entity = ProductCategory.class, parentColumns = "id", childColumns = "categoryId", onDelete = CASCADE),
indices = {@Index(value = "categoryId")})
public class Product {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @NonNull
    public String name;

    public double price;
    public int categoryId;
    public int quantity;
    public String imageUrl;
}

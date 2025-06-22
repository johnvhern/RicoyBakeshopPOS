package com.pos.ricoybakeshop;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface CategoryDao {
    @Insert void insert(ProductCategory category);
    @Query("SELECT * FROM categories")
    List<ProductCategory> getAll();
    @Delete
    void delete(ProductCategory category);
    @Query("SELECT name FROM categories WHERE id = :categoryId LIMIT 1")
    String getCategoryNameById(int categoryId);
}

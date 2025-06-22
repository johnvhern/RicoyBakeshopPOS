package com.pos.ricoybakeshop;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ProductDao {
    @Insert void insert(Product product);
    @Update void update(Product product);
    @Delete void delete(Product product);
    @Query("SELECT * FROM products")
    List<Product> getAllProducts();

    @Query("SELECT categoryId, COUNT(*) as count FROM products GROUP BY categoryId")
    List<CategoryCount> getProductCount();

    class CategoryCount{
        public int categoryId;
        public int count;
    }
}

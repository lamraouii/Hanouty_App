package com.example.hanout_app.database.Dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.hanout_app.database.Data.ProductData;

import java.util.List;

@Dao
public interface ProductDao {

    @Insert
    void addProduct(ProductData...product);

    @Delete
    void deleteProduct(ProductData...product);

    @Update
    void modifyProduct(ProductData product);

    @Query("select * from products where barcode = :barcode")
    ProductData getProductByBarcode(String barcode);

    @Query("select * from products")
    List<ProductData> getAllProducts();





}

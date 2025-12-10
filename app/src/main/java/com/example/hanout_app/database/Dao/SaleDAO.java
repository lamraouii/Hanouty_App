package com.example.hanout_app.database.Dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.hanout_app.database.Data.SaleData;
import com.example.hanout_app.model.Sale;

import java.util.List;

@Dao
public interface SaleDAO {

    @Insert
    void addSale(SaleData...sale);

    @Delete
    void deleteSale(SaleData...sale);

    @Update
    void modifySale(SaleData...sale);

    @Query("select * from sales_products where id_Sale= :id_sale")
    SaleData getSaleById(int id_sale);

    @Query("select * from Sales_products where facture_id = :id_facture")
    List<SaleData> getSalesByFactureId(int id_facture);

    @Query("select * from Sales_products")
    List<SaleData> getAllSales();

}

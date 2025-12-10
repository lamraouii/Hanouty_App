package com.example.hanout_app.database.Dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.hanout_app.database.Data.FactureData;

import java.util.List;

@Dao
public interface FactureDAO {
    @Insert
    void addFacture(FactureData...facture);

    @Delete
    void deleteFacture(FactureData...facture);
    @Update
    void modifyFacture(FactureData...facture);

    @Query("select * from factures where id_facture = :id_facture")
    FactureData getFactureById(int id_facture);

    @Query("select * from factures where userId = :id_user")
    List<FactureData> getAllFactureByUserId(int id_user);


}

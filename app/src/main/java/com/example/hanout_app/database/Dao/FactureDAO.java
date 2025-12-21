package com.example.hanout_app.database.Dao;

import androidx.lifecycle.LiveData;
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
    long addFacture(FactureData facture);

    @Delete
    void deleteFacture(FactureData... facture);

    @Update
    void modifyFacture(FactureData... facture);

    @Query("select * from factures where id_facture = :id_facture")
    LiveData<FactureData> getFactureById(int id_facture);

    @Query("select * from factures where userId = :id_user")
    LiveData<List<FactureData>> getAllFactureByUserId(int id_user);

    @Query("SELECT SUM(montant_facture) FROM factures WHERE date_facture >= :startTime AND date_facture <= :endTime AND userId = :userId")
    LiveData<Double> getDailySalesSum(long startTime, long endTime, int userId);

    @Query("SELECT * FROM factures WHERE userId = :id_user ORDER BY date_facture DESC LIMIT :limit")
    LiveData<List<FactureData>> getLastNFacturesByUserId(int id_user, int limit);
}

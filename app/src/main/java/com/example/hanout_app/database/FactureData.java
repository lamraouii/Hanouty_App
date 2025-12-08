package com.example.hanout_app.database;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.hanout_app.model.Sale;
import com.example.hanout_app.model.User;
import com.example.hanout_app.utils.helpers.DateConvert;

import java.util.Date;
import java.util.List;

@TypeConverters({DateConvert.class})
@Entity(tableName = "facture",
        foreignKeys = {
            @ForeignKey(
                    entity = User.class,
                    parentColumns = {"id_User"},
                    childColumns = {"userId"},
                    onDelete = ForeignKey.CASCADE
            )
        })
public class FactureData {
    @PrimaryKey(autoGenerate = true)
    private int id_facture;
    private Date date_facture;
    private double montant_facture;
    @NonNull
    private int userId;




    public FactureData() {
    }


    public int getId_facture() {
        return id_facture;
    }

    public void setId_facture(int id_facture) {
        this.id_facture = id_facture;
    }

    public Date getDate_facture() {
        return date_facture;
    }

    public void setDate_facture(Date date_facture) {
        this.date_facture = date_facture;
    }

    public double getMontant_facture() {
        return montant_facture;
    }

    public void setMontant_facture(double montant_facture) {
        this.montant_facture = montant_facture;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}

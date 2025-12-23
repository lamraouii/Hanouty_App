package com.example.hanout_app.database.Data;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.hanout_app.model.Facture;
import com.example.hanout_app.model.Product;
import com.example.hanout_app.utils.helpers.DateConvert;
import java.util.Date;

@TypeConverters({DateConvert.class})
@Entity(tableName = "Sales_products",
    foreignKeys = {
        @ForeignKey(
                entity = ProductData.class,
                parentColumns = {"id_Product"},
                childColumns = {"product_id"},
                onDelete = ForeignKey.CASCADE
        ),
        @ForeignKey(
                entity = FactureData.class,
                parentColumns = {"id_facture"},
                childColumns = {"facture_id"},
                onDelete = ForeignKey.CASCADE
        )
    }
)
public class SaleData {

    @PrimaryKey(autoGenerate = true)
    private int id_Sale;

    @NonNull
    private int product_id;

    @NonNull
    private int facture_id;

    private int quantity_sold;
    private Date date_sale;
    private double total_price;

    public double getTotal_price() {
        return total_price;
    }

    public void setTotal_price(double total_price) {
        this.total_price = total_price;
    }

    public Date getDate_sale() {
        return date_sale;
    }

    public void setDate_sale(Date date_sale) {
        this.date_sale = date_sale;
    }

    public int getQuantity_sold() {
        return quantity_sold;
    }

    public void setQuantity_sold(int quantity_sold) {
        this.quantity_sold = quantity_sold;
    }

    public int getProduct_id() {
        return product_id;
    }

    public void setProduct_id(int product_id) {
        this.product_id = product_id;
    }

    public int getFacture_id() {
        return facture_id;
    }

    public void setFacture_id(int facture_id) {
        this.facture_id = facture_id;
    }

    public int getId_Sale() {
        return id_Sale;
    }

    public void setId_Sale(int id_Sale) {
        this.id_Sale = id_Sale;
    }
}

package com.example.hanout_app.database.Data;


import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "products")
public class ProductData {
    @NonNull
    @PrimaryKey(autoGenerate = true)
    private int id_Product;
    private String name;
    private int quantity;
    private double price;
    private String imageUrl;
    private String barcode;


    @Ignore
    public ProductData(int id_Product, String name, int quantity, double price, String imageUrl, String barcode) {
        this.id_Product = id_Product;
        this.name = name;
        this.quantity = quantity;
        this.price = price;
        this.imageUrl = imageUrl;
        this.barcode = barcode;
    }

    public ProductData(String name, int quantity, double price) {
        this.name = name;
        this.quantity = quantity;
        this.price = price;
    }

    public int getId_Product() {
        return id_Product;
    }

    public void setId_Product(int id_Product) {
        this.id_Product = id_Product;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }
}

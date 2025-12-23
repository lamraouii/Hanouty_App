package com.example.hanout_app.model;

public class Product {
    private int id_Product;
    private String name;
    private int quantity;
    private double price;
    private String imageUrl;
    private String barcode;

    // public Product(String name, int quantity, double price, String imageUrl,
    // String barcode) {
    //// this.id_Product = id_Product;
    // this.name = name;
    // this.quantity = quantity;
    // this.price = price;
    // this.imageUrl = imageUrl;
    // this.barcode = barcode;
    // }

    private int imageResId;

    public Product(String name, int quantity, double price, int imageResId, String barcode) {
        this.name = name;
        this.quantity = quantity;
        this.price = price;
        this.imageResId = imageResId;
        this.barcode = barcode;
    }

    public int getImageResId() {
        return imageResId;
    }

    public void setImageResId(int imageResId) {
        this.imageResId = imageResId;
    }

    public int getId_Product() {
        return id_Product;
    }

    public void setId_Product(int id_Product) {
        this.id_Product = id_Product;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
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

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    // isInStock(quantityRequested) madrthach hna

}

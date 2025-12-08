package com.example.hanout_app.model;

import java.util.Date;

public class Sale {
    private int id_Sale;
//    private int id_product_solde;
    private Product product;
    private int quantity_sold;
    private Date  date_sale;
    private double total_price;

    public int getId_Sale() {
        return id_Sale;
    }

    public void setId_Sale(int id_Sale) {
        this.id_Sale = id_Sale;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }


    public Date getDate_sale() {
        return date_sale;
    }

    public void setDate_sale(Date date_sale) {
        this.date_sale = date_sale;
    }

    public double getTotal_price() {
        return this.product.getPrice()*this.quantity_sold;
    }

    public void setTotal_price(double total_price) {
        this.total_price = total_price;
    }

    public int getQuantity_sold() {
        return quantity_sold;
    }

    public void setQuantity_sold(int quantity_sold) {
        this.quantity_sold = quantity_sold;
    }


}

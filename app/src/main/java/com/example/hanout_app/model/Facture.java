package com.example.hanout_app.model;

import java.util.Date;
import java.util.List;

public class Facture {

    private int id_facture;
    private Date date_facture;
    private double montant_facture;
    private int userId;

    private List<Sale> sales;

//    public Facture(Date date_facture, int montant_facture, int userId){
    //////        this.id_facture = id_facture;
    ////        this.date_facture = date_facture;
    ////        this.montant_facture = montant_facture;
    ////        this.userId = userId;
    ////
    ////    }

    public Date getDate_facture() {
        return date_facture;
    }

    public void setDate_facture(Date date_facture) {
        this.date_facture = date_facture;
    }

    public int getId_facture() {
        return id_facture;
    }

    public void setId_facture(int id_facture) {
        this.id_facture = id_facture;
    }

    public double getMontant_facture() {
        return montant_facture;
    }

    public void setMontant_facture(int montant_facture) {
        this.montant_facture = montant_facture;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public List<Sale> getSales(){
        return sales;
    }
    public void setSales(List<Sale> sales){
        this.sales = sales;
    }
    public void addSale(Sale sale){
        sales.add(sale);
        computeTotal();
    }

    public void removeSale(Sale sale){
        sales.remove(sale);
        computeTotal();

    }

    public void computeTotal() {
        double total = 0;
        for (Sale sale : sales) {
            total += sale.getTotal_price();
        }
        montant_facture=total;
    }




}

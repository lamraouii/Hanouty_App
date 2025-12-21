package com.example.hanout_app.database.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.hanout_app.database.Dao.FactureDAO;
import com.example.hanout_app.database.Dao.ProductDAO;
import com.example.hanout_app.database.Dao.SaleDAO;
import com.example.hanout_app.database.Dao.UserDAO;
import com.example.hanout_app.database.Data.FactureData;
import com.example.hanout_app.database.Data.ProductData;
import com.example.hanout_app.database.Data.SaleData;
import com.example.hanout_app.database.Data.UserData;
import com.example.hanout_app.database.HanoutDatabase;

import java.util.List;
import java.util.concurrent.Future;

public class Repository {

    UserDAO userDAO;
    ProductDAO productDAO;
    SaleDAO saleDAO;
    FactureDAO factureDAO;

    public Repository(Application application) {
        HanoutDatabase myHanoutDatabase = HanoutDatabase.getDatabase(application);
        userDAO = myHanoutDatabase.userDAO();
        productDAO = myHanoutDatabase.productDAO();
        saleDAO = myHanoutDatabase.saleDAO();
        factureDAO = myHanoutDatabase.factureDAO();
    }

    public void addUser(UserData... user) {
        HanoutDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                userDAO.addUser(user);
            }
        });
    }

    public void modifyUser(UserData... user) {
        HanoutDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                userDAO.modifyUser(user);
            }
        });
    }

    public void deleteUser(UserData... user) {
        HanoutDatabase.databaseWriteExecutor.execute(() -> userDAO.deleteUser(user));
    }

    public LiveData<UserData> getUserById(int id_user) {
        return userDAO.getUserById(id_user);
    }

    // is this a new fncts : shouldnt be all the fnctns here already in dao???
    // public Future<UserData> getUserByIdi(int id_user) {
    // return HanoutDatabase.databaseWriteExecutor.submit(() ->
    // userDAO.getUserById(id_user));
    // }

    public LiveData<UserData> getUserByPhone(String phone) {
        return userDAO.getUserByPhone(phone);

    }

    /// product fcts:
    public void addProduct(ProductData... product) {
        HanoutDatabase.databaseWriteExecutor.execute(() -> productDAO.addProduct(product));

    }

    public void deleteProduct(ProductData... product) {
        HanoutDatabase.databaseWriteExecutor.execute(() -> productDAO.deleteProduct(product));

    }

    public void modifyProduct(ProductData product) {
        HanoutDatabase.databaseWriteExecutor.execute(() -> productDAO.modifyProduct(product));

    }

    public LiveData<ProductData> getProductByBarcode(String barcode) {
        return productDAO.getProductByBarcode(barcode);

    }

    public LiveData<List<ProductData>> getAllProducts() {
        return productDAO.getAllProducts();

    }

    // Sales fcts:

    public void addSale(SaleData... sale) {
        HanoutDatabase.databaseWriteExecutor.execute(() -> saleDAO.addSale(sale));

    }

    public void deleteSale(SaleData... sale) {
        HanoutDatabase.databaseWriteExecutor.execute(() -> saleDAO.deleteSale(sale));

    }

    public void modifySale(SaleData... sale) {
        HanoutDatabase.databaseWriteExecutor.execute(() -> saleDAO.modifySale(sale));

    }

    public SaleData getSaleById(int id_sale) {
        return saleDAO.getSaleById(id_sale);
    }

    public LiveData<List<SaleData>> getSalesByFactureId(int id_facture) {
        return saleDAO.getSalesByFactureId(id_facture);
    }

    public LiveData<List<SaleData>> getAllSales() {
        return saleDAO.getAllSales();
    }

    // facture fcts :

    public void addFacture(FactureData facture) {
        HanoutDatabase.databaseWriteExecutor.execute(() -> factureDAO.addFacture(facture));

    }

    public void deleteFacture(FactureData... facture) {
        HanoutDatabase.databaseWriteExecutor.execute(() -> factureDAO.deleteFacture(facture));

    }

    public void modifyFacture(FactureData... facture) {
        HanoutDatabase.databaseWriteExecutor.execute(() -> factureDAO.modifyFacture(facture));

    }

    public LiveData<FactureData> getFactureById(int id_facture) {
        return factureDAO.getFactureById(id_facture);
    }

    public LiveData<List<FactureData>> getAllFactureByUserId(int id_user) {
        return factureDAO.getAllFactureByUserId(id_user);
    }

}

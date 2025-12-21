package com.example.hanout_app.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.RoomDatabase.Callback;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.hanout_app.database.Dao.FactureDAO;
import com.example.hanout_app.database.Dao.ProductDAO;
import com.example.hanout_app.database.Dao.SaleDAO;
import com.example.hanout_app.database.Dao.UserDAO;
import com.example.hanout_app.database.Data.FactureData;
import com.example.hanout_app.database.Data.ProductData;
import com.example.hanout_app.database.Data.SaleData;
import com.example.hanout_app.database.Data.UserData;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = { UserData.class, ProductData.class, SaleData.class,
        FactureData.class }, version = 2, exportSchema = false)
public abstract class HanoutDatabase extends RoomDatabase {
    public abstract UserDAO userDAO();

    public abstract ProductDAO productDAO();

    public abstract FactureDAO factureDAO();

    public abstract SaleDAO saleDAO();

    private static volatile HanoutDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    private static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);

            databaseWriteExecutor.execute(() -> {
                // Populate the database in the background.
                UserDAO userDao = INSTANCE.userDAO();
                ProductDAO productDao = INSTANCE.productDAO();

                // 1. Add Default User (Admin)
                UserData adminUser = new UserData(1, "Hanouty Admin", "admin@hanouty.com", "12345678", "0600000000");
                userDao.addUser(adminUser);

                // 2. Add Products linked to User 1
                // ProductData(Name, Quantity, Price, UserId)
                productDao.addProduct(new ProductData("Lait Centrale 1L", 24, 7.00, 1));
                productDao.addProduct(new ProductData("Sidi Ali 1.5L", 12, 6.00, 1));
                productDao.addProduct(new ProductData("Coca Cola 1L", 5, 8.50, 1));
                productDao.addProduct(new ProductData("Omo Matic 5kg", 3, 95.00, 1));
                productDao.addProduct(new ProductData("Fromage La Vache", 45, 15.00, 1));
                productDao.addProduct(new ProductData("Thé Sultan 200g", 18, 18.00, 1));
            });
        }
    };

    public static HanoutDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (HanoutDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            HanoutDatabase.class, "Hanouty_database")
                            .addCallback(sRoomDatabaseCallback)
                            .fallbackToDestructiveMigration() // Wipe data on version change
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    public static HanoutDatabase getInstance(final Context context) {
        return getDatabase(context);
    }

}

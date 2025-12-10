package com.example.hanout_app.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.hanout_app.database.Dao.FactureDAO;
import com.example.hanout_app.database.Dao.ProductDao;
import com.example.hanout_app.database.Dao.SaleDAO;
import com.example.hanout_app.database.Dao.UserDAO;
import com.example.hanout_app.database.Data.FactureData;
import com.example.hanout_app.database.Data.ProductData;
import com.example.hanout_app.database.Data.SaleData;
import com.example.hanout_app.database.Data.UserData;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {UserData.class, ProductData.class, SaleData.class, FactureData.class},
        version = 1,
        exportSchema = false
            )
public abstract class HanoutDatabase extends RoomDatabase {
    public abstract UserDAO userDAO();
    public abstract ProductDao productDAO();
    public abstract FactureDAO factureDAO();
    public abstract SaleDAO saleDAO();

    private static volatile HanoutDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static HanoutDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (HanoutDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    HanoutDatabase.class, "Hanouty_database")
                            .addCallback(sRoomDatabaseCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }
    private static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);

            // If you want to keep data through app restarts,
            // comment out the following block
            databaseWriteExecutor.execute(() -> {
                // Populate the database in the background.
                // If you want to start with more words, just add them.
                // hna nktb




            });
        }
    };

}

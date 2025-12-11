package com.example.hanout_app.view;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hanout_app.R;
import com.example.hanout_app.database.Data.ProductData;
import com.example.hanout_app.database.Data.UserData;
import com.example.hanout_app.database.HanoutDatabase;
import com.example.hanout_app.database.repository.Repository;

public class MainActivity extends AppCompatActivity {

    Repository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        repository = new Repository(getApplication());

        // Insert sample data in background
        HanoutDatabase.databaseWriteExecutor.execute(() -> {
            // Insert a new user
            repository.addUser(new UserData("ismail", "0673667177", "1234"));

            // Insert sample products
            ProductData Pd1 = new ProductData("hlib", 100, 4.00);
            ProductData Pd2 = new ProductData("tmer", 100, 40.00);
            ProductData Pd3 = new ProductData("lben", 100, 5.00);
            repository.addProduct(Pd1, Pd2, Pd3);
        });

        // Observe the user with id 1 safely
        repository.getUserById(1).observe(this, user -> {
            if (user != null) {
                System.out.println(">>> Loaded User: " + user.getName());
            } else {
                System.out.println(">>> No user with id 1");
            }
        });
    }
}
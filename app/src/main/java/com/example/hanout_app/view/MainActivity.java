package com.example.hanout_app.view;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.hanout_app.R;
import com.example.hanout_app.database.Dao.UserDAO;
import com.example.hanout_app.database.Data.UserData;
import com.example.hanout_app.database.HanoutDatabase;
import com.example.hanout_app.model.User;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        HanoutDatabase db = HanoutDatabase.getDatabase(this);
        UserDAO userDAO = db.userDAO();

// TEST INSERT ON BACKGROUND THREAD
        HanoutDatabase.databaseWriteExecutor.execute(() -> {
            // Create a new User
            UserData user = new UserData("test_user", "0673667177", "1234");
            userDAO.addUser(user);

            // Read it back
            UserData loaded = userDAO.getUserById(1);

            if (loaded != null) {
                System.out.println(">>> Loaded User: " + loaded.getName());
            } else {
                System.out.println(">>> No user with id 1");
            }
        });




    }
}
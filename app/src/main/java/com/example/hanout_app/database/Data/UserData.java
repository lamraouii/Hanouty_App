package com.example.hanout_app.database.Data;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "Users")
public class UserData {
    @NonNull
    @PrimaryKey(autoGenerate = true)
    private int id_User;
    private String name;
    private String email;
    private String password;
    private String phone;



    public UserData(String name, String phone, String password) {
        this.name = name;
        this.phone= phone;
        this.password = password;

    }

    @Ignore
    public UserData(int id_User, String name, String email, String password, String phone) {
        this.id_User = id_User;
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
    }

    public int getId_User() {
        return id_User;
    }

    public void setId_User(int id_User) {
        this.id_User = id_User;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}

package com.example.hanout_app.database.Dao;

import com.example.hanout_app.database.Data.UserData;
import com.example.hanout_app.model.User;
import com.example.hanout_app.utils.helpers.DateConvert;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.TypeConverters;
import androidx.room.Update;

@Dao
@TypeConverters({DateConvert.class})
public interface UserDAO {
    @Insert
    void addUser(UserData...user);

    @Update
    void modifyUser(UserData...user);

    @Delete
    void deleteUser(UserData...user);

    @Query("select * from Users where id_User= :id_user")
    UserData getUserById(int id_user);

    @Query("select * from Users where phone = :phone")
    UserData getUserByPhone(String phone);


}

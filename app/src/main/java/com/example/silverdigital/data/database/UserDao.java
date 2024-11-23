package com.example.silverdigital.data.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.silverdigital.data.model.User;

@Dao
public interface UserDao {
    @Insert
    void insertarUsuario(User user);

    @Query("SELECT * FROM users WHERE email = :email AND password = :password")
    User autenticarUsuario(String email, String password);
}

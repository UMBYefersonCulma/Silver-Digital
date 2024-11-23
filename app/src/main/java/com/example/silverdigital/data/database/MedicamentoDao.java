package com.example.silverdigital.data.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;
import com.example.silverdigital.data.model.Medicamento;
import com.example.silverdigital.data.model.User;

@Dao
public interface MedicamentoDao {
    @Insert
    void insertar(Medicamento medicamento);

    @Update
    void actualizar(Medicamento medicamento);

    @Delete
    void delete(Medicamento medicamento);

    @Query("SELECT * FROM medicamentos")
    List<Medicamento> obtenerTodos();

    @Query("SELECT * FROM medicamentos WHERE id = :medicamentoId LIMIT 1")
    Medicamento obtenerPorId(int medicamentoId);

    @Insert
    void insertarUsuario(User user);

    @Query("SELECT * FROM users WHERE email = :email AND password = :password")
    User autenticarUsuario(String email, String password);

    @Query("SELECT * FROM medicamentos WHERE frecuencia = :frecuencia")
    List<Medicamento> obtenerPorFrecuencia(String frecuencia);
}
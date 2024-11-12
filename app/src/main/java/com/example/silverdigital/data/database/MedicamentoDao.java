package com.example.silverdigital.data.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;
import com.example.silverdigital.data.model.Medicamento;

@Dao
public interface MedicamentoDao {
    @Insert
    void insertar(Medicamento medicamento);

    @Update
    void actualizar(Medicamento medicamento);

    @Delete
    void eliminar(Medicamento medicamento);

    @Query("SELECT * FROM medicamentos")
    List<Medicamento> obtenerTodos();
}
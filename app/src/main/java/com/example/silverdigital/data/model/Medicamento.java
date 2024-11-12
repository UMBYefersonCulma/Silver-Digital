package com.example.silverdigital.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "medicamentos")
public class Medicamento {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String nombre;
    private String dosis;
    private String horario;

    // Getters y setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDosis() { return dosis; }
    public void setDosis(String dosis) { this.dosis = dosis; }
    public String getHorario() { return horario; }
    public void setHorario(String horario) { this.horario = horario; }
}
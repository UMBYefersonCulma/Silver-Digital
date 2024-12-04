package com.example.silverdigital.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "appointments")
public class Appointment {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String doctorName;
    private String specialty;
    private String date;
    private String observations;

    public Appointment(int id, String doctorName, String specialty, String date, String observations) {
        this.id = id;
        this.doctorName = doctorName;
        this.specialty = specialty;
        this.date = date;
        this.observations = observations;
    }

    // Getters y setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getDoctorName() { return doctorName; }
    public void setDoctorName(String doctorName) { this.doctorName = doctorName; }

    public String getSpecialty() { return specialty; }
    public void setSpecialty(String specialty) { this.specialty = specialty; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getObservations() { return observations; }
    public void setObservations(String observations) { this.observations = observations; }
}
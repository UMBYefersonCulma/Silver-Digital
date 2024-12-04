package com.example.silverdigital.data.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.silverdigital.data.model.Appointment;

import java.util.List;


@Dao
public interface AppointmentDao {
    @Insert
    void insertAppointment(Appointment appointment);

    @Query("SELECT * FROM appointments")
    List<Appointment> getAllAppointments();

    @Delete
    void deleteAppointment(Appointment appointment);
}
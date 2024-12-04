package com.example.silverdigital.data.database;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;

import com.example.silverdigital.data.model.Medicamento;
import com.example.silverdigital.data.model.User;
import com.example.silverdigital.data.model.Appointment;

@Database(entities = {Medicamento.class, User.class, Appointment.class}, version = 8, exportSchema = false) // Incluye Appointment y actualiza la versión
public abstract class AppDatabase extends RoomDatabase {
    public abstract MedicamentoDao medicamentoDao(); // DAO para medicamentos
    public abstract UserDao userDao(); // DAO para usuarios
    public abstract AppointmentDao appointmentDao(); // DAO para citas médicas

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "medicamentos_db")
                            .fallbackToDestructiveMigration() // Elimina los datos si el esquema cambia
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
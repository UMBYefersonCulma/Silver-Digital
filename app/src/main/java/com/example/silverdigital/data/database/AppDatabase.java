package com.example.silverdigital.data.database;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;
import com.example.silverdigital.data.model.Medicamento;
import com.example.silverdigital.data.model.User;

@Database(entities = {Medicamento.class, User.class}, version = 6) // Incluye User y actualiza la versión
public abstract class AppDatabase extends RoomDatabase {
    public abstract MedicamentoDao medicamentoDao(); // DAO para medicamentos
    public abstract UserDao userDao(); // DAO para usuarios

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
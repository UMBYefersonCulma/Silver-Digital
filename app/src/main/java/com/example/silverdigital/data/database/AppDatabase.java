package com.example.silverdigital.data.database;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;
import com.example.silverdigital.data.model.Medicamento;

@Database(entities = {Medicamento.class}, version = 2) // Incrementa la versión
public abstract class AppDatabase extends RoomDatabase {
    public abstract MedicamentoDao medicamentoDao();

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
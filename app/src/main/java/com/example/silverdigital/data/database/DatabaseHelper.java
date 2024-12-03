package com.example.silverdigital.data.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "silver_digital.db";
    private static final int DATABASE_VERSION = 2;

    // Nombre de la tabla
    private static final String TABLE_CITAS = "citas";

    // Columnas de la tabla
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_DOCTOR_NAME = "nombre_medico";
    private static final String COLUMN_SPECIALTY = "especialidad";
    private static final String COLUMN_DATE = "fecha";
    private static final String COLUMN_OBSERVATIONS = "observaciones";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_CITAS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_DOCTOR_NAME + " TEXT, " +
                COLUMN_SPECIALTY + " TEXT, " +
                COLUMN_DATE + " TEXT, " +
                COLUMN_OBSERVATIONS + " TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CITAS);
        onCreate(db);
    }

    // Método para insertar una cita
    public long insertCita(String nombreMedico, String especialidad, String fecha, String observaciones) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_DOCTOR_NAME, nombreMedico);
        values.put(COLUMN_SPECIALTY, especialidad);
        values.put(COLUMN_DATE, fecha);
        values.put(COLUMN_OBSERVATIONS, observaciones);

        return db.insert(TABLE_CITAS, null, values);
    }

    // Método para obtener todas las citas
    public Cursor getAllCitas() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_CITAS, null);
    }

    // Método para eliminar una cita
    public int deleteCita(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_CITAS, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
    }

    // Método para actualizar una cita
    public int updateCita(int id, String nombreMedico, String especialidad, String fecha, String observaciones) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("nombre_medico", nombreMedico);
        values.put("especialidad", especialidad);
        values.put("fecha", fecha);
        values.put("observaciones", observaciones);

        int rowsAffected = db.update("citas", values, "id=?", new String[]{String.valueOf(id)});
        if (rowsAffected == 0) {
            Log.e("DatabaseHelper", "No se encontró ninguna fila con el ID proporcionado: " + id);
        }
        return rowsAffected;
    }
}
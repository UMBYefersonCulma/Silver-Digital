package com.example.silverdigital;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.silverdigital.data.database.DatabaseHelper;

public class EditAppointmentActivity extends AppCompatActivity {

    private EditText etDoctorName, etDate, etSpecialty, etObservations;
    private Button btnSaveChanges, btnDeleteAppointment;
    private int appointmentId; // Identificador único de la cita
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_appointment);

        // Inicializar UI y base de datos
        etDoctorName = findViewById(R.id.etDoctorName);
        etDate = findViewById(R.id.etDate);
        etSpecialty = findViewById(R.id.etSpecialty);
        etObservations = findViewById(R.id.etObservations);
        btnSaveChanges = findViewById(R.id.btnSaveChanges);
        btnDeleteAppointment = findViewById(R.id.btnDeleteAppointment);
        dbHelper = new DatabaseHelper(this);

        // Obtener datos de la cita desde el Intent
        Intent intent = getIntent();
        if (intent != null) {
            appointmentId = intent.getIntExtra("appointment_id", -1); // ID de la cita
            String doctorName = intent.getStringExtra("doctor_name");
            String specialty = intent.getStringExtra("specialty");
            String date = intent.getStringExtra("date");
            String observations = intent.getStringExtra("observations");

            // Debug: Verificar datos recibidos
            Log.d("EditAppointmentActivity", "Datos recibidos: ID=" + appointmentId +
                    ", Doctor=" + doctorName + ", Specialty=" + specialty +
                    ", Fecha=" + date + ", Observaciones=" + observations);

            // Establecer los valores en los campos si no son nulos
            if (doctorName != null) etDoctorName.setText(doctorName);
            if (specialty != null) etSpecialty.setText(specialty);
            if (date != null) etDate.setText(date);
            if (observations != null) etObservations.setText(observations);
        }

        // Guardar cambios
        btnSaveChanges.setOnClickListener(v -> {
            String updatedDoctorName = etDoctorName.getText().toString();
            String updatedSpecialty = etSpecialty.getText().toString();
            String updatedDate = etDate.getText().toString();
            String updatedObservations = etObservations.getText().toString();

            if (updatedDoctorName.isEmpty() || updatedSpecialty.isEmpty() || updatedDate.isEmpty() || updatedObservations.isEmpty()) {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
            } else {
                int result = dbHelper.updateCita(appointmentId, updatedDoctorName, updatedSpecialty, updatedDate, updatedObservations);
                if (result > 0) {
                    Toast.makeText(this, "Cita actualizada correctamente", Toast.LENGTH_SHORT).show();
                    Intent resultIntent = new Intent();
                    setResult(RESULT_OK, resultIntent);
                    finish();
                } else {
                    Toast.makeText(this, "Error al actualizar la cita", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Eliminar cita
        btnDeleteAppointment.setOnClickListener(v -> {
            int result = dbHelper.deleteCita(appointmentId);
            if (result > 0) {
                Toast.makeText(this, "Cita eliminada correctamente", Toast.LENGTH_SHORT).show();
                Intent resultIntent = new Intent();
                resultIntent.putExtra("delete", true);
                setResult(RESULT_OK, resultIntent);
                finish();
            } else {
                Toast.makeText(this, "Error al eliminar la cita", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
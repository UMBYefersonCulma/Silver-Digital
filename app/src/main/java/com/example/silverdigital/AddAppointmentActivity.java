package com.example.silverdigital;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.silverdigital.data.database.DatabaseHelper;

import java.util.Calendar;

public class AddAppointmentActivity extends AppCompatActivity {

    private EditText etFecha;
    private Button btnCancelarCita;
    private Button btnGuardarCita;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_appointment);

        etFecha = findViewById(R.id.etSeleccionarFecha);

        // Configurar el selector de fecha
        etFecha.setOnClickListener(v -> showDatePickerDialog());

        // Inicializar botones
        btnCancelarCita = findViewById(R.id.btnCancelarCita);
        btnGuardarCita = findViewById(R.id.btnGuardarCita);

        // Configurar acción del botón Cancelar
        btnCancelarCita.setOnClickListener(v -> finish()); // Cierra la actividad actual

        // Inicialización de los campos
        EditText etNombreMedico = findViewById(R.id.etNombreMedico);
        EditText etEspecialidad = findViewById(R.id.etEspecialidad);
        EditText etObservaciones = findViewById(R.id.etObservaciones);

        // Configurar acción del botón Guardar
        btnGuardarCita.setOnClickListener(v -> {
            String doctorName = etNombreMedico.getText().toString();
            String specialty = etEspecialidad.getText().toString();
            String date = etFecha.getText().toString();
            String observations = etObservaciones.getText().toString();

            // Validar que los campos no estén vacíos
            if (doctorName.isEmpty() || specialty.isEmpty() || date.isEmpty() || observations.isEmpty()) {
                Toast.makeText(this, "Por favor completa todos los campos obligatorios", Toast.LENGTH_SHORT).show();
                return;
            }

            // Insertar la cita en la base de datos usando DatabaseHelper
            DatabaseHelper dbHelper = new DatabaseHelper(this);
            long result = dbHelper.insertCita(doctorName, specialty, date, observations);

            if (result != -1) {
                Toast.makeText(this, "Cita guardada con éxito", Toast.LENGTH_SHORT).show();
                finish(); // Regresar a la pantalla anterior
            } else {
                Toast.makeText(this, "Error al guardar la cita", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String date = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                    etFecha.setText(date);
                },
                year, month, day);

        datePickerDialog.show();
    }
}
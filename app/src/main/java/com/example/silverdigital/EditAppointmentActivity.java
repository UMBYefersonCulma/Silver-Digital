package com.example.silverdigital;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.silverdigital.data.database.DatabaseHelper;

import java.util.Calendar;

public class EditAppointmentActivity extends AppCompatActivity {

    private EditText etDoctorName, etSpecialty, etObservations;
    private Button btnSaveChanges, btnDeleteAppointment, btnDatePicker;
    private int appointmentId;
    private DatabaseHelper dbHelper;
    private Calendar selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_appointment);

        etDoctorName = findViewById(R.id.etDoctorName);
        etSpecialty = findViewById(R.id.etSpecialty);
        etObservations = findViewById(R.id.etObservations);
        btnSaveChanges = findViewById(R.id.btnSaveChanges);
        btnDeleteAppointment = findViewById(R.id.btnDeleteAppointment);
        btnDatePicker = findViewById(R.id.btnDatePicker);

        dbHelper = new DatabaseHelper(this);

        Intent intent = getIntent();
        appointmentId = intent.getIntExtra("appointment_id", -1);
        etDoctorName.setText(intent.getStringExtra("doctor_name"));
        etSpecialty.setText(intent.getStringExtra("specialty"));
        btnDatePicker.setText(intent.getStringExtra("date"));
        etObservations.setText(intent.getStringExtra("observations"));

        btnDatePicker.setOnClickListener(v -> showDatePickerDialog());

        btnSaveChanges.setOnClickListener(v -> saveAppointmentChanges());
        btnDeleteAppointment.setOnClickListener(v -> deleteAppointment());
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    selectedDate = Calendar.getInstance();
                    selectedDate.set(year, month, dayOfMonth);
                    btnDatePicker.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void saveAppointmentChanges() {
        String doctorName = etDoctorName.getText().toString();
        String specialty = etSpecialty.getText().toString();
        String observations = etObservations.getText().toString();
        String date = btnDatePicker.getText().toString();

        btnDatePicker.setText(date);

        if (doctorName.isEmpty() || specialty.isEmpty() || observations.isEmpty() || selectedDate == null) {
            Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        int result = dbHelper.updateCita(appointmentId, doctorName, specialty, date, observations);
        if (result > 0) {
            Toast.makeText(this, "Cita actualizada correctamente", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Error al actualizar la cita", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteAppointment() {
        int result = dbHelper.deleteCita(appointmentId);
        if (result > 0) {
            Toast.makeText(this, "Cita eliminada correctamente", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Error al eliminar la cita", Toast.LENGTH_SHORT).show();
        }
    }
}
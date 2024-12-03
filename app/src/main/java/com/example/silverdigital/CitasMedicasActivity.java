package com.example.silverdigital;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.silverdigital.adapters.CitasAdapter;
import com.example.silverdigital.data.database.DatabaseHelper;
import com.example.silverdigital.data.model.Appointment;

import java.util.ArrayList;
import java.util.List;

public class CitasMedicasActivity extends AppCompatActivity {

    private RecyclerView rvAppointments;
    private CitasAdapter citasAdapter;
    private List<Appointment> citasList;
    private TextView tvSubtitle;
    private Button btnAddAppointment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_citas_medicas);

        rvAppointments = findViewById(R.id.rvAppointments);
        rvAppointments.setLayoutManager(new LinearLayoutManager(this));

        citasList = new ArrayList<>();
        citasAdapter = new CitasAdapter(citasList, this::onAppointmentClicked);
        rvAppointments.setAdapter(citasAdapter);

        btnAddAppointment = findViewById(R.id.btnAddAppointment);
        btnAddAppointment.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddAppointmentActivity.class);
            startActivity(intent);
        });

        tvSubtitle = findViewById(R.id.tvSubtitle);

        loadAppointments();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAppointments(); // Recarga las citas al volver a la actividad
    }

    private void loadAppointments() {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        Cursor cursor = dbHelper.getAllCitas();

        citasList.clear();
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String doctorName = cursor.getString(cursor.getColumnIndexOrThrow("nombre_medico"));
                String specialty = cursor.getString(cursor.getColumnIndexOrThrow("especialidad"));
                String date = cursor.getString(cursor.getColumnIndexOrThrow("fecha"));
                String observations = cursor.getString(cursor.getColumnIndexOrThrow("observaciones"));

                citasList.add(new Appointment(id, doctorName, specialty, date, observations));
            } while (cursor.moveToNext());
        }

        cursor.close();
        citasAdapter.notifyDataSetChanged();

        tvSubtitle.setText("Tienes " + citasList.size() + " citas programadas.");
    }

    private void onAppointmentClicked(Appointment cita) {
        Intent intent = new Intent(this, EditAppointmentActivity.class);
        intent.putExtra("appointment_id", cita.getId());
        intent.putExtra("doctor_name", cita.getDoctorName());
        intent.putExtra("specialty", cita.getSpecialty());
        intent.putExtra("date", cita.getDate());
        intent.putExtra("observations", cita.getObservations()); // O el campo que corresponda
        startActivity(intent);
    }
}

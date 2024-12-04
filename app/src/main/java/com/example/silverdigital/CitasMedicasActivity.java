package com.example.silverdigital;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.silverdigital.adapters.CitasAdapter;
import com.example.silverdigital.data.database.DatabaseHelper;
import com.example.silverdigital.data.model.Appointment;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CitasMedicasActivity extends AppCompatActivity {

    private RecyclerView rvAppointments;
    private CitasAdapter citasAdapter;
    private List<Appointment> citasList;
    private TextView tvSubtitle;
    private Button btnAddAppointment;
    private MaterialCalendarView calendarView;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_citas_medicas);

        // Inicializar componentes de la UI
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
        calendarView = findViewById(R.id.calendarView);
        dbHelper = new DatabaseHelper(this);

        // Cargar citas y resaltar fechas en el calendario
        loadAppointments();
        highlightAppointmentsOnCalendar();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAppointments(); // Recarga las citas al volver a la actividad
        highlightAppointmentsOnCalendar(); // Actualiza el calendario
    }

    private void loadAppointments() {
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

    private void highlightAppointmentsOnCalendar() {
        List<CalendarDay> appointmentDates = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        for (Appointment appointment : citasList) {
            try {
                Date appointmentDate = sdf.parse(appointment.getDate());
                if (appointmentDate != null) {

                    // Convertir Date a LocalDate
                    java.util.Calendar calendar = java.util.Calendar.getInstance();
                    calendar.setTime(appointmentDate);
                    int year = calendar.get(java.util.Calendar.YEAR);
                    int month = calendar.get(java.util.Calendar.MONTH) + 1; // Los meses en Calendar empiezan desde 0
                    int day = calendar.get(java.util.Calendar.DAY_OF_MONTH);

                    // Crear un objeto CalendarDay desde LocalDate
                    CalendarDay calendarDay = CalendarDay.from(year, month, day);
                    appointmentDates.add(calendarDay);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        // Agregar decorador para las citas
        if (!appointmentDates.isEmpty()) {
            calendarView.addDecorator(new AppointmentDecorator(appointmentDates));
        }
    }

    private Appointment getNextAppointment() {
        Appointment next = null;
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date today = new Date();

        for (Appointment appointment : citasList) {
            try {
                Date appointmentDate = sdf.parse(appointment.getDate());
                if (appointmentDate != null && appointmentDate.after(today)) {
                    if (next == null || appointmentDate.before(sdf.parse(next.getDate()))) {
                        next = appointment;
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return next;
    }

    private void onAppointmentClicked(Appointment cita) {
        Intent intent = new Intent(this, EditAppointmentActivity.class);
        intent.putExtra("appointment_id", cita.getId());
        intent.putExtra("doctor_name", cita.getDoctorName());
        intent.putExtra("specialty", cita.getSpecialty());
        intent.putExtra("date", cita.getDate());
        intent.putExtra("observations", cita.getObservations());
        startActivity(intent);
    }
}
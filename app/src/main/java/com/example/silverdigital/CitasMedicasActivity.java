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
import com.example.silverdigital.decorators.AppointmentDecorator;
import com.example.silverdigital.decorators.TodayDecorator;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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

        loadAppointments();
        calendarView.removeDecorators();
        highlightAppointmentsOnCalendar();
        highlightToday();

        // Listener para seleccionar fechas
        calendarView.setOnDateChangedListener(onDateSelectedListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshAppointmentsAndCalendar();
    }

    private void refreshAppointmentsAndCalendar() {
        loadAppointments(); // Recargar las citas en el RecyclerView
        calendarView.removeDecorators();
        highlightAppointmentsOnCalendar(); // Actualizar decoradores en el calendario
        highlightToday(); // Asegurar que el día actual sigue destacado
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

                Calendar calendar = Calendar.getInstance();
                calendar.setTime(appointmentDate);
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH) + 1; // Meses empiezan en 0
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                CalendarDay calendarDay = CalendarDay.from(year, month, day);
                appointmentDates.add(calendarDay);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        // Agregar decorador para las citas
        if (!appointmentDates.isEmpty()) {
            calendarView.addDecorator(new AppointmentDecorator(this,appointmentDates)); // Días de citas en rojo
        }
    }

    private void highlightToday() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1; // Meses empiezan en 0
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        CalendarDay today = CalendarDay.from(year, month, day);
        calendarView.addDecorator(new TodayDecorator(this,today)); // Día actual en azul
    }

    private final OnDateSelectedListener onDateSelectedListener = (widget, date, selected) -> {
        for (Appointment appointment : citasList) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            try {
                Date appointmentDate = sdf.parse(appointment.getDate());
                Calendar appointmentCalendar = Calendar.getInstance();
                appointmentCalendar.setTime(appointmentDate);

                if (appointmentCalendar.get(Calendar.YEAR) == date.getYear() &&
                        appointmentCalendar.get(Calendar.MONTH) + 1 == date.getMonth() &&
                        appointmentCalendar.get(Calendar.DAY_OF_MONTH) == date.getDay()) {
                    // Coincide una cita, redirigir al menú de edición
                    onAppointmentClicked(appointment);
                    return;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        Toast.makeText(this, "No hay citas en esta fecha", Toast.LENGTH_SHORT).show();
    };

    private void onAppointmentClicked(Appointment cita) {
        Intent intent = new Intent(this, EditAppointmentActivity.class);
        intent.putExtra("appointment_id", cita.getId());
        intent.putExtra("doctor_name", cita.getDoctorName());
        intent.putExtra("specialty", cita.getSpecialty());
        intent.putExtra("date", cita.getDate());
        intent.putExtra("observations", cita.getObservations());
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            // Si se actualizó o eliminó una cita, recargar la vista
            refreshAppointmentsAndCalendar();
        }
    }
}
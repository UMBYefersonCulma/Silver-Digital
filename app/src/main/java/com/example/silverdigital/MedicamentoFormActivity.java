package com.example.silverdigital;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.silverdigital.data.database.AppDatabase;
import com.example.silverdigital.data.model.Medicamento;

import java.util.Calendar;
import java.util.Locale;

public class MedicamentoFormActivity extends AppCompatActivity {

    private EditText etNombre, etDosis, etHorario, etObservaciones;
    private Button btnGuardar, btnEliminar;
    private int medicamentoId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicamento_form);

        // Inicializar vistas
        etNombre = findViewById(R.id.etNombre);
        etDosis = findViewById(R.id.etDosis);
        etHorario = findViewById(R.id.etHorario);
        etObservaciones = findViewById(R.id.etObservaciones);
        btnGuardar = findViewById(R.id.btnGuardar);
        btnEliminar = findViewById(R.id.btnEliminar);

        // Verificar si estamos editando un medicamento existente
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("medicamentoId")) {
            medicamentoId = intent.getIntExtra("medicamentoId", -1);
            String nombre = intent.getStringExtra("nombre");
            String dosis = intent.getStringExtra("dosis");
            String horario = intent.getStringExtra("horario");
            String observaciones = intent.getStringExtra("observaciones");

            // Mostrar los datos en los campos de edición
            etNombre.setText(nombre);
            etDosis.setText(dosis);
            etHorario.setText(horario);
            etObservaciones.setText(observaciones);
        }

        btnGuardar.setOnClickListener(v -> guardarMedicamento());

        // Configurar el clic en etHorario para abrir el TimePickerDialog
        etHorario.setOnClickListener(v -> mostrarTimePicker());

        // Configurar el botón de eliminar (si estamos editando)
        if (medicamentoId != -1) {
            btnEliminar.setOnClickListener(v -> eliminarMedicamento());
            btnEliminar.setEnabled(true);
        } else {
            btnEliminar.setEnabled(false);
        }
    }

    private void mostrarTimePicker() {
        Calendar currentTime = Calendar.getInstance();
        int currentHour = currentTime.get(Calendar.HOUR_OF_DAY);
        int currentMinute = currentTime.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (view, hourOfDay, minute) -> {
                    String horaFormateada = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
                    etHorario.setText(horaFormateada);
                },
                currentHour,
                currentMinute,
                true
        );

        timePickerDialog.show();
    }

    private void guardarMedicamento() {
        String nombre = etNombre.getText().toString().trim();
        String dosis = etDosis.getText().toString().trim();
        String horario = etHorario.getText().toString().trim();
        String observaciones = etObservaciones.getText().toString().trim();

        if (nombre.isEmpty() || nombre==""|| dosis.isEmpty() || dosis==""|| horario.isEmpty() ||horario==""|| observaciones.isEmpty()||observaciones=="") {
            Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        AppDatabase db = AppDatabase.getDatabase(getApplicationContext());

        new Thread(() -> {
            Medicamento medicamento;
            if (medicamentoId == -1) {
                medicamento = new Medicamento();
                medicamento.setNombre(nombre);
                medicamento.setDosis(dosis);
                medicamento.setHorario(horario);
                medicamento.setObservaciones(observaciones);
                db.medicamentoDao().insertar(medicamento);
            } else {
                medicamento = db.medicamentoDao().obtenerPorId(medicamentoId);
                medicamento.setNombre(nombre);
                medicamento.setDosis(dosis);
                medicamento.setHorario(horario);
                medicamento.setObservaciones(observaciones);
                db.medicamentoDao().actualizar(medicamento);
            }

            programarRecordatorio(medicamento);

            runOnUiThread(() -> {
                Toast.makeText(this, "Medicamento guardado", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                restartApp();
            });
        }).start();
    }

    private void eliminarMedicamento() {
        AppDatabase db = AppDatabase.getDatabase(getApplicationContext());

        new Thread(() -> {
            Medicamento medicamento = db.medicamentoDao().obtenerPorId(medicamentoId);
            if (medicamento != null) {
                db.medicamentoDao().delete(medicamento);
                cancelarRecordatorio(medicamento);

                runOnUiThread(() -> {
                    Toast.makeText(this, "Medicamento eliminado", Toast.LENGTH_SHORT).show();
                    finish();
                });
                restartApp();
            }
        }).start();
    }

    private void cancelarRecordatorio(Medicamento medicamento) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(this, MedicationReminderReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                medicamento.getId(),
                intent,
                PendingIntent.FLAG_NO_CREATE | PendingIntent.FLAG_IMMUTABLE
        );

        if (alarmManager != null && pendingIntent != null) {
            alarmManager.cancel(pendingIntent);
        }

        Log.d("cancelarRecordatorio", "Recordatorio cancelado para medicamento ID: " + medicamento.getId());
    }

    private void restartApp() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @SuppressLint("ScheduleExactAlarm")
    private void programarRecordatorio(Medicamento medicamento) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        // Configurar la hora de la alarma usando el horario del medicamento
        String[] horaMinuto = medicamento.getHorario().split(":");
        int hora = Integer.parseInt(horaMinuto[0]);
        int minuto = Integer.parseInt(horaMinuto[1]);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hora);
        calendar.set(Calendar.MINUTE, minuto);
        calendar.set(Calendar.SECOND, 0);

        // --- Notificación principal ---
        Intent mainIntent = new Intent(this, MedicationReminderReceiver.class);
        mainIntent.putExtra("medicamentoId", medicamento.getId());
        mainIntent.putExtra("nombre", medicamento.getNombre()); // Nombre del medicamento

        PendingIntent mainPendingIntent = PendingIntent.getBroadcast(
                this,
                medicamento.getId(), // ID único para la notificación principal
                mainIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        if (alarmManager != null) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), mainPendingIntent);
        }

        // --- Notificación anticipada (2 minutos antes) ---
        Calendar reminderCalendar = (Calendar) calendar.clone();
        reminderCalendar.add(Calendar.MINUTE, -2); // 2 minutos antes

        Intent reminderIntent = new Intent(this, MedicationReminderReceiver.class);
        reminderIntent.putExtra("medicamentoId", medicamento.getId());
        reminderIntent.putExtra("nombre", medicamento.getNombre()); // Nombre del medicamento

        PendingIntent reminderPendingIntent = PendingIntent.getBroadcast(
                this,
                medicamento.getId() * 1000, // ID único para la notificación anticipada
                reminderIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        if (alarmManager != null) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, reminderCalendar.getTimeInMillis(), reminderPendingIntent);
        }

        // Logs para depuración
        Log.d("programarRecordatorio", "Notificación principal programada para: " + calendar.getTime());
        Log.d("programarRecordatorio", "Notificación anticipada programada para: " + reminderCalendar.getTime());
    }
}
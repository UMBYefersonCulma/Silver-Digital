package com.example.silverdigital;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.silverdigital.data.database.AppDatabase;
import com.example.silverdigital.data.model.Medicamento;

import java.util.Calendar;

public class MedicamentoFormActivity extends AppCompatActivity {

    private EditText etNombre, etDosis, etHorario;
    private Button btnGuardar;
    private int medicamentoId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicamento_form);

        etNombre = findViewById(R.id.etNombre);
        etDosis = findViewById(R.id.etDosis);
        etHorario = findViewById(R.id.etHorario);
        btnGuardar = findViewById(R.id.btnGuardar);

        // Verificar si estamos editando un medicamento existente
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("medicamentoId")) {
            medicamentoId = intent.getIntExtra("medicamentoId", -1);
            String nombre = intent.getStringExtra("nombre");
            String dosis = intent.getStringExtra("dosis");
            String horario = intent.getStringExtra("horario");

            // Mostrar los datos en los campos de edición
            etNombre.setText(nombre);
            etDosis.setText(dosis);
            etHorario.setText(horario);
        }

        btnGuardar.setOnClickListener(v -> guardarMedicamento());

        // Configurar el clic en etHorario para abrir el TimePickerDialog
        etHorario.setOnClickListener(v -> mostrarTimePicker());
    }

    private void mostrarTimePicker() {
        // Obtener la hora actual para mostrarla en el TimePicker por defecto
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        // Crear y mostrar el TimePickerDialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view, selectedHour, selectedMinute) -> {
            // Actualizar etHorario con la hora seleccionada
            etHorario.setText(String.format("%02d:%02d", selectedHour, selectedMinute));
        }, hour, minute, true);
        timePickerDialog.show();
    }

    private void guardarMedicamento() {
        String nombre = etNombre.getText().toString();
        String dosis = etDosis.getText().toString();
        String horario = etHorario.getText().toString();

        if (nombre.isEmpty() || dosis.isEmpty() || horario.isEmpty()) {
            Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        AppDatabase db = AppDatabase.getDatabase(getApplicationContext());

        new Thread(() -> {
            Medicamento medicamento;
            if (medicamentoId == -1) {
                // Crear un nuevo medicamento
                medicamento = new Medicamento();
                medicamento.setNombre(nombre);
                medicamento.setDosis(dosis);
                medicamento.setHorario(horario);
                db.medicamentoDao().insertar(medicamento);
            } else {
                // Editar un medicamento existente
                medicamento = db.medicamentoDao().obtenerPorId(medicamentoId);
                medicamento.setNombre(nombre);
                medicamento.setDosis(dosis);
                medicamento.setHorario(horario);
                db.medicamentoDao().actualizar(medicamento);
            }

            runOnUiThread(() -> {
                Toast.makeText(this, "Medicamento guardado", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);

                // Programar recordatorio para el medicamento
                programarRecordatorio(medicamento);

                finish();
            });
        }).start();
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

        // Programar notificación para la hora exacta
        Intent mainIntent = new Intent(this, MedicationReminderReceiver.class);
        mainIntent.putExtra("medicamentoId", medicamento.getId());
        mainIntent.putExtra("nombre", medicamento.getNombre());
        PendingIntent mainPendingIntent = PendingIntent.getBroadcast(
                this,
                medicamento.getId(),
                mainIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        if (alarmManager != null) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), mainPendingIntent);
        }

        // Programar notificación 2 minutos antes
        Calendar reminderCalendar = (Calendar) calendar.clone();
        reminderCalendar.add(Calendar.MINUTE, -2);

        Intent reminderIntent = new Intent(this, MedicationReminderReceiver.class);
        reminderIntent.putExtra("medicamentoId", medicamento.getId());
        reminderIntent.putExtra("nombre", "Pronto debes tomar: " + medicamento.getNombre());
        PendingIntent reminderPendingIntent = PendingIntent.getBroadcast(
                this,
                medicamento.getId() * 1000, // ID único para el recordatorio
                reminderIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        if (alarmManager != null) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, reminderCalendar.getTimeInMillis(), reminderPendingIntent);
        }

        // Imprime en Logcat para confirmar la programación
        Log.d("programarRecordatorio", "Recordatorios programados para: " + calendar.getTime() + " y " + reminderCalendar.getTime());
    }
}
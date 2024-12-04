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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.silverdigital.data.database.AppDatabase;
import com.example.silverdigital.data.model.Medicamento;

import java.util.Calendar;
import java.util.Locale;

public class MedicamentoFormActivity extends AppCompatActivity {

    private EditText etNombre, etDosis, etHorario, etObservaciones;
    private Spinner spinnerFrecuencia;
    private Button btnGuardar, btnEliminar;
    private int medicamentoId = -1;
    private CheckBox checkLunes, checkMartes, checkMiercoles, checkJueves, checkViernes, checkSabado, checkDomingo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicamento_form);

        // Inicializar vistas
        TextView titulo = findViewById(R.id.tvTituloEdicion);
        etNombre = findViewById(R.id.etNombre);
        etDosis = findViewById(R.id.etDosis);
        etHorario = findViewById(R.id.etHorario);
        etObservaciones = findViewById(R.id.etObservaciones);
        btnGuardar = findViewById(R.id.btnGuardar);
        btnEliminar = findViewById(R.id.btnEliminar);
        checkLunes = findViewById(R.id.check_lunes);
        checkMartes = findViewById(R.id.check_martes);
        checkMiercoles = findViewById(R.id.check_miercoles);
        checkJueves = findViewById(R.id.check_jueves);
        checkViernes = findViewById(R.id.check_viernes);
        checkSabado = findViewById(R.id.check_sabado);
        checkDomingo = findViewById(R.id.check_domingo);

        // Spinner de frecuencia
        spinnerFrecuencia = findViewById(R.id.spinner_frecuencia);

        // Configurar opciones del spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.frecuencia_opciones, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFrecuencia.setAdapter(adapter);

        boolean isEditing = getIntent().getBooleanExtra("isEditing", false);

        // Verificar si estamos editando un medicamento existente
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("medicamentoId") && isEditing) {
            titulo.setText("Edición de Medicina");
            btnEliminar.setVisibility(View.VISIBLE);

            medicamentoId = intent.getIntExtra("medicamentoId", -1);
            String nombre = intent.getStringExtra("nombre");
            String dosis = intent.getStringExtra("dosis");
            String horario = intent.getStringExtra("horario");
            String observaciones = intent.getStringExtra("observaciones");
            String diasSemana = intent.getStringExtra("diasSemana");
            String frecuencia = intent.getStringExtra("frecuencia");

            // Mostrar los datos en los campos de edición
            etNombre.setText(nombre);
            etDosis.setText(dosis);
            etHorario.setText(horario);
            etObservaciones.setText(observaciones);

            // Seleccionar días de la semana
            marcarDiasSeleccionados(diasSemana);

            // Seleccionar frecuencia
            if (frecuencia != null) {
                int spinnerPosition = adapter.getPosition(frecuencia);
                spinnerFrecuencia.setSelection(spinnerPosition);
            }
        } else {
            // Modo creación
            titulo.setText("Crear Nueva Medicina");
            btnEliminar.setVisibility(View.GONE);

            etNombre.setText("");
            etDosis.setText("");
            etHorario.setText("");
            etObservaciones.setText("");
        }

        btnGuardar.setOnClickListener(v -> guardarMedicamento());
        etHorario.setOnClickListener(v -> mostrarTimePicker());

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

    private void marcarDiasSeleccionados(String diasSemana) {
        if (diasSemana == null) return;

        if (diasSemana.contains("Lunes")) checkLunes.setChecked(true);
        if (diasSemana.contains("Martes")) checkMartes.setChecked(true);
        if (diasSemana.contains("Miércoles")) checkMiercoles.setChecked(true);
        if (diasSemana.contains("Jueves")) checkJueves.setChecked(true);
        if (diasSemana.contains("Viernes")) checkViernes.setChecked(true);
        if (diasSemana.contains("Sábado")) checkSabado.setChecked(true);
        if (diasSemana.contains("Domingo")) checkDomingo.setChecked(true);
    }

    private void guardarMedicamento() {
        // Obtener valores de los campos
        String nombre = etNombre.getText().toString().trim();
        String dosis = etDosis.getText().toString().trim();
        String horario = etHorario.getText().toString().trim();
        String observaciones = etObservaciones.getText().toString().trim();
        String diasSeleccionados = obtenerDiasSeleccionados();
        String frecuencia = spinnerFrecuencia.getSelectedItem().toString();

        // Validar que todos los campos estén completos
        if (nombre.isEmpty() || dosis.isEmpty() || horario.isEmpty() || observaciones.isEmpty() || diasSeleccionados.isEmpty() || frecuencia.isEmpty()) {
            Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }
        if (spinnerFrecuencia != null) {
            String frecuenciaSeleccionada = spinnerFrecuencia.getSelectedItem().toString();
            // Guarda la frecuencia o realiza operaciones con ella.
        } else {
            Log.e("MedicamentoFormActivity", "spinnerFrecuencia es null");
        }

        // Obtener instancia de la base de datos
        AppDatabase db = AppDatabase.getDatabase(getApplicationContext());

        new Thread(() -> {
            Medicamento medicamento;

            if (medicamentoId == -1) {
                // Crear un nuevo medicamento
                medicamento = new Medicamento();
                medicamento.setNombre(nombre);
                medicamento.setDosis(dosis);
                medicamento.setHorario(horario);
                medicamento.setObservaciones(observaciones);
                medicamento.setDiasSemana(diasSeleccionados);
                medicamento.setFrecuencia(frecuencia);

                // Insertar en la base de datos
                db.medicamentoDao().insertar(medicamento);
            } else {
                // Editar medicamento existente
                medicamento = db.medicamentoDao().obtenerPorId(medicamentoId);
                if (medicamento != null) {
                    medicamento.setNombre(nombre);
                    medicamento.setDosis(dosis);
                    medicamento.setHorario(horario);
                    medicamento.setObservaciones(observaciones);
                    medicamento.setDiasSemana(diasSeleccionados);
                    medicamento.setFrecuencia(frecuencia);

                    // Actualizar en la base de datos
                    db.medicamentoDao().actualizar(medicamento);
                } else {
                    runOnUiThread(() ->
                            Toast.makeText(this, "Error al cargar el medicamento", Toast.LENGTH_SHORT).show()
                    );
                    return;
                }
            }

            // Programar recordatorio para el medicamento
            programarRecordatorio(medicamento);

            // Volver al hilo principal para mostrar feedback
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

    private String obtenerDiasSeleccionados() {
        StringBuilder diasSeleccionados = new StringBuilder();

        if (checkLunes.isChecked()) diasSeleccionados.append("Lunes, ");
        if (checkMartes.isChecked()) diasSeleccionados.append("Martes, ");
        if (checkMiercoles.isChecked()) diasSeleccionados.append("Miércoles, ");
        if (checkJueves.isChecked()) diasSeleccionados.append("Jueves, ");
        if (checkViernes.isChecked()) diasSeleccionados.append("Viernes, ");
        if (checkSabado.isChecked()) diasSeleccionados.append("Sábado, ");
        if (checkDomingo.isChecked()) diasSeleccionados.append("Domingo, ");

        if (diasSeleccionados.length() > 0) {
            diasSeleccionados.setLength(diasSeleccionados.length() - 2); // Eliminar la última coma y espacio
        }

        return diasSeleccionados.toString();
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
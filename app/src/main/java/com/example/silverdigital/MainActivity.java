package com.example.silverdigital;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.silverdigital.data.database.AppDatabase;
import com.example.silverdigital.data.model.Medicamento;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rvMedicamentos;
    private MedicamentoAdapter adapter;
    private static final String CHANNEL_ID = "medication_reminder_channel";
    private static final int REQUEST_CODE_ADD_MEDICAMENTO = 1;
    private static final int REQUEST_CODE_EDIT_MEDICAMENTO = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createNotificationChannel();

        rvMedicamentos = findViewById(R.id.rvMedicamentos);
        rvMedicamentos.setLayoutManager(new LinearLayoutManager(this));

        findViewById(R.id.btnAgregarMedicamento).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MedicamentoFormActivity.class);
            startActivityForResult(intent, REQUEST_CODE_ADD_MEDICAMENTO);
        });

        cargarMedicamentos();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Recordatorios de Medicamentos",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Canal para los recordatorios de medicamentos");
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void cargarMedicamentos() {
        AppDatabase db = AppDatabase.getDatabase(getApplicationContext());
        new Thread(() -> {
            List<Medicamento> medicamentos = db.medicamentoDao().obtenerTodos();
            runOnUiThread(() -> {
                adapter = new MedicamentoAdapter(medicamentos, medicamento -> {
                    Intent intent = new Intent(MainActivity.this, MedicamentoFormActivity.class);
                    intent.putExtra("medicamentoId", medicamento.getId());
                    intent.putExtra("nombre", medicamento.getNombre());
                    intent.putExtra("dosis", medicamento.getDosis());
                    intent.putExtra("horario", medicamento.getHorario());
                    startActivityForResult(intent, REQUEST_CODE_EDIT_MEDICAMENTO);
                });
                rvMedicamentos.setAdapter(adapter);
            });
        }).start();
    }

    @SuppressLint("ScheduleExactAlarm")
    private void setReminderAlarm(Medicamento medicamento) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        try {
            Date date = sdf.parse(medicamento.getHorario());
            Calendar calendar = Calendar.getInstance();
            if (date != null) {
                calendar.setTime(date);
                calendar.set(Calendar.SECOND, 0);

                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

                // Configurar la alarma principal para la hora exacta
                Intent mainIntent = new Intent(this, MedicationReminderReceiver.class);
                mainIntent.putExtra("medicamentoId", medicamento.getId());
                mainIntent.putExtra("nombre", medicamento.getNombre());
                mainIntent.putExtra("esAnticipado", false); // Notificación principal

                PendingIntent mainPendingIntent = PendingIntent.getBroadcast(
                        this,
                        medicamento.getId(), // ID único
                        mainIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
                );

                if (alarmManager != null) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), mainPendingIntent);
                }

                // Configurar la alarma de recordatorio 2 minutos antes
                Calendar reminderCalendar = (Calendar) calendar.clone();
                reminderCalendar.add(Calendar.MINUTE, -2);

                Intent reminderIntent = new Intent(this, MedicationReminderReceiver.class);
                reminderIntent.putExtra("medicamentoId", medicamento.getId());
                reminderIntent.putExtra("nombre", medicamento.getNombre());
                reminderIntent.putExtra("esAnticipado", true); // Notificación anticipada

                PendingIntent reminderPendingIntent = PendingIntent.getBroadcast(
                        this,
                        medicamento.getId() * 1000, // ID único para la anticipada
                        reminderIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
                );

                if (alarmManager != null) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, reminderCalendar.getTimeInMillis(), reminderPendingIntent);
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(this, "Formato de hora incorrecto.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && (requestCode == REQUEST_CODE_ADD_MEDICAMENTO || requestCode == REQUEST_CODE_EDIT_MEDICAMENTO)) {
            cargarMedicamentos(); // Recargar la lista de medicamentos después de agregar o editar
            if (data != null && data.hasExtra("medicamento")) {
                Medicamento medicamento = (Medicamento) data.getSerializableExtra("medicamento");
                if (medicamento != null) {
                    setReminderAlarm(medicamento); // Configurar la alarma solo después de editar o agregar
                }
            }
        }
    }
}
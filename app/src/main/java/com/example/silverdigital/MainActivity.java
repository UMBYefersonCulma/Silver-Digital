package com.example.silverdigital;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;
import com.example.silverdigital.adapters.CitasAdapter;
import com.example.silverdigital.adapters.ConsejosAdapter;
import com.example.silverdigital.adapters.MedicamentoPagerAdapter;
import com.example.silverdigital.data.database.AppDatabase;
import com.example.silverdigital.data.model.Medicamento;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private ViewPager2 viewPagerMedicamentos;
    private static final String CHANNEL_ID = "medication_reminder_channel";
    private static final int REQUEST_CODE_ADD_MEDICAMENTO = 1;
    private static final int REQUEST_CODE_EDIT_MEDICAMENTO = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createNotificationChannel();

        // Configuración del botón de configuraciones
        findViewById(R.id.btnSettings).setOnClickListener(v -> {
            Toast.makeText(this, "Configuraciones próximamente", Toast.LENGTH_SHORT).show();
        });

        // Configurar consejos médicos
        RecyclerView rvConsejos = findViewById(R.id.rvConsejos);
        rvConsejos.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvConsejos.setAdapter(new ConsejosAdapter(getEjemploConsejos()));

        // Configurar citas médicas
        RecyclerView rvCitas = findViewById(R.id.rvCitas);
        rvCitas.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvCitas.setAdapter(new CitasAdapter(getEjemploCitas()));

        // Configurar medicamentos con ViewPager2
        viewPagerMedicamentos = findViewById(R.id.viewPagerMedicamentos);
        findViewById(R.id.btnAgregarMedicamento).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MedicamentoFormActivity.class);
            startActivityForResult(intent, REQUEST_CODE_ADD_MEDICAMENTO);
        });
        cargarMedicamentos();

        // Configuración del menú inferior
        configurarMenuInferior();
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

    private List<List<Medicamento>> agruparMedicamentos(List<Medicamento> medicamentos) {
        List<List<Medicamento>> grupos = new ArrayList<>();

        // Recorremos la lista de medicamentos en pasos de 2
        for (int i = 0; i < medicamentos.size(); i += 2) {
            // Si quedan al menos 2 medicamentos, agruparlos juntos
            if (i + 1 < medicamentos.size()) {
                grupos.add(medicamentos.subList(i, i + 2)); // Grupo de 2
            } else {
                // Si solo queda 1 medicamento, agruparlo solo
                grupos.add(medicamentos.subList(i, i + 1)); // Grupo de 1
            }
        }

        return grupos;
    }

    private void cargarMedicamentos() {
        AppDatabase db = AppDatabase.getDatabase(getApplicationContext());
        new Thread(() -> {
            List<Medicamento> medicamentos = db.medicamentoDao().obtenerTodos();
            List<List<Medicamento>> grupos = agruparMedicamentos(medicamentos);

            runOnUiThread(() -> {
                MedicamentoPagerAdapter adapter = new MedicamentoPagerAdapter(grupos);
                viewPagerMedicamentos.setAdapter(adapter);
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

                // Configurar alarma principal
                Intent mainIntent = new Intent(this, MedicationReminderReceiver.class);
                mainIntent.putExtra("medicamentoId", medicamento.getId());
                mainIntent.putExtra("nombre", medicamento.getNombre());
                mainIntent.putExtra("esAnticipado", false);

                PendingIntent mainPendingIntent = PendingIntent.getBroadcast(
                        this,
                        medicamento.getId(),
                        mainIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
                );

                if (alarmManager != null) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), mainPendingIntent);
                }

                // Configurar recordatorio 2 minutos antes
                Calendar reminderCalendar = (Calendar) calendar.clone();
                reminderCalendar.add(Calendar.MINUTE, -2);

                Intent reminderIntent = new Intent(this, MedicationReminderReceiver.class);
                reminderIntent.putExtra("medicamentoId", medicamento.getId());
                reminderIntent.putExtra("nombre", medicamento.getNombre());
                reminderIntent.putExtra("esAnticipado", true);

                PendingIntent reminderPendingIntent = PendingIntent.getBroadcast(
                        this,
                        medicamento.getId() * 1000,
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

    private List<String> getEjemploConsejos() {
        return Arrays.asList(
                "Mantén una dieta equilibrada.",
                "Haz ejercicio regularmente.",
                "Consulta al médico periódicamente."
        );
    }

    private List<String> getEjemploCitas() {
        return Arrays.asList(
                "Dr. Pérez - 20/11/2024 - Revisión General",
                "Dra. Gómez - 25/11/2024 - Cardiología"
        );
    }

    private void configurarMenuInferior() {
        BottomNavigationView bottomNavigation = findViewById(R.id.bottomNavigation);
        bottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                Toast.makeText(this, "Inicio", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.nav_medicamentos) {
                Toast.makeText(this, "Medicamentos", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.nav_citas) {
                Toast.makeText(this, "Citas Médicas", Toast.LENGTH_SHORT).show();
            }
            return true;
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && (requestCode == REQUEST_CODE_ADD_MEDICAMENTO || requestCode == REQUEST_CODE_EDIT_MEDICAMENTO)) {
            cargarMedicamentos();
            if (data != null && data.hasExtra("medicamento")) {
                Medicamento medicamento = (Medicamento) data.getSerializableExtra("medicamento");
                if (medicamento != null) {
                    setReminderAlarm(medicamento);
                }
            }
        }
    }
}
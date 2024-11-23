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
import android.util.Log;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ViewPager2 viewPagerMedicamentos;
    private MedicamentoPagerAdapter medicamentoAdapter;
    private static final String CHANNEL_ID = "medication_reminder_channel";
    private static final int REQUEST_CODE_ADD_MEDICAMENTO = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createNotificationChannel();
        configurarConsejosMedicos();
        configurarCitasMedicas();
        configurarMedicamentos();
        configurarMenuInferior();
    }

    /**
     * Crear el canal de notificaciones para recordatorios.
     */
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

    /**
     * Configura el RecyclerView para mostrar consejos médicos.
     */
    private void configurarConsejosMedicos() {
        RecyclerView rvConsejos = findViewById(R.id.rvConsejos);
        rvConsejos.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvConsejos.setAdapter(new ConsejosAdapter(getEjemploConsejos()));
    }

    /**
     * Configura el RecyclerView para mostrar citas médicas.
     */
    private void configurarCitasMedicas() {
        RecyclerView rvCitas = findViewById(R.id.rvCitas);
        rvCitas.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvCitas.setAdapter(new CitasAdapter(getEjemploCitas()));
    }

    /**
     * Configura el ViewPager2 para mostrar los medicamentos.
     */
    private void configurarMedicamentos() {
        viewPagerMedicamentos = findViewById(R.id.viewPager);

        findViewById(R.id.btnAgregarMedicamento).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MedicamentoFormActivity.class);
            startActivityForResult(intent, REQUEST_CODE_ADD_MEDICAMENTO);
        });

        medicamentoAdapter = new MedicamentoPagerAdapter(new ArrayList<>());
        viewPagerMedicamentos.setAdapter(medicamentoAdapter);
        cargarMedicamentos();
    }

    /**
     * Cargar medicamentos desde la base de datos y actualizar el adaptador.
     */
    private void cargarMedicamentos() {
        AppDatabase db = AppDatabase.getDatabase(getApplicationContext());
        new Thread(() -> {
            List<Medicamento> medicamentos = db.medicamentoDao().obtenerTodos();
            List<List<Medicamento>> grupos = agruparMedicamentos(medicamentos);

            runOnUiThread(() -> {
                medicamentoAdapter.updateData(grupos);

                // Programar notificaciones solo para medicamentos válidos
                for (Medicamento medicamento : medicamentos) {
                    cancelarNotificaciones(medicamento); // Cancelar alarmas previas
                    programarNotificaciones(medicamento); // Programar nuevas alarmas
                }
            });
        }).start();
    }

    /**
     * Agrupar medicamentos en grupos de 2 para mostrar en el ViewPager2.
     */
    private List<List<Medicamento>> agruparMedicamentos(List<Medicamento> medicamentos) {
        List<List<Medicamento>> grupos = new ArrayList<>();
        for (int i = 0; i < medicamentos.size(); i += 2) {
            grupos.add(medicamentos.subList(i, Math.min(i + 2, medicamentos.size())));
        }
        return grupos;
    }

    /**
     * Configurar el menú inferior (BottomNavigationView).
     */
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

    /**
     * Obtener ejemplos de consejos médicos.
     */
    private List<String> getEjemploConsejos() {
        return Arrays.asList(
                "Mantén una dieta equilibrada.",
                "Haz ejercicio regularmente.",
                "Consulta al médico periódicamente."
        );
    }

    /**
     * Obtener ejemplos de citas médicas.
     */
    private List<String> getEjemploCitas() {
        return Arrays.asList(
                "Dr. Pérez - 20/11/2024 - Revisión General",
                "Dra. Gómez - 25/11/2024 - Cardiología"
        );
    }

    /**
     * Manejar resultados de actividades.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_ADD_MEDICAMENTO) {
            cargarMedicamentos();
        }
    }

    @SuppressLint("ScheduleExactAlarm")
    private void programarNotificaciones(Medicamento medicamento) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        // Calcular los tiempos en milisegundos
        long horarioEnMilis = convertirHorarioAMillis(medicamento.getHorario());
        long ahora = System.currentTimeMillis();

        // No programar notificaciones si el horario ya pasó
        if (horarioEnMilis <= ahora) {
            Log.d("ProgramarNotificaciones", "Horario pasado para " + medicamento.getNombre() + ". No se programará.");
            return;
        }

        // Hora exacta
        Intent exactIntent = new Intent(this, MedicationReminderReceiver.class);
        exactIntent.putExtra("medicamentoId", medicamento.getId());
        exactIntent.putExtra("nombre", medicamento.getNombre());
        exactIntent.putExtra("esAnticipado", false);
        PendingIntent exactPendingIntent = PendingIntent.getBroadcast(
                this,
                medicamento.getId(),
                exactIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Hora ajustada (2 minutos antes)
        Intent earlyIntent = new Intent(this, MedicationReminderReceiver.class);
        earlyIntent.putExtra("medicamentoId", medicamento.getId());
        earlyIntent.putExtra("nombre", medicamento.getNombre());
        earlyIntent.putExtra("esAnticipado", true);
        PendingIntent earlyPendingIntent = PendingIntent.getBroadcast(
                this,
                medicamento.getId() + 1000,
                earlyIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        if (alarmManager != null) {
            // Programar la alarma para 2 minutos antes
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, horarioEnMilis - (2 * 60 * 1000), earlyPendingIntent);
            Log.d("ProgramarNotificaciones", "Notificación 2 minutos antes programada para " + medicamento.getNombre());

            // Programar la alarma para la hora exacta
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, horarioEnMilis, exactPendingIntent);
            Log.d("ProgramarNotificaciones", "Notificación exacta programada para " + medicamento.getNombre());
        }
    }

    private void cancelarNotificaciones(Medicamento medicamento) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        // Hora exacta
        Intent exactIntent = new Intent(this, MedicationReminderReceiver.class);
        PendingIntent exactPendingIntent = PendingIntent.getBroadcast(
                this,
                medicamento.getId(),
                exactIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Hora ajustada (2 minutos antes)
        Intent earlyIntent = new Intent(this, MedicationReminderReceiver.class);
        PendingIntent earlyPendingIntent = PendingIntent.getBroadcast(
                this,
                medicamento.getId() + 1000,
                earlyIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        if (alarmManager != null) {
            if (exactPendingIntent != null) {
                alarmManager.cancel(exactPendingIntent);
                Log.d("CancelarNotificaciones", "Notificación exacta cancelada para " + medicamento.getNombre());
            }
            if (earlyPendingIntent != null) {
                alarmManager.cancel(earlyPendingIntent);
                Log.d("CancelarNotificaciones", "Notificación anticipada cancelada para " + medicamento.getNombre());
            }
        }
    }

    private long convertirHorarioAMillis(String horario) {
        try {
            @SuppressLint("SimpleDateFormat")
            java.text.SimpleDateFormat formato = new java.text.SimpleDateFormat("HH:mm");
            java.util.Date fecha = formato.parse(horario);

            java.util.Calendar calendario = java.util.Calendar.getInstance();
            if (fecha != null) {
                calendario.setTime(fecha);
                java.util.Calendar hoy = java.util.Calendar.getInstance();
                calendario.set(java.util.Calendar.YEAR, hoy.get(java.util.Calendar.YEAR));
                calendario.set(java.util.Calendar.MONTH, hoy.get(java.util.Calendar.MONTH));
                calendario.set(java.util.Calendar.DAY_OF_MONTH, hoy.get(java.util.Calendar.DAY_OF_MONTH));
                return calendario.getTimeInMillis();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0; // En caso de error
    }
}
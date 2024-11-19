package com.example.silverdigital;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
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
        configurarBotonConfiguraciones();
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
     * Configura el botón de configuraciones.
     */
    private void configurarBotonConfiguraciones() {
        findViewById(R.id.btnSettings).setOnClickListener(v ->
                Toast.makeText(this, "Configuraciones próximamente", Toast.LENGTH_SHORT).show()
        );
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
        viewPagerMedicamentos = findViewById(R.id.viewPagerMedicamentos);

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
        new Thread(() -> {
            AppDatabase db = AppDatabase.getDatabase(getApplicationContext());
            List<Medicamento> medicamentos = db.medicamentoDao().obtenerTodos();

            List<List<Medicamento>> grupos = agruparMedicamentos(medicamentos);

            runOnUiThread(() -> {
                if (medicamentoAdapter == null) {
                    medicamentoAdapter = new MedicamentoPagerAdapter(grupos);
                    viewPagerMedicamentos.setAdapter(medicamentoAdapter);
                } else {
                    medicamentoAdapter.updateData(grupos); // Actualizar el adaptador
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
            cargarMedicamentos(); // Recargar los medicamentos después de agregar o editar
        }
    }
}
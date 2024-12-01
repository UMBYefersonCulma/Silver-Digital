package com.example.silverdigital;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        configurarBotonesPrincipales();

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        float smallestWidth = Math.min(metrics.widthPixels / metrics.density, metrics.heightPixels / metrics.density);
        Log.d("SmallestWidth", "Smallest Width in dp: " + smallestWidth);
    }

    private void configurarBotonesPrincipales() {
        LinearLayout btnMedicamentos = findViewById(R.id.btnMedicamentos);
        LinearLayout btnCitasMedicas = findViewById(R.id.btnCitasMedicas);
        LinearLayout btnConsejosSalud = findViewById(R.id.btnConsejosSalud);

        // Redirigir a la actividad de Medicamentos
        btnMedicamentos.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MedicamentosActivity.class);
            startActivity(intent);
            Toast.makeText(this, "Sección Medicamentos", Toast.LENGTH_SHORT).show();
        });

        // Redirigir a la actividad de Citas Médicas
        btnCitasMedicas.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CitasMedicasActivity.class);
            startActivity(intent);
            Toast.makeText(this, "Sección Citas Médicas", Toast.LENGTH_SHORT).show();
        });

        // Redirigir a la actividad de Consejos de Salud
        btnConsejosSalud.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ConsejosSaludActivity.class);
            startActivity(intent);
            Toast.makeText(this, "Sección Recomendaciones De Salud", Toast.LENGTH_SHORT).show();
        });

        LinearLayout perfilButton = findViewById(R.id.btnPerfil); // Asegúrate que este sea el ID del botón
        perfilButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, PerfilActivity.class);
            startActivity(intent);
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(this, MainActivity.class));
            } else if (id == R.id.nav_medicamentos) {
                startActivity(new Intent(this, MedicamentosActivity.class));
            } else if (id == R.id.nav_citas) {
                startActivity(new Intent(this, CitasMedicasActivity.class));
            } else if (id == R.id.nav_consejos) {
                startActivity(new Intent(this, ConsejosSaludActivity.class));
            } else if (id == R.id.nav_perfil) {
                startActivity(new Intent(this, PerfilActivity.class));
            }
            return true;
        });
    }
}
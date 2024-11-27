package com.example.silverdigital;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

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
            Toast.makeText(this, "Sección Consejos de Salud", Toast.LENGTH_SHORT).show();
        });

        LinearLayout perfilButton = findViewById(R.id.btnPerfil); // Asegúrate que este sea el ID del botón
        perfilButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, PerfilActivity.class);
            startActivity(intent);
        });
    }
}
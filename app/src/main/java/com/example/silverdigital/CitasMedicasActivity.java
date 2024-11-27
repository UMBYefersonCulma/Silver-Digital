package com.example.silverdigital;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.silverdigital.adapters.CitasAdapter;
import java.util.Arrays;
import java.util.List;

public class CitasMedicasActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_citas_medicas);

        configurarCitasMedicas();
    }

    private void configurarCitasMedicas() {
        RecyclerView rvCitas = findViewById(R.id.rvCitas);
        rvCitas.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvCitas.setAdapter(new CitasAdapter(getEjemploCitas()));
    }

    private List<String> getEjemploCitas() {
        return Arrays.asList(
                "Dr. Pérez - 20/11/2024 - Revisión General",
                "Dra. Gómez - 25/11/2024 - Cardiología"
        );
    }
}
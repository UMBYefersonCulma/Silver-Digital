package com.example.silverdigital;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.silverdigital.adapters.ConsejosAdapter;
import java.util.Arrays;
import java.util.List;

public class ConsejosSaludActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consejos_salud);

        configurarConsejosSalud();
    }

    private void configurarConsejosSalud() {
        RecyclerView rvConsejos = findViewById(R.id.rvConsejos);
        rvConsejos.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvConsejos.setAdapter(new ConsejosAdapter(getEjemploConsejos()));
    }

    private List<String> getEjemploConsejos() {
        return Arrays.asList(
                "Mantén una dieta equilibrada.",
                "Haz ejercicio regularmente.",
                "Consulta al médico periódicamente."
        );
    }
}
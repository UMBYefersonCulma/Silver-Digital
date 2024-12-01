package com.example.silverdigital;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.silverdigital.adapters.ConsejosAdapter;

import java.util.ArrayList;
import java.util.List;

public class ConsejosSaludActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ConsejosAdapter adapter;
    private List<String> consejosList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consejos_salud);

        RecyclerView recyclerView = findViewById(R.id.recyclerViewConsejos);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Lista de nuevos consejos
        List<String> consejosList = new ArrayList<>();
        consejosList.add("Duerme al menos 7 horas diarias");
        consejosList.add("Reduce el consumo de alimentos procesados");
        consejosList.add("Practica la meditación para reducir el estrés");
        consejosList.add("Camina al menos 30 minutos al día");
        consejosList.add("Lávate las manos con frecuencia");
        consejosList.add("Mantén una buena postura al trabajar o estudiar");
        consejosList.add("Evita el consumo excesivo de azúcar");
        consejosList.add("Incluye frutas y verduras en cada comida");
        consejosList.add("Realiza chequeos médicos anuales");

// Lista de URLs correspondientes
        List<String> urlList = new ArrayList<>();
        urlList.add("https://www.sleepfoundation.org");
        urlList.add("https://www.eatright.org");
        urlList.add("https://www.headspace.com");
        urlList.add("https://www.mayoclinic.org");
        urlList.add("https://www.who.int");
        urlList.add("https://www.healthline.com");
        urlList.add("https://www.diabetes.org");
        urlList.add("https://www.choosemyplate.gov");
        urlList.add("https://www.cdc.gov");

        ConsejosAdapter adapter = new ConsejosAdapter(consejosList, urlList);
        recyclerView.setAdapter(adapter);
    }
}
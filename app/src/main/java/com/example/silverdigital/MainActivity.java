package com.example.silverdigital;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.silverdigital.data.database.AppDatabase;
import com.example.silverdigital.data.database.MedicamentoDao;
import com.example.silverdigital.data.model.Medicamento;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    @RequiresApi(api = Build.VERSION_CODES.S)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Detecta el modo oscuro
        int currentNightMode = getResources().getConfiguration().uiMode
                & android.content.res.Configuration.UI_MODE_NIGHT_MASK;

        if (currentNightMode == android.content.res.Configuration.UI_MODE_NIGHT_YES) {
            setTheme(R.style.Theme_SilverDigital_Dark);
        } else {
            setTheme(R.style.Theme_SilverDigital_Light);
        }

        setContentView(R.layout.activity_main);

        // Prueba de la base de datos
        AppDatabase db = AppDatabase.getDatabase(getApplicationContext());
        MedicamentoDao dao = db.medicamentoDao();

        // Crear un medicamento de prueba
        Medicamento medicamento = new Medicamento();
        medicamento.setNombre("Paracetamol");
        medicamento.setDosis("500mg");
        medicamento.setHorario("08:00 AM");

        // Insertar el medicamento en un hilo separado
        new Thread(() -> {
            // Insertar el medicamento
            dao.insertar(medicamento);

            // Obtener todos los medicamentos para verificar la inserción
            List<Medicamento> medicamentos = dao.obtenerTodos();
            for (Medicamento med : medicamentos) {
                Log.d("DatabaseTest", "Medicamento: " + med.getNombre() + ", Dosis: " + med.getDosis() + ", Horario: " + med.getHorario());
            }
        }).start();
    }
}
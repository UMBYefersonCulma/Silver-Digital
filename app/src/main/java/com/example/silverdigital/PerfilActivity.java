package com.example.silverdigital;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class PerfilActivity extends AppCompatActivity {

    private EditText etNombre, etPassword;
    private Button btnGuardar, btnCerrarSesion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        // Referenciar vistas
        etNombre = findViewById(R.id.etNombre);
        etPassword = findViewById(R.id.etPassword);
        btnGuardar = findViewById(R.id.btnGuardar);
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);

        // Cargar datos actuales (si están disponibles)
        cargarDatosUsuario();

        // Guardar cambios
        btnGuardar.setOnClickListener(v -> {
            String nombre = etNombre.getText().toString();
            String password = etPassword.getText().toString();

            if (!nombre.isEmpty() && !password.isEmpty()) {
                guardarDatosUsuario(nombre, password);
                Toast.makeText(this, "Cambios guardados", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
            }
        });

        // Cerrar sesión
        btnCerrarSesion.setOnClickListener(v -> {
            cerrarSesion();
        });
    }

    private void cargarDatosUsuario() {
        // Aquí deberías cargar los datos guardados (por ejemplo, usando SharedPreferences o SQLite)
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        etNombre.setText(prefs.getString("nombre", ""));
        etPassword.setText(prefs.getString("password", ""));
    }

    private void guardarDatosUsuario(String nombre, String password) {
        // Guardar los datos actualizados (por ejemplo, usando SharedPreferences)
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("nombre", nombre);
        editor.putString("password", password);
        editor.apply();
    }

    private void cerrarSesion() {
        // Limpiar datos de usuario (opcional) y redirigir al LoginActivity
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear(); // Limpia todos los datos
        editor.apply();

        // Redirigir al LoginActivity
        Intent intent = new Intent(PerfilActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish(); // Finaliza PerfilActivity
    }
}
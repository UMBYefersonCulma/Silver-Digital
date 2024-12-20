package com.example.silverdigital;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.silverdigital.data.database.AppDatabase;
import com.example.silverdigital.data.model.User;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Verificar si hay una sesión activa
        if (verificarSesionActiva()) {
            // Si ya hay sesión activa, redirigir a MainActivity
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        setContentView(R.layout.activity_login);

        // Vincular los elementos del diseño
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);

        // Manejar el clic del botón de iniciar sesión
        btnLogin.setOnClickListener(v -> validarLogin());

        // Manejar el clic en "Registrarse"
        tvRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private void validarLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Validar que los campos no estén vacíos
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Por favor, ingresa tu correo electrónico");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Por favor, ingresa tu contraseña");
            return;
        }

        // Llamar a la base de datos para validar el login
        autenticarUsuario(email, password);
    }

    private void autenticarUsuario(String email, String password) {
        new Thread(() -> {
            AppDatabase db = AppDatabase.getDatabase(getApplicationContext());
            User user = db.userDao().autenticarUsuario(email, password);

            runOnUiThread(() -> {
                if (user != null) {
                    // Login exitoso: guardar los datos en SharedPreferences
                    guardarSesionActiva(user);

                    Toast.makeText(this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    // Error en el login
                    Toast.makeText(this, "Correo o contraseña incorrectos", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }

    private void guardarSesionActiva(User user) {
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("nombre", user.getName()); // Suponiendo que User tiene un campo 'nombre'
        editor.putString("email", user.getEmail());
        editor.putBoolean("isLoggedIn", true); // Marcar que el usuario está logueado
        editor.apply();
    }

    private boolean verificarSesionActiva() {
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        return prefs.getBoolean("isLoggedIn", false); // Si está logueado, devuelve true
    }
}
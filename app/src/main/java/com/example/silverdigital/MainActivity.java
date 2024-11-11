package com.example.silverdigital;

import android.os.Build;
import android.os.Bundle;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

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
    }
}
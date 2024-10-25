package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.util.Log;
import com.google.android.material.button.MaterialButton;

public class MainActivity extends AppCompatActivity {
    private MaterialButton btnRegister, btnLogin, btnVideos, btnToggleTheme;
    private boolean isDarkModeEnabled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnRegister = findViewById(R.id.btn_register);
        btnLogin = findViewById(R.id.btn_login);
        btnVideos = findViewById(R.id.btn_videos);
        btnToggleTheme = findViewById(R.id.btn_toggle_theme);

        btnRegister.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        btnLogin.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        btnVideos.setOnClickListener(v -> {
            Log.d("MainActivity", "Videos button clicked");
            Intent intent = new Intent(MainActivity.this, VideoListActivity.class);
            startActivity(intent);
        });

        btnToggleTheme.setOnClickListener(v -> toggleTheme());

        // Check the current theme mode
        int currentNightMode = getResources().getConfiguration().uiMode & android.content.res.Configuration.UI_MODE_NIGHT_MASK;
        isDarkModeEnabled = (currentNightMode == android.content.res.Configuration.UI_MODE_NIGHT_YES);
        updateThemeButtonIcon();
    }

    private void toggleTheme() {
        if (isDarkModeEnabled) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        isDarkModeEnabled = !isDarkModeEnabled;
        updateThemeButtonIcon();
    }

    private void updateThemeButtonIcon() {
        btnToggleTheme.setIconResource(isDarkModeEnabled ? R.drawable.ic_light_mode : R.drawable.ic_dark_mode);
    }
}
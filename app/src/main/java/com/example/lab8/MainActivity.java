package com.example.lab8;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private Intent serviceIntent;
    private static final int PERMISSION_REQUEST_CODE = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        serviceIntent = new Intent(this, MyService.class);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions();
        }
    }

    private void requestPermissions() {
        String[] permissions = {
            Manifest.permission.POST_NOTIFICATIONS,
            Manifest.permission.FOREGROUND_SERVICE_MEDIA_PLAYBACK
        };

        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE);
                break;
            }
        }
    }

    public void startService(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.FOREGROUND_SERVICE_MEDIA_PLAYBACK)
                    != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Требуется разрешение для работы сервиса",
                    Toast.LENGTH_LONG).show();
                requestPermissions();
                return;
            }
        }
        try {
            ContextCompat.startForegroundService(this, serviceIntent);
        } catch (Exception e) {
            Toast.makeText(this, "Ошибка запуска сервиса: " + e.getMessage(),
                Toast.LENGTH_LONG).show();
        }
    }

    public void nextActivity(View view) {
        startActivity(new Intent(this, MainActivity2.class));
    }

    public void stopService(View view) {
        try {
            stopService(serviceIntent);
        } catch (Exception e) {
            Toast.makeText(this, "Ошибка остановки сервиса: " + e.getMessage(),
                Toast.LENGTH_LONG).show();
        }
    }
}
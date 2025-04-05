package com.example.lab8;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class MyService extends Service {
    private ToneGenerator toneGenerator;
    private String CHANNEL_ID = "channelId";
    private NotificationManager notifManager;
    private static final String TAG = "MyService";
    private boolean isPlaying = false;

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            Log.d(TAG, "onCreate: Создание сервиса");
            toneGenerator = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
            Log.d(TAG, "onCreate: ToneGenerator создан успешно");
        } catch (Exception e) {
            Log.e(TAG, "onCreate: Ошибка", e);
            Toast.makeText(this, "Ошибка: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("ForegroundServiceType")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            Log.d(TAG, "onStartCommand: Запуск сервиса");
            createNotificationChannel();

            NotificationCompat.Builder sNotifBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_music)
                    .setContentTitle("Мой музыкальный плеер")
                    .setContentText("Проигрывается звук")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

            Notification servNotification = sNotifBuilder.build();
            startForeground(1, servNotification);

            if (toneGenerator != null && !isPlaying) {
                Log.d(TAG, "onStartCommand: Начинаем воспроизведение");
                isPlaying = true;
                // Запускаем звук в отдельном потоке
                new Thread(() -> {
                    while (isPlaying) {
                        toneGenerator.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 1000);
                        try {
                            Thread.sleep(1100); // Небольшая пауза между повторами
                        } catch (InterruptedException e) {
                            break;
                        }
                    }
                }).start();
            } else {
                Log.e(TAG, "onStartCommand: ToneGenerator не готов");
            }
        } catch (Exception e) {
            Log.e(TAG, "onStartCommand: Ошибка", e);
            Toast.makeText(this, "Ошибка: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            stopSelf();
        }
        return START_STICKY;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelName = "Канал сервиса";
            String channelDescription = "Музыкальный канал";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, channelName, importance);
            channel.setDescription(channelDescription);

            notifManager = getSystemService(NotificationManager.class);
            if (notifManager != null) {
                notifManager.createNotificationChannel(channel);
                Log.d(TAG, "createNotificationChannel: Канал создан");
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: Остановка сервиса");
        isPlaying = false;
        if (toneGenerator != null) {
            toneGenerator.release();
            toneGenerator = null;
            Log.d(TAG, "onDestroy: ToneGenerator освобожден");
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
}
}

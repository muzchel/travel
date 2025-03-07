package com.example.travel;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Switch;
import androidx.appcompat.app.AppCompatActivity;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import java.util.concurrent.TimeUnit;

public class SettingsActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "AppPrefs";
    private static final String NOTIFICATIONS_ENABLED = "notifications_enabled";
    private static final String DEVELOPER_EMAIL = "sokolnik2281441@gmail.com"; // Замени на свою почту
    private Switch switchNotifications;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        // Кнопка "Профиль"
        ImageButton buttonProfile = findViewById(R.id.buttonProfile);
        buttonProfile.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        // Кнопка "Дом"
        ImageButton buttonHome = findViewById(R.id.buttonHome);
        buttonHome.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        // Кнопка "Оставить отзыв"
        Button buttonSendFeedback = findViewById(R.id.buttonSendFeedback);
        buttonSendFeedback.setOnClickListener(v -> sendFeedbackViaGmail());

        // Инициализация Switch для уведомлений
        switchNotifications = findViewById(R.id.switchNotifications);

        // Загружаем состояние переключателя из SharedPreferences
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean isEnabled = prefs.getBoolean(NOTIFICATIONS_ENABLED, true);
        switchNotifications.setChecked(isEnabled);

        // Обработчик переключателя
        switchNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(NOTIFICATIONS_ENABLED, isChecked);
            editor.apply();

            if (isChecked) {
                enableNotifications();
            } else {
                disableNotifications();
            }
        });
    }

    private void enableNotifications() {
        PeriodicWorkRequest notificationWork =
                new PeriodicWorkRequest.Builder(NotificationWorker.class, 2, TimeUnit.HOURS)
                        .build();

        WorkManager.getInstance(this)
                .enqueueUniquePeriodicWork("notificationWork", ExistingPeriodicWorkPolicy.REPLACE, notificationWork);
    }

    private void disableNotifications() {
        WorkManager.getInstance(this).cancelUniqueWork("notificationWork");
    }

    private void sendFeedbackViaGmail() {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:" + DEVELOPER_EMAIL));
        intent.putExtra(Intent.EXTRA_SUBJECT, "Отзыв о приложении Travel");
        intent.putExtra(Intent.EXTRA_TEXT, "Здравствуйте! Хочу оставить отзыв...");

        try {
            startActivity(Intent.createChooser(intent, "Выберите почтовое приложение"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

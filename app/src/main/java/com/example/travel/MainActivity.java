package com.example.travel;

import android.Manifest;
import androidx.annotation.NonNull;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.widget.ImageButton;
import database.DatabaseHelper;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    private static final String CHANNEL_ID = "travel_notifications";
    private static final int PERMISSION_REQUEST_CODE = 101;
    private DatabaseHelper databaseHelper;
    private LinearLayout tourContainer;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        databaseHelper = new DatabaseHelper(this);
        tourContainer = findViewById(R.id.tourcontainer1);

        createNotificationChannel();
        requestNotificationPermission();
        scheduleNotificationWorker();
        loadToursFromDatabase();

        ImageButton buttonProfile = findViewById(R.id.buttonProfile);
        ImageButton buttonSettings = findViewById(R.id.buttonSettings);
        TextView textViewTourName1 = findViewById(R.id.name_tour_1);
        TextView textViewTourName2 = findViewById(R.id.name_tour_2);
        TextView textViewTourName3 = findViewById(R.id.name_tour_3);

        // Получаем email пользователя, если он был передан
        userEmail = getIntent().getStringExtra("user_email");

        if (buttonProfile != null) {
            buttonProfile.setOnClickListener(v -> {
                Intent intent = new Intent(this, ProfileActivity.class);
                if (userEmail != null) {
                    intent.putExtra("user_email", userEmail);
                }
                startActivity(intent);
            });
        }
        if (buttonSettings != null) {
            buttonSettings.setOnClickListener(v -> startActivity(new Intent(this, SettingsActivity.class)));
        }

        if (textViewTourName1 != null) {
            textViewTourName1.setOnClickListener(v -> openTourDetail("Грузия: Винные приключения", TourDetailActivity.class));
        }
        if (textViewTourName2 != null) {
            textViewTourName2.setOnClickListener(v -> openTourDetail("Памуккале: Белоснежные террасы", Tour2DetailActivity.class));
        }
        if (textViewTourName3 != null) {
            textViewTourName3.setOnClickListener(v -> openTourDetail("Величие пирамид и Сфинкса", Tour3DetailActivity.class));
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID, "Travel Notifications", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("Канал для уведомлений о турах");
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, PERMISSION_REQUEST_CODE);
            }
        }
    }

    private void scheduleNotificationWorker() {
        WorkManager.getInstance(this).cancelAllWorkByTag("notification_worker");

        PeriodicWorkRequest notificationWork = new PeriodicWorkRequest.Builder(NotificationWorker.class, 2, TimeUnit.HOURS)
                .addTag("notification_worker")
                .build();
        WorkManager.getInstance(this).enqueue(notificationWork);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                scheduleNotificationWorker();
            }
        }
    }

    private void loadToursFromDatabase() {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id, name FROM tours", null);

        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String tourName = cursor.getString(1);

            TextView tourView = new TextView(this);
            tourView.setText(tourName);
            tourView.setTextSize(18);
            tourView.setPadding(10, 10, 10, 10);
            tourView.setOnClickListener(v -> openTourDetail(tourName, getTourDetailActivity(id)));

            tourContainer.addView(tourView);
        }
        cursor.close();
        db.close();
    }

    private Class<?> getTourDetailActivity(int tourId) {
        switch (tourId) {
            case 1:
                return TourDetailActivity.class;
            case 2:
                return Tour2DetailActivity.class;
            case 3:
                return Tour3DetailActivity.class;
            default:
                return TourDetailActivity.class;
        }
    }

    private void openTourDetail(String tourName, Class<?> activityClass) {
        Intent intent = new Intent(this, activityClass);
        intent.putExtra("TOUR_NAME", tourName);
        startActivity(intent);
    }
}

package com.example.travel;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import java.util.concurrent.TimeUnit;
import database.DatabaseHelper;

public class HomeFragment extends Fragment {
    private static final String CHANNEL_ID = "travel_notifications";
    private static final int PERMISSION_REQUEST_CODE = 101;
    private DatabaseHelper databaseHelper;
    private LinearLayout tourContainer;
    private String userEmail;
    public HomeFragment() {
        super(R.layout.fragment_home);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        databaseHelper = new DatabaseHelper(requireContext());
        tourContainer = view.findViewById(R.id.tourcontainer1);

        createNotificationChannel();
        requestNotificationPermission();
        scheduleNotificationWorker();
        loadToursFromDatabase();

        TextView textViewTourName1 = view.findViewById(R.id.name_tour_1);
        TextView textViewTourName2 = view.findViewById(R.id.name_tour_2);
        TextView textViewTourName3 = view.findViewById(R.id.name_tour_3);

        if (textViewTourName1 != null) {
            textViewTourName1.setOnClickListener(v -> openTourDetail("Грузия: Винные приключения", TourDetailActivity.class));
        }
        if (textViewTourName2 != null) {
            textViewTourName2.setOnClickListener(v -> openTourDetail("Памуккале: Белоснежные террасы", Tour2DetailActivity.class));
        }
        if (textViewTourName3 != null) {
            textViewTourName3.setOnClickListener(v -> openTourDetail("Величие пирамид и Сфинкса", Tour3DetailActivity.class));
        }
        return view;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID, "Travel Notifications", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("Канал для уведомлений о турах");
            NotificationManager manager = requireContext().getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.POST_NOTIFICATIONS}, PERMISSION_REQUEST_CODE);
            }
        }
    }

    private void scheduleNotificationWorker() {
        WorkManager.getInstance(requireContext()).cancelAllWorkByTag("notification_worker");

        PeriodicWorkRequest notificationWork = new PeriodicWorkRequest.Builder(NotificationWorker.class, 2, TimeUnit.HOURS)
                .addTag("notification_worker")
                .build();
        WorkManager.getInstance(requireContext()).enqueue(notificationWork);
    }

    private void loadToursFromDatabase() {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id, name FROM tours", null);

        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String tourName = cursor.getString(1);

            TextView tourView = new TextView(requireContext());
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
        Intent intent = new Intent(requireContext(), activityClass);
        intent.putExtra("TOUR_NAME", tourName);
        startActivity(intent);
    }

}

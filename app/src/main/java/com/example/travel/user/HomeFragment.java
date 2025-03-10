package com.example.travel.user;

import android.Manifest;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.bumptech.glide.Glide;
import com.example.travel.NotificationWorker;
import com.example.travel.R;
import com.example.travel.TourDetailFragment;

import database.DatabaseHelper;

import java.util.concurrent.TimeUnit;

public class HomeFragment extends Fragment {
    private static final String CHANNEL_ID = "travel_notifications";
    private static final int PERMISSION_REQUEST_CODE = 101;
    private DatabaseHelper databaseHelper;
    private LinearLayout tourContainer;
    private EditText searchField;
    private String userEmail;

    private Button filtersButton;

    public HomeFragment() {
        super(R.layout.fragment_home);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Инициализация компонента
        tourContainer = view.findViewById(R.id.tourContainer);  // Убедитесь, что контейнер есть в макете
        searchField = view.findViewById(R.id.editTextSearch);
        filtersButton = view.findViewById(R.id.buttonFilters);// Поле для поиска
        databaseHelper = new DatabaseHelper(requireContext());

        createNotificationChannel();
        requestNotificationPermission();
        scheduleNotificationWorker();

        // Загружаем туры при запуске
        loadToursFromDatabase(null);  // Загружаем все туры по умолчанию

        filtersButton.setOnClickListener(v -> openFiltersDialog());


        // Обработчик поиска
        searchField.setOnEditorActionListener((v, actionId, event) -> {
            String query = searchField.getText().toString();
            loadToursFromDatabase(query);  // Фильтруем туры по запросу
            return false;
        });


        return view;
    }

    private void openFiltersDialog() {
        // Создание диалогового окна для фильтров
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Выберите фильтры");

        // Создание элементов для фильтрации
        // Местоположение
        final EditText locationInput = new EditText(requireContext());
        locationInput.setHint("Местоположение");

        // Цена
        final EditText priceInput = new EditText(requireContext());
        priceInput.setHint("Макс. цена");

        // Добавляем поля в диалог
        LinearLayout layout = new LinearLayout(requireContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 30, 50, 30);
        layout.addView(locationInput);
        layout.addView(priceInput);
        builder.setView(layout);

        // Кнопка подтверждения
        builder.setPositiveButton("Применить", (dialog, which) -> {
            // Получаем значения из полей
            String location = locationInput.getText().toString();
            String price = priceInput.getText().toString();

            // Формируем запрос с фильтрами
            String query = "";
            if (!location.isEmpty()) {
                query += "location LIKE '%" + location + "%'";
            }
            if (!price.isEmpty()) {
                if (!query.isEmpty()) {
                    query += " AND ";
                }
                query += "price <= " + price;
            }

            // Загружаем туры с фильтрами
            loadToursFromDatabase(query);
        });

        // Кнопка отмены
        builder.setNegativeButton("Отмена", (dialog, which) -> dialog.dismiss());

        // Показываем диалог
        builder.create().show();
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

    // Загрузка туров из базы данных с фильтрацией по запросу
    private void loadToursFromDatabase(String query) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String sqlQuery = "SELECT id, name, description, location, price, contact_info, images FROM tours";

        // Если есть поисковый запрос, добавляем фильтрацию
        if (query != null && !query.isEmpty()) {
            sqlQuery += " WHERE " + query;
        }

        Cursor cursor = db.rawQuery(sqlQuery, null);

        // Очищаем контейнер для туров перед добавлением новых
        tourContainer.removeAllViews();

        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String name = cursor.getString(1);
            String description = cursor.getString(2);
            String location = cursor.getString(3);
            double price = cursor.getDouble(4);
            String contact = cursor.getString(5);
            String imageUrl = cursor.getString(6);  // Изображение тура

            // Создаем контейнер для тура
            LinearLayout tourLayout = new LinearLayout(requireContext());
            tourLayout.setOrientation(LinearLayout.HORIZONTAL);
            tourLayout.setPadding(10, 10, 10, 10);
            tourLayout.setBackgroundColor(getResources().getColor(R.color.hint));

            // Загрузка изображения с помощью Glide
            ImageView tourImage = new ImageView(requireContext());
            tourImage.setLayoutParams(new LinearLayout.LayoutParams(150, 150));
            Glide.with(requireContext()).load(imageUrl).into(tourImage);  // Загрузка изображения

            // Добавляем название тура
            TextView tourNameView = new TextView(requireContext());
            tourNameView.setText(name);
            tourNameView.setTextColor(getResources().getColor(R.color.orange));
            tourNameView.setTextSize(18);
            tourNameView.setGravity(Gravity.CENTER_VERTICAL);

            // Обработка клика на название тура
            tourNameView.setOnClickListener(v -> {
                // Открываем фрагмент с подробной информацией о туре
                Fragment fragment = TourDetailFragment.newInstance(name, description, location, price, contact);
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(
                                R.anim.slide_in_right,  // Анимация входа (для нового фрагмента)
                                R.anim.slide_out_left,  // Анимация выхода (для текущего фрагмента)
                                R.anim.slide_in_left,  // Анимация входа при возврате (для предыдущего фрагмента)
                                R.anim.slide_out_right)  // Анимация выхода при возврате (для предыдущего фрагмента)
                        .replace(R.id.fragment_container, fragment)  // заменяем контейнер фрагментов
                        .addToBackStack(null)  // добавляем в стек возврата
                        .commit();
            });

            // Добавляем элементы в layout
            tourLayout.addView(tourImage);
            tourLayout.addView(tourNameView);

            // Добавляем тур в основной контейнер
            tourContainer.addView(tourLayout);
        }

        cursor.close();
        db.close();
    }
}
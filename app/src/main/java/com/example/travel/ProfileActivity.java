package com.example.travel;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import database.DatabaseHelper;
import database.User;

public class ProfileActivity extends AppCompatActivity {

    private TextView textViewName, textViewLastName, textViewUsername, textViewEmail;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_activity); // Загружаем layout

        // Инициализируем кнопки
        ImageButton buttonHome = findViewById(R.id.buttonHome);
        ImageButton buttonSettings = findViewById(R.id.buttonSettings);

        // Инициализируем текстовые поля
        textViewName = findViewById(R.id.textViewName);
        textViewLastName = findViewById(R.id.textViewLastName);
        textViewUsername = findViewById(R.id.textViewUsername);
        textViewEmail = findViewById(R.id.textViewEmail);

        databaseHelper = new DatabaseHelper(this);

        // 1️⃣ Загружаем данные из SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String firstName = sharedPreferences.getString("firstName", "Неизвестно");
        String lastName = sharedPreferences.getString("lastName", "Неизвестно");
        String username = sharedPreferences.getString("username", "Неизвестно");
        String email = sharedPreferences.getString("email", null);

        // Устанавливаем сохраненные данные
        textViewName.setText("Имя: " + firstName);
        textViewLastName.setText("Фамилия: " + lastName);
        textViewUsername.setText("Логин: " + username);
        textViewEmail.setText("Email: " + (email != null ? email : "Неизвестно"));

        // 2️⃣ Если email есть, загружаем актуальные данные из БД
        if (email != null) {
            loadUserData(email);
        }

        // Переход в настройки
        buttonSettings.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, SettingsActivity.class);
            startActivity(intent);
        });

        // Переход на главную страницу
        buttonHome.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void loadUserData(String email) {
        User user = databaseHelper.getUserInfo(email);
        if (user != null) {
            textViewName.setText("Имя: " + user.getFirstName());
            textViewLastName.setText("Фамилия: " + user.getLastName());
            textViewUsername.setText("Логин: " + user.getUsername());
            textViewEmail.setText("Email: " + user.getEmail());

            // Обновляем SharedPreferences с актуальными данными из БД
            SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("firstName", user.getFirstName());
            editor.putString("lastName", user.getLastName());
            editor.putString("username", user.getUsername());
            editor.putString("email", user.getEmail());
            editor.apply();
        }
    }
}

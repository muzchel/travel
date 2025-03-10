package com.example.travel.admin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import com.example.travel.user.LoginActivity;
import com.example.travel.R;

public class AdminActivity extends AppCompatActivity {
    private Button buttonTours, buttonUsers, buttonLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        buttonTours = findViewById(R.id.buttonTours);
        buttonUsers = findViewById(R.id.buttonUsers);
        buttonLogout = findViewById(R.id.buttonLogout);

        // Обработчик кнопки "Туры"
     buttonTours.setOnClickListener(v -> {
      Intent intent = new Intent(AdminActivity.this, ToursActivity.class);
     startActivity(intent);
   });

        // Обработчик кнопки "Пользователи"
       // buttonUsers.setOnClickListener(v -> {
         //   Intent intent = new Intent(AdminActivity.this, UsersActivity.class);
           // startActivity(intent);
        //});

        // Обработчик кнопки "Выход"
        buttonLogout.setOnClickListener(v -> {
            Intent intent = new Intent(AdminActivity.this, LoginActivity.class);
            startActivity(intent);
            finish(); // Закрыть AdminActivity, чтобы не вернуться назад
        });
    }
}

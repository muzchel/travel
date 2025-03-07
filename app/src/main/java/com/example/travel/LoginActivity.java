package com.example.travel;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import database.DatabaseHelper;
import database.User;

public class LoginActivity extends AppCompatActivity {
    private EditText editTextEmail, editTextPassword;
    private Button buttonLogin, buttonGoToRegister;
    private DatabaseHelper databaseHelper;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        databaseHelper = new DatabaseHelper(this);

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        buttonGoToRegister = findViewById(R.id.buttonGoToRegister);

        buttonLogin.setOnClickListener(v -> loginUser());
        buttonGoToRegister.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            finish();
        });
    }

    private void loginUser() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Введите email и пароль!", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean userExists = databaseHelper.checkUser(email, password);

        if (userExists) {
            User user = databaseHelper.getUserInfo(email);
            if (user == null) {
                Toast.makeText(this, "Ошибка загрузки данных пользователя!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Сохраняем данные в SharedPreferences
            SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("firstName", user.getFirstName());
            editor.putString("lastName", user.getLastName());
            editor.putString("username", user.getUsername());
            editor.putString("email", user.getEmail());
            editor.apply();

            // Переход в MainActivity (а не сразу в ProfileActivity)
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.putExtra("user_email", email); // Передаем email
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Неверный email или пароль!", Toast.LENGTH_SHORT).show();
        }
    }
}


package com.example.travel;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.travel.admin.AdminActivity;

import database.DatabaseHelper;
import database.User;

public class LoginActivity extends AppCompatActivity {
    private EditText editTextEmail, editTextPassword;
    private Button buttonLogin, buttonGoToRegister;
    private CheckBox checkBoxAdmin;
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
        checkBoxAdmin = findViewById(R.id.AdminLogin);

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

        if (checkBoxAdmin.isChecked()) {
            // Проверка на вход как администратор
            if (email.equals("sokolnik2281441@gmail.com") && password.equals("admin123")) {
                Intent intent = new Intent(LoginActivity.this, AdminActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Неверный email или пароль для администратора!", Toast.LENGTH_SHORT).show();
            }
        } else {
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

                // Переход в MainActivity
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.putExtra("user_email", email); // Передаем email
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Неверный email или пароль!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

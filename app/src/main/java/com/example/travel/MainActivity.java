package com.example.travel;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends AppCompatActivity {

    private ImageButton settings_button_main;
    private ImageButton home_button_main;
    private ImageButton profile_button_main;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getAllId();

        // Загружаем главный фрагмент при старте
        if (savedInstanceState == null) {
            replaceFragment(new HomeFragment(), false);
        }

        profile_button_main.setOnClickListener(v -> replaceFragment(new ProfileFragment(), true));
        home_button_main.setOnClickListener(v -> replaceFragment(new HomeFragment(), false));
        settings_button_main.setOnClickListener(v -> replaceFragment(new SettingsFragment(), true));
    }

    private void getAllId() {
        settings_button_main = findViewById(R.id.buttonSettings);
        home_button_main = findViewById(R.id.buttonHome);
        profile_button_main = findViewById(R.id.buttonProfile);
    }

    private void replaceFragment(Fragment fragment, boolean swipeRight) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        // Устанавливаем анимации перехода для свайпа
        if (swipeRight) {
            // Свайп вправо для настроек и профиля
            transaction.setCustomAnimations(
                    R.anim.slide_in_right,  // Вход справа
                    R.anim.slide_out_left   // Выход слева
            );
        } else {
            // Свайп влево для главной страницы
            transaction.setCustomAnimations(
                    R.anim.slide_in_left,   // Вход слева
                    R.anim.slide_out_right  // Выход вправо
            );
        }

        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);  // Добавляем в стек возврата
        transaction.commit();
    }
}

package com.example.travel;

import android.os.Bundle;
import android.util.Log;
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
            replaceFragment(new HomeFragment());
        }

        profile_button_main.setOnClickListener(v -> replaceFragment(new ProfileFragment()));
        home_button_main.setOnClickListener(v -> replaceFragment(new HomeFragment()));
        settings_button_main.setOnClickListener(v -> replaceFragment(new SettingsFragment()));
    }

    private void getAllId() {
        settings_button_main = findViewById(R.id.buttonSettings);
        home_button_main = findViewById(R.id.buttonHome);
        profile_button_main = findViewById(R.id.buttonProfile);
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}

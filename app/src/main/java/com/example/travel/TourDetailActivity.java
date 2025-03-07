package com.example.travel;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class TourDetailActivity extends AppCompatActivity {

    private static final String AGENCY_EMAIL = "sokolnik2281441@gmail.com";
    private static final String AGENCY_PHONE = "+375445155655";
    private String location; // Добавлено поле для хранения местоположения

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tour_detail);
        Log.d("TourDetailActivity", "Activity created");

        // Получаем местоположение из Intent
        location = getIntent().getStringExtra("location");

        ImageButton buttonProfile = findViewById(R.id.buttonProfile);
        ImageButton buttonHome = findViewById(R.id.buttonHome);
        ImageButton buttonSettings = findViewById(R.id.buttonSettings);
        Button buttonTourAgencyEmail = findViewById(R.id.buttonTourAgencyEmail);
        TextView textPhoneNumber = findViewById(R.id.textPhoneNumber);

        buttonProfile.setOnClickListener(v -> startActivity(new Intent(TourDetailActivity.this, ProfileActivity.class)));
        buttonHome.setOnClickListener(v -> startActivity(new Intent(TourDetailActivity.this, MainActivity.class)));
        buttonSettings.setOnClickListener(v -> startActivity(new Intent(TourDetailActivity.this, SettingsActivity.class)));
        buttonTourAgencyEmail.setOnClickListener(v -> sendEmailToAgency());
        textPhoneNumber.setOnClickListener(v -> dialPhoneNumber());
    }

    private void sendEmailToAgency() {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:" + AGENCY_EMAIL));
        intent.putExtra(Intent.EXTRA_SUBJECT, "Запрос информации о туре");
        intent.putExtra(Intent.EXTRA_TEXT, "Здравствуйте! Хотел бы узнать больше о данном туре...");
        startActivity(Intent.createChooser(intent, "Выберите почтовое приложение"));
    }

    private void dialPhoneNumber() {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + AGENCY_PHONE));
        startActivity(intent);
    }
}

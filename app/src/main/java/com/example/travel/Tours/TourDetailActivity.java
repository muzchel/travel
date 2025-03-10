package com.example.travel.Tours;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.travel.R;
import database.DatabaseHelper;

public class TourDetailActivity extends AppCompatActivity {

    private TextView tourNameTextView, descriptionTextView, priceTextView, locationTextView;
    private ImageView tourImageView;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tour_detail);

        // Инициализация представлений
        tourNameTextView = findViewById(R.id.tourNameTextView);
        descriptionTextView = findViewById(R.id.descriptionTextView);
        priceTextView = findViewById(R.id.priceTextView);
        locationTextView = findViewById(R.id.locationTextView);
        tourImageView = findViewById(R.id.tourImageView);

        databaseHelper = new DatabaseHelper(this);

        // Получаем ID тура из Intent
        int tourId = getIntent().getIntExtra("TOUR_ID", -1);

        if (tourId != -1) {
            loadTourDetails(tourId);
        }
    }

    private void loadTourDetails(int tourId) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT name, description, location, price, images FROM tours WHERE id = ?",
                new String[]{String.valueOf(tourId)});


        if (cursor.moveToFirst()) {
            // Получаем индексы колонок
            int nameIndex = cursor.getColumnIndex("name");
            int descriptionIndex = cursor.getColumnIndex("description");
            int locationIndex = cursor.getColumnIndex("location");
            int priceIndex = cursor.getColumnIndex("price");
            int imageIndex = cursor.getColumnIndex("images");

            // Проверяем, что индекс не равен -1, перед тем как извлекать данные
            String name = nameIndex != -1 ? cursor.getString(nameIndex) : "Без названия";
            String description = descriptionIndex != -1 ? cursor.getString(descriptionIndex) : "Нет описания";
            String location = locationIndex != -1 ? cursor.getString(locationIndex) : "Не указано";
            double price = priceIndex != -1 ? cursor.getDouble(priceIndex) : 0.0;
            String image = imageIndex != -1 ? cursor.getString(imageIndex) : "default_image";  // Дефолтное изображение

            // Дальше используйте эти данные, например, добавляйте их в UI или список
        }
        cursor.close();
        db.close();
    }
}

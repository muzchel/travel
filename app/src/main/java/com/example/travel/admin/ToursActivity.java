package com.example.travel.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import Adapter.TourAdapter;
import com.example.travel.AddEditTourActivity;
import com.example.travel.R;
import database.DatabaseHelper;
import database.Tour;

import java.util.List;

public class ToursActivity extends AppCompatActivity {

    private Button buttonAddTour;
    private RecyclerView recycler;
    private DatabaseHelper databaseHelper;
    private TourAdapter tourAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tours);

        recycler = findViewById(R.id.recycler);  // Инициализация RecyclerView
        recycler.setLayoutManager(new LinearLayoutManager(this));  // Устанавливаем LayoutManager

        buttonAddTour = findViewById(R.id.buttonAddTour);
        databaseHelper = new DatabaseHelper(this);

        // Загрузка туров
        loadTours();

        // Обработка кнопки добавления тура
        buttonAddTour.setOnClickListener(v -> {
            Intent intent = new Intent(ToursActivity.this, AddEditTourActivity.class);
            startActivity(intent);
        });
    }

    // Метод загрузки туров в RecyclerView
    private void loadTours() {
        List<Tour> tours = databaseHelper.geAllTours();  // Получаем список туров
        if (tours != null) {
            // Создаем адаптер для RecyclerView и передаем слушатель для кликов
            tourAdapter = new TourAdapter(this, tours, new TourAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(Tour tour) {
                    // Переход на экран редактирования тура
                    Intent intent = new Intent(ToursActivity.this, AddEditTourActivity.class);
                    intent.putExtra("tourId", tour.getId());
                    startActivity(intent);
                }

                @Override
                public void onItemLongClick(Tour tour) {
                    // Обработка удаления тура
                    deleteTour(tour);
                }
            });

            recycler.setAdapter(tourAdapter);  // Устанавливаем адаптер
        }
    }

    // Метод для удаления тура
    private void deleteTour(Tour tour) {
        boolean result = databaseHelper.deleteTour(tour.getId());  // Удаляем тур из базы данных
        if (result) {
            Toast.makeText(this, "Тур успешно удален", Toast.LENGTH_SHORT).show();
            loadTours();  // Перезагружаем список туров
        } else {
            Toast.makeText(this, "Ошибка при удалении тура", Toast.LENGTH_SHORT).show();
        }
    }
}
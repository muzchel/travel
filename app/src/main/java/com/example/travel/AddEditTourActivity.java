package com.example.travel;

import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.travel.R;
import com.example.travel.admin.AdminActivity;

import database.DatabaseHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class AddEditTourActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText editTextTourName, editTextTourDescription, editTextTourLocation, editTextTourPrice, editTextTourContact;
    private ImageView imageViewTourImage;
    private Button buttonSaveTour, buttonSelectImage;
    private DatabaseHelper databaseHelper;

    private String selectedImagePath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_tour);

        // Инициализация компонентов
        editTextTourName = findViewById(R.id.editTextName);
        editTextTourDescription = findViewById(R.id.editTextDescription);
        editTextTourLocation = findViewById(R.id.editTextLocation);
        editTextTourPrice = findViewById(R.id.editTextPrice);
        editTextTourContact = findViewById(R.id.editTextContact);
        imageViewTourImage = findViewById(R.id.imageViewTour);
        buttonSaveTour = findViewById(R.id.buttonSaveTour);
        buttonSelectImage = findViewById(R.id.buttonSelectImage);

        databaseHelper = new DatabaseHelper(this);

        // Обработчик для выбора изображения
        buttonSelectImage.setOnClickListener(v -> {
            // Открываем галерею для выбора изображения
            openGallery();
        });

        // Обработчик для сохранения тура
        buttonSaveTour.setOnClickListener(v -> saveTour());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Разрешение предоставлено, можно продолжать работу с хранилищем
                Toast.makeText(this, "Разрешение на запись предоставлено", Toast.LENGTH_SHORT).show();
            } else {
                // Разрешение не предоставлено
                Toast.makeText(this, "Разрешение на запись в хранилище не предоставлено", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void openGallery() {
        // Открытие галереи для выбора изображения
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            // Получаем URI выбранного изображения
            Uri selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                imageViewTourImage.setImageURI(selectedImageUri);  // Отображаем изображение в ImageView

                // Сохранить путь к изображению в переменную или базу данных
                selectedImagePath = selectedImageUri.toString();
                try {
                    // Сохраняем изображение и получаем путь к нему
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                    saveImageToInternalStorage(selectedImageUri);
                    imageViewTourImage.setImageBitmap(bitmap);  // Отображаем выбранное изображение в ImageView
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Ошибка при загрузке изображения", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void saveImageToInternalStorage(Uri imageUri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            if (inputStream != null) {
                String fileName = System.currentTimeMillis() + ".jpg";  // Генерация уникального имени файла
                File imageFile = new File(getFilesDir(), fileName);
                try (FileOutputStream fos = new FileOutputStream(imageFile)) {
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = inputStream.read(buffer)) != -1) {
                        fos.write(buffer, 0, length);
                    }
                    selectedImagePath = imageFile.getAbsolutePath();  // Сохраняем путь к изображению
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Ошибка при сохранении изображения", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Ошибка при открытии изображения", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveTour() {
        String tourName = editTextTourName.getText().toString().trim();
        String tourDescription = editTextTourDescription.getText().toString().trim();
        String tourLocation = editTextTourLocation.getText().toString().trim();
        String tourPriceString = editTextTourPrice.getText().toString().trim();
        String tourContact = editTextTourContact.getText().toString().trim();

        // Проверка на пустые поля
        if (tourName.isEmpty() || tourDescription.isEmpty() || tourLocation.isEmpty() || tourPriceString.isEmpty() || tourContact.isEmpty()) {
            Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
            return;
        }

        double tourPrice = 0;
        try {
            tourPrice = Double.parseDouble(tourPriceString);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Некорректная цена", Toast.LENGTH_SHORT).show();
            return;
        }

        // Если изображение не выбрано, используем путь по умолчанию
        if (selectedImagePath.isEmpty()) {
            selectedImagePath = "default_image";  // Путь к изображению по умолчанию
        }

        // Сохранение информации о туре в базу данных
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", tourName);
        values.put("description", tourDescription);
        values.put("image_path", selectedImagePath);  // Путь к изображению
        values.put("price", tourPrice);  // Цена
        values.put("location", tourLocation);  // Место
        values.put("contact_info", tourContact);  // Контактная информация

        try {
            long result = db.insert("tours", null, values);
            db.close();

            if (result != -1) {
                Toast.makeText(this, "Тур добавлен", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, AdminActivity.class));  // Переход в AdminActivity
                finish();
            } else {
                Log.e("AddEditTourActivity", "Ошибка при добавлении тура, код ошибки: " + result);
                Toast.makeText(this, "Ошибка при добавлении тура", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e("AddEditTourActivity", "Ошибка при добавлении тура: " + e.getMessage());
            Toast.makeText(this, "Произошла ошибка при добавлении тура", Toast.LENGTH_SHORT).show();
        }
    }

}

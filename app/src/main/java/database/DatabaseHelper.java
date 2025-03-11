package database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "app_database.db";
    private static final int DATABASE_VERSION = 2; // Обновляем версию базы данных на 2

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "first_name TEXT NOT NULL," +
                "last_name TEXT NOT NULL," +
                "username TEXT UNIQUE NOT NULL," +
                "email TEXT UNIQUE NOT NULL," +
                "password TEXT NOT NULL," +  // Пароль теперь будет храниться в хешированном виде
                "role TEXT CHECK(role IN ('user', 'admin')) NOT NULL DEFAULT 'user'," +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ");");

        db.execSQL("CREATE TABLE tours (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "description TEXT, " +
                "location TEXT NOT NULL, " +
                "price REAL NOT NULL, " +
                "rating REAL DEFAULT 0, " +
                "contact_info TEXT, " +
                "images TEXT, " +
                "image_path TEXT, " +  // добавляем поле для пути к изображению
                "is_verified INTEGER DEFAULT 0, " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ");");

        db.execSQL("CREATE TABLE reviews (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "user_id INTEGER NOT NULL," +
                "tour_id INTEGER NOT NULL," +
                "rating INTEGER CHECK(rating BETWEEN 1 AND 5) NOT NULL," +
                "comment TEXT," +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE," +
                "FOREIGN KEY (tour_id) REFERENCES tours(id) ON DELETE CASCADE" +
                ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            // Для версии 2 базы данных добавляем новый столбец 'image_path'
            db.execSQL("ALTER TABLE tours ADD COLUMN image_path TEXT");
        }
        // Здесь можно добавить другие изменения для будущих версий базы данных
    }

    public Cursor getAllUsers() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM users", null);
    }

    public boolean deleteUser(String username) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete("users", "username=?", new String[]{username});
        return result > 0;
    }

    // Метод хеширования пароля (SHA-256)
    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return password;
        }
    }

    // Регистрация пользователя с хешированием пароля
    public boolean registerUser(String username, String firstName, String lastName, String email, String password, String role) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("username", username);
        values.put("first_name", firstName);
        values.put("last_name", lastName);
        values.put("email", email);
        values.put("password", hashPassword(password));  // Хешируем пароль
        values.put("role", role);

        long result = db.insert("users", null, values);
        return result != -1;
    }

    // Проверка существования пользователя по email
    public boolean isEmailExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE email = ?", new String[]{email});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    // Проверка логина (сравнение хешированного пароля)
    public boolean checkUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String hashedPassword = hashPassword(password);
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE email = ? AND password = ?", new String[]{email, hashedPassword});

        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return exists;
    }

    // Получение информации о пользователе
    public User getUserInfo(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT first_name, last_name, username, email FROM users WHERE email = ?", new String[]{email});

        if (cursor.moveToFirst()) {
            String firstName = cursor.getString(0);
            String lastName = cursor.getString(1);
            String username = cursor.getString(2);
            String userEmail = cursor.getString(3);
            cursor.close();
            return new User(firstName, lastName, username, userEmail);
        }
        cursor.close();
        return null;
    }
    public void printAllUsers() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM users", null);

        // Получаем имена всех столбцов
        String[] columnNames = cursor.getColumnNames();
        for (String columnName : columnNames) {
            Log.d("DatabaseHelper", "Column name: " + columnName);
        }

        // Проверяем, есть ли данные в курсоре
        if (cursor != null && cursor.moveToFirst()) {
            do {
                // Печатаем информацию о каждом пользователе
                int idIndex = cursor.getColumnIndex("id");
                int firstNameIndex = cursor.getColumnIndex("first_name");
                int lastNameIndex = cursor.getColumnIndex("last_name");
                int usernameIndex = cursor.getColumnIndex("username");
                int emailIndex = cursor.getColumnIndex("email");
                int roleIndex = cursor.getColumnIndex("role");
                int createdAtIndex = cursor.getColumnIndex("created_at");

                // Проверяем, что индексы >= 0
                if (idIndex >= 0 && firstNameIndex >= 0 && lastNameIndex >= 0 && usernameIndex >= 0 && emailIndex >= 0) {
                    int id = cursor.getInt(idIndex);
                    String firstName = cursor.getString(firstNameIndex);
                    String lastName = cursor.getString(lastNameIndex);
                    String username = cursor.getString(usernameIndex);
                    String email = cursor.getString(emailIndex);
                    String role = cursor.getString(roleIndex);
                    String createdAt = cursor.getString(createdAtIndex);

                    Log.d("DatabaseHelper", "User ID: " + id + ", Name: " + firstName + " " + lastName + ", Username: " + username + ", Email: " + email + ", Role: " + role + ", Created At: " + createdAt);
                }
            } while (cursor.moveToNext());

            cursor.close();
        } else {
            Log.d("DatabaseHelper", "No users found");
        }
    }

    // Получение роли пользователя
    public String getUserRole(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT role FROM users WHERE email = ?", new String[]{email});

        if (cursor.moveToFirst()) {
            String role = cursor.getString(0);
            cursor.close();
            return role;
        }
        cursor.close();
        return "user";
    }

    // Методы для работы с турами
    public boolean addTour(String name, String description, String location, double price, String contactInfo, String imagePath) {
        if (name == null || location == null || price <= 0) {
            Log.e("AddTour", "Некорректные данные для добавления тура.");
            return false;  // Проверка на пустые или некорректные значения
        }

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("description", description);
        values.put("location", location);
        values.put("price", price);
        values.put("contact_info", contactInfo);
        values.put("image_path", imagePath);  // Добавляем новый столбец

        long result = db.insert("tours", null, values);
        if (result == -1) {
            Log.e("DatabaseError", "Ошибка при добавлении тура, код ошибки: -1");
            return false;
        }

        return true;
    }

    // Обновление данных тура
    public boolean updateTour(int id, String name, String description, String location, double price, String contactInfo, String imagePath) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("description", description);
        values.put("location", location);
        values.put("price", price);
        values.put("contact_info", contactInfo);
        values.put("image_path", imagePath);  // Обновляем поле

        int result = db.update("tours", values, "id=?", new String[]{String.valueOf(id)});
        return result > 0;
    }

    public boolean deleteTour(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete("tours", "id=?", new String[]{String.valueOf(id)});
        return result > 0;
    }

    // Получение списка всех туров

    public List<Tour> geAllTours() {
        List<Tour> tours = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT id, name, description, location, price, rating, contact_info, images, image_path, is_verified FROM tours", null);

        if (cursor != null && cursor.moveToFirst()) {
            // Проверяем существование каждого столбца
            int idIndex = cursor.getColumnIndex("id");
            int nameIndex = cursor.getColumnIndex("name");
            int descriptionIndex = cursor.getColumnIndex("description");
            int locationIndex = cursor.getColumnIndex("location");
            int priceIndex = cursor.getColumnIndex("price");
            int ratingIndex = cursor.getColumnIndex("rating");
            int contactInfoIndex = cursor.getColumnIndex("contact_info");
            int imagesIndex = cursor.getColumnIndex("images");
            int imagePathIndex = cursor.getColumnIndex("image_path");
            int isVerifiedIndex = cursor.getColumnIndex("is_verified");

            do {
                if (idIndex >= 0) {
                    int id = cursor.getInt(idIndex);
                    String name = nameIndex >= 0 ? cursor.getString(nameIndex) : null;
                    String description = descriptionIndex >= 0 ? cursor.getString(descriptionIndex) : null;
                    String location = locationIndex >= 0 ? cursor.getString(locationIndex) : null;
                    double price = priceIndex >= 0 ? cursor.getDouble(priceIndex) : 0;
                    double rating = ratingIndex >= 0 ? cursor.getDouble(ratingIndex) : 0;
                    String contactInfo = contactInfoIndex >= 0 ? cursor.getString(contactInfoIndex) : null;
                    String images = imagesIndex >= 0 ? cursor.getString(imagesIndex) : null;
                    String imagePath = imagePathIndex >= 0 ? cursor.getString(imagePathIndex) : null;
                    boolean isVerified = isVerifiedIndex >= 0 && cursor.getInt(isVerifiedIndex) == 1;

                    tours.add(new Tour(id, name, description, location, price, rating, contactInfo, images, imagePath, isVerified));
                }
            } while (cursor.moveToNext());
        }

        if (cursor != null) {
            cursor.close();
        }
        return tours;
    }
}

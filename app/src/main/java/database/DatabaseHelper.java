package database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "app_database.db";
    private static final int DATABASE_VERSION = 1;

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
                "password TEXT NOT NULL," +
                "role TEXT CHECK(role IN ('user', 'admin')) NOT NULL DEFAULT 'user'," +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ");");

        db.execSQL("CREATE TABLE tours (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT NOT NULL," +
                "description TEXT," +
                "location TEXT NOT NULL," +
                "price REAL NOT NULL," +
                "rating REAL DEFAULT 0," +
                "contact_info TEXT," +
                "images TEXT," +
                "is_verified INTEGER DEFAULT 0," +
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
        db.execSQL("DROP TABLE IF EXISTS users");
        db.execSQL("DROP TABLE IF EXISTS tours");
        db.execSQL("DROP TABLE IF EXISTS reviews");
        onCreate(db);
    }

    public boolean registerUser(String username, String firstName, String lastName, String email, String password, String role) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("username", username);  // ✅ Добавляем username
        values.put("first_name", firstName);
        values.put("last_name", lastName);
        values.put("email", email);
        values.put("password", password);
        values.put("role", role);

        long result = db.insert("users", null, values);
        return result != -1;
    }
    public boolean isEmailExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE email = ?", new String[]{email});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }
    public void printAllUsers() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM users", null);

        while (cursor.moveToNext()) {
            String userId = cursor.getString(cursor.getColumnIndexOrThrow("id"));
            String userEmail = cursor.getString(cursor.getColumnIndexOrThrow("email"));
            String userPassword = cursor.getString(cursor.getColumnIndexOrThrow("password")); // ✅ Проверяем, что есть пароль

            Log.d("DatabaseHelper", "ID: " + userId + " | Email: " + userEmail + " | Пароль: " + userPassword);
        }
        cursor.close();
    }
    public boolean checkUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE email = ? AND password = ?", new String[]{email, password});

        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();

        return exists;
    }

    public User getUserInfo(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT first_name, last_name, username, email FROM users WHERE email = ?", new String[]{email});

        if (cursor.moveToFirst()) {
            String firstName = cursor.getString(cursor.getColumnIndexOrThrow("first_name"));
            String lastName = cursor.getString(cursor.getColumnIndexOrThrow("last_name"));
            String username = cursor.getString(cursor.getColumnIndexOrThrow("username"));
            String userEmail = cursor.getString(cursor.getColumnIndexOrThrow("email"));

            cursor.close();
            return new User(firstName, lastName, username, userEmail);
        }
        cursor.close();
        return null;
    }



}

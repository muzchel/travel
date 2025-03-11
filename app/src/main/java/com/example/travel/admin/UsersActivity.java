package com.example.travel.admin;

import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travel.R;

import java.util.ArrayList;

import Adapter.UserAdapter;
import database.DatabaseHelper;
import database.User;

public class UsersActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private DatabaseHelper databaseHelper;
    private ArrayList<User> userList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        recyclerView = findViewById(R.id.recyclerViewUsers);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        databaseHelper = new DatabaseHelper(this);
        userList = getUsersFromDatabase();

        userAdapter = new UserAdapter(userList, new UserAdapter.OnUserDeleteListener() {
            @Override
            public void onUserDelete(final String username) {
                confirmDeleteUser(username);
            }
        });
        recyclerView.setAdapter(userAdapter);
    }

    // Получение пользователей из базы данных
    private ArrayList<User> getUsersFromDatabase() {
        ArrayList<User> users = new ArrayList<>();
        Cursor cursor = databaseHelper.getAllUsers();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String firstName = cursor.getString(0);
                String lastName = cursor.getString(1);
                String username = cursor.getString(2);
                String email = cursor.getString(3);
                users.add(new User(firstName, lastName, username, email));
            }
            cursor.close();
        }
        return users;
    }

    // Подтверждение удаления пользователя
    private void confirmDeleteUser(final String username) {
        new AlertDialog.Builder(this)
                .setTitle("Удаление пользователя")
                .setMessage("Вы уверены, что хотите удалить этого пользователя?")
                .setPositiveButton("Да", (dialog, which) -> {
                    // Удаление пользователя из базы данных и обновление списка
                    boolean deleted = databaseHelper.deleteUser(username);
                    if (deleted) {
                        Toast.makeText(this, "Пользователь удален", Toast.LENGTH_SHORT).show();
                        updateUserList(); // Обновление списка пользователей
                    } else {
                        Toast.makeText(this, "Ошибка удаления", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Отмена", null)
                .show();
    }

    // Обновление списка пользователей после удаления
    private void updateUserList() {
        userList.clear();
        userList.addAll(getUsersFromDatabase());  // Получаем актуальный список пользователей из базы данных
        userAdapter.notifyDataSetChanged();  // Уведомляем адаптер, что данные изменились
    }
}

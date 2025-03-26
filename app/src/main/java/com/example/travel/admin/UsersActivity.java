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
        loadUsers();

        userAdapter = new UserAdapter(userList, new UserAdapter.OnUserDeleteListener() {
            @Override
            public void onUserDelete(final String username) {
                confirmDeleteUser(username);
            }
        });
        recyclerView.setAdapter(userAdapter);
    }

    // Загружаем пользователей из базы данных
    private void loadUsers() {
        userList = new ArrayList<>();
        Cursor cursor = databaseHelper.getAllUsers();

        if (cursor != null) {
            while (cursor.moveToNext()) {
                int firstNameIndex = cursor.getColumnIndex("first_name");
                int lastNameIndex = cursor.getColumnIndex("last_name");
                int usernameIndex = cursor.getColumnIndex("username");
                int emailIndex = cursor.getColumnIndex("email");

                if (firstNameIndex >= 0 && lastNameIndex >= 0 && usernameIndex >= 0 && emailIndex >= 0) {
                    String firstName = cursor.getString(firstNameIndex);
                    String lastName = cursor.getString(lastNameIndex);
                    String username = cursor.getString(usernameIndex);
                    String email = cursor.getString(emailIndex);

                    userList.add(new User(firstName, lastName, username, email));
                }
            }
            cursor.close();
        }
    }

    // Подтверждение удаления пользователя
    private void confirmDeleteUser(final String username) {
        new AlertDialog.Builder(this)
                .setTitle("Удаление пользователя")
                .setMessage("Вы уверены, что хотите удалить пользователя: " + username + "?")
                .setPositiveButton("Да", (dialog, which) -> {
                    boolean deleted = databaseHelper.deleteUser(username);
                    if (deleted) {
                        Toast.makeText(this, "Пользователь удален", Toast.LENGTH_SHORT).show();
                        updateUserList();
                    } else {
                        Toast.makeText(this, "Ошибка удаления", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Отмена", null)
                .show();
    }

    // Обновление списка после удаления
    private void updateUserList() {
        userList.clear();
        loadUsers();
        userAdapter.notifyDataSetChanged();
    }
}

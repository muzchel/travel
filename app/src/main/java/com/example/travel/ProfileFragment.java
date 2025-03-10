package com.example.travel;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import database.DatabaseHelper;
import database.User;

public class ProfileFragment extends Fragment {

    private TextView textViewName, textViewLastName, textViewUsername, textViewEmail;
    private DatabaseHelper databaseHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false); // Используем XML из Activity

        // Инициализация UI
        textViewName = view.findViewById(R.id.textViewName);
        textViewLastName = view.findViewById(R.id.textViewLastName);
        textViewUsername = view.findViewById(R.id.textViewUsername);
        textViewEmail = view.findViewById(R.id.textViewEmail);


        databaseHelper = new DatabaseHelper(requireContext());

        // Загружаем данные пользователя
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String firstName = sharedPreferences.getString("firstName", "Неизвестно");
        String lastName = sharedPreferences.getString("lastName", "Неизвестно");
        String username = sharedPreferences.getString("username", "Неизвестно");
        String email = sharedPreferences.getString("email", null);

        textViewName.setText("Имя: " + firstName);
        textViewLastName.setText("Фамилия: " + lastName);
        textViewUsername.setText("Логин: " + username);
        textViewEmail.setText("Email: " + (email != null ? email : "Неизвестно"));

        if (email != null) {
            loadUserData(email);
        }

        // Переход в настройки
        return view;
    }

    private void loadUserData(String email) {
        User user = databaseHelper.getUserInfo(email);
        if (user != null) {
            textViewName.setText("Имя: " + user.getFirstName());
            textViewLastName.setText("Фамилия: " + user.getLastName());
            textViewUsername.setText("Логин: " + user.getUsername());
            textViewEmail.setText("Email: " + user.getEmail());

            SharedPreferences sharedPreferences = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("firstName", user.getFirstName());
            editor.putString("lastName", user.getLastName());
            editor.putString("username", user.getUsername());
            editor.putString("email", user.getEmail());
            editor.apply();
        }
    }

    private void replaceFragment(Fragment fragment) {
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }
}

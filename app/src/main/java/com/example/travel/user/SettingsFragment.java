package com.example.travel.user;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.example.travel.NotificationWorker;
import com.example.travel.R;

import java.util.concurrent.TimeUnit;

public class SettingsFragment extends Fragment {

    private static final String PREFS_NAME = "AppPrefs";
    private static final String NOTIFICATIONS_ENABLED = "notifications_enabled";
    private static final String DEVELOPER_EMAIL = "sokolnik2281441@gmail.com";

    private Switch switchNotifications;

    private Button buttonLogout;

    public SettingsFragment() {
        // Пустой конструктор (обязательно для фрагментов)
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        buttonLogout = view.findViewById(R.id.buttonLogout);

        // Обработчик кнопки "Выйти"
        buttonLogout.setOnClickListener(v -> logout());


        // Кнопка "Оставить отзыв"
        Button buttonSendFeedback = view.findViewById(R.id.buttonSendFeedback);
        buttonSendFeedback.setOnClickListener(v -> sendFeedbackViaGmail());

        // Инициализация Switch для уведомлений
        switchNotifications = view.findViewById(R.id.switchNotifications);

        // Загружаем состояние переключателя из SharedPreferences
        SharedPreferences prefs = requireActivity().getSharedPreferences(PREFS_NAME, getActivity().MODE_PRIVATE);
        boolean isEnabled = prefs.getBoolean(NOTIFICATIONS_ENABLED, true);
        switchNotifications.setChecked(isEnabled);

        // Обработчик переключателя
        switchNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(NOTIFICATIONS_ENABLED, isChecked);
            editor.apply();

            if (isChecked) {
                enableNotifications();
            } else {
                disableNotifications();
            }
        });
    }

    private void logout() {
        // Очистить все данные о пользователе (например, удалить токены или очистить сессии)
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);  // Чтобы вернуться на экран входа и не было возможности вернуться в SettingsFragment
        startActivity(intent);
        getActivity().finish(); // Закрыть текущую Activity
    }

    private void enableNotifications() {
        PeriodicWorkRequest notificationWork =
                new PeriodicWorkRequest.Builder(NotificationWorker.class, 2, TimeUnit.HOURS)
                        .build();

        WorkManager.getInstance(requireContext())
                .enqueueUniquePeriodicWork("notificationWork", ExistingPeriodicWorkPolicy.REPLACE, notificationWork);
    }

    private void disableNotifications() {
        WorkManager.getInstance(requireContext()).cancelUniqueWork("notificationWork");
    }

    private void sendFeedbackViaGmail() {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:" + DEVELOPER_EMAIL));
        intent.putExtra(Intent.EXTRA_SUBJECT, "Отзыв о приложении Travel");
        intent.putExtra(Intent.EXTRA_TEXT, "Здравствуйте! Хочу оставить отзыв...");

        try {
            startActivity(Intent.createChooser(intent, "Выберите почтовое приложение"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
package com.example.travel;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class TourDetailFragment extends Fragment {

    private TextView textViewTourName;
    private TextView textViewDescription;
    private TextView textViewLocation;
    private TextView textViewPrice;
    private TextView textViewContact;

    private String tourName;
    private String description;
    private String location;
    private double price;
    private String contact;

    // Статический метод для создания нового экземпляра фрагмента
    public static TourDetailFragment newInstance(String name, String description, String location, double price, String contact) {
        TourDetailFragment fragment = new TourDetailFragment();
        Bundle args = new Bundle();
        args.putString("name", name);
        args.putString("description", description);
        args.putString("location", location);
        args.putDouble("price", price);
        args.putString("contact", contact);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tour_detail, container, false);

        // Инициализация TextView
        textViewTourName = view.findViewById(R.id.textViewTourName);
        textViewDescription = view.findViewById(R.id.textViewDescription);
        textViewLocation = view.findViewById(R.id.textViewLocation);
        textViewPrice = view.findViewById(R.id.textViewPrice);
        textViewContact = view.findViewById(R.id.textViewContact);

        // Получаем переданные данные из Bundle
        if (getArguments() != null) {
            tourName = getArguments().getString("name");
            description = getArguments().getString("description");
            location = getArguments().getString("location");
            price = getArguments().getDouble("price");
            contact = getArguments().getString("contact");

            // Отображаем данные
            textViewTourName.setText(tourName);
            textViewDescription.setText(description);
            textViewLocation.setText(location);
            textViewPrice.setText(String.valueOf(price));
            textViewContact.setText(contact);
        }

        return view;
    }
}

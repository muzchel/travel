package Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travel.R;
import database.Tour;

import java.util.List;

public class TourAdapter extends RecyclerView.Adapter<TourAdapter.TourViewHolder> {

    private Context context;
    private List<Tour> tours;
    private OnItemClickListener listener;

    // Конструктор
    public TourAdapter(Context context, List<Tour> tours, OnItemClickListener listener) {
        this.context = context;
        this.tours = tours;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TourViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Создаем элемент для списка (view) из XML
        View view = LayoutInflater.from(context).inflate(R.layout.item_tour, parent, false);
        return new TourViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TourViewHolder holder, int position) {
        Tour tour = tours.get(position);
        holder.bind(tour);
    }

    @Override
    public int getItemCount() {
        return tours.size();
    }

    // ViewHolder для каждого элемента в списке
    public class TourViewHolder extends RecyclerView.ViewHolder {

        TextView textViewTourName;
        TextView textViewTourDescription;

        public TourViewHolder(View itemView) {
            super(itemView);

            // Инициализация TextView с правильными ID
            textViewTourName = itemView.findViewById(R.id.textViewTourName);  // Убедитесь, что такие ID есть в вашем layout
            textViewTourDescription = itemView.findViewById(R.id.textViewTourDescription);

            // Обработчик кликов на элемент
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onItemClick(tours.get(position));
                }
            });

            // Обработчик долгого клика (для удаления)
            itemView.setOnLongClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onItemLongClick(tours.get(position));
                }
                return true;  // Возвращаем true, чтобы событие не прошло дальше
            });
        }

        // Метод связывания данных с элементом
        public void bind(Tour tour) {
            if (textViewTourName!= null && textViewTourDescription != null) {
                textViewTourName.setText(tour.getName());
                textViewTourDescription.setText(tour.getDescription());
            } else {
                // Если данные пустые, устанавливаем текст по умолчанию
                textViewTourName.setText("Без названия");
                textViewTourDescription.setText("Описание отсутствует");
            }
        }
    }

    // Интерфейс для кликов по элементам
    public interface OnItemClickListener {
        void onItemClick(Tour tour);
        void onItemLongClick(Tour tour);
    }
}

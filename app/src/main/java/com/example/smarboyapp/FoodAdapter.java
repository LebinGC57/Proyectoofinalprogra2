package com.example.smarboyapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smarboyapp.database.FavoritesManager;

import java.util.List;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.FoodViewHolder> {

    private List<FoodItem> foodList;
    private OnItemClickListener listener;
    private FavoritesManager favoritesManager;

    public interface OnItemClickListener {
        void onItemClick(FoodItem foodItem);
    }

    public FoodAdapter(List<FoodItem> foodList, OnItemClickListener listener) {
        this.foodList = foodList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_food, parent, false);
        return new FoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int position) {
        FoodItem item = foodList.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }

    class FoodViewHolder extends RecyclerView.ViewHolder {
        private TextView tvFoodIcon, tvFoodName, tvFoodPrice, tvFoodRating, tvFoodDistance;
        private ImageButton btnFavorite;

        public FoodViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFoodIcon = itemView.findViewById(R.id.tvFoodIcon);
            tvFoodName = itemView.findViewById(R.id.tvFoodName);
            tvFoodPrice = itemView.findViewById(R.id.tvFoodPrice);
            tvFoodRating = itemView.findViewById(R.id.tvFoodRating);
            tvFoodDistance = itemView.findViewById(R.id.tvFoodDistance);
            btnFavorite = itemView.findViewById(R.id.btnFavorite);

            favoritesManager = new FavoritesManager(itemView.getContext());

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(foodList.get(position));
                    }
                }
            });
        }

        public void bind(FoodItem item) {
            tvFoodIcon.setText(item.getIcon());
            tvFoodName.setText(item.getName());
            tvFoodPrice.setText(item.getPrice());
            tvFoodRating.setText("★★★★★ (" + item.getRating() + ")");
            tvFoodDistance.setText("📍 " + item.getDistance());

            // Configurar el botón de favoritos
            updateFavoriteButton(item);

            btnFavorite.setOnClickListener(v -> {
                if (favoritesManager.isFavorite(item.getName())) {
                    favoritesManager.removeFromFavorites(item);
                    Toast.makeText(v.getContext(), "Eliminado de favoritos ❤️", Toast.LENGTH_SHORT).show();
                } else {
                    favoritesManager.addToFavorites(item);
                    Toast.makeText(v.getContext(), "Agregado a favoritos ❤️", Toast.LENGTH_SHORT).show();
                }
                updateFavoriteButton(item);
            });
        }

        private void updateFavoriteButton(FoodItem item) {
            if (favoritesManager.isFavorite(item.getName())) {
                btnFavorite.setImageResource(android.R.drawable.btn_star_big_on);
            } else {
                btnFavorite.setImageResource(android.R.drawable.btn_star_big_off);
            }
        }
    }
}

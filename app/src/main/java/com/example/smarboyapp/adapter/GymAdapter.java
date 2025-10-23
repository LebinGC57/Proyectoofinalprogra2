package com.example.smarboyapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smarboyapp.R;
import com.example.smarboyapp.model.GymItem;

import java.util.List;

public class GymAdapter extends RecyclerView.Adapter<GymAdapter.GymViewHolder> {

    private List<GymItem> gymList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(GymItem gymItem);
    }

    public GymAdapter(List<GymItem> gymList, OnItemClickListener listener) {
        this.gymList = gymList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public GymViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_gym, parent, false);
        return new GymViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GymViewHolder holder, int position) {
        GymItem item = gymList.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return gymList.size();
    }

    class GymViewHolder extends RecyclerView.ViewHolder {
        private TextView tvGymIcon, tvGymName, tvGymPrice, tvGymRating, tvGymDistance, tvGymType;

        public GymViewHolder(@NonNull View itemView) {
            super(itemView);
            tvGymIcon = itemView.findViewById(R.id.tvGymIcon);
            tvGymName = itemView.findViewById(R.id.tvGymName);
            tvGymPrice = itemView.findViewById(R.id.tvGymPrice);
            tvGymRating = itemView.findViewById(R.id.tvGymRating);
            tvGymDistance = itemView.findViewById(R.id.tvGymDistance);
            tvGymType = itemView.findViewById(R.id.tvGymType);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(gymList.get(position));
                    }
                }
            });
        }

        public void bind(GymItem item) {
            tvGymIcon.setText(item.getIcon());
            tvGymName.setText(item.getName());
            tvGymPrice.setText(item.getPrice());
            tvGymRating.setText("★★★★★ (" + item.getRating() + ")");
            tvGymDistance.setText("📍 " + item.getDistance());

            // Mostrar tipo de servicio con icono
            String typeIcon = getTypeIcon(item.getType());
            tvGymType.setText(typeIcon + " " + getTypeDisplayName(item.getType()));
        }

        private String getTypeIcon(String type) {
            switch (type) {
                case "membership": return "💳";
                case "class": return "👥";
                case "supplement": return "🥤";
                case "equipment": return "⚙️";
                default: return "💼";
            }
        }

        private String getTypeDisplayName(String type) {
            switch (type) {
                case "membership": return "Membresía";
                case "class": return "Clase";
                case "supplement": return "Suplemento";
                case "equipment": return "Equipo";
                default: return "Servicio";
            }
        }
    }
}

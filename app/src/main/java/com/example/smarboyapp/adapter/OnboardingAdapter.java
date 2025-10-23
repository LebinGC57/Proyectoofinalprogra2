package com.example.smarboyapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smarboyapp.R;

public class OnboardingAdapter extends RecyclerView.Adapter<OnboardingAdapter.OnboardingViewHolder> {

    private final String[] titles = {
            "🍽️ Recomendaciones Personalizadas",
            "⭐ Califica y Guarda",
            "🤖 Asistente Inteligente",
            "🎯 Alcanza tus Metas"
    };

    private final String[] descriptions = {
            "Descubre comida y gimnasios perfectos para ti basados en tus preferencias, presupuesto y objetivos de salud.",
            "Califica tus experiencias, guarda tus favoritos y ayuda a otros usuarios con tus reseñas.",
            "Chatea con nuestro asistente IA para obtener consejos personalizados sobre nutrición y ejercicio.",
            "Establece metas diarias, semanales y mensuales. Desbloquea logros mientras mejoras tu estilo de vida."
    };

    private final String[] icons = {"🍽️", "⭐", "🤖", "🎯"};

    private android.content.Context context;

    public OnboardingAdapter(android.content.Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public OnboardingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_onboarding, parent, false);
        return new OnboardingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OnboardingViewHolder holder, int position) {
        holder.tvIcon.setText(icons[position]);
        holder.tvTitle.setText(titles[position]);
        holder.tvDescription.setText(descriptions[position]);
    }

    @Override
    public int getItemCount() {
        return titles.length;
    }

    static class OnboardingViewHolder extends RecyclerView.ViewHolder {
        TextView tvIcon, tvTitle, tvDescription;

        public OnboardingViewHolder(@NonNull View itemView) {
            super(itemView);
            tvIcon = itemView.findViewById(R.id.tvIcon);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
        }
    }
}


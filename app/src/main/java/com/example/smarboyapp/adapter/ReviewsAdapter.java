package com.example.smarboyapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smarboyapp.R;
import com.example.smarboyapp.model.Review;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewViewHolder> {

    private List<Review> reviewsList;

    public ReviewsAdapter(List<Review> reviewsList) {
        this.reviewsList = reviewsList;
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_review, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        Review review = reviewsList.get(position);
        holder.bind(review);
    }

    @Override
    public int getItemCount() {
        return reviewsList.size();
    }

    static class ReviewViewHolder extends RecyclerView.ViewHolder {
        private TextView tvUserName, tvRating, tvDate, tvComment, tvHelpful;

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvRating = itemView.findViewById(R.id.tvRating);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvComment = itemView.findViewById(R.id.tvComment);
            tvHelpful = itemView.findViewById(R.id.tvHelpful);
        }

        public void bind(Review review) {
            tvUserName.setText(review.getUserName());
            tvRating.setText(getStars(review.getRating()) + " " + review.getRating());
            tvComment.setText(review.getComment());

            // Formatear fecha
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
            tvDate.setText(sdf.format(new Date(review.getTimestamp())));

            tvHelpful.setText("👍 " + review.getHelpfulCount() + " útil");
        }

        private String getStars(float rating) {
            int stars = Math.round(rating);
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < stars; i++) {
                sb.append("⭐");
            }
            return sb.toString();
        }
    }
}


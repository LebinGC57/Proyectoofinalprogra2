package com.example.smarboyapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smarboyapp.adapter.ReviewsAdapter;
import com.example.smarboyapp.database.ReviewsManager;
import com.example.smarboyapp.model.Review;

import java.util.List;

public class ReviewsActivity extends AppCompatActivity {

    private TextView tvItemName, tvAverageRating, tvTotalReviews;
    private RatingBar ratingBarUser;
    private EditText etReviewText;
    private Button btnSubmitReview, btnBack;
    private RecyclerView recyclerViewReviews;
    private ReviewsAdapter adapter;
    private ReviewsManager reviewsManager;
    private String itemId, itemName;
    private List<Review> reviewsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviews);

        reviewsManager = new ReviewsManager(this);

        // Obtener datos del item
        itemId = getIntent().getStringExtra("item_id");
        itemName = getIntent().getStringExtra("item_name");

        initializeViews();
        loadReviews();
        setupListeners();
    }

    private void initializeViews() {
        tvItemName = findViewById(R.id.tvItemName);
        tvAverageRating = findViewById(R.id.tvAverageRating);
        tvTotalReviews = findViewById(R.id.tvTotalReviews);
        ratingBarUser = findViewById(R.id.ratingBarUser);
        etReviewText = findViewById(R.id.etReviewText);
        btnSubmitReview = findViewById(R.id.btnSubmitReview);
        btnBack = findViewById(R.id.btnBack);
        recyclerViewReviews = findViewById(R.id.recyclerViewReviews);

        tvItemName.setText(itemName);

        reviewsList = reviewsManager.getReviews(itemId);
        adapter = new ReviewsAdapter(reviewsList);
        recyclerViewReviews.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewReviews.setAdapter(adapter);
    }

    private void loadReviews() {
        reviewsList.clear();
        reviewsList.addAll(reviewsManager.getReviews(itemId));
        adapter.notifyDataSetChanged();

        // Calcular promedio
        float average = reviewsManager.getAverageRating(itemId);
        int total = reviewsList.size();

        tvAverageRating.setText(String.format("%.1f ⭐", average));
        tvTotalReviews.setText(total + " reseñas");
    }

    private void setupListeners() {
        btnSubmitReview.setOnClickListener(v -> submitReview());
        btnBack.setOnClickListener(v -> finish());
    }

    private void submitReview() {
        float rating = ratingBarUser.getRating();
        String reviewText = etReviewText.getText().toString().trim();

        if (rating == 0) {
            Toast.makeText(this, "Por favor califica con estrellas", Toast.LENGTH_SHORT).show();
            return;
        }

        if (reviewText.isEmpty()) {
            Toast.makeText(this, "Por favor escribe tu opinión", Toast.LENGTH_SHORT).show();
            return;
        }

        // Guardar review
        Review review = new Review(
            itemId,
            itemName,
            "Usuario", // Aquí puedes poner el nombre real del usuario
            rating,
            reviewText,
            System.currentTimeMillis()
        );

        reviewsManager.addReview(review);

        Toast.makeText(this, "¡Reseña publicada! ⭐", Toast.LENGTH_SHORT).show();

        // Limpiar formulario
        ratingBarUser.setRating(0);
        etReviewText.setText("");

        // Recargar reviews
        loadReviews();
    }
}

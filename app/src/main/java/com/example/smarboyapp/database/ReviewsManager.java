package com.example.smarboyapp.database;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.smarboyapp.model.Review;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ReviewsManager {
    private static final String PREF_NAME = "SmartBoyReviews";
    private static final String KEY_REVIEWS = "reviews_list";

    private SharedPreferences preferences;

    public ReviewsManager(Context context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void addReview(Review review) {
        try {
            JSONArray reviews = getReviewsArray();

            JSONObject reviewObj = new JSONObject();
            reviewObj.put("itemId", review.getItemId());
            reviewObj.put("itemName", review.getItemName());
            reviewObj.put("userName", review.getUserName());
            reviewObj.put("rating", review.getRating());
            reviewObj.put("comment", review.getComment());
            reviewObj.put("timestamp", review.getTimestamp());
            reviewObj.put("helpfulCount", review.getHelpfulCount());

            reviews.put(reviewObj);

            preferences.edit().putString(KEY_REVIEWS, reviews.toString()).apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public List<Review> getReviews(String itemId) {
        List<Review> reviewsList = new ArrayList<>();
        try {
            JSONArray reviews = getReviewsArray();

            for (int i = 0; i < reviews.length(); i++) {
                JSONObject obj = reviews.getJSONObject(i);

                if (obj.getString("itemId").equals(itemId)) {
                    Review review = new Review(
                        obj.getString("itemId"),
                        obj.getString("itemName"),
                        obj.getString("userName"),
                        (float) obj.getDouble("rating"),
                        obj.getString("comment"),
                        obj.getLong("timestamp")
                    );
                    review.setHelpfulCount(obj.getInt("helpfulCount"));
                    reviewsList.add(review);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return reviewsList;
    }

    public float getAverageRating(String itemId) {
        List<Review> reviews = getReviews(itemId);
        if (reviews.isEmpty()) return 0;

        float total = 0;
        for (Review review : reviews) {
            total += review.getRating();
        }
        return total / reviews.size();
    }

    private JSONArray getReviewsArray() {
        String reviewsStr = preferences.getString(KEY_REVIEWS, "[]");
        try {
            return new JSONArray(reviewsStr);
        } catch (JSONException e) {
            e.printStackTrace();
            return new JSONArray();
        }
    }
}


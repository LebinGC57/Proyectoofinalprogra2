package com.example.smarboyapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.smarboyapp.adapter.OnboardingAdapter;
import com.example.smarboyapp.database.UserPreferencesManager;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class OnboardingActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private Button btnNext, btnSkip;
    private TextView tvGetStarted;
    private UserPreferencesManager preferencesManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        preferencesManager = new UserPreferencesManager(this);

        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);
        btnNext = findViewById(R.id.btnNext);
        btnSkip = findViewById(R.id.btnSkip);
        tvGetStarted = findViewById(R.id.tvGetStarted);

        setupViewPager();
        setupButtons();
    }

    private void setupViewPager() {
        OnboardingAdapter adapter = new OnboardingAdapter(this);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {}).attach();

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                if (position == 3) { // Última página
                    btnNext.setVisibility(View.GONE);
                    btnSkip.setVisibility(View.GONE);
                    tvGetStarted.setVisibility(View.VISIBLE);
                } else {
                    btnNext.setVisibility(View.VISIBLE);
                    btnSkip.setVisibility(View.VISIBLE);
                    tvGetStarted.setVisibility(View.GONE);
                }
            }
        });
    }

    private void setupButtons() {
        btnNext.setOnClickListener(v -> {
            int currentItem = viewPager.getCurrentItem();
            if (currentItem < 3) {
                viewPager.setCurrentItem(currentItem + 1);
            }
        });

        btnSkip.setOnClickListener(v -> finishOnboarding());

        tvGetStarted.setOnClickListener(v -> finishOnboarding());
    }

    private void finishOnboarding() {
        preferencesManager.setOnboardingCompleted(true);
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}


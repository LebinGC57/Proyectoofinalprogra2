package com.example.smarboyapp;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smarboyapp.database.UserPreferencesManager;
import com.example.smarboyapp.utils.N8nSyncManager;

public class EditProfileActivity extends AppCompatActivity {

    private EditText etName, etAge, etWeight, etHeight, etBudget;
    private Button btnSave, btnBack;
    private UserPreferencesManager preferencesManager;
    private N8nSyncManager syncManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        preferencesManager = new UserPreferencesManager(this);
        syncManager = new N8nSyncManager(this);

        // Initialize views
        etName = findViewById(R.id.etName);
        etAge = findViewById(R.id.etAge);
        etWeight = findViewById(R.id.etWeight);
        etHeight = findViewById(R.id.etHeight);
        etBudget = findViewById(R.id.etBudget);
        btnSave = findViewById(R.id.btnSave);
        btnBack = findViewById(R.id.btnBack);

        // Cargar datos existentes
        loadExistingData();

        // Set up text watchers for validation
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateFields();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };

        etName.addTextChangedListener(textWatcher);
        etAge.addTextChangedListener(textWatcher);
        etWeight.addTextChangedListener(textWatcher);
        etHeight.addTextChangedListener(textWatcher);
        etBudget.addTextChangedListener(textWatcher);

        btnSave.setOnClickListener(v -> saveProfile());
        btnBack.setOnClickListener(v -> finish());
    }

    private void loadExistingData() {
        etName.setText(preferencesManager.getUserName());

        int age = preferencesManager.getUserAge();
        if (age > 0) etAge.setText(String.valueOf(age));

        double weight = preferencesManager.getUserWeight();
        if (weight > 0) etWeight.setText(String.valueOf(weight));

        double height = preferencesManager.getUserHeight();
        if (height > 0) etHeight.setText(String.valueOf(height));

        double budget = preferencesManager.getUserBudget();
        if (budget > 0) etBudget.setText(String.valueOf(budget));
    }

    private void validateFields() {
        String name = etName.getText().toString().trim();
        String ageStr = etAge.getText().toString().trim();
        String weightStr = etWeight.getText().toString().trim();
        String heightStr = etHeight.getText().toString().trim();
        String budgetStr = etBudget.getText().toString().trim();

        boolean isValid = !name.isEmpty() && !ageStr.isEmpty() && !weightStr.isEmpty()
                         && !heightStr.isEmpty() && !budgetStr.isEmpty();

        btnSave.setEnabled(isValid);
        btnSave.setAlpha(isValid ? 1.0f : 0.5f);
    }

    private void saveProfile() {
        // Validar campos
        String name = etName.getText().toString().trim();
        String ageStr = etAge.getText().toString().trim();
        String weightStr = etWeight.getText().toString().trim();
        String heightStr = etHeight.getText().toString().trim();
        String budgetStr = etBudget.getText().toString().trim();

        if (name.isEmpty()) {
            Toast.makeText(this, "Por favor ingresa tu nombre", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int age = Integer.parseInt(ageStr);
            double weight = Double.parseDouble(weightStr);
            double height = Double.parseDouble(heightStr);
            double budget = Double.parseDouble(budgetStr);

            // Guardar todos los datos usando el método correcto
            preferencesManager.saveUserProfile(name, age, weight, height, budget);

            // Sincronizar con n8n webhook
            syncManager.syncProfileUpdate(name, age, weight, height);

            Toast.makeText(this, "✅ Perfil actualizado y sincronizado", Toast.LENGTH_SHORT).show();
            finish();

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Por favor verifica que todos los campos sean válidos", Toast.LENGTH_SHORT).show();
        }
    }
}

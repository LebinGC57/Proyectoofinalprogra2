package com.example.smarboyapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smarboyapp.database.UserPreferencesManager;

public class UserProfileActivity extends AppCompatActivity {

    private EditText etName, etAge, etWeight, etHeight, etBudget;
    private Button btnNext;
    private UserPreferencesManager preferencesManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferencesManager = new UserPreferencesManager(this);

        // Si el perfil ya está completado, ir directamente a PreferencesActivity
        if (preferencesManager.isProfileCompleted()) {
            startActivity(new Intent(this, PreferencesActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_user_profile);

        // Initialize views
        etName = findViewById(R.id.etName);
        etAge = findViewById(R.id.etAge);
        etWeight = findViewById(R.id.etWeight);
        etHeight = findViewById(R.id.etHeight);
        etBudget = findViewById(R.id.etBudget);
        btnNext = findViewById(R.id.btnNext);

        // Cargar datos existentes si los hay
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

        btnNext.setOnClickListener(v -> saveProfileAndContinue());

        // Validar campos iniciales
        validateFields();
    }

    private void loadExistingData() {
        String name = preferencesManager.getUserName();
        if (!name.isEmpty()) {
            etName.setText(name);
        }

        int age = preferencesManager.getUserAge();
        if (age > 0) {
            etAge.setText(String.valueOf(age));
        }

        double weight = preferencesManager.getUserWeight();
        if (weight > 0) {
            etWeight.setText(String.valueOf(weight));
        }

        double height = preferencesManager.getUserHeight();
        if (height > 0) {
            etHeight.setText(String.valueOf(height));
        }

        double budget = preferencesManager.getUserBudget();
        if (budget > 0) {
            etBudget.setText(String.valueOf(budget));
        }
    }

    private void saveProfileAndContinue() {
        try {
            String name = etName.getText().toString().trim();
            int age = Integer.parseInt(etAge.getText().toString().trim());
            double weight = Double.parseDouble(etWeight.getText().toString().trim());
            double height = Double.parseDouble(etHeight.getText().toString().trim());
            double budget = Double.parseDouble(etBudget.getText().toString().trim());

            // Guardar perfil
            preferencesManager.saveUserProfile(name, age, weight, height, budget);

            Toast.makeText(this, "Perfil guardado exitosamente", Toast.LENGTH_SHORT).show();

            // Ir a PreferencesActivity para seleccionar preferencias de comida, gimnasio y presupuesto
            Intent intent = new Intent(UserProfileActivity.this, PreferencesActivity.class);
            startActivity(intent);
            finish(); // Evitar que el usuario regrese a esta pantalla

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Por favor, verifica que todos los campos numéricos sean válidos", Toast.LENGTH_LONG).show();
        }
    }

    private void validateFields() {
        boolean allFieldsFilled = !etName.getText().toString().trim().isEmpty() &&
                !etAge.getText().toString().trim().isEmpty() &&
                !etWeight.getText().toString().trim().isEmpty() &&
                !etHeight.getText().toString().trim().isEmpty() &&
                !etBudget.getText().toString().trim().isEmpty();

        btnNext.setEnabled(allFieldsFilled);
    }
}

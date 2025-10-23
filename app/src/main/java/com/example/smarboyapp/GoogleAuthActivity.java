package com.example.smarboyapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smarboyapp.auth.GoogleAuthManager;
import com.example.smarboyapp.database.UserPreferencesManager;
import com.example.smarboyapp.utils.EmailSender;
import com.example.smarboyapp.utils.N8nSyncManager;

public class GoogleAuthActivity extends AppCompatActivity {

    private EditText etEmail, etPassword, etConfirmPassword;
    private Button btnAuth, btnBack;
    private TextView tvTitle, tvToggleMode, tvLoadingMessage;
    private ProgressBar progressBar;

    private GoogleAuthManager authManager;
    private UserPreferencesManager preferencesManager;
    private N8nSyncManager syncManager;

    private boolean isLoginMode = true; // true = login, false = registro

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_auth);

        initializeViews();
        setupManagers();
        setupListeners();
        updateUIForMode();
    }

    private void initializeViews() {
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnAuth = findViewById(R.id.btnAuth);
        btnBack = findViewById(R.id.btnBack);
        tvTitle = findViewById(R.id.tvTitle);
        tvToggleMode = findViewById(R.id.tvToggleMode);
        tvLoadingMessage = findViewById(R.id.tvLoadingMessage);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupManagers() {
        authManager = new GoogleAuthManager(this);
        preferencesManager = new UserPreferencesManager(this);
        syncManager = new N8nSyncManager(this);
    }

    private void setupListeners() {
        btnAuth.setOnClickListener(v -> handleAuthentication());
        btnBack.setOnClickListener(v -> finish());
        tvToggleMode.setOnClickListener(v -> toggleMode());
    }

    private void toggleMode() {
        isLoginMode = !isLoginMode;
        updateUIForMode();
    }

    private void updateUIForMode() {
        if (isLoginMode) {
            tvTitle.setText("📧 INICIAR SESIÓN CON GMAIL");
            btnAuth.setText("INICIAR SESIÓN");
            etConfirmPassword.setVisibility(View.GONE);
            tvToggleMode.setText("¿No tienes cuenta? Regístrate aquí");
        } else {
            tvTitle.setText("📧 REGISTRARSE CON GMAIL");
            btnAuth.setText("REGISTRARSE");
            etConfirmPassword.setVisibility(View.VISIBLE);
            tvToggleMode.setText("¿Ya tienes cuenta? Inicia sesión aquí");
        }
    }

    private void handleAuthentication() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isLoginMode) {
            String confirmPassword = etConfirmPassword.getText().toString().trim();
            if (!password.equals(confirmPassword)) {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        showLoading(true);

        // Usar el método correcto: signInWithGmail
        authManager.signInWithGmail(email, password, new GoogleAuthManager.AuthCallback() {
            @Override
            public void onSuccess(String userEmail, String userName) {
                runOnUiThread(() -> {
                    showLoading(false);

                    // Sincronizar login con n8n
                    syncManager.syncUserLogin(userEmail);

                    String message = isLoginMode ?
                            "¡Bienvenido " + userName + "! 🔄 Sincronizado" :
                            "✅ Cuenta creada y sincronizada";

                    Toast.makeText(GoogleAuthActivity.this, message, Toast.LENGTH_SHORT).show();
                    finish();
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    showLoading(false);
                    Toast.makeText(GoogleAuthActivity.this,
                            "Error: " + error, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void sendWelcomeEmail(String email, String name) {
        tvLoadingMessage.setText("📧 Enviando correo de bienvenida...");

        EmailSender.sendWelcomeEmail(email, name, new EmailSender.EmailCallback() {
            @Override
            public void onSuccess() {
                runOnUiThread(() -> {
                    showLoading(false);
                    Toast.makeText(GoogleAuthActivity.this,
                            "¡Registro exitoso! 🎉\n✅ Correo de bienvenida enviado a " + email,
                            Toast.LENGTH_LONG).show();
                    completeAuthentication(email, name);
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    showLoading(false);
                    // Aunque falle el correo, el registro fue exitoso
                    Toast.makeText(GoogleAuthActivity.this,
                            "¡Registro exitoso! 🎉\n⚠️ No se pudo enviar el correo: " + error,
                            Toast.LENGTH_LONG).show();
                    completeAuthentication(email, name);
                });
            }
        });
    }

    private void completeAuthentication(String email, String name) {
        // Actualizar el perfil del usuario con el nombre de Gmail
        updateUserProfileWithGmail(name, email);

        Toast.makeText(this, "¡Bienvenido " + name + "! 🎉", Toast.LENGTH_SHORT).show();

        // Regresar a la actividad anterior con resultado exitoso
        setResult(RESULT_OK);
        finish();
    }

    private void updateUserProfileWithGmail(String name, String email) {
        // Si el usuario ya tiene un perfil, actualizar solo el nombre
        if (preferencesManager.isProfileCompleted()) {
            // Obtener datos existentes
            int age = preferencesManager.getUserAge();
            double weight = preferencesManager.getUserWeight();
            double height = preferencesManager.getUserHeight();
            double budget = preferencesManager.getUserBudget();

            // Actualizar con el nombre de Gmail
            preferencesManager.saveUserProfile(name, age, weight, height, budget);
        } else {
            // Si no hay perfil, crear uno básico con el nombre de Gmail
            preferencesManager.saveUserProfile(name, 0, 0, 0, 0);
        }
    }

    private void showLoading(boolean show) {
        if (show) {
            progressBar.setVisibility(View.VISIBLE);
            tvLoadingMessage.setVisibility(View.VISIBLE);
            btnAuth.setEnabled(false);
            btnBack.setEnabled(false);
        } else {
            progressBar.setVisibility(View.GONE);
            tvLoadingMessage.setVisibility(View.GONE);
            btnAuth.setEnabled(true);
            btnBack.setEnabled(true);
        }
    }
}

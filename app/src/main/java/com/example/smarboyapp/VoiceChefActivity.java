package com.example.smarboyapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class VoiceChefActivity extends AppCompatActivity implements RecognitionListener, TextToSpeech.OnInitListener {

    private static final String TAG = "VoiceChefActivity";
    private static final int PERMISSION_REQUEST_CODE = 300;
    
    private Button btnStartListening, btnStopListening, btnBack;
    private TextView tvStatus, tvInstructions, tvResponse;
    private LinearLayout llResponseContainer;
    private SpeechRecognizer speechRecognizer;
    private TextToSpeech textToSpeech;
    private boolean isListening = false;
    private boolean isTTSInitialized = false;
    private String currentIngredients = "";
    private int currentRecipeIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_voice_chef);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeViews();
        setupClickListeners();
        initializeSpeechRecognition();
        initializeTextToSpeech();
        
        if (checkMicrophonePermission()) {
            updateUIForReady();
        } else {
            requestMicrophonePermission();
        }
    }

    private void initializeViews() {
        btnStartListening = findViewById(R.id.btnStartListening);
        btnStopListening = findViewById(R.id.btnStopListening);
        btnBack = findViewById(R.id.btnBack);
        tvStatus = findViewById(R.id.tvStatus);
        tvInstructions = findViewById(R.id.tvInstructions);
        tvResponse = findViewById(R.id.tvResponse);
        llResponseContainer = findViewById(R.id.llResponseContainer);
    }

    private void setupClickListeners() {
        btnStartListening.setOnClickListener(v -> {
            if (checkMicrophonePermission()) {
                startListening();
            } else {
                requestMicrophonePermission();
            }
        });

        btnStopListening.setOnClickListener(v -> {
            stopListening();
        });

        btnBack.setOnClickListener(v -> {
            finish();
        });
    }

    private void initializeSpeechRecognition() {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        speechRecognizer.setRecognitionListener(this);
    }

    private void initializeTextToSpeech() {
        textToSpeech = new TextToSpeech(this, this);
    }

    private boolean checkMicrophonePermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestMicrophonePermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSION_REQUEST_CODE);
    }

    private void updateUIForReady() {
        tvStatus.setText("🎤 Chef Consciente está listo");
        tvInstructions.setText("Di: 'Chef Consciente, tengo estos ingredientes...' o 'Chef, ¿Qué cocino con...?'");
        btnStartListening.setVisibility(View.VISIBLE);
        btnStopListening.setVisibility(View.GONE);
    }

    private void startListening() {
        if (!isListening) {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Dime tus ingredientes...");
            intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
            intent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);

            try {
                speechRecognizer.startListening(intent);
                isListening = true;
                updateUIForListening();
                speak("Escuchando tus ingredientes...");
            } catch (Exception e) {
                Log.e(TAG, "Error al iniciar reconocimiento: " + e.getMessage());
                Toast.makeText(this, "Error al iniciar reconocimiento de voz", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void stopListening() {
        if (isListening && speechRecognizer != null) {
            speechRecognizer.stopListening();
            isListening = false;
            updateUIForReady();
        }
    }

    private void updateUIForListening() {
        tvStatus.setText("🎤 Escuchando...");
        tvInstructions.setText("Habla claramente tus ingredientes");
        btnStartListening.setVisibility(View.GONE);
        btnStopListening.setVisibility(View.VISIBLE);
    }

    private void speak(String text) {
        if (isTTSInitialized && textToSpeech != null) {
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        }
    }

    private void processIngredients(String ingredientsText) {
        currentIngredients = ingredientsText.toLowerCase();
        tvResponse.setText("Ingredientes detectados: " + ingredientsText);
        
        // Enviar a N8N
        sendToN8NChefVoz(ingredientsText);
        
        // Generar receta principal
        Recipe mainRecipe = generateMainRecipe(currentIngredients);
        displayRecipe(mainRecipe, 1);
        
        // Respuesta de voz del Chef Consciente
        String voiceResponse = "Entendido. Con " + getKeyIngredients(mainRecipe.ingredients) + 
                            ", te sugiero preparar " + mainRecipe.name + ". " +
                            "Esta receta utiliza casi todo lo que tienes. ¿Te dicto ahora los pasos sencillos o prefieres otra opción?";
        
        speak(voiceResponse);
        
        // Mostrar opciones de voz
        showVoiceOptions();
    }

    private String getKeyIngredients(List<String> ingredients) {
        // Tomar los primeros 3-4 ingredientes más importantes
        int maxIngredients = Math.min(3, ingredients.size());
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < maxIngredients; i++) {
            if (i > 0) result.append(", ");
            result.append(ingredients.get(i));
        }
        return result.toString();
    }

    private void showVoiceOptions() {
        tvInstructions.setText("Di: 'Dame los pasos' o 'Otra opción'");
        btnStartListening.setText("🎤 Responder");
        btnStartListening.setVisibility(View.VISIBLE);
        btnStopListening.setVisibility(View.GONE);
    }

    private void processVoiceCommand(String command) {
        command = command.toLowerCase().trim();
        
        if (command.contains("dame los pasos") || command.contains("pasos")) {
            speakRecipeSteps();
        } else if (command.contains("otra opción") || command.contains("otra receta")) {
            generateAlternativeRecipe();
        } else if (command.contains("ingredientes") || command.contains("tengo")) {
            // Procesar nueva lista de ingredientes
            processIngredients(command);
        } else {
            speak("No entendí. Di 'dame los pasos' o 'otra opción'");
        }
    }

    private void speakRecipeSteps() {
        Recipe currentRecipe = getCurrentRecipe();
        if (currentRecipe != null) {
            StringBuilder steps = new StringBuilder("Pasos para " + currentRecipe.name + ": ");
            for (int i = 0; i < currentRecipe.steps.size(); i++) {
                steps.append("Paso ").append(i + 1).append(": ").append(currentRecipe.steps.get(i)).append(". ");
            }
            speak(steps.toString());
        }
    }

    private void generateAlternativeRecipe() {
        currentRecipeIndex++;
        Recipe alternativeRecipe = generateAlternativeRecipe(currentIngredients);
        displayRecipe(alternativeRecipe, currentRecipeIndex + 1);
        
        String voiceResponse = "Entendido. Con " + getKeyIngredients(alternativeRecipe.ingredients) + 
                            ", te sugiero preparar " + alternativeRecipe.name + ". " +
                            "Esta es otra opción que también utiliza tus ingredientes. ¿Te dicto los pasos o prefieres otra opción?";
        
        speak(voiceResponse);
        showVoiceOptions();
    }

    private Recipe getCurrentRecipe() {
        // Retornar la receta actual basada en el índice
        return generateMainRecipe(currentIngredients);
    }

    private Recipe generateMainRecipe(String ingredients) {
        Recipe recipe = new Recipe();
        
        // Análisis básico de ingredientes
        List<String> detectedIngredients = analyzeIngredients(ingredients);
        
        if (detectedIngredients.contains("pollo") || detectedIngredients.contains("carne")) {
            recipe.name = "Arroz salteado con pollo y vegetales";
            recipe.ingredients = Arrays.asList("Pollo", "Arroz", "Cebolla", "Ajo", "Pimiento");
            recipe.steps = Arrays.asList(
                "Corta el pollo en trozos pequeños",
                "Cocina el arroz según las instrucciones",
                "Sofríe la cebolla y el ajo en aceite",
                "Agrega el pollo y cocina hasta dorar",
                "Añade los vegetales y cocina 5 minutos",
                "Mezcla con el arroz y sirve"
            );
        } else if (detectedIngredients.contains("pasta")) {
            recipe.name = "Pasta con vegetales frescos";
            recipe.ingredients = Arrays.asList("Pasta", "Tomate", "Cebolla", "Ajo", "Queso");
            recipe.steps = Arrays.asList(
                "Hierve agua y cocina la pasta",
                "Sofríe cebolla y ajo en aceite",
                "Agrega tomate picado y cocina 5 minutos",
                "Mezcla con la pasta cocida",
                "Sirve con queso rallado"
            );
        } else {
            recipe.name = "Ensalada nutritiva completa";
            recipe.ingredients = Arrays.asList("Lechuga", "Tomate", "Cebolla", "Pepino", "Aceite");
            recipe.steps = Arrays.asList(
                "Lava y corta todos los vegetales",
                "Mezcla en un bowl grande",
                "Aliña con aceite, sal y pimienta",
                "Sirve inmediatamente"
            );
        }
        
        return recipe;
    }

    private Recipe generateAlternativeRecipe(String ingredients) {
        Recipe recipe = new Recipe();
        List<String> detectedIngredients = analyzeIngredients(ingredients);
        
        if (detectedIngredients.contains("huevo")) {
            recipe.name = "Tortilla de vegetales";
            recipe.ingredients = Arrays.asList("Huevo", "Cebolla", "Pimiento", "Tomate", "Queso");
            recipe.steps = Arrays.asList(
                "Bate los huevos con sal",
                "Sofríe los vegetales picados",
                "Vierte los huevos sobre los vegetales",
                "Cocina hasta que esté firme",
                "Sirve caliente"
            );
        } else {
            recipe.name = "Sopa de vegetales";
            recipe.ingredients = Arrays.asList("Vegetales", "Cebolla", "Ajo", "Sal", "Pimienta");
            recipe.steps = Arrays.asList(
                "Pica todos los vegetales",
                "Sofríe cebolla y ajo",
                "Agrega los vegetales y cocina 10 minutos",
                "Añade agua y hierve 20 minutos",
                "Sazona y sirve caliente"
            );
        }
        
        return recipe;
    }

    private List<String> analyzeIngredients(String ingredientsText) {
        List<String> commonIngredients = Arrays.asList(
            "pollo", "carne", "pescado", "huevo", "leche", "queso",
            "arroz", "pasta", "pan", "papa", "tomate", "cebolla",
            "ajo", "pimiento", "zanahoria", "lechuga", "pepino",
            "brócoli", "espinaca", "aceite", "sal", "pimienta"
        );
        
        List<String> detected = new ArrayList<>();
        for (String ingredient : commonIngredients) {
            if (ingredientsText.contains(ingredient)) {
                detected.add(ingredient);
            }
        }
        
        return detected;
    }

    private void displayRecipe(Recipe recipe, int recipeNumber) {
        llResponseContainer.removeAllViews();
        
        TextView recipeTitle = new TextView(this);
        recipeTitle.setText("Receta " + recipeNumber + ": " + recipe.name);
        recipeTitle.setTextSize(18);
        recipeTitle.setTextColor(getResources().getColor(android.R.color.black));
        recipeTitle.setTypeface(null, android.graphics.Typeface.BOLD);
        recipeTitle.setPadding(0, 0, 0, 8);
        
        TextView ingredientsText = new TextView(this);
        ingredientsText.setText("Ingredientes: " + String.join(", ", recipe.ingredients));
        ingredientsText.setTextSize(14);
        ingredientsText.setTextColor(getResources().getColor(android.R.color.black));
        ingredientsText.setPadding(0, 0, 0, 8);
        
        llResponseContainer.addView(recipeTitle);
        llResponseContainer.addView(ingredientsText);
    }

    // RecognitionListener methods
    @Override
    public void onReadyForSpeech(Bundle params) {
        Log.d(TAG, "Listo para escuchar");
    }

    @Override
    public void onBeginningOfSpeech() {
        Log.d(TAG, "Inicio de habla detectado");
    }

    @Override
    public void onRmsChanged(float rmsdB) {
        // Nivel de audio - opcional
    }

    @Override
    public void onBufferReceived(byte[] buffer) {
        // Buffer de audio - opcional
    }

    @Override
    public void onEndOfSpeech() {
        Log.d(TAG, "Fin de habla detectado");
    }

    @Override
    public void onError(int error) {
        Log.e(TAG, "Error en reconocimiento: " + error);
        isListening = false;
        updateUIForReady();
        
        String errorMessage = "Error en reconocimiento de voz";
        switch (error) {
            case SpeechRecognizer.ERROR_AUDIO:
                errorMessage = "Error de audio";
                break;
            case SpeechRecognizer.ERROR_CLIENT:
                errorMessage = "Error del cliente";
                break;
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                errorMessage = "Permisos insuficientes";
                break;
            case SpeechRecognizer.ERROR_NETWORK:
                errorMessage = "Error de red";
                break;
            case SpeechRecognizer.ERROR_NO_MATCH:
                errorMessage = "No se pudo reconocer el habla";
                break;
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                errorMessage = "Reconocedor ocupado";
                break;
            case SpeechRecognizer.ERROR_SERVER:
                errorMessage = "Error del servidor";
                break;
        }
        
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResults(Bundle results) {
        ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        if (matches != null && !matches.isEmpty()) {
            String recognizedText = matches.get(0);
            Log.d(TAG, "Texto reconocido: " + recognizedText);
            
            if (recognizedText.toLowerCase().contains("chef consciente") || 
                recognizedText.toLowerCase().contains("chef") ||
                recognizedText.toLowerCase().contains("ingredientes")) {
                processIngredients(recognizedText);
            } else {
                processVoiceCommand(recognizedText);
            }
        }
        
        isListening = false;
        updateUIForReady();
    }

    @Override
    public void onPartialResults(Bundle partialResults) {
        // Resultados parciales - opcional
    }

    @Override
    public void onEvent(int eventType, Bundle params) {
        // Eventos adicionales - opcional
    }

    // TextToSpeech.OnInitListener
    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = textToSpeech.setLanguage(Locale.getDefault());
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e(TAG, "Idioma no soportado");
            } else {
                isTTSInitialized = true;
                Log.d(TAG, "TTS inicializado correctamente");
            }
        } else {
            Log.e(TAG, "Error al inicializar TTS");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                updateUIForReady();
            } else {
                Toast.makeText(this, "Permiso de micrófono denegado", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (speechRecognizer != null) {
            speechRecognizer.destroy();
        }
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
    }

    private void sendToN8NChefVoz(String voiceData) {
        NetworkManager.sendChefVoz(this, voiceData, new NetworkManager.NetworkCallback() {
            @Override
            public void onSuccess(String response) {
                runOnUiThread(() -> {
                    Log.d("N8N", "Respuesta exitosa de N8N para Chef Voz: " + response);
                    Toast.makeText(VoiceChefActivity.this, "Datos de voz enviados a N8N exitosamente", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    Log.e("N8N", "Error enviando a N8N: " + error);
                    Toast.makeText(VoiceChefActivity.this, "Error conectando con N8N: " + error, Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private static class Recipe {
        String name;
        List<String> ingredients;
        List<String> steps;
    }
}

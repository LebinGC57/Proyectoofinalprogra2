package com.example.smarboyapp;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class ChefConscienteActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_PICK = 2;
    private static final int PERMISSION_REQUEST_CODE = 100;

    private Button btnTakePhoto, btnSelectPhoto, btnAnalyzeIngredients, btnWhatToCook, btnVoiceChef, btnChefInnovador, btnInventarioInteligente;
    private EditText etIngredientsList;
    private ImageView ivSelectedImage;
    private LinearLayout llRecipeContainer;
    private ScrollView scrollView;
    private TextView tvDetectedIngredients;

    private ActivityResultLauncher<Intent> cameraLauncher;
    private ActivityResultLauncher<Intent> galleryLauncher;
    private ActivityResultLauncher<Intent> customCameraLauncher;
    private Bitmap currentImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chef_consciente);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeViews();
        setupClickListeners();
        setupActivityResultLaunchers();
    }

    private void initializeViews() {
        btnTakePhoto = findViewById(R.id.btnTakePhoto);
        btnSelectPhoto = findViewById(R.id.btnSelectPhoto);
        btnAnalyzeIngredients = findViewById(R.id.btnAnalyzeIngredients);
        btnWhatToCook = findViewById(R.id.btnWhatToCook);
        btnVoiceChef = findViewById(R.id.btnVoiceChef);
        btnChefInnovador = findViewById(R.id.btnChefInnovador);
        btnInventarioInteligente = findViewById(R.id.btnInventarioInteligente);
        etIngredientsList = findViewById(R.id.etIngredientsList);
        ivSelectedImage = findViewById(R.id.ivSelectedImage);
        llRecipeContainer = findViewById(R.id.llRecipeContainer);
        scrollView = findViewById(R.id.scrollView);
        tvDetectedIngredients = findViewById(R.id.tvDetectedIngredients);
    }

    private void setupClickListeners() {
        btnTakePhoto.setOnClickListener(v -> {
            Toast.makeText(this, "Abriendo cámara personalizada...", Toast.LENGTH_SHORT).show();
            if (checkCameraPermission()) {
                openCustomCamera();
            } else {
                Toast.makeText(this, "Solicitando permiso de cámara...", Toast.LENGTH_SHORT).show();
                requestCameraPermission();
            }
        });

        btnSelectPhoto.setOnClickListener(v -> openGallery());

        btnAnalyzeIngredients.setOnClickListener(v -> {
            if (currentImage != null) {
                analyzeImageForIngredients();
            } else {
                Toast.makeText(this, "Por favor, selecciona una imagen primero", Toast.LENGTH_SHORT).show();
            }
        });

        btnWhatToCook.setOnClickListener(v -> {
            String ingredients = etIngredientsList.getText().toString().trim();
            if (!TextUtils.isEmpty(ingredients)) {
                // Enviar a N8N y generar sugerencias locales
                sendToN8NQueCocinoHoy(ingredients);
                generateRecipeSuggestions(ingredients);
            } else {
                Toast.makeText(this, "Por favor, ingresa una lista de ingredientes", Toast.LENGTH_SHORT).show();
            }
        });

        btnVoiceChef.setOnClickListener(v -> {
            Intent intent = new Intent(ChefConscienteActivity.this, VoiceChefActivity.class);
            startActivity(intent);
        });

        btnChefInnovador.setOnClickListener(v -> {
            Intent intent = new Intent(ChefConscienteActivity.this, ChefInnovadorActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        btnInventarioInteligente.setOnClickListener(v -> {
            Intent intent = new Intent(ChefConscienteActivity.this, InventarioInteligenteActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
    }

    private void setupActivityResultLaunchers() {
        // Launcher para cámara del sistema (mantener para compatibilidad)
        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Bundle extras = result.getData().getExtras();
                        if (extras != null && extras.containsKey("data")) {
                            currentImage = (Bitmap) extras.get("data");
                            if (currentImage != null) {
                                ivSelectedImage.setImageBitmap(currentImage);
                                ivSelectedImage.setVisibility(View.VISIBLE);
                                btnAnalyzeIngredients.setVisibility(View.VISIBLE);
                                Toast.makeText(this, "Foto tomada exitosamente", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else if (result.getResultCode() == Activity.RESULT_CANCELED) {
                            Toast.makeText(this, "Captura de foto cancelada", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        // Launcher para cámara personalizada
        customCameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        String imagePath = result.getData().getStringExtra("image_path");
                        if (imagePath != null) {
                            loadImageFromPath(imagePath);
                        }
                    } else if (result.getResultCode() == Activity.RESULT_CANCELED) {
                        Toast.makeText(this, "Captura de foto cancelada", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Intent data = result.getData();
                        if (data.getData() != null) {
                            try {
                                Uri imageUri = data.getData();
                                currentImage = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                                if (currentImage != null) {
                                    ivSelectedImage.setImageBitmap(currentImage);
                                    ivSelectedImage.setVisibility(View.VISIBLE);
                                    btnAnalyzeIngredients.setVisibility(View.VISIBLE);
                                    Toast.makeText(this, "Imagen seleccionada exitosamente", Toast.LENGTH_SHORT).show();
                                }
                            } catch (IOException e) {
                                Toast.makeText(this, "Error al cargar la imagen: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else if (result.getResultCode() == Activity.RESULT_CANCELED) {
                        Toast.makeText(this, "Selección de imagen cancelada", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private boolean checkCameraPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CODE);
    }

    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            Toast.makeText(this, "Lanzando aplicación de cámara...", Toast.LENGTH_SHORT).show();
            cameraLauncher.launch(takePictureIntent);
        } else {
            Toast.makeText(this, "No se encontró una aplicación de cámara", Toast.LENGTH_SHORT).show();
        }
    }

    private void openCustomCamera() {
        Intent cameraIntent = new Intent(this, CameraActivity.class);
        customCameraLauncher.launch(cameraIntent);
    }

    private void loadImageFromPath(String imagePath) {
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 2; // Reducir tamaño para mejor rendimiento
            currentImage = BitmapFactory.decodeFile(imagePath, options);
            
            if (currentImage != null) {
                ivSelectedImage.setImageBitmap(currentImage);
                ivSelectedImage.setVisibility(View.VISIBLE);
                btnAnalyzeIngredients.setVisibility(View.VISIBLE);
                Toast.makeText(this, "Foto cargada exitosamente", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Error al cargar la imagen", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error al procesar la imagen: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void openGallery() {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickPhoto.setType("image/*");
        if (pickPhoto.resolveActivity(getPackageManager()) != null) {
            galleryLauncher.launch(pickPhoto);
        } else {
            Toast.makeText(this, "No se encontró una aplicación de galería", Toast.LENGTH_SHORT).show();
        }
    }

    private void analyzeImageForIngredients() {
        // Simulación de análisis de imagen - en una implementación real usarías ML Kit o similar
        String[] commonIngredients = {
            "Tomate", "Cebolla", "Ajo", "Pimiento", "Zanahoria", "Papa", "Lechuga", "Pepino",
            "Huevo", "Leche", "Queso", "Pollo", "Carne", "Pescado", "Arroz", "Pasta",
            "Pan", "Aceite", "Sal", "Pimienta", "Limón", "Hierbas", "Especias"
        };

        // Simular detección aleatoria de ingredientes
        Random random = new Random();
        List<String> detectedIngredients = new ArrayList<>();
        int numIngredients = random.nextInt(5) + 3; // Entre 3 y 7 ingredientes

        for (int i = 0; i < numIngredients; i++) {
            String ingredient = commonIngredients[random.nextInt(commonIngredients.length)];
            if (!detectedIngredients.contains(ingredient)) {
                detectedIngredients.add(ingredient);
            }
        }

        StringBuilder ingredientsText = new StringBuilder();
        for (String ingredient : detectedIngredients) {
            ingredientsText.append(ingredient).append(", ");
        }

        String ingredientsString = ingredientsText.toString();
        if (ingredientsString.endsWith(", ")) {
            ingredientsString = ingredientsString.substring(0, ingredientsString.length() - 2);
        }

        etIngredientsList.setText(ingredientsString);
        tvDetectedIngredients.setText("Ingredientes detectados: " + ingredientsString);
        tvDetectedIngredients.setVisibility(View.VISIBLE);

        Toast.makeText(this, "Análisis completado. Ingredientes detectados: " + ingredientsString, Toast.LENGTH_LONG).show();
    }

    private void generateRecipeSuggestions(String ingredients) {
        llRecipeContainer.removeAllViews();
        
        String[] ingredientsArray = ingredients.split(",");
        List<String> ingredientsList = new ArrayList<>();
        for (String ingredient : ingredientsArray) {
            ingredientsList.add(ingredient.trim());
        }

        // Generar 3 recetas basadas en los ingredientes
        List<Recipe> recipes = generateRecipesFromIngredients(ingredientsList);

        for (int i = 0; i < recipes.size(); i++) {
            Recipe recipe = recipes.get(i);
            addRecipeToContainer(recipe, i + 1);
        }

        scrollView.post(() -> scrollView.fullScroll(View.FOCUS_DOWN));
    }

    private List<Recipe> generateRecipesFromIngredients(List<String> ingredients) {
        List<Recipe> recipes = new ArrayList<>();

        // Receta 1: Ensalada fresca
        Recipe recipe1 = new Recipe();
        recipe1.name = "Ensalada Fresca y Nutritiva";
        recipe1.ingredients = new ArrayList<>();
        recipe1.steps = new ArrayList<>();

        if (ingredients.contains("Lechuga") || ingredients.contains("Tomate")) {
            recipe1.ingredients.add("Lechuga fresca");
            recipe1.ingredients.add("Tomate");
        }
        if (ingredients.contains("Cebolla")) recipe1.ingredients.add("Cebolla");
        if (ingredients.contains("Pepino")) recipe1.ingredients.add("Pepino");
        if (ingredients.contains("Aceite")) recipe1.ingredients.add("Aceite de oliva");
        if (ingredients.contains("Limón")) recipe1.ingredients.add("Limón");

        recipe1.steps.add("Lava y corta todos los vegetales");
        recipe1.steps.add("Mezcla en un bowl grande");
        recipe1.steps.add("Aliña con aceite, limón, sal y pimienta");
        recipe1.steps.add("Sirve inmediatamente");

        recipes.add(recipe1);

        // Receta 2: Pasta simple
        Recipe recipe2 = new Recipe();
        recipe2.name = "Pasta Rápida y Deliciosa";
        recipe2.ingredients = new ArrayList<>();
        recipe2.steps = new ArrayList<>();

        if (ingredients.contains("Pasta")) recipe2.ingredients.add("Pasta");
        if (ingredients.contains("Tomate")) recipe2.ingredients.add("Tomate");
        if (ingredients.contains("Ajo")) recipe2.ingredients.add("Ajo");
        if (ingredients.contains("Cebolla")) recipe2.ingredients.add("Cebolla");
        if (ingredients.contains("Aceite")) recipe2.ingredients.add("Aceite");
        if (ingredients.contains("Queso")) recipe2.ingredients.add("Queso rallado");

        recipe2.steps.add("Hierve agua y cocina la pasta");
        recipe2.steps.add("Sofríe ajo y cebolla en aceite");
        recipe2.steps.add("Agrega tomate picado y cocina 5 minutos");
        recipe2.steps.add("Mezcla con la pasta y sirve con queso");

        recipes.add(recipe2);

        // Receta 3: Arroz con vegetales
        Recipe recipe3 = new Recipe();
        recipe3.name = "Arroz con Vegetales";
        recipe3.ingredients = new ArrayList<>();
        recipe3.steps = new ArrayList<>();

        if (ingredients.contains("Arroz")) recipe3.ingredients.add("Arroz");
        if (ingredients.contains("Zanahoria")) recipe3.ingredients.add("Zanahoria");
        if (ingredients.contains("Cebolla")) recipe3.ingredients.add("Cebolla");
        if (ingredients.contains("Ajo")) recipe3.ingredients.add("Ajo");
        if (ingredients.contains("Aceite")) recipe3.ingredients.add("Aceite");
        if (ingredients.contains("Pimiento")) recipe3.ingredients.add("Pimiento");

        recipe3.steps.add("Cocina el arroz según las instrucciones");
        recipe3.steps.add("Corta todos los vegetales en cubos pequeños");
        recipe3.steps.add("Sofríe los vegetales en aceite hasta que estén tiernos");
        recipe3.steps.add("Mezcla con el arroz cocido y sirve");

        recipes.add(recipe3);

        return recipes;
    }

    private void addRecipeToContainer(Recipe recipe, int recipeNumber) {
        LinearLayout recipeLayout = new LinearLayout(this);
        recipeLayout.setOrientation(LinearLayout.VERTICAL);
        recipeLayout.setPadding(16, 16, 16, 16);
        recipeLayout.setBackground(getResources().getDrawable(R.drawable.recipe_card_background));

        // Título de la receta
        TextView titleView = new TextView(this);
        titleView.setText("Receta " + recipeNumber + ": " + recipe.name);
        titleView.setTextSize(18);
        titleView.setTextColor(getResources().getColor(android.R.color.black));
        titleView.setTypeface(null, android.graphics.Typeface.BOLD);
        titleView.setPadding(0, 0, 0, 8);

        // Ingredientes
        TextView ingredientsView = new TextView(this);
        ingredientsView.setText("Ingredientes a usar: " + String.join(", ", recipe.ingredients));
        ingredientsView.setTextSize(14);
        ingredientsView.setTextColor(getResources().getColor(android.R.color.black));
        ingredientsView.setPadding(0, 0, 0, 8);

        // Pasos
        TextView stepsView = new TextView(this);
        StringBuilder stepsText = new StringBuilder("Pasos sencillos:\n");
        for (int i = 0; i < recipe.steps.size(); i++) {
            stepsText.append((i + 1)).append(". ").append(recipe.steps.get(i)).append("\n");
        }
        stepsView.setText(stepsText.toString());
        stepsView.setTextSize(14);
        stepsView.setTextColor(getResources().getColor(android.R.color.black));

        recipeLayout.addView(titleView);
        recipeLayout.addView(ingredientsView);
        recipeLayout.addView(stepsView);

        llRecipeContainer.addView(recipeLayout);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void sendToN8NQueCocinoHoy(String ingredients) {
        NetworkManager.sendQueCocinoHoy(this, ingredients, new NetworkManager.NetworkCallback() {
            @Override
            public void onSuccess(String response) {
                runOnUiThread(() -> {
                    Log.d("N8N", "Respuesta exitosa de N8N para ¿Qué cocino hoy?: " + response);
                    Toast.makeText(ChefConscienteActivity.this, "Datos enviados a N8N exitosamente", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    Log.e("N8N", "Error enviando a N8N: " + error);
                    Toast.makeText(ChefConscienteActivity.this, "Error conectando con N8N: " + error, Toast.LENGTH_LONG).show();
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

package com.example.smarboyapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class InventarioInteligenteActivity extends AppCompatActivity {

    private EditText etCurrentInventory, etMenuObjective, etBasicNeeds;
    private Button btnAnalyzeInventory, btnTakePhotoInventory, btnSelectPhotoInventory, btnClearAll, btnBackToChef;
    private ImageView ivInventoryPhoto;
    private LinearLayout llReportContainer;
    private ScrollView scrollView;
    private TextView tvTitle, tvSubtitle;

    private ActivityResultLauncher<Intent> galleryLauncher;
    private ActivityResultLauncher<Intent> cameraLauncher;
    private Bitmap currentInventoryImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventario_inteligente);

        initializeViews();
        setupClickListeners();
        setupActivityResultLaunchers();
        applyAnimations();
    }

    private void initializeViews() {
        etCurrentInventory = findViewById(R.id.etCurrentInventory);
        etMenuObjective = findViewById(R.id.etMenuObjective);
        etBasicNeeds = findViewById(R.id.etBasicNeeds);
        btnAnalyzeInventory = findViewById(R.id.btnAnalyzeInventory);
        btnTakePhotoInventory = findViewById(R.id.btnTakePhotoInventory);
        btnSelectPhotoInventory = findViewById(R.id.btnSelectPhotoInventory);
        btnClearAll = findViewById(R.id.btnClearAll);
        btnBackToChef = findViewById(R.id.btnBackToChef);
        ivInventoryPhoto = findViewById(R.id.ivInventoryPhoto);
        llReportContainer = findViewById(R.id.llReportContainer);
        scrollView = findViewById(R.id.scrollView);
        tvTitle = findViewById(R.id.tvTitle);
        tvSubtitle = findViewById(R.id.tvSubtitle);
    }

    private void setupClickListeners() {
        btnAnalyzeInventory.setOnClickListener(v -> {
            String inventory = etCurrentInventory.getText().toString().trim();
            String menuObjective = etMenuObjective.getText().toString().trim();
            String basicNeeds = etBasicNeeds.getText().toString().trim();

            if (!TextUtils.isEmpty(inventory) && !TextUtils.isEmpty(menuObjective)) {
                // Enviar a N8N y generar reporte local
                sendToN8NGestorInventario(inventory, menuObjective, basicNeeds);
                generateInventoryReport(inventory, menuObjective, basicNeeds);
            } else {
                Toast.makeText(this, "Por favor, completa el inventario actual y el objetivo del menú", Toast.LENGTH_SHORT).show();
            }
        });

        btnTakePhotoInventory.setOnClickListener(v -> {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                cameraLauncher.launch(takePictureIntent);
            } else {
                Toast.makeText(this, "No se encontró una aplicación de cámara", Toast.LENGTH_SHORT).show();
            }
        });

        btnSelectPhotoInventory.setOnClickListener(v -> {
            Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            pickPhoto.setType("image/*");
            if (pickPhoto.resolveActivity(getPackageManager()) != null) {
                galleryLauncher.launch(pickPhoto);
            } else {
                Toast.makeText(this, "No se encontró una aplicación de galería", Toast.LENGTH_SHORT).show();
            }
        });

        btnClearAll.setOnClickListener(v -> {
            etCurrentInventory.setText("");
            etMenuObjective.setText("");
            etBasicNeeds.setText("");
            llReportContainer.removeAllViews();
            ivInventoryPhoto.setVisibility(View.GONE);
            currentInventoryImage = null;
            Toast.makeText(this, "Datos limpiados", Toast.LENGTH_SHORT).show();
        });

        btnBackToChef.setOnClickListener(v -> {
            Intent intent = new Intent(InventarioInteligenteActivity.this, ChefConscienteActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
    }

    private void setupActivityResultLaunchers() {
        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Bundle extras = result.getData().getExtras();
                        if (extras != null && extras.containsKey("data")) {
                            currentInventoryImage = (Bitmap) extras.get("data");
                            if (currentInventoryImage != null) {
                                ivInventoryPhoto.setImageBitmap(currentInventoryImage);
                                ivInventoryPhoto.setVisibility(View.VISIBLE);
                                analyzeInventoryFromPhoto();
                            }
                        }
                    }
                }
        );

        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Intent data = result.getData();
                        if (data.getData() != null) {
                            try {
                                Uri imageUri = data.getData();
                                currentInventoryImage = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                                if (currentInventoryImage != null) {
                                    ivInventoryPhoto.setImageBitmap(currentInventoryImage);
                                    ivInventoryPhoto.setVisibility(View.VISIBLE);
                                    analyzeInventoryFromPhoto();
                                }
                            } catch (IOException e) {
                                Toast.makeText(this, "Error al cargar la imagen: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
        );
    }

    private void applyAnimations() {
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        Animation slideIn = AnimationUtils.loadAnimation(this, R.anim.slide_in_right);
        
        tvTitle.startAnimation(fadeIn);
        tvSubtitle.startAnimation(slideIn);
    }

    private void analyzeInventoryFromPhoto() {
        // Simulación de análisis de imagen - en una implementación real usarías ML Kit
        String[] commonIngredients = {
            "Leche", "Huevos", "Pan", "Mantequilla", "Queso", "Yogurt", "Frutas", "Verduras",
            "Carne", "Pollo", "Pescado", "Arroz", "Pasta", "Aceite", "Sal", "Especias"
        };

        Random random = new Random();
        List<String> detectedIngredients = new ArrayList<>();
        int numIngredients = random.nextInt(6) + 4; // Entre 4 y 9 ingredientes

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

        etCurrentInventory.setText(ingredientsString);
        Toast.makeText(this, "Ingredientes detectados: " + ingredientsString, Toast.LENGTH_LONG).show();
    }

    private void generateInventoryReport(String inventory, String menuObjective, String basicNeeds) {
        llReportContainer.removeAllViews();
        
        String[] inventoryArray = inventory.split(",");
        List<String> availableIngredients = new ArrayList<>();
        for (String ingredient : inventoryArray) {
            availableIngredients.add(ingredient.trim());
        }

        String[] basicNeedsArray = basicNeeds.isEmpty() ? new String[0] : basicNeeds.split(",");
        List<String> basicNeedsList = new ArrayList<>();
        for (String need : basicNeedsArray) {
            basicNeedsList.add(need.trim());
        }

        // Generar reporte de inventario
        InventoryReport report = createInventoryReport(availableIngredients, menuObjective, basicNeedsList);
        displayInventoryReport(report);

        scrollView.post(() -> scrollView.fullScroll(View.FOCUS_DOWN));
    }

    private InventoryReport createInventoryReport(List<String> availableIngredients, String menuObjective, List<String> basicNeeds) {
        InventoryReport report = new InventoryReport();
        report.menuObjective = menuObjective;
        report.availableIngredients = availableIngredients;
        report.basicNeeds = basicNeeds;

        // Analizar ingredientes útiles
        List<String> usefulIngredients = new ArrayList<>();
        for (String ingredient : availableIngredients) {
            if (isUsefulIngredient(ingredient)) {
                usefulIngredients.add(ingredient);
            }
        }
        report.usefulIngredients = usefulIngredients;

        // Identificar básicos faltantes
        List<String> missingBasics = identifyMissingBasics(availableIngredients, basicNeeds);
        report.missingBasics = missingBasics;

        // Generar lista de compras para el menú
        List<ShoppingItem> menuShoppingList = generateMenuShoppingList(menuObjective, availableIngredients);
        report.menuShoppingList = menuShoppingList;

        // Generar consejo de optimización
        report.optimizationAdvice = generateOptimizationAdvice(availableIngredients, menuObjective);

        return report;
    }

    private boolean isUsefulIngredient(String ingredient) {
        String[] usefulIngredients = {
            "Leche", "Huevos", "Pan", "Mantequilla", "Queso", "Aceite", "Sal", "Ajo", "Cebolla",
            "Tomate", "Papa", "Arroz", "Pasta", "Carne", "Pollo", "Pescado"
        };
        
        for (String useful : usefulIngredients) {
            if (ingredient.toLowerCase().contains(useful.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    private List<String> identifyMissingBasics(List<String> availableIngredients, List<String> basicNeeds) {
        List<String> missingBasics = new ArrayList<>();
        
        // Básicos comunes que siempre se necesitan
        String[] commonBasics = {"Leche", "Huevos", "Pan", "Mantequilla", "Aceite", "Sal"};
        
        for (String basic : commonBasics) {
            boolean found = false;
            for (String available : availableIngredients) {
                if (available.toLowerCase().contains(basic.toLowerCase())) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                missingBasics.add(basic);
            }
        }

        // Agregar básicos específicos mencionados por el usuario
        for (String need : basicNeeds) {
            if (!missingBasics.contains(need)) {
                missingBasics.add(need);
            }
        }

        return missingBasics;
    }

    private List<ShoppingItem> generateMenuShoppingList(String menuObjective, List<String> availableIngredients) {
        List<ShoppingItem> shoppingList = new ArrayList<>();
        
        // Generar ingredientes faltantes basados en el objetivo del menú
        String[] menuIngredients = generateMenuIngredients(menuObjective);
        
        for (String ingredient : menuIngredients) {
            boolean found = false;
            for (String available : availableIngredients) {
                if (available.toLowerCase().contains(ingredient.toLowerCase())) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                ShoppingItem item = new ShoppingItem();
                item.name = ingredient;
                item.role = generateIngredientRole(ingredient, menuObjective);
                item.priority = determinePriority(ingredient);
                shoppingList.add(item);
            }
        }

        return shoppingList;
    }

    private String[] generateMenuIngredients(String menuObjective) {
        String objective = menuObjective.toLowerCase();
        
        if (objective.contains("mexicana") || objective.contains("mexicano")) {
            return new String[]{"Tortillas", "Frijoles", "Queso fresco", "Cilantro", "Limón", "Chile", "Cebolla", "Tomate"};
        } else if (objective.contains("italiana") || objective.contains("italiano")) {
            return new String[]{"Pasta", "Tomate", "Albahaca", "Queso parmesano", "Ajo", "Aceite de oliva", "Cebolla"};
        } else if (objective.contains("asiática") || objective.contains("asiático")) {
            return new String[]{"Arroz", "Salsa de soja", "Jengibre", "Ajo", "Cebolla", "Zanahoria", "Brócoli"};
        } else if (objective.contains("semana") || objective.contains("semanal")) {
            return new String[]{"Pollo", "Carne", "Pescado", "Verduras", "Frutas", "Pan", "Leche", "Huevos"};
        } else {
            return new String[]{"Proteína", "Verduras", "Carbohidratos", "Condimentos", "Aceite", "Sal"};
        }
    }

    private String generateIngredientRole(String ingredient, String menuObjective) {
        String[] roles = {
            "Necesario para la receta principal",
            "Esencial para el sabor base",
            "Necesario para ambas recetas",
            "Complemento de textura",
            "Aromatizante principal"
        };
        Random random = new Random();
        return roles[random.nextInt(roles.length)];
    }

    private String determinePriority(String ingredient) {
        String[] highPriority = {"Carne", "Pollo", "Pescado", "Huevos", "Leche"};
        for (String high : highPriority) {
            if (ingredient.toLowerCase().contains(high.toLowerCase())) {
                return "Alta";
            }
        }
        return "Media";
    }

    private String generateOptimizationAdvice(List<String> availableIngredients, String menuObjective) {
        Random random = new Random();
        String[] advices = {
            "Usa el " + availableIngredients.get(random.nextInt(availableIngredients.size())) + " restante para hacer una ensalada o snack saludable.",
            "Compra ingredientes en cantidades que puedas usar en múltiples recetas para evitar desperdicio.",
            "Considera comprar versiones congeladas de vegetales para extender su vida útil.",
            "Planifica usar los ingredientes más perecederos primero en tu menú semanal."
        };
        return advices[random.nextInt(advices.length)];
    }

    private void displayInventoryReport(InventoryReport report) {
        // Contenedor principal del reporte
        LinearLayout reportLayout = new LinearLayout(this);
        reportLayout.setOrientation(LinearLayout.VERTICAL);
        reportLayout.setPadding(24, 24, 24, 24);
        reportLayout.setBackground(getResources().getDrawable(R.drawable.recipe_container_background));

        // Título del reporte
        TextView titleView = createStyledTextView("📊 Informe de Inventario y Lista de Compras Inteligente", 20, true);
        titleView.setTextColor(getResources().getColor(android.R.color.holo_blue_dark));
        titleView.setPadding(0, 0, 0, 16);

        // Objetivo de compra
        TextView objectiveView = createStyledTextView("Objetivo de Compra: " + report.menuObjective, 16, true);
        objectiveView.setTextColor(getResources().getColor(android.R.color.black));
        objectiveView.setPadding(0, 8, 0, 16);

        // Análisis de stock
        TextView stockTitle = createStyledTextView("Análisis de Stock Rápido:", 16, true);
        stockTitle.setTextColor(getResources().getColor(android.R.color.black));
        stockTitle.setPadding(0, 8, 0, 4);

        StringBuilder usefulText = new StringBuilder("Lo que ya tienes y es útil: ");
        for (int i = 0; i < Math.min(4, report.usefulIngredients.size()); i++) {
            usefulText.append(report.usefulIngredients.get(i));
            if (i < Math.min(4, report.usefulIngredients.size()) - 1) {
                usefulText.append(", ");
            }
        }
        TextView usefulView = createStyledTextView(usefulText.toString(), 14, false);
        usefulView.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        usefulView.setPadding(0, 4, 0, 8);

        // Riesgo de agotamiento
        if (!report.missingBasics.isEmpty()) {
            StringBuilder riskText = new StringBuilder("Riesgo de Agotamiento (Básicos): ");
            for (int i = 0; i < Math.min(2, report.missingBasics.size()); i++) {
                riskText.append(report.missingBasics.get(i));
                if (i < Math.min(2, report.missingBasics.size()) - 1) {
                    riskText.append(", ");
                }
            }
            TextView riskView = createStyledTextView(riskText.toString(), 14, false);
            riskView.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            riskView.setPadding(0, 4, 0, 16);
            reportLayout.addView(riskView);
        }

        // Lista de compras
        TextView shoppingTitle = createStyledTextView("🛒 Lista de Compras Prioritaria:", 16, true);
        shoppingTitle.setTextColor(getResources().getColor(android.R.color.black));
        shoppingTitle.setPadding(0, 8, 0, 8);

        // Reabastecimiento de básicos
        if (!report.missingBasics.isEmpty()) {
            TextView basicsTitle = createStyledTextView("Reabastecimiento de Básicos Esenciales:", 14, true);
            basicsTitle.setTextColor(getResources().getColor(android.R.color.holo_orange_dark));
            basicsTitle.setPadding(0, 8, 0, 4);

            for (String basic : report.missingBasics) {
                TextView basicItem = createStyledTextView("• " + basic, 14, false);
                basicItem.setTextColor(getResources().getColor(android.R.color.black));
                basicItem.setPadding(16, 2, 0, 2);
                reportLayout.addView(basicItem);
            }
        }

        // Ingredientes para el menú
        if (!report.menuShoppingList.isEmpty()) {
            TextView menuTitle = createStyledTextView("Ingredientes Faltantes para " + report.menuObjective + ":", 14, true);
            menuTitle.setTextColor(getResources().getColor(android.R.color.holo_purple));
            menuTitle.setPadding(0, 16, 0, 4);

            for (ShoppingItem item : report.menuShoppingList) {
                TextView menuItem = createStyledTextView("• " + item.name + " (Rol: " + item.role + ")", 14, false);
                menuItem.setTextColor(getResources().getColor(android.R.color.black));
                menuItem.setPadding(16, 2, 0, 2);
                reportLayout.addView(menuItem);
            }
        }

        // Consejo de planificación
        TextView adviceTitle = createStyledTextView("📝 Consejo de Planificación y Uso Eficiente:", 16, true);
        adviceTitle.setTextColor(getResources().getColor(android.R.color.black));
        adviceTitle.setPadding(0, 16, 0, 4);

        TextView adviceView = createStyledTextView(report.optimizationAdvice, 14, false);
        adviceView.setTextColor(getResources().getColor(android.R.color.darker_gray));
        adviceView.setPadding(0, 4, 0, 16);

        // Instrucción final
        TextView finalInstruction = createStyledTextView("¿Listo para generar tu ruta de compras más eficiente?", 14, true);
        finalInstruction.setTextColor(getResources().getColor(android.R.color.holo_blue_dark));
        finalInstruction.setPadding(0, 8, 0, 0);

        // Agregar todas las vistas al layout
        reportLayout.addView(titleView);
        reportLayout.addView(objectiveView);
        reportLayout.addView(stockTitle);
        reportLayout.addView(usefulView);
        reportLayout.addView(shoppingTitle);
        reportLayout.addView(adviceTitle);
        reportLayout.addView(adviceView);
        reportLayout.addView(finalInstruction);

        llReportContainer.addView(reportLayout);

        // Aplicar animación de entrada
        Animation slideIn = AnimationUtils.loadAnimation(this, R.anim.slide_in_right);
        reportLayout.startAnimation(slideIn);
    }

    private TextView createStyledTextView(String text, int textSize, boolean bold) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setTextSize(textSize);
        if (bold) {
            textView.setTypeface(null, android.graphics.Typeface.BOLD);
        }
        return textView;
    }

    private static class InventoryReport {
        String menuObjective;
        List<String> availableIngredients;
        List<String> basicNeeds;
        List<String> usefulIngredients;
        List<String> missingBasics;
        List<ShoppingItem> menuShoppingList;
        String optimizationAdvice;
    }

    private void sendToN8NGestorInventario(String inventory, String menuObjective, String basicNeeds) {
        NetworkManager.sendGestorInventario(this, inventory, menuObjective, basicNeeds, new NetworkManager.NetworkCallback() {
            @Override
            public void onSuccess(String response) {
                runOnUiThread(() -> {
                    Log.d("N8N", "Respuesta exitosa de N8N para Gestor Inventario: " + response);
                    Toast.makeText(InventarioInteligenteActivity.this, "Datos enviados a N8N exitosamente", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    Log.e("N8N", "Error enviando a N8N: " + error);
                    Toast.makeText(InventarioInteligenteActivity.this, "Error conectando con N8N: " + error, Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private static class ShoppingItem {
        String name;
        String role;
        String priority;
    }
}

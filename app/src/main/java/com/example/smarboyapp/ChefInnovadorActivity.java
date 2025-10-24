package com.example.smarboyapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class ChefInnovadorActivity extends AppCompatActivity {

    private EditText etIngredientsInput;
    private Button btnGenerateProposal, btnClearIngredients, btnBackToChef;
    private LinearLayout llProposalContainer;
    private ScrollView scrollView;
    private TextView tvTitle, tvSubtitle;
    private Button btnSaveRecipe; // nuevo botón

    private GourmetProposal currentProposal; // almacenar la propuesta actual

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chef_innovador);

        initializeViews();
        setupClickListeners();
        applyAnimations();
    }

    private void initializeViews() {
        etIngredientsInput = findViewById(R.id.etIngredientsInput);
        btnGenerateProposal = findViewById(R.id.btnGenerateProposal);
        btnClearIngredients = findViewById(R.id.btnClearIngredients);
        btnBackToChef = findViewById(R.id.btnBackToChef);
        llProposalContainer = findViewById(R.id.llProposalContainer);
        scrollView = findViewById(R.id.scrollView);
        tvTitle = findViewById(R.id.tvTitle);
        tvSubtitle = findViewById(R.id.tvSubtitle);
        btnSaveRecipe = findViewById(R.id.btnSaveRecipe);
    }

    private void setupClickListeners() {
        btnGenerateProposal.setOnClickListener(v -> {
            String ingredients = etIngredientsInput.getText().toString().trim();
            if (!TextUtils.isEmpty(ingredients)) {
                // Solo generar propuesta local, SIN enviar a N8N
                generateGourmetProposal(ingredients);
            } else {
                Toast.makeText(this, "Por favor, ingresa una lista de ingredientes (4-8 ingredientes)", Toast.LENGTH_SHORT).show();
            }
        });

        btnClearIngredients.setOnClickListener(v -> {
            etIngredientsInput.setText("");
            llProposalContainer.removeAllViews();
            btnSaveRecipe.setVisibility(View.GONE);
            currentProposal = null;
            Toast.makeText(this, "Lista de ingredientes limpiada", Toast.LENGTH_SHORT).show();
        });

        btnBackToChef.setOnClickListener(v -> {
            Intent intent = new Intent(ChefInnovadorActivity.this, ChefConscienteActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        btnSaveRecipe.setOnClickListener(v -> saveCurrentProposal());
    }

    private void applyAnimations() {
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        Animation slideIn = AnimationUtils.loadAnimation(this, R.anim.slide_in_right);

        tvTitle.startAnimation(fadeIn);
        tvSubtitle.startAnimation(slideIn);
    }

    private void generateGourmetProposal(String ingredients) {
        llProposalContainer.removeAllViews();

        String[] ingredientsArray = ingredients.split(",");
        List<String> ingredientsList = new ArrayList<>();
        for (String ingredient : ingredientsArray) {
            ingredientsList.add(ingredient.trim());
        }

        if (ingredientsList.size() < 4) {
            Toast.makeText(this, "Necesitas al menos 4 ingredientes para una propuesta gourmet", Toast.LENGTH_SHORT).show();
            return;
        }

        if (ingredientsList.size() > 8) {
            Toast.makeText(this, "Máximo 8 ingredientes para mantener la creatividad", Toast.LENGTH_SHORT).show();
            return;
        }

        // Generar propuesta gourmet
        GourmetProposal proposal = createGourmetProposal(ingredientsList);
        displayGourmetProposal(proposal);

        // Guardar propuesta actual y mostrar botón de guardar
        currentProposal = proposal;
        btnSaveRecipe.setVisibility(View.VISIBLE);

        if (scrollView != null) {
            scrollView.post(() -> scrollView.fullScroll(View.FOCUS_DOWN));
        }
    }

    private GourmetProposal createGourmetProposal(List<String> ingredients) {
        GourmetProposal proposal = new GourmetProposal();
        Random random = new Random();

        // Guardar ingredientes en la propuesta
        proposal.ingredients = new ArrayList<>(ingredients);

        // Seleccionar ingredientes base
        String protein = selectProtein(ingredients);
        String contrast = selectContrastElement(ingredients, protein);
        String texture = selectTextureElement(ingredients, protein, contrast);
        String umami = selectUmamiElement(ingredients, protein, contrast, texture);

        // Crear concepto único
        proposal.dishName = generateDishName(protein, contrast);
        proposal.innovation = generateInnovation(protein, contrast, texture, umami);
        proposal.protein = protein;
        proposal.contrast = contrast;
        proposal.texture = texture;
        proposal.umami = umami;
        proposal.presentation = generatePresentationAdvice(protein, contrast, texture);
        proposal.technique = generateAdvancedTechnique(protein, contrast);

        return proposal;
    }

    private String selectProtein(List<String> ingredients) {
        String[] proteins = {"Pollo", "Carne", "Pescado", "Huevo", "Tofu", "Queso", "Jamón", "Salmón", "Atún", "Cordero"};
        for (String protein : proteins) {
            for (String ingredient : ingredients) {
                if (ingredient.toLowerCase().contains(protein.toLowerCase())) {
                    return protein;
                }
            }
        }
        return "Pollo"; // Default
    }

    private String selectContrastElement(List<String> ingredients, String protein) {
        String[] contrasts = {"Limón", "Naranja", "Vinagre", "Tomate", "Pimiento", "Cebolla", "Ajo", "Jengibre", "Menta", "Albahaca"};
        for (String contrast : contrasts) {
            for (String ingredient : ingredients) {
                if (ingredient.toLowerCase().contains(contrast.toLowerCase())) {
                    return contrast;
                }
            }
        }
        return "Limón"; // Default
    }

    private String selectTextureElement(List<String> ingredients, String protein, String contrast) {
        String[] textures = {"Papa", "Arroz", "Pasta", "Pan", "Nueces", "Almendras", "Aceitunas", "Pepino", "Zanahoria", "Apio"};
        for (String texture : textures) {
            for (String ingredient : ingredients) {
                if (ingredient.toLowerCase().contains(texture.toLowerCase())) {
                    return texture;
                }
            }
        }
        return "Papa"; // Default
    }

    private String selectUmamiElement(List<String> ingredients, String protein, String contrast, String texture) {
        String[] umami = {"Queso", "Aceitunas", "Tomate", "Champiñones", "Salsa de soja", "Anchoas", "Parmesano", "Miso", "Algas", "Trufa"};
        for (String u : umami) {
            for (String ingredient : ingredients) {
                if (ingredient.toLowerCase().contains(u.toLowerCase())) {
                    return u;
                }
            }
        }
        return "Queso"; // Default
    }

    private String generateDishName(String protein, String contrast) {
        String[] dishNames = {
            protein + " a la Parrilla con Emulsión de " + contrast,
            "Terrina de " + protein + " y " + contrast + " Confitado",
            protein + " Sous Vide con " + contrast + " y Especias",
            "Carpaccio de " + protein + " con " + contrast + " y Microgreens",
            protein + " en Salmuera Seca con " + contrast + " Asado"
        };
        Random random = new Random();
        return dishNames[random.nextInt(dishNames.length)];
    }

    private String generateInnovation(String protein, String contrast, String texture, String umami) {
        String[] innovations = {
            "La sinergia umami entre " + protein + " y " + umami + " se realza con la acidez del " + contrast + ", creando un contraste perfecto de sabores.",
            "La técnica de salmuera seca en el " + protein + " concentra los sabores, mientras que el " + contrast + " aporta frescura y equilibrio.",
            "La emulsión de " + contrast + " con " + umami + " crea una textura sedosa que complementa la firmeza del " + protein + ".",
            "El contraste de temperaturas entre el " + protein + " caliente y el " + contrast + " frío genera una experiencia sensorial única.",
            "La fermentación rápida del " + contrast + " desarrolla sabores complejos que realzan el " + protein + "."
        };
        Random random = new Random();
        return innovations[random.nextInt(innovations.length)];
    }

    private String generateAdvancedTechnique(String protein, String contrast) {
        String[] techniques = {
            "Sous Vide a 65°C por 2 horas",
            "Salmuera seca con especias por 24 horas",
            "Emulsión con lecitina de soja",
            "Fermentación rápida con lactobacillus",
            "Esferificación con alginato de sodio",
            "Deshidratación a baja temperatura",
            "Confit en aceite a 80°C",
            "Cocción al vacío con especias"
        };
        Random random = new Random();
        return techniques[random.nextInt(techniques.length)];
    }

    private String generatePresentationAdvice(String protein, String contrast, String texture) {
        String[] presentations = {
            "Coloca el " + protein + " en el centro del plato, rodeado por una emulsión de " + contrast + " y decora con microgreens y flores comestibles.",
            "Crea altura con el " + protein + " como base, añade el " + contrast + " en forma de espuma y termina con " + texture + " crujiente.",
            "Usa el principio del vacío: deja espacios en blanco y coloca el " + protein + " en una esquina con el " + contrast + " como acento.",
            "Aplica la regla de los tercios: " + protein + " en un tercio, " + contrast + " en otro, y " + texture + " como elemento de conexión.",
            "Crea contraste de colores: " + protein + " dorado, " + contrast + " vibrante, y " + texture + " en tonos neutros."
        };
        Random random = new Random();
        return presentations[random.nextInt(presentations.length)];
    }

    private void displayGourmetProposal(GourmetProposal proposal) {
        // Contenedor principal de la propuesta
        LinearLayout proposalLayout = new LinearLayout(this);
        proposalLayout.setOrientation(LinearLayout.VERTICAL);
        proposalLayout.setPadding(24, 24, 24, 24);
        proposalLayout.setBackground(getResources().getDrawable(R.drawable.recipe_container_background));

        // Título principal
        TextView titleView = createStyledTextView("🌟 Propuesta Culinaria del Chef Innovador", 20, true);
        titleView.setTextColor(getResources().getColor(android.R.color.holo_orange_dark));
        titleView.setPadding(0, 0, 0, 16);

        // Concepto del plato
        TextView conceptView = createStyledTextView("Concepto del Plato:", 16, true);
        conceptView.setTextColor(getResources().getColor(android.R.color.black));
        conceptView.setPadding(0, 8, 0, 4);

        TextView dishNameView = createStyledTextView(proposal.dishName, 18, true);
        dishNameView.setTextColor(getResources().getColor(android.R.color.holo_blue_dark));
        dishNameView.setPadding(0, 0, 0, 16);

        // La chispa innovadora
        TextView innovationTitle = createStyledTextView("La Chispa Innovadora:", 16, true);
        innovationTitle.setTextColor(getResources().getColor(android.R.color.black));
        innovationTitle.setPadding(0, 8, 0, 4);

        TextView innovationView = createStyledTextView(proposal.innovation, 14, false);
        innovationView.setTextColor(getResources().getColor(android.R.color.darker_gray));
        innovationView.setPadding(0, 0, 0, 16);

        // Ingredientes con rol
        TextView ingredientsTitle = createStyledTextView("Ingredientes a Usar (con rol):", 16, true);
        ingredientsTitle.setTextColor(getResources().getColor(android.R.color.black));
        ingredientsTitle.setPadding(0, 8, 0, 8);

        TextView proteinView = createStyledTextView("🥩 Proteína/Base: " + proposal.protein + " (Preparado con " + proposal.technique + ")", 14, false);
        proteinView.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        proteinView.setPadding(0, 4, 0, 4);

        TextView contrastView = createStyledTextView("🍋 Elemento de Contraste: " + proposal.contrast + " (Aportando Acidez y Frescura)", 14, false);
        contrastView.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        contrastView.setPadding(0, 4, 0, 4);

        TextView textureView = createStyledTextView("🥔 Textura Clave: " + proposal.texture + " (Convertido en Crujiente)", 14, false);
        textureView.setTextColor(getResources().getColor(android.R.color.holo_orange_dark));
        textureView.setPadding(0, 4, 0, 4);

        TextView umamiView = createStyledTextView("🧀 Salsa/Sabor Umami: " + proposal.umami + " (Transformado en Salsa)", 14, false);
        umamiView.setTextColor(getResources().getColor(android.R.color.holo_purple));
        umamiView.setPadding(0, 4, 0, 16);

        // Consejo de emplatado
        TextView presentationTitle = createStyledTextView("Consejo de Emplatado (Gourmet):", 16, true);
        presentationTitle.setTextColor(getResources().getColor(android.R.color.black));
        presentationTitle.setPadding(0, 8, 0, 4);

        TextView presentationView = createStyledTextView(proposal.presentation, 14, false);
        presentationView.setTextColor(getResources().getColor(android.R.color.darker_gray));
        presentationView.setPadding(0, 0, 0, 16);

        // Instrucción final
        TextView finalInstruction = createStyledTextView("¿Deseas el desglose paso a paso de este plato o prefieres explorar una fusión cultural diferente?", 14, true);
        finalInstruction.setTextColor(getResources().getColor(android.R.color.holo_blue_dark));
        finalInstruction.setPadding(0, 8, 0, 0);

        // Agregar todas las vistas al layout
        proposalLayout.addView(titleView);
        proposalLayout.addView(conceptView);
        proposalLayout.addView(dishNameView);
        proposalLayout.addView(innovationTitle);
        proposalLayout.addView(innovationView);
        proposalLayout.addView(ingredientsTitle);
        proposalLayout.addView(proteinView);
        proposalLayout.addView(contrastView);
        proposalLayout.addView(textureView);
        proposalLayout.addView(umamiView);
        proposalLayout.addView(presentationTitle);
        proposalLayout.addView(presentationView);
        proposalLayout.addView(finalInstruction);

        llProposalContainer.addView(proposalLayout);

        // Aplicar animación de entrada
        Animation slideIn = AnimationUtils.loadAnimation(this, R.anim.slide_in_right);
        proposalLayout.startAnimation(slideIn);
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

    // Nuevo método para enviar la propuesta completa al webhook
    private void saveCurrentProposal() {
        if (currentProposal == null) {
            Toast.makeText(this, "No hay ninguna propuesta para guardar", Toast.LENGTH_SHORT).show();
            return;
        }

        btnSaveRecipe.setEnabled(false);
        try {
            JSONObject data = new JSONObject();
            data.put("dish_name", currentProposal.dishName);
            data.put("innovation", currentProposal.innovation);
            data.put("protein", currentProposal.protein);
            data.put("contrast", currentProposal.contrast);
            data.put("texture", currentProposal.texture);
            data.put("umami", currentProposal.umami);
            data.put("presentation", currentProposal.presentation);
            data.put("technique", currentProposal.technique);
            data.put("ingredients", TextUtils.join(", ", currentProposal.ingredients));
            data.put("timestamp", System.currentTimeMillis());
            data.put("source", "android_app");
            data.put("function", "chef_innovador_save");

            // También enviar como arreglo
            JSONArray arr = new JSONArray();
            for (String ing : currentProposal.ingredients) arr.put(ing);
            data.put("ingredients_array", arr);

            // Enviar usando el webhook definido en NetworkManager
            NetworkManager.sendToN8N(this, NetworkManager.WEBHOOK_CHEF_INNOVADOR, data, new NetworkManager.NetworkCallback() {
                @Override
                public void onSuccess(String response) {
                    runOnUiThread(() -> {
                        btnSaveRecipe.setEnabled(true);
                        Toast.makeText(ChefInnovadorActivity.this, "Receta guardada y enviada a N8N", Toast.LENGTH_SHORT).show();
                        Log.d("N8N", "Guardado exitoso: " + response);
                    });
                }

                @Override
                public void onError(String error) {
                    runOnUiThread(() -> {
                        btnSaveRecipe.setEnabled(true);
                        Toast.makeText(ChefInnovadorActivity.this, "Error guardando receta: " + error, Toast.LENGTH_LONG).show();
                        Log.e("N8N", "Error guardando receta: " + error);
                    });
                }
            });

        } catch (JSONException e) {
            btnSaveRecipe.setEnabled(true);
            Toast.makeText(this, "Error preparando datos: " + e.getMessage(), Toast.LENGTH_LONG).show();
            Log.e("N8N", "JSONException: " + e.getMessage());
        }
    }

    private static class GourmetProposal {
        String dishName;
        String innovation;
        String protein;
        String contrast;
        String texture;
        String umami;
        String presentation;
        String technique;
        List<String> ingredients; // nueva lista
    }
}

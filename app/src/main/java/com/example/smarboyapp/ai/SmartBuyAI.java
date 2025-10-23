package com.example.smarboyapp.ai;

import com.example.smarboyapp.database.UserPreferencesManager;
import com.example.smarboyapp.utils.SmartRecommendationEngine;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class SmartBuyAI {

    private UserPreferencesManager preferencesManager;
    private Random random;

    public SmartBuyAI(UserPreferencesManager preferencesManager) {
        this.preferencesManager = preferencesManager;
        this.random = new Random();
    }

    public String generateResponse(String userMessage) {
        String message = userMessage.toLowerCase().trim();

        // Crear perfil del usuario para respuestas personalizadas
        SmartRecommendationEngine.UserProfile userProfile = createUserProfile();

        // Categorizar la pregunta y generar respuesta apropiada
        if (isGreeting(message)) {
            return generateGreeting(userProfile);
        } else if (isFoodQuestion(message)) {
            return generateFoodAdvice(message, userProfile);
        } else if (isExerciseQuestion(message)) {
            return generateExerciseAdvice(message, userProfile);
        } else if (isHealthQuestion(message)) {
            return generateHealthAdvice(message, userProfile);
        } else if (isBudgetQuestion(message)) {
            return generateBudgetAdvice(message, userProfile);
        } else if (isWeightQuestion(message)) {
            return generateWeightAdvice(message, userProfile);
        } else if (isMotivationQuestion(message)) {
            return generateMotivation(userProfile);
        } else {
            return generateGeneralResponse(message, userProfile);
        }
    }

    private SmartRecommendationEngine.UserProfile createUserProfile() {
        SmartRecommendationEngine.UserProfile profile = new SmartRecommendationEngine.UserProfile();
        profile.name = preferencesManager.getUserName();
        profile.age = preferencesManager.getUserAge();
        profile.weight = preferencesManager.getUserWeight();
        profile.height = preferencesManager.getUserHeight();
        profile.budget = preferencesManager.getUserBudget();
        return profile;
    }

    private boolean isGreeting(String message) {
        List<String> greetings = Arrays.asList("hola", "buenas", "buenos días", "buenas tardes", "hey", "hi");
        return greetings.stream().anyMatch(message::contains);
    }

    private boolean isFoodQuestion(String message) {
        List<String> foodKeywords = Arrays.asList("comer", "comida", "almorzar", "desayunar", "cenar",
            "plato", "receta", "cocinar", "alimento", "nutrición", "dieta");
        return foodKeywords.stream().anyMatch(message::contains);
    }

    private boolean isExerciseQuestion(String message) {
        List<String> exerciseKeywords = Arrays.asList("ejercicio", "gym", "gimnasio", "entrenar",
            "workout", "rutina", "fitness", "cardio", "pesas", "correr");
        return exerciseKeywords.stream().anyMatch(message::contains);
    }

    private boolean isHealthQuestion(String message) {
        List<String> healthKeywords = Arrays.asList("salud", "saludable", "imc", "peso", "altura",
            "consejos", "tips", "bienestar", "vida sana");
        return healthKeywords.stream().anyMatch(message::contains);
    }

    private boolean isBudgetQuestion(String message) {
        List<String> budgetKeywords = Arrays.asList("presupuesto", "dinero", "barato", "económico",
            "precio", "costo", "gastar", "ahorrar");
        return budgetKeywords.stream().anyMatch(message::contains);
    }

    private boolean isWeightQuestion(String message) {
        List<String> weightKeywords = Arrays.asList("bajar de peso", "perder peso", "adelgazar",
            "ganar peso", "engordar", "masa muscular", "definir");
        return weightKeywords.stream().anyMatch(message::contains);
    }

    private boolean isMotivationQuestion(String message) {
        List<String> motivationKeywords = Arrays.asList("motivación", "ánimo", "desanimado",
            "cansado", "rendirse", "difícil", "no puedo");
        return motivationKeywords.stream().anyMatch(message::contains);
    }

    private String generateGreeting(SmartRecommendationEngine.UserProfile profile) {
        String[] greetings = {
            "¡Hola " + profile.name + "! 👋 Soy tu asistente personal de SmartBuy. ¿En qué puedo ayudarte hoy?",
            "¡Buenos días, " + profile.name + "! 🌅 Estoy aquí para ayudarte con recomendaciones de comida y ejercicio.",
            "¡Hola! 😊 ¿Listo para hacer elecciones inteligentes hoy? Pregúntame lo que necesites."
        };
        return greetings[random.nextInt(greetings.length)];
    }

    private String generateFoodAdvice(String message, SmartRecommendationEngine.UserProfile profile) {
        String bmiCategory = profile.getBMICategory();
        double bmi = profile.calculateBMI();

        StringBuilder response = new StringBuilder();
        response.append("🍽️ ¡Excelente pregunta sobre alimentación, ").append(profile.name).append("! ");

        if (message.contains("desayunar")) {
            response.append("Para el desayuno te recomiendo: ");
            switch (bmiCategory) {
                case "underweight":
                    response.append("Avena con frutas y nueces 🥜, huevos revueltos 🍳, y un batido proteico 🥤");
                    break;
                case "overweight":
                case "obese":
                    response.append("Yogurt griego bajo en grasa 🥛, frutas frescas 🍓, y té verde 🍵");
                    break;
                default:
                    response.append("Un desayuno balanceado con proteína, carbohidratos complejos y frutas 🍌");
            }
        } else if (message.contains("almorzar")) {
            response.append("Para el almuerzo considera: ");
            switch (bmiCategory) {
                case "underweight":
                    response.append("Pollo con quinoa 🍗, aguacate 🥑, y arroz integral");
                    break;
                case "overweight":
                case "obese":
                    response.append("Ensalada grande con proteína magra 🥗, pescado al vapor 🐟");
                    break;
                default:
                    response.append("Un plato equilibrado con 50% vegetales, 25% proteína y 25% carbohidratos");
            }
        } else {
            response.append("Basado en tu IMC de ").append(String.format("%.1f", bmi)).append(", te sugiero ");
            switch (bmiCategory) {
                case "underweight":
                    response.append("aumentar las calorías saludables con frutos secos, batidos proteicos y comidas frecuentes.");
                    break;
                case "overweight":
                case "obese":
                    response.append("enfocarte en alimentos bajos en calorías pero ricos en nutrientes como vegetales y proteínas magras.");
                    break;
                default:
                    response.append("mantener una dieta balanceada con porciones moderadas y variedad de nutrientes.");
            }
        }

        response.append("\n\n💡 ¿Te gustaría que busque opciones específicas en tu área?");
        return response.toString();
    }

    private String generateExerciseAdvice(String message, SmartRecommendationEngine.UserProfile profile) {
        String bmiCategory = profile.getBMICategory();
        String ageGroup = profile.getAgeGroup();

        StringBuilder response = new StringBuilder();
        response.append("💪 ¡Perfecto, ").append(profile.name).append("! El ejercicio es clave para tu bienestar. ");

        response.append("Basado en tu perfil, te recomiendo: ");

        switch (bmiCategory) {
            case "underweight":
                response.append("\n🏋️ Entrenamiento de fuerza 3-4 veces por semana")
                        .append("\n💪 Ejercicios compuestos como sentadillas y press de banca")
                        .append("\n⏱️ Descanso de 48-72 horas entre sesiones");
                break;
            case "overweight":
            case "obese":
                response.append("\n🏃 Cardio moderado 4-5 veces por semana")
                        .append("\n🚴 Actividades de bajo impacto como natación o bicicleta")
                        .append("\n🧘 Yoga o pilates para flexibilidad");
                break;
            default:
                response.append("\n⚖️ Combinación de cardio y fuerza")
                        .append("\n🏃 3 días de cardio, 2-3 días de pesas")
                        .append("\n🤸 Ejercicios funcionales");
        }

        // Ajustar por edad
        if ("senior".equals(ageGroup)) {
            response.append("\n\n👴 Por tu edad, enfócate en ejercicios de bajo impacto y equilibrio.");
        } else if ("young".equals(ageGroup)) {
            response.append("\n\n⚡ A tu edad puedes incluir entrenamientos más intensos como HIIT.");
        }

        response.append("\n\n🏥 Recuerda: siempre consulta con un profesional antes de comenzar una rutina nueva.");
        return response.toString();
    }

    private String generateHealthAdvice(String message, SmartRecommendationEngine.UserProfile profile) {
        double bmi = profile.calculateBMI();
        String bmiCategory = profile.getBMICategory();

        StringBuilder response = new StringBuilder();
        response.append("🏥 ¡Excelente que te preocupes por tu salud, ").append(profile.name).append("! ");

        response.append("Tu IMC actual es ").append(String.format("%.1f", bmi)).append(" (").append(bmiCategory).append("). ");

        switch (bmiCategory) {
            case "underweight":
                response.append("Esto indica que podrías beneficiarte de ganar peso saludable. ")
                        .append("🥑 Incluye grasas saludables, 🥜 frutos secos y 🥤 batidos proteicos en tu dieta.");
                break;
            case "normal":
                response.append("¡Felicidades! Mantienes un peso saludable. ")
                        .append("💚 Continúa con tus hábitos actuales y mantén un estilo de vida activo.");
                break;
            case "overweight":
                response.append("Te beneficiarías de perder algunos kilos de forma gradual. ")
                        .append("🥗 Enfócate en una dieta balanceada y 🏃 ejercicio regular.");
                break;
            case "obese":
                response.append("Es importante trabajar en reducir peso para mejorar tu salud general. ")
                        .append("👨‍⚕️ Te recomiendo consultar con un profesional de la salud.");
                break;
        }

        response.append("\n\n💡 Consejos generales:")
                .append("\n💧 Bebe 8 vasos de agua al día")
                .append("\n😴 Duerme 7-8 horas diarias")
                .append("\n🧘 Practica técnicas de relajación");

        return response.toString();
    }

    private String generateBudgetAdvice(String message, SmartRecommendationEngine.UserProfile profile) {
        double budget = profile.budget;

        StringBuilder response = new StringBuilder();
        response.append("💰 ¡Excelente pregunta sobre presupuesto! ");

        if (budget > 0) {
            double dailyBudget = budget / 30;
            response.append("Con tu presupuesto mensual de Q").append(String.format("%.0f", budget))
                    .append(" (aproximadamente Q").append(String.format("%.0f", dailyBudget)).append(" diarios), ");

            if (dailyBudget < 50) {
                response.append("te recomiendo:")
                        .append("\n🍚 Arroz, frijoles y huevos como base")
                        .append("\n🍌 Frutas de temporada")
                        .append("\n🥕 Verduras locales")
                        .append("\n🏠 Cocinar en casa siempre que sea posible");
            } else if (dailyBudget < 100) {
                response.append("puedes permitirte:")
                        .append("\n🍗 Proteínas variadas (pollo, pescado)")
                        .append("\n🥗 Ensaladas frescas")
                        .append("\n🍽️ Ocasionalmente comer fuera")
                        .append("\n🥤 Batidos y suplementos básicos");
            } else {
                response.append("tienes flexibilidad para:")
                        .append("\n🥩 Proteínas premium")
                        .append("\n🍱 Comida preparada de calidad")
                        .append("\n💎 Suplementos especializados")
                        .append("\n🍽️ Restaurantes saludables");
            }
        } else {
            response.append("Para optimizar tu presupuesto:")
                    .append("\n📝 Planifica tus comidas semanalmente")
                    .append("\n🛒 Compra ingredientes básicos en cantidad")
                    .append("\n🏠 Cocina en lotes grandes")
                    .append("\n💡 Busca ofertas y descuentos");
        }

        return response.toString();
    }

    private String generateWeightAdvice(String message, SmartRecommendationEngine.UserProfile profile) {
        String bmiCategory = profile.getBMICategory();

        StringBuilder response = new StringBuilder();
        response.append("⚖️ Entiendo tu preocupación por el peso, ").append(profile.name).append(". ");

        if (message.contains("bajar") || message.contains("perder") || message.contains("adelgazar")) {
            if ("overweight".equals(bmiCategory) || "obese".equals(bmiCategory)) {
                response.append("Es un objetivo saludable para ti. ")
                        .append("\n📉 Meta: Perder 0.5-1 kg por semana")
                        .append("\n🔥 Crear déficit calórico de 500 cal/día")
                        .append("\n🏃 Combinar cardio y entrenamiento de fuerza")
                        .append("\n🥗 Priorizar alimentos integrales y vegetales");
            } else {
                response.append("Tu peso actual está en rango saludable. ")
                        .append("En lugar de perder peso, considera enfocarte en:")
                        .append("\n💪 Ganar masa muscular")
                        .append("\n🏃 Mejorar tu condición física")
                        .append("\n⚖️ Mantener tu peso actual");
            }
        } else if (message.contains("ganar") || message.contains("engordar") || message.contains("masa")) {
            if ("underweight".equals(bmiCategory)) {
                response.append("Es un objetivo apropiado para ti. ")
                        .append("\n📈 Meta: Ganar 0.25-0.5 kg por semana")
                        .append("\n🍽️ Aumentar calorías en 300-500 por día")
                        .append("\n💪 Entrenamiento de fuerza 3-4 veces/semana")
                        .append("\n🥤 Batidos proteicos entre comidas");
            } else {
                response.append("Si quieres ganar masa muscular:")
                        .append("\n🏋️ Entrenamiento de resistencia constante")
                        .append("\n🍗 Proteína: 1.6-2.2g por kg de peso corporal")
                        .append("\n😴 Descanso adecuado para recuperación")
                        .append("\n⏱️ Paciencia: los resultados toman tiempo");
            }
        }

        response.append("\n\n⚠️ Importante: Los cambios sostenibles son graduales. Consulta un profesional para un plan personalizado.");
        return response.toString();
    }

    private String generateMotivation(SmartRecommendationEngine.UserProfile profile) {
        String[] motivationalMessages = {
            "💪 ¡No te rindas, " + profile.name + "! Cada pequeño paso cuenta hacia tu objetivo.",
            "🌟 Recuerda por qué empezaste. Tu salud es la mejor inversión que puedes hacer.",
            "🏆 Los resultados no llegan de la noche a la mañana, pero cada día que persistes te acercas más a tu meta.",
            "💚 Tu cuerpo es capaz de cosas increíbles. Dale tiempo, nutrición y movimiento.",
            "🚀 No se trata de perfección, se trata de progreso. ¡Sigue adelante!"
        };

        String baseMessage = motivationalMessages[random.nextInt(motivationalMessages.length)];

        return baseMessage + "\n\n💡 Consejo del día: Celebra cada pequeño logro. ¿Qué pequeña acción saludable puedes hacer ahora mismo?";
    }

    private String generateGeneralResponse(String message, SmartRecommendationEngine.UserProfile profile) {
        String[] responses = {
            "🤔 Interesante pregunta. Como tu asistente de SmartBuy, puedo ayudarte con recomendaciones de comida, ejercicio, nutrición y bienestar.",
            "💡 No estoy seguro de entender completamente. ¿Podrías ser más específico sobre alimentación o ejercicio?",
            "🎯 Mi especialidad es ayudarte con decisiones inteligentes sobre comida y fitness. ¿En qué área específica necesitas ayuda?",
            "🤖 Estoy aquí para ser tu guía en temas de salud y bienestar. ¿Qué aspecto te interesa más: nutrición o ejercicio?"
        };

        String baseResponse = responses[random.nextInt(responses.length)];

        return baseResponse + "\n\n📋 Puedes preguntarme sobre:\n• Qué comer según tu perfil\n• Rutinas de ejercicio\n• Consejos de salud\n• Presupuesto para alimentación";
    }
}

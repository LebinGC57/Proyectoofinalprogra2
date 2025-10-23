# 🔄 Integración con n8n Webhook

## 📋 Descripción
Tu app SmartBoy ahora está conectada con n8n mediante un webhook que permite sincronizar datos y eventos en tiempo real.

## 🔗 URL del Webhook
```
https://lebincc.app.n8n.cloud/webhook-test/android-synck
```

## 🚀 Funcionalidades Implementadas

### 1. **N8nWebhookService** (Servicio Principal)
Ubicación: `app/src/main/java/com/example/smarboyapp/api/N8nWebhookService.java`

#### Métodos disponibles:

- **`sendToWebhook(Map<String, Object> data, WebhookCallback callback)`**
  - Envía datos genéricos al webhook
  - Ejemplo:
  ```java
  Map<String, Object> data = new HashMap<>();
  data.put("evento", "test");
  data.put("usuario", "Juan");
  
  N8nWebhookService.sendToWebhook(data, new N8nWebhookService.WebhookCallback() {
      @Override
      public void onSuccess(String response) {
          // Éxito
      }
      
      @Override
      public void onError(String error) {
          // Error
      }
  });
  ```

- **`sendUserPreferences(String userName, List<String> preferences, WebhookCallback callback)`**
  - Envía las preferencias del usuario
  - Se ejecuta automáticamente cuando el usuario selecciona sus preferencias

- **`sendRecommendationInteraction(String userName, String itemName, String itemType, String action, WebhookCallback callback)`**
  - Registra interacciones con recomendaciones (visto, guardado, ignorado)

- **`sendReview(String userName, String itemName, float rating, String reviewText, WebhookCallback callback)`**
  - Envía calificaciones y reseñas de los usuarios

### 2. **N8nSyncManager** (Manager de Alto Nivel)
Ubicación: `app/src/main/java/com/example/smarboyapp/utils/N8nSyncManager.java`

Facilita el uso del webhook desde cualquier actividad.

#### Métodos disponibles:

```java
N8nSyncManager syncManager = new N8nSyncManager(context);

// Login del usuario
syncManager.syncUserLogin("email@ejemplo.com");

// Cuando agrega un favorito
syncManager.syncFavoriteAdded("Ensalada César", "food", "Q25");

// Actualización de perfil
syncManager.syncProfileUpdate("Juan", 25, 70.5, 175);

// Interacción con IA
syncManager.syncAiInteraction("¿Qué comida saludable recomiendas?", "Te recomiendo...");

// Búsqueda
syncManager.syncSearch("pizza", 10);

// Evento personalizado
Map<String, Object> customData = new HashMap<>();
customData.put("accion", "compartir");
customData.put("item", "Ensalada");
syncManager.syncCustomEvent("share_item", customData);
```

## 📊 Estructura de Datos Enviados

### Preferencias del Usuario
```json
{
  "event_type": "user_preferences",
  "user_name": "Juan Pérez",
  "preferences": [
    "food_Comida Saludable",
    "gym_Ganar Músculo",
    "budget_Medio (Q100-Q300)"
  ],
  "timestamp": 1234567890
}
```

### Login de Usuario
```json
{
  "event_type": "user_login",
  "user_name": "Juan Pérez",
  "email": "juan@ejemplo.com",
  "timestamp": 1234567890,
  "device": "Android"
}
```

### Favorito Agregado
```json
{
  "event_type": "favorite_added",
  "user_name": "Juan Pérez",
  "item_name": "Ensalada César",
  "item_type": "food",
  "item_price": "Q25",
  "timestamp": 1234567890
}
```

### Actualización de Perfil
```json
{
  "event_type": "profile_update",
  "user_name": "Juan Pérez",
  "age": 25,
  "weight": 70.5,
  "height": 175,
  "bmi": 23.02,
  "timestamp": 1234567890
}
```

### Interacción con IA
```json
{
  "event_type": "ai_interaction",
  "user_name": "Juan Pérez",
  "user_message": "¿Qué comida saludable recomiendas?",
  "ai_response": "Te recomiendo ensalada césar...",
  "timestamp": 1234567890
}
```

### Búsqueda
```json
{
  "event_type": "search",
  "user_name": "Juan Pérez",
  "search_query": "pizza",
  "results_count": 10,
  "timestamp": 1234567890
}
```

## 💡 Casos de Uso

### 1. Sincronizar cuando el usuario se registra
```java
// En GoogleAuthActivity.java o donde manejes el login
N8nSyncManager syncManager = new N8nSyncManager(this);
syncManager.syncUserLogin(userEmail);
```

### 2. Sincronizar cuando agrega a favoritos
```java
// En DetailedFoodRecommendationActivity.java
N8nSyncManager syncManager = new N8nSyncManager(this);
syncManager.syncFavoriteAdded(foodName, "food", foodPrice);
```

### 3. Sincronizar actualización de perfil
```java
// En EditProfileActivity.java
N8nSyncManager syncManager = new N8nSyncManager(this);
syncManager.syncProfileUpdate(name, age, weight, height);
```

### 4. Sincronizar interacción con el asistente
```java
// En AiAssistantActivity.java
N8nSyncManager syncManager = new N8nSyncManager(this);
syncManager.syncAiInteraction(userMessage, aiResponse);
```

## 🔧 Configuración en n8n

En tu flujo de n8n (https://lebincc.app.n8n.cloud), deberías configurar:

1. **Webhook Node** (POST)
   - URL: `/webhook-test/android-synck`
   - Método: POST
   - Recibe datos JSON

2. **Posibles acciones**:
   - Guardar en base de datos (Google Sheets, Airtable, etc.)
   - Enviar notificaciones (Email, Slack, Discord, etc.)
   - Analizar datos (filtrar por tipo de evento)
   - Generar reportes
   - Integrar con otros servicios

## 📝 Ejemplo de Flujo en n8n

```
Webhook (Recibir datos)
    ↓
Switch (Por tipo de evento)
    ↓
    ├─ user_preferences → Guardar en Google Sheets
    ├─ user_login → Enviar email de bienvenida
    ├─ favorite_added → Actualizar estadísticas
    ├─ ai_interaction → Guardar en base de datos para análisis
    └─ search → Registrar búsquedas populares
```

## ✅ Estado de Implementación

- ✅ Servicio N8nWebhookService creado
- ✅ Manager N8nSyncManager creado
- ✅ Integración en PreferencesActivity (envía preferencias automáticamente)
- ✅ Integración en GoogleAuthActivity (envía login/registro)
- ✅ Integración en DetailedFoodRecommendationActivity (envía favoritos)
- ✅ Integración en AiAssistantActivity (envía interacciones con IA)
- ✅ Integración en EditProfileActivity (envía actualizaciones de perfil)
- ✅ Permisos de internet configurados

## 🎯 Eventos Que Se Sincronizan Automáticamente

1. **Login/Registro** → Cuando el usuario inicia sesión o se registra
2. **Preferencias** → Cuando selecciona sus preferencias de comida/gym
3. **Favoritos** → Cuando guarda una recomendación en favoritos
4. **Perfil** → Cuando actualiza su información personal
5. **IA** → Cada conversación con el asistente inteligente

## 🧪 Cómo Probar la Integración

### Desde tu App:
1. Ejecuta la app en el emulador o dispositivo
2. Realiza cualquier acción (login, guardar favorito, etc.)
3. Verás un Toast que dice "🔄 Sincronizado"

### En n8n:
1. Ve a tu flujo en https://lebincc.app.n8n.cloud
2. Observa los datos que llegan al webhook
3. Configura nodos para procesar los eventos

### Ver logs en Android Studio:
```
Logcat → Filtrar por: N8nWebhookService, N8nSyncManager, o PreferencesActivity
```

## 📱 Ejemplo de Datos Reales

Cuando un usuario llamado "Juan" guarda una ensalada en favoritos, se envía:
```json
{
  "event_type": "favorite_added",
  "user_name": "Juan",
  "item_name": "Ensalada César Saludable",
  "item_type": "food",
  "item_price": "Q25",
  "timestamp": 1729641234567
}
```

## 🔐 Seguridad

- ✅ Conexión HTTPS segura
- ✅ Timeout de 15 segundos para evitar bloqueos
- ✅ Manejo de errores sin afectar la experiencia del usuario
- ✅ Los datos se envían en segundo plano (no bloquea la UI)

## 🐛 Troubleshooting

- **No se envían datos**: Verifica que el dispositivo tenga conexión a internet
- **Error de conexión**: Revisa que la URL del webhook sea correcta
- **Timeout**: El webhook tiene un timeout de 15 segundos, revisa tu conexión
- **Logs**: Todos los errores se registran en Logcat con el tag correspondiente

## 📞 Soporte

Si necesitas ayuda con la configuración del webhook o tienes dudas, revisa los logs de Android Studio en la pestaña Logcat filtrando por:
- `N8nWebhookService`
- `N8nSyncManager`
- `PreferencesActivity`
- `GoogleAuthActivity`
- `AiAssistantActivity`

## 🎉 ¡Listo para Usar!

Tu app ahora está completamente integrada con n8n. Cada acción importante del usuario se sincroniza automáticamente con tu webhook, permitiéndote:
- 📊 Analizar el comportamiento de los usuarios
- 📧 Enviar notificaciones personalizadas
- 💾 Guardar datos en la nube
- 🤖 Automatizar procesos
- 📈 Generar reportes y estadísticas

package com.example.smarboyapp;

import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smarboyapp.adapter.ChatAdapter;
import com.example.smarboyapp.ai.SmartBuyAI;
import com.example.smarboyapp.database.UserPreferencesManager;
import com.example.smarboyapp.model.ChatMessage;
import com.example.smarboyapp.utils.N8nSyncManager;

import java.util.ArrayList;
import java.util.List;

public class AiAssistantActivity extends AppCompatActivity {

    private RecyclerView recyclerViewChat;
    private EditText etMessage;
    private Button btnSend, btnBackChat;

    private ChatAdapter chatAdapter;
    private List<ChatMessage> messages;
    private SmartBuyAI smartBuyAI;
    private N8nSyncManager syncManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai_assistant);

        initializeViews();
        setupAI();
        setupRecyclerView();
        setupListeners();
        showWelcomeMessage();
    }

    private void initializeViews() {
        recyclerViewChat = findViewById(R.id.recyclerViewChat);
        etMessage = findViewById(R.id.etMessage);
        btnSend = findViewById(R.id.btnSend);
        btnBackChat = findViewById(R.id.btnBackChat);
    }

    private void setupAI() {
        UserPreferencesManager preferencesManager = new UserPreferencesManager(this);
        smartBuyAI = new SmartBuyAI(preferencesManager);
        syncManager = new N8nSyncManager(this);
    }

    private void setupRecyclerView() {
        messages = new ArrayList<>();
        chatAdapter = new ChatAdapter(messages);
        recyclerViewChat.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewChat.setAdapter(chatAdapter);
    }

    private void setupListeners() {
        btnSend.setOnClickListener(v -> sendMessage());
        btnBackChat.setOnClickListener(v -> finish()); // Botón para regresar

        etMessage.setOnEditorActionListener((v, actionId, event) -> {
            sendMessage();
            return true;
        });
    }

    private void showWelcomeMessage() {
        // Mensaje de bienvenida más simple sin preguntas predeterminadas
        ChatMessage welcomeMessage = new ChatMessage(
            "¡Hola! 👋 Soy tu asistente inteligente de SmartBoy. Puedo ayudarte con:\n\n" +
            "🍽️ Recomendaciones de comida\n" +
            "🏋️ Consejos de gimnasio\n" +
            "💡 Sugerencias personalizadas\n" +
            "📊 Información nutricional\n\n" +
            "¿En qué puedo ayudarte hoy?",
            false
        );

        messages.add(welcomeMessage);
        chatAdapter.notifyItemInserted(messages.size() - 1);
        scrollToBottom();
    }

    private void sendMessage() {
        String messageText = etMessage.getText().toString().trim();
        if (messageText.isEmpty()) {
            return;
        }

        // Agregar mensaje del usuario
        ChatMessage userMessage = new ChatMessage(messageText, true);
        messages.add(userMessage);
        chatAdapter.notifyItemInserted(messages.size() - 1);
        scrollToBottom();

        // Limpiar campo de texto
        etMessage.setText("");

        // Mostrar indicador de escritura
        showTypingIndicator();

        // Simular delay de respuesta del AI
        new Handler().postDelayed(() -> {
            hideTypingIndicator();
            String aiResponse = smartBuyAI.generateResponse(messageText);

            ChatMessage aiMessage = new ChatMessage(aiResponse, false);
            messages.add(aiMessage);
            chatAdapter.notifyItemInserted(messages.size() - 1);
            scrollToBottom();

            // Sincronizar interacción con n8n
            syncManager.syncAiInteraction(messageText, aiResponse);
        }, 1500);
    }

    private void showTypingIndicator() {
        ChatMessage typingMessage = new ChatMessage("🤖 Escribiendo...", false);
        messages.add(typingMessage);
        chatAdapter.notifyItemInserted(messages.size() - 1);
        scrollToBottom();
    }

    private void hideTypingIndicator() {
        if (!messages.isEmpty() && messages.get(messages.size() - 1).getMessage().contains("Escribiendo...")) {
            messages.remove(messages.size() - 1);
            chatAdapter.notifyItemRemoved(messages.size());
        }
    }

    private void scrollToBottom() {
        if (!messages.isEmpty()) {
            recyclerViewChat.smoothScrollToPosition(messages.size() - 1);
        }
    }
}

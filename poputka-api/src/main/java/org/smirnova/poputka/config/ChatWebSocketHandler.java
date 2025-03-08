package org.smirnova.poputka.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final List<String> generalChatHistory = new ArrayList<>();
    private final Map<String, List<String>> privateChatHistory = new ConcurrentHashMap<>();
    private final Map<String, WebSocketSession> userSessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String userId = getUserIdFromSession(session);
        if (userId != null) {
            userSessions.put(userId, session);

            for (String message : generalChatHistory) {
                session.sendMessage(new TextMessage(message));
            }

            for (Map.Entry<String, List<String>> entry : privateChatHistory.entrySet()) {
                if (entry.getKey().equals(userId)) {
                    for (String message : entry.getValue()) {
                        session.sendMessage(new TextMessage(message));
                    }
                }
            }
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        Map<String, String> payload = objectMapper.readValue(message.getPayload(), Map.class);
        String from = payload.get("from");
        String to = payload.get("to");
        String text = payload.get("text");

        String formattedMessage = to.equals("general")
                ? String.format("GENERAL: %s: %s", from, text)
                : String.format("FROM %s TO %s: %s", from, to, text);

        if (to.equals("general")) {
            // Добавляем в историю общего чата
            generalChatHistory.add(formattedMessage);
            for (WebSocketSession wsSession : userSessions.values()) {
                if (wsSession.isOpen()) {
                    wsSession.sendMessage(new TextMessage(formattedMessage));
                }
            }
        } else {
            // Добавляем в историю чата между пользователями
            privateChatHistory.computeIfAbsent(from, k -> new ArrayList<>()).add(formattedMessage);
            privateChatHistory.computeIfAbsent(to, k -> new ArrayList<>()).add(formattedMessage);

            WebSocketSession recipientSession = userSessions.get(to);
            if (recipientSession != null && recipientSession.isOpen()) {
                recipientSession.sendMessage(new TextMessage(formattedMessage));
            }

            session.sendMessage(new TextMessage(formattedMessage));
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, org.springframework.web.socket.CloseStatus status) throws Exception {
        String userId = getUserIdFromSession(session);
        if (userId != null) {
            userSessions.remove(userId);
        }
    }

    private String getUserIdFromSession(WebSocketSession session) {
        String query = session.getUri().getQuery();
        if (query != null && query.startsWith("userId=")) {
            return query.split("=")[1];
        }
        return null;
    }
}

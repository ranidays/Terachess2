package raaidsm.spring.test.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import raaidsm.spring.test.database.UserDataServiceImpl;
import raaidsm.spring.test.web_models.ChatMessage;
import raaidsm.spring.test.web_models.MessageType;

import java.util.Objects;

@Controller
public class ChatController {
    private final Logger logger = LoggerFactory.getLogger(ChatController.class);
    private final UserDataServiceImpl userDataService;

    public ChatController(UserDataServiceImpl userDataService) {
        this.userDataService = userDataService;
    }

    @MessageMapping("/new-user")
    @SendTo("/topic/public")
    public ChatMessage newUser(@Payload final ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
        String currentUserColourName = userDataService.addUser(chatMessage.getSender());
        ChatMessage newChatMessage;
        if (currentUserColourName != null) {
            Objects.requireNonNull(headerAccessor.getSessionAttributes()).put("username", chatMessage.getSender());
            newChatMessage = ChatMessage.builder()
                    .type(chatMessage.getType())
                    .sender(chatMessage.getSender())
                    .content(chatMessage.getSender() + " connected!")
                    .userColour(currentUserColourName)
                    .build();
        }
        else {
            newChatMessage = ChatMessage.builder()
                    .type(MessageType.REJECT)
                    .sender(chatMessage.getSender())
                    .content("User rejected. Game is full!")
                    .build();
        }
        return newChatMessage;
    }

    @MessageMapping("/message")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(@Payload final ChatMessage chatMessage) {
        return chatMessage;
    }

    @MessageMapping("/move")
    @SendTo("/topic/public")
    public ChatMessage makeMove(@Payload final ChatMessage chatMessage) {
        return chatMessage;
    }
}
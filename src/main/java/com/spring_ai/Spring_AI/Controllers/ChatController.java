package com.spring_ai.Spring_AI.Controllers;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class ChatController {
    // to show that the controller is working
//    @GetMapping("/chat")
//    public ResponseEntity<String> chat(
//            @RequestParam(value = "message", required = true ) String message
//    ) {
//        return ResponseEntity.ok("Hello from ChatController!");
//    }

    // now to implement the chat functionality to ask questions to the AI model4



    private final ChatClient chatClient;
    public ChatController(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }
    @GetMapping("/ai")
    public String GetAiResponse(@RequestParam String prompt){
        return chatClient.prompt(prompt)
                .call()
                .content();
    }
}

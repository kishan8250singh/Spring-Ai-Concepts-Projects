package com.spring_ai.Spring_AI.Controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring_ai.Spring_AI.DTO.AiResponse;
import org.springframework.ai.chat.client.ChatClient;
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
    private final ObjectMapper objectMapper = new ObjectMapper();
    public ChatController(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }
    @GetMapping("/ai")
    public String GetAiResponse(@RequestParam String prompt){
        return chatClient.prompt(prompt)
                .call()
                .content();
    }

    // learning : System + User prompt(Prompt engineering)f
    @GetMapping("ai/explain")
    public String GetAiExplainResponse(@RequestParam String prompt){
        String systemPrompt = "You are a helpful assistant that explains complex concepts in simple terms.";
         return chatClient.prompt()
                .system(systemPrompt) // train the model
                .user("explain the prompt" + prompt)         // ask question
                .call()
                .content();
    }

    // learning JSON response
    @GetMapping("ai/summary")
    public AiResponse getSummary(@RequestParam String topic) throws Exception {

        String jsonResponse = chatClient
            .prompt("""
                        You are an assistant for Java backend students.
                        For the given topic, return a JSON only in this exact format:
                        {
                          "summary": "short summary here",
                          "difficulty": "Easy/Medium/Hard",
                          "rating": 1-10
                        }

                        Topic: %s
                        Do not add any extra text outside JSON.
                        """.formatted(topic))
            .call()
            .content();

    // JSON (String) -> AiResponse (Java object)
        return objectMapper.readValue(jsonResponse, AiResponse.class);

    }
}

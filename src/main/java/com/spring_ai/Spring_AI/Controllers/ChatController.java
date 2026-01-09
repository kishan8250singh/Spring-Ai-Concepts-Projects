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
   
    // learning JSON output and mapping to Java object
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

    // learning JSON output and a retry mechanism + fallback


    @GetMapping("ai/safe-summary")
    public AiResponse getStrictSummary(@RequestParam String topic) throws Exception {
        // first attempt simple JSON prompt
        String JsonResponse = callAiForJson(topic);
        // try to map to Java object
        try{
            return objectMapper.readValue(JsonResponse, AiResponse.class);
        }catch(Exception e){
            // if mapping fails, try stricter JSON prompt
            String strictJsonResponse = callAiForStrictJson(topic);
        }
           try{
               // map to Java object
               return objectMapper.readValue(callAiForStrictJson(topic), AiResponse.class);
           }catch (Exception ex){
               // if still fails, return default AiResponse
              AiResponse fallback = new AiResponse();
                fallback.setSummary("Not sure about this topic");
                fallback.setSummary("Unknown");
                fallback.setRating(0);
                return fallback;
        }
    }


     // # helper method -> simple JSON Prompt call
    public String callAiForJson(String topic) {
        return chatClient
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
    }
    // # helper method -> Stricter JSON Prompt call
    public String callAiForStrictJson(String topic) {
        return chatClient
            .prompt("""
                    IMPORTANT: Return ONLY valid JSON. No explanation, no extra text.
                    JSON format:
                        {
                          "summary": "short summary here",
                          "difficulty": "Easy/Medium/Hard",
                          "rating": 1-10
                        }

                        Topic: %s
                           If you cannot understand, still return JSON with:
                               {
                                 "summary": "Not sure about this topic",
                                 "difficulty": "Unknown",
                                 "rating": 0
                               }
                   """.formatted(topic))
            .call()
            .content();
    }
}


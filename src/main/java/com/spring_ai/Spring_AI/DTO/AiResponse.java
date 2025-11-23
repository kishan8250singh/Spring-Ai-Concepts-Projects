package com.spring_ai.Spring_AI.DTO;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class AiResponse {
    private String summary;
    private String difficulty;
    private int rating;
}

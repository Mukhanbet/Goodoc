package com.example.goodoc.controller;

import com.example.goodoc.dto.bot.BotRequest;
import com.example.goodoc.dto.bot.BotResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/bot")
public class BotController {
    @Value("${openai.model}")
    private String modelName;

    @Value("${openai.api.url}")
    private String apiURL;

    private final RestTemplate template;

    @GetMapping("/chat")
    @ResponseBody
    public Map<String, String> chat(@RequestParam("prompt") String prompt) {
        try {
            BotRequest botRequest = new BotRequest(modelName, prompt);
            BotResponse botResponse = template.postForObject(apiURL, botRequest, BotResponse.class);
            Map<String, String> responseMap = new HashMap<>();
            if (botResponse != null && botResponse.getChoices() != null && !botResponse.getChoices().isEmpty() && botResponse.getChoices().get(0).getMessage() != null) {
                responseMap.put("response", botResponse.getChoices().get(0).getMessage().getContent());
            } else {
                responseMap.put("response", "No response received");
            }
            return responseMap;
        } catch (Exception e) {
            log.error("Error communicating with OpenAI API", e);
            return Collections.singletonMap("response", "Error processing your request");
        }
    }
}

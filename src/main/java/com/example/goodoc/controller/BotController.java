package com.example.goodoc.controller;

import com.example.goodoc.dto.bot.BotRequest;
import com.example.goodoc.dto.bot.BotResponse;
import com.example.goodoc.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/bot")
@CrossOrigin(origins = "*", maxAge = 3600)
public class BotController {
    private final RestTemplate template;
    @Value("${openai.model}")
    private String modelName;
    @Value("${openai.api.url}")
    private String apiURL;

    @GetMapping("/chat")
    @ResponseBody
    public Map<String, String> chat(@RequestParam("prompt") String prompt) {
        try {
            BotRequest botRequest = new BotRequest(modelName, prompt);
            BotResponse botResponse = template.postForObject(apiURL, botRequest, BotResponse.class);
            return prepareResponse(botResponse);
        } catch (Exception e) {
            log.error("Error communicating with OpenAI API", e);
            throw new CustomException("Error processing your request", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/image-chat")
    @ResponseBody
    public Map<String, String> chatWithImage(@RequestParam("prompt") String prompt, @RequestParam("image") MultipartFile image) {
        try {
            String imageData = encodeFileToBase64(image);
            BotRequest botRequest = new BotRequest(modelName, prompt + "\n\n" + imageData);
            BotResponse botResponse = template.postForObject(apiURL, botRequest, BotResponse.class);
            return prepareResponse(botResponse);
        } catch (Exception e) {
            log.error("Error communicating with OpenAI API", e);
            throw new CustomException("Error processing your request", HttpStatus.BAD_REQUEST);
        }
    }

    private String encodeFileToBase64(MultipartFile file) throws IOException {
        return Base64.getEncoder().encodeToString(file.getBytes());
    }

    private Map<String, String> prepareResponse(BotResponse botResponse) {
        Map<String, String> responseMap = new HashMap<>();
        if (botResponse != null && botResponse.getChoices() != null && !botResponse.getChoices().isEmpty() && botResponse.getChoices().get(0).getMessage() != null) {
            responseMap.put("response", botResponse.getChoices().get(0).getMessage().getContent());
        } else {
            responseMap.put("response", "No response received");
        }
        return responseMap;
    }
}

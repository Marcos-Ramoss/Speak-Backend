package com.vozsocial.domain.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Service responsável pela integração com Google AI para transcrição e transformação de voz
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class GoogleAIService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${google.ai.api.key}")
    private String apiKey;

    @Value("${google.ai.api.base-url}")
    private String baseUrl;

    /**
     * Transcreve áudio usando Google AI
     */
    public String transcreverAudio(String audioDataUri) {
        log.debug("Iniciando transcrição de áudio");
        
        String url = baseUrl + "/models/gemini-2.0-flash-exp:generateContent?key=" + apiKey;
        
        Map<String, Object> requestBody = criarCorpoRequisicaoTranscricao(audioDataUri);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-goog-api-key", apiKey);
        
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
        
        ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
        
        return extrairTranscricao(response.getBody());
    }

    /**
     * Transforma voz para robótica usando Google AI TTS
     */
    public String transformarVozRobotica(String audioDataUri, String transcricao) {
        log.debug("Iniciando transformação de voz para robótica");
        
        // Implementação da transformação de voz
        // Por enquanto, retorna o áudio original com indicação de processamento
        log.warn("Transformação de voz robótica ainda não implementada completamente");
        
        return audioDataUri; // Placeholder
    }

    /**
     * Cria corpo da requisição para transcrição
     */
    private Map<String, Object> criarCorpoRequisicaoTranscricao(String audioDataUri) {
        Map<String, Object> requestBody = new HashMap<>();
        
        // Extrair dados base64 do dataUri
        String audioData = audioDataUri.contains(",") ? 
            audioDataUri.split(",")[1] : audioDataUri;
        
        Map<String, Object> inlineData = new HashMap<>();
        inlineData.put("mimeType", "audio/webm");
        inlineData.put("data", audioData);
        
        Map<String, Object> audioPart = new HashMap<>();
        audioPart.put("inlineData", inlineData);
        
        Map<String, Object> textPart = new HashMap<>();
        textPart.put("text", "Por favor, transcreva este áudio em português brasileiro.");
        
        Map<String, Object> content = new HashMap<>();
        content.put("parts", Arrays.asList(textPart, audioPart));
        
        requestBody.put("contents", Arrays.asList(content));
        
        // Configurações de geração
        Map<String, Object> generationConfig = new HashMap<>();
        generationConfig.put("temperature", 0.1);
        generationConfig.put("maxOutputTokens", 1000);
        
        requestBody.put("generationConfig", generationConfig);
        
        return requestBody;
    }

    /**
     * Extrai transcrição da resposta da API
     */
    private String extrairTranscricao(String responseBody) {
        JsonNode rootNode = parseJson(responseBody);
        
        if (rootNode == null) {
            log.error("Erro ao fazer parse da resposta JSON");
            throw new RuntimeException("Erro ao processar resposta da API");
        }
        
        JsonNode candidatesNode = rootNode.path("candidates");
        if (candidatesNode.isArray() && candidatesNode.size() > 0) {
            JsonNode firstCandidate = candidatesNode.get(0);
            JsonNode contentNode = firstCandidate.path("content");
            JsonNode partsNode = contentNode.path("parts");
            
            if (partsNode.isArray() && partsNode.size() > 0) {
                JsonNode firstPart = partsNode.get(0);
                String transcricao = firstPart.path("text").asText();
                
                log.debug("Transcrição extraída com sucesso");
                return transcricao;
            }
        }
        
        log.error("Não foi possível extrair transcrição da resposta");
        throw new RuntimeException("Falha na transcrição do áudio");
    }

    /**
     * Faz parse seguro do JSON
     */
    private JsonNode parseJson(String json) {
        try {
            return objectMapper.readTree(json);
        } catch (Exception e) {
            log.error("Erro ao fazer parse do JSON: {}", e.getMessage());
            return null;
        }
    }
}

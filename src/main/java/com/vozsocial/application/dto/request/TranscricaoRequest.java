package com.vozsocial.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para requisição de transcrição de áudio
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TranscricaoRequest {

    @NotBlank(message = "Dados do áudio são obrigatórios")
    private String audioDataUri;
}

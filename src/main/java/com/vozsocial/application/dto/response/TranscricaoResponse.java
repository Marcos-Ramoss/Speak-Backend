package com.vozsocial.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para resposta de transcrição de áudio
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TranscricaoResponse {

    private String transcricao;
    private Boolean sucesso;
    private String mensagem;
}

package com.vozsocial.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para resposta de transformação de voz
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransformacaoVozResponse {

    private String audioTransformadoDataUri;
    private String transcricao;
    private Boolean sucesso;
    private String mensagem;
}

package com.vozsocial.application.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO para transferência de dados de arquivo de áudio
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArquivoAudioDto {

    private Long id;
    private Long usuarioId;
    private String nomeArquivoOriginal;
    private String caminhoArquivo;
    private Long tamanhoArquivo;
    private BigDecimal duracaoSegundos;
    private String tipoMime;
    private String transcricao;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime criadoEm;
}

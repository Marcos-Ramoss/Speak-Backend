package com.vozsocial.application.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.vozsocial.domain.enums.TipoFiltroVoz;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO para transferência de dados de post de áudio
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostAudioDto {

    private Long id;
    private UsuarioDto usuario;
    private ArquivoAudioDto arquivoAudio;
    private String conteudo;
    private Integer quantidadeCurtidas;
    private Integer quantidadeComentarios;
    private Integer quantidadeCompartilhamentos;
    private Boolean processado;
    private TipoFiltroVoz tipoFiltroVoz;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime criadoEm;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime atualizadoEm;
}

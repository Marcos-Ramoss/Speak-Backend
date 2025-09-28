package com.vozsocial.application.dto.request;

import com.vozsocial.domain.enums.TipoFiltroVoz;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para requisição de transformação de voz
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransformacaoVozRequest {

    @NotBlank(message = "Dados do áudio são obrigatórios")
    private String audioDataUri;

    @NotNull(message = "Tipo de filtro é obrigatório")
    private TipoFiltroVoz tipoFiltro;

    private String transcricao;
}

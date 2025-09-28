package com.vozsocial.application.dto.request;

import com.vozsocial.domain.enums.TipoFiltroVoz;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para requisição de criação de post
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CriarPostRequest {

    @NotNull(message = "ID do usuário é obrigatório")
    private Long usuarioId;

    private String conteudo;

    @Builder.Default
    private TipoFiltroVoz tipoFiltroVoz = TipoFiltroVoz.NATURAL;

    // Para upload de áudio via base64
    private String audioDataUri;
    
    // Para upload de áudio via multipart
    private String nomeArquivo;
}

package com.vozsocial.application.mapper;

import com.vozsocial.application.dto.PostAudioDto;
import com.vozsocial.domain.entity.PostAudio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Mapper para conversão entre PostAudio e PostAudioDto
 */
@Component
@RequiredArgsConstructor
public class PostAudioMapper {

    private final UsuarioMapper usuarioMapper;
    private final ArquivoAudioMapper arquivoAudioMapper;

    /**
     * Converte entidade PostAudio para PostAudioDto
     */
    public PostAudioDto paraDto(PostAudio postAudio) {
        if (postAudio == null) {
            return null;
        }

        return PostAudioDto.builder()
                .id(postAudio.getId())
                .usuario(usuarioMapper.paraDto(postAudio.getUsuario()))
                .arquivoAudio(arquivoAudioMapper.paraDto(postAudio.getArquivoAudio()))
                .conteudo(postAudio.getConteudo())
                .quantidadeCurtidas(postAudio.getQuantidadeCurtidas())
                .quantidadeComentarios(postAudio.getQuantidadeComentarios())
                .quantidadeCompartilhamentos(postAudio.getQuantidadeCompartilhamentos())
                .processado(postAudio.getProcessado())
                .tipoFiltroVoz(postAudio.getTipoFiltroVoz())
                .criadoEm(postAudio.getCriadoEm())
                .atualizadoEm(postAudio.getAtualizadoEm())
                .build();
    }

    /**
     * Converte PostAudioDto para entidade PostAudio (sem relacionamentos)
     */
    public PostAudio paraEntidade(PostAudioDto postAudioDto) {
        if (postAudioDto == null) {
            return null;
        }

        return PostAudio.builder()
                .id(postAudioDto.getId())
                .conteudo(postAudioDto.getConteudo())
                .quantidadeCurtidas(postAudioDto.getQuantidadeCurtidas())
                .quantidadeComentarios(postAudioDto.getQuantidadeComentarios())
                .quantidadeCompartilhamentos(postAudioDto.getQuantidadeCompartilhamentos())
                .processado(postAudioDto.getProcessado())
                .tipoFiltroVoz(postAudioDto.getTipoFiltroVoz())
                .criadoEm(postAudioDto.getCriadoEm())
                .atualizadoEm(postAudioDto.getAtualizadoEm())
                .build();
    }

    /**
     * Atualiza entidade PostAudio com dados do PostAudioDto
     */
    public void atualizarEntidade(PostAudio postAudio, PostAudioDto postAudioDto) {
        if (postAudio == null || postAudioDto == null) {
            return;
        }

        postAudio.setConteudo(postAudioDto.getConteudo());
        postAudio.setQuantidadeCurtidas(postAudioDto.getQuantidadeCurtidas());
        postAudio.setQuantidadeComentarios(postAudioDto.getQuantidadeComentarios());
        postAudio.setQuantidadeCompartilhamentos(postAudioDto.getQuantidadeCompartilhamentos());
        postAudio.setProcessado(postAudioDto.getProcessado());
        postAudio.setTipoFiltroVoz(postAudioDto.getTipoFiltroVoz());
    }
}

package com.vozsocial.application.mapper;

import com.vozsocial.application.dto.ArquivoAudioDto;
import com.vozsocial.domain.entity.ArquivoAudio;
import org.springframework.stereotype.Component;

/**
 * Mapper para conversão entre ArquivoAudio e ArquivoAudioDto
 */
@Component
public class ArquivoAudioMapper {

    /**
     * Converte entidade ArquivoAudio para ArquivoAudioDto
     */
    public ArquivoAudioDto paraDto(ArquivoAudio arquivoAudio) {
        if (arquivoAudio == null) {
            return null;
        }

        return ArquivoAudioDto.builder()
                .id(arquivoAudio.getId())
                .usuarioId(arquivoAudio.getUsuario() != null ? arquivoAudio.getUsuario().getId() : null)
                .nomeArquivoOriginal(arquivoAudio.getNomeArquivoOriginal())
                .caminhoArquivo(arquivoAudio.getCaminhoArquivo())
                .tamanhoArquivo(arquivoAudio.getTamanhoArquivo())
                .duracaoSegundos(arquivoAudio.getDuracaoSegundos())
                .tipoMime(arquivoAudio.getTipoMime())
                .transcricao(arquivoAudio.getTranscricao())
                .criadoEm(arquivoAudio.getCriadoEm())
                .build();
    }

    /**
     * Converte ArquivoAudioDto para entidade ArquivoAudio (sem relacionamentos)
     */
    public ArquivoAudio paraEntidade(ArquivoAudioDto arquivoAudioDto) {
        if (arquivoAudioDto == null) {
            return null;
        }

        return ArquivoAudio.builder()
                .id(arquivoAudioDto.getId())
                .nomeArquivoOriginal(arquivoAudioDto.getNomeArquivoOriginal())
                .caminhoArquivo(arquivoAudioDto.getCaminhoArquivo())
                .tamanhoArquivo(arquivoAudioDto.getTamanhoArquivo())
                .duracaoSegundos(arquivoAudioDto.getDuracaoSegundos())
                .tipoMime(arquivoAudioDto.getTipoMime())
                .transcricao(arquivoAudioDto.getTranscricao())
                .criadoEm(arquivoAudioDto.getCriadoEm())
                .build();
    }

    /**
     * Atualiza entidade ArquivoAudio com dados do ArquivoAudioDto
     */
    public void atualizarEntidade(ArquivoAudio arquivoAudio, ArquivoAudioDto arquivoAudioDto) {
        if (arquivoAudio == null || arquivoAudioDto == null) {
            return;
        }

        arquivoAudio.setNomeArquivoOriginal(arquivoAudioDto.getNomeArquivoOriginal());
        arquivoAudio.setCaminhoArquivo(arquivoAudioDto.getCaminhoArquivo());
        arquivoAudio.setTamanhoArquivo(arquivoAudioDto.getTamanhoArquivo());
        arquivoAudio.setDuracaoSegundos(arquivoAudioDto.getDuracaoSegundos());
        arquivoAudio.setTipoMime(arquivoAudioDto.getTipoMime());
        arquivoAudio.setTranscricao(arquivoAudioDto.getTranscricao());
    }
}

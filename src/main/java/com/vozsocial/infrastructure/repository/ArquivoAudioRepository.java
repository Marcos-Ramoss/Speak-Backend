package com.vozsocial.infrastructure.repository;

import com.vozsocial.domain.entity.ArquivoAudio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositório para operações de persistência da entidade ArquivoAudio
 */
@Repository
public interface ArquivoAudioRepository extends JpaRepository<ArquivoAudio, Long> {

    /**
     * Busca arquivos de áudio por usuário
     */
    List<ArquivoAudio> findByUsuarioIdOrderByCriadoEmDesc(Long usuarioId);

    /**
     * Busca arquivos de áudio por tipo MIME
     */
    List<ArquivoAudio> findByTipoMime(String tipoMime);

    /**
     * Busca arquivos de áudio criados em um período
     */
    @Query("SELECT a FROM ArquivoAudio a WHERE a.criadoEm BETWEEN :dataInicio AND :dataFim ORDER BY a.criadoEm DESC")
    List<ArquivoAudio> buscarPorPeriodo(@Param("dataInicio") LocalDateTime dataInicio, 
                                       @Param("dataFim") LocalDateTime dataFim);

    /**
     * Busca arquivos de áudio que possuem transcrição
     */
    @Query("SELECT a FROM ArquivoAudio a WHERE a.transcricao IS NOT NULL AND a.transcricao != ''")
    List<ArquivoAudio> buscarComTranscricao();

    /**
     * Busca arquivos de áudio por duração máxima
     */
    @Query("SELECT a FROM ArquivoAudio a WHERE a.duracaoSegundos <= :duracaoMaxima")
    List<ArquivoAudio> buscarPorDuracaoMaxima(@Param("duracaoMaxima") java.math.BigDecimal duracaoMaxima);
}

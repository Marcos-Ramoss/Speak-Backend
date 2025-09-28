package com.vozsocial.infrastructure.repository;

import com.vozsocial.domain.entity.PostAudio;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositório para operações de persistência da entidade PostAudio
 */
@Repository
public interface PostAudioRepository extends JpaRepository<PostAudio, Long> {

    /**
     * Busca todos os posts ordenados por data de criação (feed principal)
     */
    Page<PostAudio> findAllByOrderByCriadoEmDesc(Pageable pageable);

    /**
     * Busca posts por usuário
     */
    Page<PostAudio> findByUsuarioIdOrderByCriadoEmDesc(Long usuarioId, Pageable pageable);

    /**
     * Busca posts processados
     */
    Page<PostAudio> findByProcessadoTrueOrderByCriadoEmDesc(Pageable pageable);

    /**
     * Busca posts não processados
     */
    List<PostAudio> findByProcessadoFalseOrderByCriadoEmAsc();

    /**
     * Busca posts por período
     */
    @Query("SELECT p FROM PostAudio p WHERE p.criadoEm BETWEEN :dataInicio AND :dataFim ORDER BY p.criadoEm DESC")
    Page<PostAudio> buscarPorPeriodo(@Param("dataInicio") LocalDateTime dataInicio, 
                                    @Param("dataFim") LocalDateTime dataFim, 
                                    Pageable pageable);

    /**
     * Busca posts mais curtidos
     */
    @Query("SELECT p FROM PostAudio p ORDER BY p.quantidadeCurtidas DESC, p.criadoEm DESC")
    Page<PostAudio> buscarMaisCurtidos(Pageable pageable);

    /**
     * Busca posts com mais comentários
     */
    @Query("SELECT p FROM PostAudio p ORDER BY p.quantidadeComentarios DESC, p.criadoEm DESC")
    Page<PostAudio> buscarComMaisComentarios(Pageable pageable);

    /**
     * Conta posts por usuário
     */
    long countByUsuarioId(Long usuarioId);
}

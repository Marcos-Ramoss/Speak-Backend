package com.vozsocial.infrastructure.repository;

import com.vozsocial.domain.entity.ComentarioPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositório para operações de persistência da entidade ComentarioPost
 */
@Repository
public interface ComentarioPostRepository extends JpaRepository<ComentarioPost, Long> {

    /**
     * Busca comentários por post
     */
    Page<ComentarioPost> findByPostIdOrderByCriadoEmDesc(Long postId, Pageable pageable);

    /**
     * Busca comentários por usuário
     */
    Page<ComentarioPost> findByUsuarioIdOrderByCriadoEmDesc(Long usuarioId, Pageable pageable);

    /**
     * Conta comentários por post
     */
    long countByPostId(Long postId);

    /**
     * Busca últimos comentários de um post
     */
    @Query("SELECT c FROM ComentarioPost c WHERE c.post.id = :postId ORDER BY c.criadoEm DESC")
    List<ComentarioPost> buscarUltimosComentarios(@Param("postId") Long postId, Pageable pageable);

    /**
     * Conta comentários por usuário
     */
    long countByUsuarioId(Long usuarioId);
}

package com.vozsocial.infrastructure.repository;

import com.vozsocial.domain.entity.CurtidaPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositório para operações de persistência da entidade CurtidaPost
 */
@Repository
public interface CurtidaPostRepository extends JpaRepository<CurtidaPost, Long> {

    /**
     * Busca curtida por post e usuário
     */
    Optional<CurtidaPost> findByPostIdAndUsuarioId(Long postId, Long usuarioId);

    /**
     * Verifica se usuário já curtiu o post
     */
    boolean existsByPostIdAndUsuarioId(Long postId, Long usuarioId);

    /**
     * Conta curtidas por post
     */
    long countByPostId(Long postId);

    /**
     * Remove curtida por post e usuário
     */
    void deleteByPostIdAndUsuarioId(Long postId, Long usuarioId);

    /**
     * Busca curtidas de um usuário
     */
    @Query("SELECT c FROM CurtidaPost c WHERE c.usuario.id = :usuarioId ORDER BY c.criadoEm DESC")
    java.util.List<CurtidaPost> buscarCurtidasDoUsuario(@Param("usuarioId") Long usuarioId);
}

package com.vozsocial.infrastructure.repository;

import com.vozsocial.domain.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositório para operações de persistência da entidade Usuario
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    /**
     * Busca usuário por nome de usuário
     */
    Optional<Usuario> findByNomeUsuario(String nomeUsuario);

    /**
     * Busca usuário por email
     */
    Optional<Usuario> findByEmail(String email);

    /**
     * Verifica se existe usuário com o nome de usuário informado
     */
    boolean existsByNomeUsuario(String nomeUsuario);

    /**
     * Verifica se existe usuário com o email informado
     */
    boolean existsByEmail(String email);

    /**
     * Busca usuários ativos
     */
    @Query("SELECT u FROM Usuario u WHERE u.ativo = true ORDER BY u.criadoEm DESC")
    java.util.List<Usuario> buscarUsuariosAtivos();

    /**
     * Busca usuário por nome de usuário ignorando case
     */
    @Query("SELECT u FROM Usuario u WHERE LOWER(u.nomeUsuario) = LOWER(:nomeUsuario)")
    Optional<Usuario> buscarPorNomeUsuarioIgnorandoCase(@Param("nomeUsuario") String nomeUsuario);
}

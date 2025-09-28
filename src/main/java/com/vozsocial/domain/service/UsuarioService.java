package com.vozsocial.domain.service;

import com.vozsocial.application.dto.UsuarioDto;
import com.vozsocial.application.mapper.UsuarioMapper;
import com.vozsocial.domain.entity.Usuario;
import com.vozsocial.infrastructure.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service responsável pela lógica de negócio relacionada aos usuários
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;

    /**
     * Busca usuário por ID
     */
    @Transactional(readOnly = true)
    public Optional<UsuarioDto> buscarPorId(Long id) {
        log.debug("Buscando usuário por ID: {}", id);
        
        return usuarioRepository.findById(id)
                .map(usuarioMapper::paraDto);
    }

    /**
     * Busca usuário por nome de usuário
     */
    @Transactional(readOnly = true)
    public Optional<UsuarioDto> buscarPorNomeUsuario(String nomeUsuario) {
        log.debug("Buscando usuário por nome de usuário: {}", nomeUsuario);
        
        return usuarioRepository.findByNomeUsuario(nomeUsuario)
                .map(usuarioMapper::paraDto);
    }

    /**
     * Busca usuário por email
     */
    @Transactional(readOnly = true)
    public Optional<UsuarioDto> buscarPorEmail(String email) {
        log.debug("Buscando usuário por email: {}", email);
        
        return usuarioRepository.findByEmail(email)
                .map(usuarioMapper::paraDto);
    }

    /**
     * Lista todos os usuários ativos
     */
    @Transactional(readOnly = true)
    public List<UsuarioDto> listarUsuariosAtivos() {
        log.debug("Listando usuários ativos");
        
        return usuarioRepository.buscarUsuariosAtivos()
                .stream()
                .map(usuarioMapper::paraDto)
                .collect(Collectors.toList());
    }

    /**
     * Cria novo usuário
     */
    public UsuarioDto criarUsuario(UsuarioDto usuarioDto) {
        log.info("Criando novo usuário: {}", usuarioDto.getNomeUsuario());
        
        validarDadosUsuario(usuarioDto);
        verificarUnicidadeNomeUsuario(usuarioDto.getNomeUsuario(), null);
        
        if (usuarioDto.getEmail() != null) {
            verificarUnicidadeEmail(usuarioDto.getEmail(), null);
        }

        Usuario usuario = usuarioMapper.paraEntidade(usuarioDto);
        usuario.setAtivo(true);
        
        Usuario usuarioSalvo = usuarioRepository.save(usuario);
        
        log.info("Usuário criado com sucesso. ID: {}", usuarioSalvo.getId());
        return usuarioMapper.paraDto(usuarioSalvo);
    }

    /**
     * Atualiza dados do usuário
     */
    public UsuarioDto atualizarUsuario(Long id, UsuarioDto usuarioDto) {
        log.info("Atualizando usuário ID: {}", id);
        
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        validarDadosUsuario(usuarioDto);
        verificarUnicidadeNomeUsuario(usuarioDto.getNomeUsuario(), id);
        
        if (usuarioDto.getEmail() != null) {
            verificarUnicidadeEmail(usuarioDto.getEmail(), id);
        }

        usuarioMapper.atualizarEntidade(usuario, usuarioDto);
        
        Usuario usuarioAtualizado = usuarioRepository.save(usuario);
        
        log.info("Usuário atualizado com sucesso. ID: {}", usuarioAtualizado.getId());
        return usuarioMapper.paraDto(usuarioAtualizado);
    }

    /**
     * Desativa usuário (soft delete)
     */
    public void desativarUsuario(Long id) {
        log.info("Desativando usuário ID: {}", id);
        
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        usuario.setAtivo(false);
        usuarioRepository.save(usuario);
        
        log.info("Usuário desativado com sucesso. ID: {}", id);
    }

    /**
     * Valida dados básicos do usuário
     */
    private void validarDadosUsuario(UsuarioDto usuarioDto) {
        if (usuarioDto.getNomeUsuario() == null || usuarioDto.getNomeUsuario().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome de usuário é obrigatório");
        }
        
        if (usuarioDto.getNome() == null || usuarioDto.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome é obrigatório");
        }
        
        if (usuarioDto.getNomeUsuario().length() > 50) {
            throw new IllegalArgumentException("Nome de usuário deve ter no máximo 50 caracteres");
        }
        
        if (usuarioDto.getNome().length() > 100) {
            throw new IllegalArgumentException("Nome deve ter no máximo 100 caracteres");
        }
    }

    /**
     * Verifica se nome de usuário já existe
     */
    private void verificarUnicidadeNomeUsuario(String nomeUsuario, Long idUsuarioAtual) {
        Optional<Usuario> usuarioExistente = usuarioRepository.findByNomeUsuario(nomeUsuario);
        
        if (usuarioExistente.isPresent() && 
            (idUsuarioAtual == null || !usuarioExistente.get().getId().equals(idUsuarioAtual))) {
            throw new IllegalArgumentException("Nome de usuário já está em uso");
        }
    }

    /**
     * Verifica se email já existe
     */
    private void verificarUnicidadeEmail(String email, Long idUsuarioAtual) {
        Optional<Usuario> usuarioExistente = usuarioRepository.findByEmail(email);
        
        if (usuarioExistente.isPresent() && 
            (idUsuarioAtual == null || !usuarioExistente.get().getId().equals(idUsuarioAtual))) {
            throw new IllegalArgumentException("Email já está em uso");
        }
    }
}

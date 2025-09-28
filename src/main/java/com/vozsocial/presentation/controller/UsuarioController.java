package com.vozsocial.presentation.controller;

import com.vozsocial.application.dto.UsuarioDto;
import com.vozsocial.domain.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller responsável pelos endpoints relacionados aos usuários
 */
@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Usuários", description = "Operações relacionadas aos usuários do sistema")
@CrossOrigin(origins = "*")
public class UsuarioController {

    private final UsuarioService usuarioService;

    /**
     * Busca usuário por ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Buscar usuário por ID", description = "Retorna os dados de um usuário específico")
    public ResponseEntity<UsuarioDto> buscarPorId(
            @Parameter(description = "ID do usuário") @PathVariable Long id) {
        
        log.debug("Requisição para buscar usuário por ID: {}", id);
        
        return usuarioService.buscarPorId(id)
                .map(usuario -> ResponseEntity.ok(usuario))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Busca usuário por nome de usuário
     */
    @GetMapping("/nome-usuario/{nomeUsuario}")
    @Operation(summary = "Buscar usuário por nome de usuário", 
               description = "Retorna os dados de um usuário pelo nome de usuário")
    public ResponseEntity<UsuarioDto> buscarPorNomeUsuario(
            @Parameter(description = "Nome de usuário") @PathVariable String nomeUsuario) {
        
        log.debug("Requisição para buscar usuário por nome: {}", nomeUsuario);
        
        return usuarioService.buscarPorNomeUsuario(nomeUsuario)
                .map(usuario -> ResponseEntity.ok(usuario))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Lista todos os usuários ativos
     */
    @GetMapping
    @Operation(summary = "Listar usuários ativos", 
               description = "Retorna lista de todos os usuários ativos do sistema")
    public ResponseEntity<List<UsuarioDto>> listarUsuariosAtivos() {
        log.debug("Requisição para listar usuários ativos");
        
        List<UsuarioDto> usuarios = usuarioService.listarUsuariosAtivos();
        return ResponseEntity.ok(usuarios);
    }

    /**
     * Cria novo usuário
     */
    @PostMapping
    @Operation(summary = "Criar usuário", description = "Cria um novo usuário no sistema")
    public ResponseEntity<UsuarioDto> criarUsuario(
            @Parameter(description = "Dados do usuário a ser criado") 
            @Valid @RequestBody UsuarioDto usuarioDto) {
        
        log.info("Requisição para criar usuário: {}", usuarioDto.getNomeUsuario());
        
        UsuarioDto usuarioCriado = usuarioService.criarUsuario(usuarioDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioCriado);
    }

    /**
     * Atualiza dados do usuário
     */
    @PutMapping("/{id}")
    @Operation(summary = "Atualizar usuário", description = "Atualiza os dados de um usuário existente")
    public ResponseEntity<UsuarioDto> atualizarUsuario(
            @Parameter(description = "ID do usuário") @PathVariable Long id,
            @Parameter(description = "Novos dados do usuário") 
            @Valid @RequestBody UsuarioDto usuarioDto) {
        
        log.info("Requisição para atualizar usuário ID: {}", id);
        
        UsuarioDto usuarioAtualizado = usuarioService.atualizarUsuario(id, usuarioDto);
        return ResponseEntity.ok(usuarioAtualizado);
    }

    /**
     * Desativa usuário
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Desativar usuário", description = "Desativa um usuário do sistema")
    public ResponseEntity<Void> desativarUsuario(
            @Parameter(description = "ID do usuário") @PathVariable Long id) {
        
        log.info("Requisição para desativar usuário ID: {}", id);
        
        usuarioService.desativarUsuario(id);
        return ResponseEntity.noContent().build();
    }
}

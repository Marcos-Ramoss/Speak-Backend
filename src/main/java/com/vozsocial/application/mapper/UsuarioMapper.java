package com.vozsocial.application.mapper;

import com.vozsocial.application.dto.UsuarioDto;
import com.vozsocial.domain.entity.Usuario;
import org.springframework.stereotype.Component;

/**
 * Mapper para conversão entre Usuario e UsuarioDto
 */
@Component
public class UsuarioMapper {

    /**
     * Converte entidade Usuario para UsuarioDto
     */
    public UsuarioDto paraDto(Usuario usuario) {
        if (usuario == null) {
            return null;
        }

        return UsuarioDto.builder()
                .id(usuario.getId())
                .nomeUsuario(usuario.getNomeUsuario())
                .nome(usuario.getNome())
                .email(usuario.getEmail())
                .urlAvatar(usuario.getUrlAvatar())
                .dicaAvatar(usuario.getDicaAvatar())
                .ativo(usuario.getAtivo())
                .criadoEm(usuario.getCriadoEm())
                .atualizadoEm(usuario.getAtualizadoEm())
                .build();
    }

    /**
     * Converte UsuarioDto para entidade Usuario
     */
    public Usuario paraEntidade(UsuarioDto usuarioDto) {
        if (usuarioDto == null) {
            return null;
        }

        return Usuario.builder()
                .id(usuarioDto.getId())
                .nomeUsuario(usuarioDto.getNomeUsuario())
                .nome(usuarioDto.getNome())
                .email(usuarioDto.getEmail())
                .urlAvatar(usuarioDto.getUrlAvatar())
                .dicaAvatar(usuarioDto.getDicaAvatar())
                .ativo(usuarioDto.getAtivo())
                .criadoEm(usuarioDto.getCriadoEm())
                .atualizadoEm(usuarioDto.getAtualizadoEm())
                .build();
    }

    /**
     * Atualiza entidade Usuario com dados do UsuarioDto
     */
    public void atualizarEntidade(Usuario usuario, UsuarioDto usuarioDto) {
        if (usuario == null || usuarioDto == null) {
            return;
        }

        usuario.setNomeUsuario(usuarioDto.getNomeUsuario());
        usuario.setNome(usuarioDto.getNome());
        usuario.setEmail(usuarioDto.getEmail());
        usuario.setUrlAvatar(usuarioDto.getUrlAvatar());
        usuario.setDicaAvatar(usuarioDto.getDicaAvatar());
        usuario.setAtivo(usuarioDto.getAtivo());
    }
}

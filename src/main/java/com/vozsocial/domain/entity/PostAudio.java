package com.vozsocial.domain.entity;

import com.vozsocial.domain.enums.TipoFiltroVoz;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Entidade que representa um post de áudio no feed
 */
@Entity
@Table(name = "posts_audio")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostAudio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "arquivo_audio_id", nullable = false)
    private ArquivoAudio arquivoAudio;

    @Column(name = "conteudo", columnDefinition = "TEXT")
    private String conteudo;

    @Column(name = "quantidade_curtidas")
    @Builder.Default
    private Integer quantidadeCurtidas = 0;

    @Column(name = "quantidade_comentarios")
    @Builder.Default
    private Integer quantidadeComentarios = 0;

    @Column(name = "quantidade_compartilhamentos")
    @Builder.Default
    private Integer quantidadeCompartilhamentos = 0;

    @Column(name = "processado")
    @Builder.Default
    private Boolean processado = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_filtro_voz")
    @Builder.Default
    private TipoFiltroVoz tipoFiltroVoz = TipoFiltroVoz.NATURAL;

    @CreationTimestamp
    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @UpdateTimestamp
    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;

    // Relacionamentos
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CurtidaPost> curtidas;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ComentarioPost> comentarios;
}

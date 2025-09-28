package com.vozsocial.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidade que representa um arquivo de áudio no sistema
 */
@Entity
@Table(name = "arquivos_audio")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArquivoAudio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(name = "nome_arquivo_original", length = 255)
    private String nomeArquivoOriginal;

    @Column(name = "caminho_arquivo", nullable = false, length = 500)
    private String caminhoArquivo;

    @Column(name = "tamanho_arquivo")
    private Long tamanhoArquivo;

    @Column(name = "duracao_segundos", precision = 8, scale = 2)
    private BigDecimal duracaoSegundos;

    @Column(name = "tipo_mime", length = 100)
    private String tipoMime;

    @Column(name = "transcricao", columnDefinition = "TEXT")
    private String transcricao;

    @CreationTimestamp
    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    // Relacionamento com posts
    @OneToOne(mappedBy = "arquivoAudio", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private PostAudio postAudio;
}

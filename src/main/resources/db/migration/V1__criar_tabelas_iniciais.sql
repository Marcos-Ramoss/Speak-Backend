-- =====================================================
-- Script de migração V1 - Criação das tabelas iniciais
-- VozSocial MVP - Sistema de posts de áudio
-- =====================================================

-- Tabela de usuários
CREATE TABLE usuarios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome_usuario VARCHAR(50) UNIQUE NOT NULL,
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE,
    url_avatar VARCHAR(255),
    dica_avatar VARCHAR(100),
    ativo BOOLEAN DEFAULT TRUE,
    criado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_nome_usuario (nome_usuario),
    INDEX idx_email (email),
    INDEX idx_ativo_criado (ativo, criado_em)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabela de arquivos de áudio
CREATE TABLE arquivos_audio (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    nome_arquivo_original VARCHAR(255),
    caminho_arquivo VARCHAR(500) NOT NULL,
    tamanho_arquivo BIGINT,
    duracao_segundos DECIMAL(8,2),
    tipo_mime VARCHAR(100),
    transcricao TEXT,
    criado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    INDEX idx_usuario_criado (usuario_id, criado_em),
    INDEX idx_tipo_mime (tipo_mime)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabela de posts de áudio
CREATE TABLE posts_audio (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    arquivo_audio_id BIGINT NOT NULL,
    conteudo TEXT,
    quantidade_curtidas INT DEFAULT 0,
    quantidade_comentarios INT DEFAULT 0,
    quantidade_compartilhamentos INT DEFAULT 0,
    processado BOOLEAN DEFAULT FALSE,
    tipo_filtro_voz ENUM('NATURAL', 'ROBOTICO') DEFAULT 'NATURAL',
    criado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    FOREIGN KEY (arquivo_audio_id) REFERENCES arquivos_audio(id) ON DELETE CASCADE,
    INDEX idx_usuario_criado (usuario_id, criado_em),
    INDEX idx_criado_em (criado_em),
    INDEX idx_processado (processado),
    INDEX idx_curtidas (quantidade_curtidas DESC),
    INDEX idx_comentarios (quantidade_comentarios DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabela de curtidas dos posts
CREATE TABLE curtidas_post (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    post_id BIGINT NOT NULL,
    usuario_id BIGINT NOT NULL,
    criado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (post_id) REFERENCES posts_audio(id) ON DELETE CASCADE,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    UNIQUE KEY uk_usuario_post_curtida (post_id, usuario_id),
    INDEX idx_post_criado (post_id, criado_em),
    INDEX idx_usuario_criado (usuario_id, criado_em)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabela de comentários dos posts
CREATE TABLE comentarios_post (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    post_id BIGINT NOT NULL,
    usuario_id BIGINT NOT NULL,
    conteudo TEXT NOT NULL,
    criado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (post_id) REFERENCES posts_audio(id) ON DELETE CASCADE,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    INDEX idx_post_criado (post_id, criado_em),
    INDEX idx_usuario_criado (usuario_id, criado_em)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

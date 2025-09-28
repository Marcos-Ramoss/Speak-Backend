-- =====================================================
-- Script de inicialização do MySQL para Docker
-- VozSocial MVP - Configurações iniciais do banco
-- =====================================================

-- Criar database se não existir
CREATE DATABASE IF NOT EXISTS vozsocial CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Usar o database
USE vozsocial;

-- Configurações de timezone
SET time_zone = '+00:00';

-- Configurações de charset
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;

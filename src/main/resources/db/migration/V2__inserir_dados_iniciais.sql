-- =====================================================
-- Script de migração V2 - Inserção de dados iniciais
-- VozSocial MVP - Dados de exemplo para desenvolvimento
-- =====================================================

-- Inserir usuários de exemplo
INSERT INTO usuarios (nome_usuario, nome, email, url_avatar, dica_avatar, ativo) VALUES
('admin', 'Administrador', 'admin@vozsocial.com', 'https://ui-avatars.com/api/?name=Admin&background=007bff&color=fff', 'Administrador do sistema', true),
('joao_silva', 'João Silva', 'joao.silva@email.com', 'https://ui-avatars.com/api/?name=João+Silva&background=28a745&color=fff', 'Desenvolvedor apaixonado por tecnologia', true),
('maria_santos', 'Maria Santos', 'maria.santos@email.com', 'https://ui-avatars.com/api/?name=Maria+Santos&background=dc3545&color=fff', 'Designer UX/UI criativa', true);

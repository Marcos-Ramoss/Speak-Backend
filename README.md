# 🎤 VozSocial MVP - Backend

Sistema de rede social focado em posts de áudio com integração de IA para transcrição e transformação de voz.

## 🚀 Tecnologias Utilizadas

- **Java 17** - Linguagem de programação
- **Spring Boot 3.2** - Framework principal
- **MySQL 8.0** - Banco de dados
- **Redis** - Cache e sessões
- **Docker & Docker Compose** - Containerização
- **Google AI (Gemini)** - Transcrição de áudio
- **Flyway** - Migrations do banco
- **Swagger/OpenAPI** - Documentação da API
- **Lombok** - Redução de boilerplate

## 🏗️ Arquitetura

O projeto segue os princípios de **Clean Architecture** e **SOLID**:

```
src/
├── main/java/com/vozsocial/
│   ├── VozSocialApplication.java           # Classe principal
│   ├── domain/                             # Camada de domínio
│   │   ├── entity/                         # Entidades JPA
│   │   ├── enums/                          # Enumerações
│   │   └── service/                        # Serviços de negócio
│   ├── application/                        # Camada de aplicação
│   │   ├── dto/                            # DTOs
│   │   └── mapper/                         # Mapeadores
│   ├── infrastructure/                     # Camada de infraestrutura
│   │   ├── repository/                     # Repositórios JPA
│   │   ├── config/                         # Configurações
│   │   └── exception/                      # Tratamento de exceções
│   └── presentation/                       # Camada de apresentação
│       └── controller/                     # Controllers REST
└── resources/
    ├── application.yml                     # Configurações
    └── db/migration/                       # Scripts Flyway
```

## 🎯 Funcionalidades

### ✅ Implementadas
- 👤 **Gestão de Usuários** - CRUD completo
- 🎵 **Upload de Áudio** - Via arquivo ou base64
- 🤖 **Transcrição de IA** - Google Gemini AI
- 📱 **Sistema de Posts** - Feed com paginação
- ❤️ **Curtidas e Comentários** - Interações sociais
- 🔍 **API Documentada** - Swagger UI
- 🐳 **Docker Ready** - Ambiente containerizado

### 🔄 Próximas Funcionalidades
- 🔐 Autenticação JWT
- 🎛️ Transformação de voz robótica
- 📊 Analytics e métricas
- 🔔 Sistema de notificações
- 📤 Compartilhamento de posts

## 🚀 Como Executar

### Opção 1: Docker (Recomendado)

1. **Clone o repositório:**
   ```bash
   git clone <url-do-repositorio>
   cd vozsocial-backend
   ```

2. **Configure a API Key do Google AI:**
   - Obtenha uma chave em: https://ai.google.dev/
   - Edite o arquivo `src/main/resources/application.yml`
   - Substitua `your-api-key-here` pela sua chave

3. **Execute o script de inicialização:**
   ```bash
   # Windows
   start-vozsocial.bat
   
   # Linux/Mac
   chmod +x start-vozsocial.sh
   ./start-vozsocial.sh
   ```

### Opção 2: Desenvolvimento Local

1. **Pré-requisitos:**
   - Java 17+
   - Maven 3.6+
   - MySQL 8.0+ (ou use Docker apenas para o banco)

2. **Execute o script local:**
   ```bash
   # Windows
   start-local.bat
   ```

### Opção 3: Manual

1. **Inicie o banco de dados:**
   ```bash
   docker-compose up -d mysql redis
   ```

2. **Compile e execute:**
   ```bash
   ./mvnw clean spring-boot:run
   ```

## 📍 URLs Importantes

- **API Backend:** http://localhost:8080/api
- **Swagger UI:** http://localhost:8080/api/swagger-ui.html
- **Health Check:** http://localhost:8080/api/actuator/health
- **MySQL:** localhost:3306 (usuário: `vozsocial`, senha: `vozsocial123`)
- **Redis:** localhost:6379

## 🔧 Scripts Disponíveis

| Script | Descrição |
|--------|-----------|
| `start-vozsocial.bat` | Inicia todo o ambiente com Docker |
| `stop-vozsocial.bat` | Para todos os containers |
| `start-local.bat` | Execução local para desenvolvimento |

## 📊 Endpoints Principais

### Usuários
- `GET /api/usuarios` - Listar usuários
- `POST /api/usuarios` - Criar usuário
- `GET /api/usuarios/{id}` - Buscar usuário
- `PUT /api/usuarios/{id}` - Atualizar usuário

### Posts
- `GET /api/posts/feed` - Feed principal
- `POST /api/posts/com-audio-base64` - Criar post com áudio
- `POST /api/posts/{id}/curtir` - Curtir/descurtir post
- `GET /api/posts/usuario/{id}` - Posts de um usuário

### Áudio
- `POST /api/audio/transcrever` - Transcrever áudio
- `POST /api/audio/upload` - Upload de arquivo
- `POST /api/audio/transformar-voz` - Transformar voz

## 🗄️ Banco de Dados

O sistema usa **MySQL** com as seguintes tabelas:

- `usuarios` - Dados dos usuários
- `arquivos_audio` - Metadados dos áudios
- `posts_audio` - Posts do feed
- `curtidas_post` - Curtidas dos posts
- `comentarios_post` - Comentários dos posts

As migrations são executadas automaticamente pelo **Flyway**.

## 🔑 Variáveis de Ambiente

| Variável | Descrição | Padrão |
|----------|-----------|---------|
| `GOOGLE_AI_API_KEY` | Chave da API do Google AI | `your-api-key-here` |
| `AUDIO_STORAGE_PATH` | Caminho para salvar áudios | `./uploads/audio` |
| `SPRING_PROFILES_ACTIVE` | Profile ativo | `default` |

## 🧪 Testes

```bash
# Executar testes
./mvnw test

# Executar com cobertura
./mvnw test jacoco:report
```

## 📝 Logs

```bash
# Ver logs da aplicação
docker-compose logs -f app

# Ver logs do MySQL
docker-compose logs -f mysql

# Ver todos os logs
docker-compose logs -f
```

## 🤝 Contribuição

1. Fork o projeto
2. Crie uma branch para sua feature (`git checkout -b feature/AmazingFeature`)
3. Commit suas mudanças (`git commit -m 'Add some AmazingFeature'`)
4. Push para a branch (`git push origin feature/AmazingFeature`)
5. Abra um Pull Request

## 📄 Licença

Este projeto está sob a licença MIT. Veja o arquivo [LICENSE](LICENSE) para mais detalhes.

## 👥 Equipe

- **Desenvolvimento Backend** - Implementação da API REST
- **Arquitetura** - Clean Architecture e SOLID
- **DevOps** - Docker e automação

---

**🎤 VozSocial MVP - Conectando pessoas através da voz!**

@echo off
chcp 65001 >nul
cls

echo ====================================================
echo 🎤 VozSocial MVP - Script de Inicialização
echo ====================================================
echo.

:: Verificar se Docker está rodando
echo ⏳ Verificando se Docker está rodando...
docker --version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Docker não encontrado! Por favor, instale o Docker Desktop.
    pause
    exit /b 1
)

docker info >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Docker não está rodando! Por favor, inicie o Docker Desktop.
    pause
    exit /b 1
)
echo ✅ Docker está rodando!
echo.

:: Parar containers existentes se estiverem rodando
echo 🛑 Parando containers existentes...
docker-compose down >nul 2>&1
echo.

:: Limpar imagens antigas (opcional)
echo 🧹 Limpando imagens antigas...
docker image prune -f >nul 2>&1
echo.

:: Verificar se Java está instalado
echo ⏳ Verificando Java...
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ⚠️  Java não encontrado! Tentando usar Docker para build...
) else (
    echo ✅ Java encontrado!
)
echo.

:: Criar diretórios necessários
echo 📁 Criando diretórios necessários...
if not exist "uploads\audio" mkdir uploads\audio
if not exist "docker\mysql\init" mkdir docker\mysql\init
echo ✅ Diretórios criados!
echo.

:: Compilar aplicação
echo 🔨 Compilando aplicação Spring Boot...
if exist "mvnw.cmd" (
    call mvnw.cmd clean package -DskipTests
) else if exist "mvnw" (
    call mvnw clean package -DskipTests
) else (
    echo ⚠️  Maven Wrapper não encontrado! Tentando usar Maven global...
    mvn clean package -DskipTests
)

if %errorlevel% neq 0 (
    echo ❌ Erro na compilação! Verifique os logs acima.
    pause
    exit /b 1
)
echo ✅ Compilação concluída!
echo.

:: Construir e iniciar containers
echo 🐳 Construindo e iniciando containers Docker...
docker-compose up --build -d

if %errorlevel% neq 0 (
    echo ❌ Erro ao iniciar containers! Verifique os logs.
    pause
    exit /b 1
)
echo.

:: Aguardar containers iniciarem
echo ⏳ Aguardando containers iniciarem...
timeout /t 10 /nobreak >nul
echo.

:: Verificar status dos containers
echo 📊 Status dos containers:
docker-compose ps
echo.

:: Aguardar MySQL estar pronto
echo ⏳ Aguardando MySQL estar pronto...
:wait_mysql
docker exec vozsocial-mysql mysqladmin ping -h localhost -u root -prootpassword >nul 2>&1
if %errorlevel% neq 0 (
    echo    Aguardando MySQL... ⏳
    timeout /t 3 /nobreak >nul
    goto wait_mysql
)
echo ✅ MySQL está pronto!
echo.

:: Aguardar aplicação estar pronta
echo ⏳ Aguardando aplicação Spring Boot estar pronta...
timeout /t 15 /nobreak >nul

:wait_app
curl -s http://localhost:8080/api/actuator/health >nul 2>&1
if %errorlevel% neq 0 (
    echo    Aguardando aplicação... ⏳
    timeout /t 5 /nobreak >nul
    goto wait_app
)
echo ✅ Aplicação está pronta!
echo.

:: Mostrar informações finais
echo ====================================================
echo 🎉 VozSocial MVP iniciado com sucesso!
echo ====================================================
echo.
echo 📍 URLs importantes:
echo    🌐 API Backend: http://localhost:8080/api
echo    📚 Swagger UI: http://localhost:8080/api/swagger-ui.html
echo    🔍 Health Check: http://localhost:8080/api/actuator/health
echo    🗄️  MySQL: localhost:3306 (usuário: vozsocial, senha: vozsocial123)
echo    🔴 Redis: localhost:6379
echo.
echo 📋 Comandos úteis:
echo    docker-compose logs -f app     (ver logs da aplicação)
echo    docker-compose logs -f mysql   (ver logs do MySQL)
echo    docker-compose down            (parar todos os containers)
echo    docker-compose restart app     (reiniciar apenas a aplicação)
echo.
echo 🚀 Endpoints principais da API:
echo    GET  /api/usuarios              (listar usuários)
echo    GET  /api/posts/feed            (feed de posts)
echo    POST /api/posts/com-audio-base64 (criar post com áudio)
echo    POST /api/audio/transcrever     (transcrever áudio)
echo.

:: Perguntar se quer abrir o browser
set /p open_browser="🌐 Deseja abrir o Swagger UI no navegador? (s/n): "
if /i "%open_browser%"=="s" (
    start http://localhost:8080/api/swagger-ui.html
    echo ✅ Swagger UI aberto no navegador!
)
echo.

:: Perguntar se quer ver os logs
set /p show_logs="📋 Deseja ver os logs da aplicação? (s/n): "
if /i "%show_logs%"=="s" (
    echo.
    echo 📋 Logs da aplicação (Ctrl+C para sair):
    echo ====================================================
    docker-compose logs -f app
) else (
    echo.
    echo ✅ VozSocial MVP está rodando em background!
    echo    Use 'docker-compose logs -f app' para ver os logs
    echo    Use 'docker-compose down' para parar os containers
)

echo.
pause

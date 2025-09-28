@echo off
chcp 65001 >nul
cls

echo ====================================================
echo 🎤 VozSocial MVP - Desenvolvimento Local
echo ====================================================
echo.

:: Verificar se Java está instalado
echo ⏳ Verificando Java...
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Java não encontrado! Por favor, instale Java 17+.
    pause
    exit /b 1
)
echo ✅ Java encontrado!
echo.

:: Verificar se MySQL está rodando localmente
echo ⏳ Verificando MySQL local...
netstat -an | find "3306" >nul 2>&1
if %errorlevel% neq 0 (
    echo ⚠️  MySQL não encontrado na porta 3306!
    echo    Você pode:
    echo    1. Instalar MySQL localmente
    echo    2. Usar apenas os containers de banco: docker-compose up -d mysql redis
    echo.
    set /p use_docker_db="🐳 Deseja usar MySQL e Redis via Docker? (s/n): "
    if /i "!use_docker_db!"=="s" (
        echo 🐳 Iniciando apenas MySQL e Redis...
        docker-compose up -d mysql redis
        echo ⏳ Aguardando MySQL estar pronto...
        timeout /t 10 /nobreak >nul
    ) else (
        echo ❌ Configure um banco MySQL local ou use Docker.
        pause
        exit /b 1
    )
) else (
    echo ✅ MySQL encontrado!
)
echo.

:: Criar diretórios necessários
echo 📁 Criando diretórios necessários...
if not exist "uploads\audio" mkdir uploads\audio
echo ✅ Diretórios criados!
echo.

:: Compilar aplicação
echo 🔨 Compilando aplicação...
if exist "mvnw.cmd" (
    call mvnw.cmd clean compile
) else if exist "mvnw" (
    call mvnw clean compile
) else (
    mvn clean compile
)

if %errorlevel% neq 0 (
    echo ❌ Erro na compilação!
    pause
    exit /b 1
)
echo ✅ Compilação concluída!
echo.

:: Executar aplicação
echo 🚀 Iniciando aplicação Spring Boot...
echo ====================================================
echo 📍 URLs importantes:
echo    🌐 API Backend: http://localhost:8080/api
echo    📚 Swagger UI: http://localhost:8080/swagger-ui.html
echo    🔍 Health Check: http://localhost:8080/api/actuator/health
echo ====================================================
echo.
echo 💡 Pressione Ctrl+C para parar a aplicação
echo.

if exist "mvnw.cmd" (
    call mvnw.cmd spring-boot:run
) else if exist "mvnw" (
    call mvnw spring-boot:run
) else (
    mvn spring-boot:run
)

echo.
echo 🛑 Aplicação parada.
pause

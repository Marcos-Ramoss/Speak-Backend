@echo off
chcp 65001 >nul
cls

echo ====================================================
echo 🛑 VozSocial MVP - Script de Parada
echo ====================================================
echo.

:: Verificar se Docker está rodando
echo ⏳ Verificando se Docker está rodando...
docker --version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Docker não encontrado!
    pause
    exit /b 1
)

docker info >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Docker não está rodando!
    pause
    exit /b 1
)
echo ✅ Docker está rodando!
echo.

:: Mostrar containers ativos
echo 📊 Containers ativos:
docker-compose ps
echo.

:: Parar containers
echo 🛑 Parando containers do VozSocial...
docker-compose down

if %errorlevel% neq 0 (
    echo ❌ Erro ao parar containers!
    pause
    exit /b 1
)
echo ✅ Containers parados com sucesso!
echo.

:: Perguntar se quer limpar volumes
set /p clean_volumes="🧹 Deseja limpar os volumes (dados do banco serão perdidos)? (s/n): "
if /i "%clean_volumes%"=="s" (
    echo 🧹 Limpando volumes...
    docker-compose down -v
    docker volume prune -f
    echo ✅ Volumes limpos!
)
echo.

:: Perguntar se quer limpar imagens
set /p clean_images="🧹 Deseja limpar imagens não utilizadas? (s/n): "
if /i "%clean_images%"=="s" (
    echo 🧹 Limpando imagens...
    docker image prune -f
    echo ✅ Imagens limpas!
)
echo.

echo ====================================================
echo ✅ VozSocial MVP parado com sucesso!
echo ====================================================
echo.
echo Para reiniciar, execute: start-vozsocial.bat
echo.
pause

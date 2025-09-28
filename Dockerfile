FROM openjdk:17-jdk-slim

# Instalar dependências necessárias
RUN apt-get update && apt-get install -y \
    curl \
    maven \
    && rm -rf /var/lib/apt/lists/*

# Criar diretório da aplicação
WORKDIR /app

# Copiar arquivos de configuração do Maven
COPY pom.xml .

# Baixar dependências (cache layer)
RUN mvn dependency:go-offline -B

# Copiar código fonte
COPY src src

# Compilar aplicação
RUN mvn clean package -DskipTests

# Criar diretório para uploads
RUN mkdir -p /app/uploads/audio

# Expor porta
EXPOSE 8080

# Comando para executar a aplicação
ENTRYPOINT ["java", "-jar", "target/vozsocial-backend-1.0.0.jar"]

package com.vozsocial.domain.service;

import com.vozsocial.application.dto.ArquivoAudioDto;
import com.vozsocial.application.mapper.ArquivoAudioMapper;
import com.vozsocial.domain.entity.ArquivoAudio;
import com.vozsocial.domain.entity.Usuario;
import com.vozsocial.infrastructure.repository.ArquivoAudioRepository;
import com.vozsocial.infrastructure.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service responsável pela lógica de negócio relacionada aos arquivos de áudio
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ArquivoAudioService {

    private final ArquivoAudioRepository arquivoAudioRepository;
    private final UsuarioRepository usuarioRepository;
    private final ArquivoAudioMapper arquivoAudioMapper;
    private final GoogleAIService googleAIService;

    @Value("${storage.audio.base-path}")
    private String caminhoBaseStorage;

    @Value("${storage.audio.max-duration-seconds:15}")
    private int duracaoMaximaSegundos;

    /**
     * Busca arquivo de áudio por ID
     */
    @Transactional(readOnly = true)
    public Optional<ArquivoAudioDto> buscarPorId(Long id) {
        log.debug("Buscando arquivo de áudio por ID: {}", id);
        
        return arquivoAudioRepository.findById(id)
                .map(arquivoAudioMapper::paraDto);
    }

    /**
     * Lista arquivos de áudio por usuário
     */
    @Transactional(readOnly = true)
    public List<ArquivoAudioDto> listarPorUsuario(Long usuarioId) {
        log.debug("Listando arquivos de áudio do usuário: {}", usuarioId);
        
        return arquivoAudioRepository.findByUsuarioIdOrderByCriadoEmDesc(usuarioId)
                .stream()
                .map(arquivoAudioMapper::paraDto)
                .collect(Collectors.toList());
    }

    /**
     * Processa upload de arquivo de áudio via MultipartFile
     */
    public ArquivoAudioDto processarUploadArquivo(MultipartFile arquivo, Long usuarioId) {
        log.info("Processando upload de arquivo de áudio para usuário: {}", usuarioId);
        
        validarArquivo(arquivo);
        Usuario usuario = buscarUsuario(usuarioId);
        
        String nomeArquivo = gerarNomeArquivoUnico(arquivo.getOriginalFilename());
        String caminhoArquivo = salvarArquivo(arquivo, nomeArquivo);
        
        ArquivoAudio arquivoAudio = ArquivoAudio.builder()
                .usuario(usuario)
                .nomeArquivoOriginal(arquivo.getOriginalFilename())
                .caminhoArquivo(caminhoArquivo)
                .tamanhoArquivo(arquivo.getSize())
                .tipoMime(arquivo.getContentType())
                .duracaoSegundos(calcularDuracaoAproximada(arquivo.getSize()))
                .build();
        
        ArquivoAudio arquivoSalvo = arquivoAudioRepository.save(arquivoAudio);
        
        log.info("Arquivo de áudio salvo com sucesso. ID: {}", arquivoSalvo.getId());
        return arquivoAudioMapper.paraDto(arquivoSalvo);
    }

    /**
     * Processa upload de áudio via base64 DataURI
     */
    public ArquivoAudioDto processarUploadBase64(String audioDataUri, Long usuarioId, String nomeArquivo) {
        log.info("Processando upload de áudio base64 para usuário: {}", usuarioId);
        
        validarAudioDataUri(audioDataUri);
        Usuario usuario = buscarUsuario(usuarioId);
        
        byte[] dadosAudio = extrairDadosBase64(audioDataUri);
        String tipoMime = extrairTipoMime(audioDataUri);
        
        String nomeArquivoFinal = nomeArquivo != null ? nomeArquivo : 
            "audio_" + UUID.randomUUID().toString() + ".webm";
        
        String caminhoArquivo = salvarArquivoBase64(dadosAudio, nomeArquivoFinal);
        
        ArquivoAudio arquivoAudio = ArquivoAudio.builder()
                .usuario(usuario)
                .nomeArquivoOriginal(nomeArquivoFinal)
                .caminhoArquivo(caminhoArquivo)
                .tamanhoArquivo((long) dadosAudio.length)
                .tipoMime(tipoMime)
                .duracaoSegundos(calcularDuracaoAproximada((long) dadosAudio.length))
                .build();
        
        ArquivoAudio arquivoSalvo = arquivoAudioRepository.save(arquivoAudio);
        
        log.info("Arquivo de áudio base64 salvo com sucesso. ID: {}", arquivoSalvo.getId());
        return arquivoAudioMapper.paraDto(arquivoSalvo);
    }

    /**
     * Transcreve áudio usando Google AI
     */
    public ArquivoAudioDto transcreverAudio(Long arquivoId, String audioDataUri) {
        log.info("Transcrevendo áudio ID: {}", arquivoId);
        
        ArquivoAudio arquivoAudio = arquivoAudioRepository.findById(arquivoId)
                .orElseThrow(() -> new RuntimeException("Arquivo de áudio não encontrado"));
        
        String transcricao = googleAIService.transcreverAudio(audioDataUri);
        
        arquivoAudio.setTranscricao(transcricao);
        ArquivoAudio arquivoAtualizado = arquivoAudioRepository.save(arquivoAudio);
        
        log.info("Transcrição concluída para arquivo ID: {}", arquivoId);
        return arquivoAudioMapper.paraDto(arquivoAtualizado);
    }

    /**
     * Remove arquivo de áudio
     */
    public void removerArquivo(Long id) {
        log.info("Removendo arquivo de áudio ID: {}", id);
        
        ArquivoAudio arquivoAudio = arquivoAudioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Arquivo de áudio não encontrado"));
        
        // Remove arquivo físico
        removerArquivoFisico(arquivoAudio.getCaminhoArquivo());
        
        // Remove registro do banco
        arquivoAudioRepository.delete(arquivoAudio);
        
        log.info("Arquivo de áudio removido com sucesso. ID: {}", id);
    }

    /**
     * Valida arquivo de upload
     */
    private void validarArquivo(MultipartFile arquivo) {
        if (arquivo == null || arquivo.isEmpty()) {
            throw new IllegalArgumentException("Arquivo é obrigatório");
        }
        
        if (arquivo.getSize() > 50 * 1024 * 1024) { // 50MB
            throw new IllegalArgumentException("Arquivo muito grande. Máximo 50MB");
        }
        
        String contentType = arquivo.getContentType();
        if (contentType == null || !isFormatoSuportado(contentType)) {
            throw new IllegalArgumentException("Formato de arquivo não suportado");
        }
    }

    /**
     * Valida audio data URI
     */
    private void validarAudioDataUri(String audioDataUri) {
        if (audioDataUri == null || audioDataUri.trim().isEmpty()) {
            throw new IllegalArgumentException("Dados do áudio são obrigatórios");
        }
        
        if (!audioDataUri.startsWith("data:audio/")) {
            throw new IllegalArgumentException("Formato de dados inválido");
        }
    }

    /**
     * Verifica se formato é suportado
     */
    private boolean isFormatoSuportado(String contentType) {
        return contentType.equals("audio/webm") || 
               contentType.equals("audio/mp3") || 
               contentType.equals("audio/wav") ||
               contentType.equals("audio/mpeg");
    }

    /**
     * Busca usuário por ID
     */
    private Usuario buscarUsuario(Long usuarioId) {
        return usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }

    /**
     * Gera nome único para arquivo
     */
    private String gerarNomeArquivoUnico(String nomeOriginal) {
        String extensao = extrairExtensao(nomeOriginal);
        return UUID.randomUUID().toString() + "_" + 
               LocalDateTime.now().toString().replace(":", "-") + 
               extensao;
    }

    /**
     * Extrai extensão do arquivo
     */
    private String extrairExtensao(String nomeArquivo) {
        if (nomeArquivo == null || !nomeArquivo.contains(".")) {
            return ".webm";
        }
        return nomeArquivo.substring(nomeArquivo.lastIndexOf("."));
    }

    /**
     * Salva arquivo no sistema de arquivos
     */
    private String salvarArquivo(MultipartFile arquivo, String nomeArquivo) {
        Path diretorioDestino = Paths.get(caminhoBaseStorage);
        criarDiretorioSeNaoExistir(diretorioDestino);
        
        Path caminhoCompleto = diretorioDestino.resolve(nomeArquivo);
        
        try {
            Files.copy(arquivo.getInputStream(), caminhoCompleto, StandardCopyOption.REPLACE_EXISTING);
            return caminhoCompleto.toString();
        } catch (IOException e) {
            log.error("Erro ao salvar arquivo: {}", e.getMessage());
            throw new RuntimeException("Erro ao salvar arquivo");
        }
    }

    /**
     * Salva arquivo base64 no sistema de arquivos
     */
    private String salvarArquivoBase64(byte[] dadosAudio, String nomeArquivo) {
        Path diretorioDestino = Paths.get(caminhoBaseStorage);
        criarDiretorioSeNaoExistir(diretorioDestino);
        
        Path caminhoCompleto = diretorioDestino.resolve(nomeArquivo);
        
        try {
            Files.write(caminhoCompleto, dadosAudio);
            return caminhoCompleto.toString();
        } catch (IOException e) {
            log.error("Erro ao salvar arquivo base64: {}", e.getMessage());
            throw new RuntimeException("Erro ao salvar arquivo");
        }
    }

    /**
     * Cria diretório se não existir
     */
    private void criarDiretorioSeNaoExistir(Path diretorio) {
        try {
            if (!Files.exists(diretorio)) {
                Files.createDirectories(diretorio);
            }
        } catch (IOException e) {
            log.error("Erro ao criar diretório: {}", e.getMessage());
            throw new RuntimeException("Erro ao criar diretório de storage");
        }
    }

    /**
     * Extrai dados base64 do data URI
     */
    private byte[] extrairDadosBase64(String audioDataUri) {
        String dadosBase64 = audioDataUri.substring(audioDataUri.indexOf(",") + 1);
        return Base64.getDecoder().decode(dadosBase64);
    }

    /**
     * Extrai tipo MIME do data URI
     */
    private String extrairTipoMime(String audioDataUri) {
        String header = audioDataUri.substring(0, audioDataUri.indexOf(";"));
        return header.substring(5); // Remove "data:"
    }

    /**
     * Calcula duração aproximada baseada no tamanho do arquivo
     */
    private BigDecimal calcularDuracaoAproximada(Long tamanhoBytes) {
        // Estimativa aproximada: 1 segundo ≈ 16KB para WebM
        double duracaoEstimada = tamanhoBytes / (16.0 * 1024);
        return BigDecimal.valueOf(Math.min(duracaoEstimada, duracaoMaximaSegundos));
    }

    /**
     * Remove arquivo físico do sistema
     */
    private void removerArquivoFisico(String caminhoArquivo) {
        try {
            Path path = Paths.get(caminhoArquivo);
            if (Files.exists(path)) {
                Files.delete(path);
                log.debug("Arquivo físico removido: {}", caminhoArquivo);
            }
        } catch (IOException e) {
            log.warn("Erro ao remover arquivo físico: {}", e.getMessage());
        }
    }
}

package com.vozsocial.presentation.controller;

import com.vozsocial.application.dto.ArquivoAudioDto;
import com.vozsocial.application.dto.request.TranscricaoRequest;
import com.vozsocial.application.dto.request.TransformacaoVozRequest;
import com.vozsocial.application.dto.response.TranscricaoResponse;
import com.vozsocial.application.dto.response.TransformacaoVozResponse;
import com.vozsocial.domain.service.ArquivoAudioService;
import com.vozsocial.domain.service.GoogleAIService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Controller responsável pelos endpoints relacionados ao processamento de áudio
 */
@RestController
@RequestMapping("/audio")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Áudio", description = "Operações relacionadas ao processamento de áudio")
@CrossOrigin(origins = "*")
public class AudioController {

    private final ArquivoAudioService arquivoAudioService;
    private final GoogleAIService googleAIService;

    /**
     * Busca arquivo de áudio por ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Buscar arquivo de áudio", description = "Retorna dados de um arquivo de áudio específico")
    public ResponseEntity<ArquivoAudioDto> buscarArquivoPorId(
            @Parameter(description = "ID do arquivo de áudio") @PathVariable Long id) {
        
        log.debug("Requisição para buscar arquivo de áudio ID: {}", id);
        
        return arquivoAudioService.buscarPorId(id)
                .map(arquivo -> ResponseEntity.ok(arquivo))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Lista arquivos de áudio por usuário
     */
    @GetMapping("/usuario/{usuarioId}")
    @Operation(summary = "Listar arquivos por usuário", 
               description = "Retorna lista de arquivos de áudio de um usuário")
    public ResponseEntity<List<ArquivoAudioDto>> listarArquivosPorUsuario(
            @Parameter(description = "ID do usuário") @PathVariable Long usuarioId) {
        
        log.debug("Requisição para listar arquivos do usuário: {}", usuarioId);
        
        List<ArquivoAudioDto> arquivos = arquivoAudioService.listarPorUsuario(usuarioId);
        return ResponseEntity.ok(arquivos);
    }

    /**
     * Upload de arquivo de áudio via multipart
     */
    @PostMapping("/upload")
    @Operation(summary = "Upload de arquivo de áudio", 
               description = "Faz upload de um arquivo de áudio via multipart")
    public ResponseEntity<ArquivoAudioDto> uploadArquivo(
            @Parameter(description = "Arquivo de áudio") @RequestParam("arquivo") MultipartFile arquivo,
            @Parameter(description = "ID do usuário") @RequestParam("usuarioId") Long usuarioId) {
        
        log.info("Requisição para upload de arquivo do usuário: {}", usuarioId);
        
        ArquivoAudioDto arquivoSalvo = arquivoAudioService.processarUploadArquivo(arquivo, usuarioId);
        return ResponseEntity.status(HttpStatus.CREATED).body(arquivoSalvo);
    }

    /**
     * Upload de áudio via base64
     */
    @PostMapping("/upload-base64")
    @Operation(summary = "Upload de áudio base64", 
               description = "Faz upload de áudio através de dados base64")
    public ResponseEntity<ArquivoAudioDto> uploadAudioBase64(
            @Parameter(description = "Dados do áudio em base64") @RequestParam("audioDataUri") String audioDataUri,
            @Parameter(description = "ID do usuário") @RequestParam("usuarioId") Long usuarioId,
            @Parameter(description = "Nome do arquivo") @RequestParam(value = "nomeArquivo", required = false) String nomeArquivo) {
        
        log.info("Requisição para upload base64 do usuário: {}", usuarioId);
        
        ArquivoAudioDto arquivoSalvo = arquivoAudioService.processarUploadBase64(
            audioDataUri, usuarioId, nomeArquivo);
        return ResponseEntity.status(HttpStatus.CREATED).body(arquivoSalvo);
    }

    /**
     * Transcreve áudio usando Google AI
     */
    @PostMapping("/transcrever")
    @Operation(summary = "Transcrever áudio", 
               description = "Transcreve áudio usando Google AI")
    public ResponseEntity<TranscricaoResponse> transcreverAudio(
            @Parameter(description = "Dados para transcrição") 
            @Valid @RequestBody TranscricaoRequest request) {
        
        log.info("Requisição para transcrição de áudio");
        
        try {
            String transcricao = googleAIService.transcreverAudio(request.getAudioDataUri());
            
            TranscricaoResponse response = TranscricaoResponse.builder()
                    .transcricao(transcricao)
                    .sucesso(true)
                    .mensagem("Transcrição realizada com sucesso")
                    .build();
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Erro na transcrição: {}", e.getMessage());
            
            TranscricaoResponse response = TranscricaoResponse.builder()
                    .sucesso(false)
                    .mensagem("Erro na transcrição: " + e.getMessage())
                    .build();
            
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Transforma voz para robótica
     */
    @PostMapping("/transformar-voz")
    @Operation(summary = "Transformar voz", 
               description = "Transforma voz aplicando filtros (natural/robótico)")
    public ResponseEntity<TransformacaoVozResponse> transformarVoz(
            @Parameter(description = "Dados para transformação de voz") 
            @Valid @RequestBody TransformacaoVozRequest request) {
        
        log.info("Requisição para transformação de voz - tipo: {}", request.getTipoFiltro());
        
        try {
            String audioTransformado;
            String transcricao = request.getTranscricao();
            
            switch (request.getTipoFiltro()) {
                case ROBOTICO:
                    audioTransformado = googleAIService.transformarVozRobotica(
                        request.getAudioDataUri(), transcricao);
                    break;
                case NATURAL:
                default:
                    audioTransformado = request.getAudioDataUri(); // Sem transformação
                    break;
            }
            
            // Se não há transcrição, gera uma
            if (transcricao == null || transcricao.trim().isEmpty()) {
                transcricao = googleAIService.transcreverAudio(request.getAudioDataUri());
            }
            
            TransformacaoVozResponse response = TransformacaoVozResponse.builder()
                    .audioTransformadoDataUri(audioTransformado)
                    .transcricao(transcricao)
                    .sucesso(true)
                    .mensagem("Transformação realizada com sucesso")
                    .build();
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Erro na transformação de voz: {}", e.getMessage());
            
            TransformacaoVozResponse response = TransformacaoVozResponse.builder()
                    .sucesso(false)
                    .mensagem("Erro na transformação: " + e.getMessage())
                    .build();
            
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Remove arquivo de áudio
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Remover arquivo de áudio", 
               description = "Remove um arquivo de áudio do sistema")
    public ResponseEntity<Void> removerArquivo(
            @Parameter(description = "ID do arquivo de áudio") @PathVariable Long id) {
        
        log.info("Requisição para remover arquivo de áudio ID: {}", id);
        
        arquivoAudioService.removerArquivo(id);
        return ResponseEntity.noContent().build();
    }
}

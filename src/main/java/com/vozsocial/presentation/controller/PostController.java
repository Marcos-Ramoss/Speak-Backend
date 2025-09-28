package com.vozsocial.presentation.controller;

import com.vozsocial.application.dto.PostAudioDto;
import com.vozsocial.application.dto.request.CriarPostRequest;
import com.vozsocial.domain.service.PostAudioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * Controller responsável pelos endpoints relacionados aos posts de áudio
 */
@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Posts", description = "Operações relacionadas aos posts de áudio")
@CrossOrigin(origins = "*")
public class PostController {

    private final PostAudioService postAudioService;

    /**
     * Busca post por ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Buscar post por ID", description = "Retorna dados de um post específico")
    public ResponseEntity<PostAudioDto> buscarPorId(
            @Parameter(description = "ID do post") @PathVariable Long id) {
        
        log.debug("Requisição para buscar post ID: {}", id);
        
        return postAudioService.buscarPorId(id)
                .map(post -> ResponseEntity.ok(post))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Busca feed principal de posts
     */
    @GetMapping("/feed")
    @Operation(summary = "Buscar feed de posts", description = "Retorna feed principal com posts mais recentes")
    public ResponseEntity<Page<PostAudioDto>> buscarFeed(
            @Parameter(description = "Número da página (0-based)") @RequestParam(defaultValue = "0") int pagina,
            @Parameter(description = "Tamanho da página") @RequestParam(defaultValue = "10") int tamanho) {
        
        log.debug("Requisição para buscar feed - página: {}, tamanho: {}", pagina, tamanho);
        
        Pageable pageable = PageRequest.of(pagina, tamanho);
        Page<PostAudioDto> feed = postAudioService.buscarFeed(pageable);
        
        return ResponseEntity.ok(feed);
    }

    /**
     * Busca posts por usuário
     */
    @GetMapping("/usuario/{usuarioId}")
    @Operation(summary = "Buscar posts por usuário", description = "Retorna posts de um usuário específico")
    public ResponseEntity<Page<PostAudioDto>> buscarPostsDoUsuario(
            @Parameter(description = "ID do usuário") @PathVariable Long usuarioId,
            @Parameter(description = "Número da página (0-based)") @RequestParam(defaultValue = "0") int pagina,
            @Parameter(description = "Tamanho da página") @RequestParam(defaultValue = "10") int tamanho) {
        
        log.debug("Requisição para buscar posts do usuário: {}", usuarioId);
        
        Pageable pageable = PageRequest.of(pagina, tamanho);
        Page<PostAudioDto> posts = postAudioService.buscarPostsDoUsuario(usuarioId, pageable);
        
        return ResponseEntity.ok(posts);
    }

    /**
     * Busca posts mais curtidos
     */
    @GetMapping("/mais-curtidos")
    @Operation(summary = "Buscar posts mais curtidos", description = "Retorna posts ordenados por número de curtidas")
    public ResponseEntity<Page<PostAudioDto>> buscarPostsMaisCurtidos(
            @Parameter(description = "Número da página (0-based)") @RequestParam(defaultValue = "0") int pagina,
            @Parameter(description = "Tamanho da página") @RequestParam(defaultValue = "10") int tamanho) {
        
        log.debug("Requisição para buscar posts mais curtidos");
        
        Pageable pageable = PageRequest.of(pagina, tamanho);
        Page<PostAudioDto> posts = postAudioService.buscarPostsMaisCurtidos(pageable);
        
        return ResponseEntity.ok(posts);
    }

    /**
     * Cria post com upload de arquivo
     */
    @PostMapping("/com-arquivo")
    @Operation(summary = "Criar post com arquivo", description = "Cria novo post fazendo upload de arquivo de áudio")
    public ResponseEntity<PostAudioDto> criarPostComArquivo(
            @Parameter(description = "Arquivo de áudio") @RequestParam("arquivo") MultipartFile arquivo,
            @Parameter(description = "ID do usuário") @RequestParam("usuarioId") Long usuarioId,
            @Parameter(description = "Conteúdo do post") @RequestParam(value = "conteudo", required = false) String conteudo,
            @Parameter(description = "Tipo de filtro de voz") @RequestParam(value = "tipoFiltroVoz", defaultValue = "NATURAL") String tipoFiltroVoz) {
        
        log.info("Requisição para criar post com arquivo - usuário: {}", usuarioId);
        
        CriarPostRequest request = CriarPostRequest.builder()
                .usuarioId(usuarioId)
                .conteudo(conteudo)
                .tipoFiltroVoz(com.vozsocial.domain.enums.TipoFiltroVoz.valueOf(tipoFiltroVoz))
                .build();
        
        PostAudioDto postCriado = postAudioService.criarPostComArquivo(arquivo, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(postCriado);
    }

    /**
     * Cria post com áudio base64
     */
    @PostMapping("/com-audio-base64")
    @Operation(summary = "Criar post com áudio base64", description = "Cria novo post com áudio em formato base64")
    public ResponseEntity<PostAudioDto> criarPostComAudioBase64(
            @Parameter(description = "Dados do post com áudio base64") 
            @Valid @RequestBody CriarPostRequest request) {
        
        log.info("Requisição para criar post com áudio base64 - usuário: {}", request.getUsuarioId());
        
        PostAudioDto postCriado = postAudioService.criarPostComAudioBase64(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(postCriado);
    }

    /**
     * Atualiza conteúdo do post
     */
    @PutMapping("/{id}")
    @Operation(summary = "Atualizar post", description = "Atualiza o conteúdo de um post existente")
    public ResponseEntity<PostAudioDto> atualizarPost(
            @Parameter(description = "ID do post") @PathVariable Long id,
            @Parameter(description = "Novo conteúdo") @RequestParam("conteudo") String conteudo) {
        
        log.info("Requisição para atualizar post ID: {}", id);
        
        PostAudioDto postAtualizado = postAudioService.atualizarPost(id, conteudo);
        return ResponseEntity.ok(postAtualizado);
    }

    /**
     * Curte ou descurte um post
     */
    @PostMapping("/{id}/curtir")
    @Operation(summary = "Curtir/Descurtir post", description = "Alterna curtida em um post")
    public ResponseEntity<PostAudioDto> alternarCurtida(
            @Parameter(description = "ID do post") @PathVariable Long id,
            @Parameter(description = "ID do usuário") @RequestParam("usuarioId") Long usuarioId) {
        
        log.info("Requisição para alternar curtida - post: {}, usuário: {}", id, usuarioId);
        
        PostAudioDto postAtualizado = postAudioService.alternarCurtida(id, usuarioId);
        return ResponseEntity.ok(postAtualizado);
    }

    /**
     * Remove post
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Remover post", description = "Remove um post do sistema")
    public ResponseEntity<Void> removerPost(
            @Parameter(description = "ID do post") @PathVariable Long id) {
        
        log.info("Requisição para remover post ID: {}", id);
        
        postAudioService.removerPost(id);
        return ResponseEntity.noContent().build();
    }
}

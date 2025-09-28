package com.vozsocial.domain.service;

import com.vozsocial.application.dto.ArquivoAudioDto;
import com.vozsocial.application.dto.PostAudioDto;
import com.vozsocial.application.dto.request.CriarPostRequest;
import com.vozsocial.application.mapper.PostAudioMapper;
import com.vozsocial.domain.entity.ArquivoAudio;
import com.vozsocial.domain.entity.PostAudio;
import com.vozsocial.domain.entity.Usuario;
import com.vozsocial.infrastructure.repository.ArquivoAudioRepository;
import com.vozsocial.infrastructure.repository.CurtidaPostRepository;
import com.vozsocial.infrastructure.repository.PostAudioRepository;
import com.vozsocial.infrastructure.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

/**
 * Service responsável pela lógica de negócio relacionada aos posts de áudio
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PostAudioService {

    private final PostAudioRepository postAudioRepository;
    private final UsuarioRepository usuarioRepository;
    private final ArquivoAudioRepository arquivoAudioRepository;
    private final CurtidaPostRepository curtidaPostRepository;
    private final PostAudioMapper postAudioMapper;
    private final ArquivoAudioService arquivoAudioService;
    private final GoogleAIService googleAIService;

    /**
     * Busca post por ID
     */
    @Transactional(readOnly = true)
    public Optional<PostAudioDto> buscarPorId(Long id) {
        log.debug("Buscando post por ID: {}", id);
        
        return postAudioRepository.findById(id)
                .map(postAudioMapper::paraDto);
    }

    /**
     * Busca feed principal de posts
     */
    @Transactional(readOnly = true)
    public Page<PostAudioDto> buscarFeed(Pageable pageable) {
        log.debug("Buscando feed de posts - página: {}, tamanho: {}", 
                 pageable.getPageNumber(), pageable.getPageSize());
        
        return postAudioRepository.findAllByOrderByCriadoEmDesc(pageable)
                .map(postAudioMapper::paraDto);
    }

    /**
     * Busca posts por usuário
     */
    @Transactional(readOnly = true)
    public Page<PostAudioDto> buscarPostsDoUsuario(Long usuarioId, Pageable pageable) {
        log.debug("Buscando posts do usuário: {}", usuarioId);
        
        return postAudioRepository.findByUsuarioIdOrderByCriadoEmDesc(usuarioId, pageable)
                .map(postAudioMapper::paraDto);
    }

    /**
     * Busca posts mais curtidos
     */
    @Transactional(readOnly = true)
    public Page<PostAudioDto> buscarPostsMaisCurtidos(Pageable pageable) {
        log.debug("Buscando posts mais curtidos");
        
        return postAudioRepository.buscarMaisCurtidos(pageable)
                .map(postAudioMapper::paraDto);
    }

    /**
     * Cria novo post com upload de arquivo
     */
    public PostAudioDto criarPostComArquivo(MultipartFile arquivo, CriarPostRequest request) {
        log.info("Criando post com arquivo para usuário: {}", request.getUsuarioId());
        
        Usuario usuario = buscarUsuario(request.getUsuarioId());
        
        // Processa upload do arquivo de áudio
        ArquivoAudioDto arquivoAudioDto = arquivoAudioService.processarUploadArquivo(
            arquivo, request.getUsuarioId());
        
        // Busca a entidade do arquivo de áudio
        ArquivoAudio arquivoAudio = buscarArquivoAudio(arquivoAudioDto.getId());
        
        // Cria o post
        PostAudio post = PostAudio.builder()
                .usuario(usuario)
                .arquivoAudio(arquivoAudio)
                .conteudo(request.getConteudo())
                .tipoFiltroVoz(request.getTipoFiltroVoz())
                .processado(false)
                .quantidadeCurtidas(0)
                .quantidadeComentarios(0)
                .quantidadeCompartilhamentos(0)
                .build();
        
        PostAudio postSalvo = postAudioRepository.save(post);
        
        log.info("Post criado com sucesso. ID: {}", postSalvo.getId());
        return postAudioMapper.paraDto(postSalvo);
    }

    /**
     * Cria novo post com áudio base64
     */
    public PostAudioDto criarPostComAudioBase64(CriarPostRequest request) {
        log.info("Criando post com áudio base64 para usuário: {}", request.getUsuarioId());
        
        Usuario usuario = buscarUsuario(request.getUsuarioId());
        
        // Processa upload do áudio base64
        ArquivoAudioDto arquivoAudioDto = arquivoAudioService.processarUploadBase64(
            request.getAudioDataUri(), request.getUsuarioId(), request.getNomeArquivo());
        
        // Busca a entidade do arquivo de áudio
        ArquivoAudio arquivoAudio = buscarArquivoAudio(arquivoAudioDto.getId());
        
        // Cria o post
        PostAudio post = PostAudio.builder()
                .usuario(usuario)
                .arquivoAudio(arquivoAudio)
                .conteudo(request.getConteudo())
                .tipoFiltroVoz(request.getTipoFiltroVoz())
                .processado(false)
                .quantidadeCurtidas(0)
                .quantidadeComentarios(0)
                .quantidadeCompartilhamentos(0)
                .build();
        
        PostAudio postSalvo = postAudioRepository.save(post);
        
        // Processa transcrição assíncrona (em produção seria em background)
        processarTranscricaoPost(postSalvo.getId(), request.getAudioDataUri());
        
        log.info("Post criado com sucesso. ID: {}", postSalvo.getId());
        return postAudioMapper.paraDto(postSalvo);
    }

    /**
     * Atualiza conteúdo do post
     */
    public PostAudioDto atualizarPost(Long id, String novoConteudo) {
        log.info("Atualizando post ID: {}", id);
        
        PostAudio post = postAudioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post não encontrado"));
        
        post.setConteudo(novoConteudo);
        PostAudio postAtualizado = postAudioRepository.save(post);
        
        log.info("Post atualizado com sucesso. ID: {}", id);
        return postAudioMapper.paraDto(postAtualizado);
    }

    /**
     * Curte ou descurte um post
     */
    public PostAudioDto alternarCurtida(Long postId, Long usuarioId) {
        log.info("Alternando curtida do post {} pelo usuário {}", postId, usuarioId);
        
        PostAudio post = postAudioRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post não encontrado"));
        
        boolean jaCurtiu = curtidaPostRepository.existsByPostIdAndUsuarioId(postId, usuarioId);
        
        if (jaCurtiu) {
            // Remove curtida
            curtidaPostRepository.deleteByPostIdAndUsuarioId(postId, usuarioId);
            post.setQuantidadeCurtidas(post.getQuantidadeCurtidas() - 1);
            log.debug("Curtida removida do post {}", postId);
        } else {
            // Adiciona curtida
            Usuario usuario = buscarUsuario(usuarioId);
            com.vozsocial.domain.entity.CurtidaPost curtida = 
                com.vozsocial.domain.entity.CurtidaPost.builder()
                    .post(post)
                    .usuario(usuario)
                    .build();
            
            curtidaPostRepository.save(curtida);
            post.setQuantidadeCurtidas(post.getQuantidadeCurtidas() + 1);
            log.debug("Curtida adicionada ao post {}", postId);
        }
        
        PostAudio postAtualizado = postAudioRepository.save(post);
        return postAudioMapper.paraDto(postAtualizado);
    }

    /**
     * Remove post
     */
    public void removerPost(Long id) {
        log.info("Removendo post ID: {}", id);
        
        PostAudio post = postAudioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post não encontrado"));
        
        // Remove arquivo de áudio associado
        if (post.getArquivoAudio() != null) {
            arquivoAudioService.removerArquivo(post.getArquivoAudio().getId());
        }
        
        // Remove post
        postAudioRepository.delete(post);
        
        log.info("Post removido com sucesso. ID: {}", id);
    }

    /**
     * Processa transcrição do post
     */
    private void processarTranscricaoPost(Long postId, String audioDataUri) {
        log.debug("Processando transcrição para post ID: {}", postId);
        
        PostAudio post = postAudioRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post não encontrado"));
        
        String transcricao = googleAIService.transcreverAudio(audioDataUri);
        
        // Atualiza transcrição no arquivo de áudio
        ArquivoAudio arquivoAudio = post.getArquivoAudio();
        arquivoAudio.setTranscricao(transcricao);
        
        // Marca post como processado
        post.setProcessado(true);
        
        postAudioRepository.save(post);
        
        log.info("Transcrição processada para post ID: {}", postId);
    }

    /**
     * Busca usuário por ID
     */
    private Usuario buscarUsuario(Long usuarioId) {
        return usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }

    /**
     * Busca arquivo de áudio por ID
     */
    private ArquivoAudio buscarArquivoAudio(Long arquivoId) {
        return arquivoAudioRepository.findById(arquivoId)
                .orElseThrow(() -> new RuntimeException("Arquivo de áudio não encontrado"));
    }
}

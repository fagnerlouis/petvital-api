package com.petvital.api.service;

import com.petvital.api.domain.model.Clinica;
import com.petvital.api.domain.model.EstoqueMovimento;
import com.petvital.api.domain.model.Produto;
import com.petvital.api.domain.model.Usuario;
import com.petvital.api.domain.repository.ClinicaRepository;
import com.petvital.api.domain.repository.EstoqueMovimentoRepository;
import com.petvital.api.domain.repository.ProdutoRepository;
import com.petvital.api.domain.repository.UsuarioRepository;
import com.petvital.api.dto.EstoqueMovimentoRequestDTO;
import com.petvital.api.dto.EstoqueMovimentoResponseDTO;
import com.petvital.api.dto.ProdutoRequestDTO;
import com.petvital.api.dto.ProdutoResponseDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;
    private final EstoqueMovimentoRepository estoqueRepository;
    private final ClinicaRepository clinicaRepository;
    private final UsuarioRepository usuarioRepository;

    public ProdutoService(ProdutoRepository produtoRepository,
                          EstoqueMovimentoRepository estoqueRepository,
                          ClinicaRepository clinicaRepository,
                          UsuarioRepository usuarioRepository) {
        this.produtoRepository = produtoRepository;
        this.estoqueRepository = estoqueRepository;
        this.clinicaRepository = clinicaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public ProdutoResponseDTO criarProduto(ProdutoRequestDTO request) {
        Clinica clinica = clinicaRepository.findById(request.getClinicaId())
                .orElseThrow(() -> new IllegalArgumentException("Clínica não encontrada."));

        Produto produto = new Produto();
        produto.setClinica(clinica);
        produto.setNome(request.getNome());
        produto.setTipo(request.getTipo());
        produto.setUnidadeMedida(request.getUnidadeMedida());
        produto.setEstoqueMinimo(request.getEstoqueMinimo());
        produto.setPrecoVenda(request.getPrecoVenda());
        produto.setAtivo(true);

        return toProdutoResponseDTO(produtoRepository.save(produto));
    }

    @Transactional(readOnly = true)
    public List<ProdutoResponseDTO> listarProdutosAtivos(Long clinicaId) {
        return produtoRepository.findAllByClinicaIdAndAtivoTrueOrderByNomeAsc(clinicaId)
                .stream().map(this::toProdutoResponseDTO).collect(Collectors.toList());
    }

    @Transactional
    public EstoqueMovimentoResponseDTO registrarMovimento(EstoqueMovimentoRequestDTO request, String emailUsuario) {
        Produto produto = produtoRepository.findById(request.getProdutoId())
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado."));

        if (!produto.getClinica().getId().equals(request.getClinicaId())) {
            throw new IllegalArgumentException("Produto não pertence à clínica informada.");
        }

        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));

        EstoqueMovimento mov = new EstoqueMovimento();
        mov.setClinica(produto.getClinica());
        mov.setProduto(produto);
        mov.setTipoMovimento(request.getTipoMovimento()); // ENTRADA, SAIDA, AJUSTE
        mov.setQuantidade(request.getQuantidade());
        mov.setLote(request.getLote());
        mov.setValidade(request.getValidade());
        mov.setReferenciaId(request.getReferenciaId());
        mov.setUsuario(usuario);

        return toEstoqueResponseDTO(estoqueRepository.save(mov));
    }

    @Transactional(readOnly = true)
    public List<EstoqueMovimentoResponseDTO> listarMovimentos(Long produtoId, Long clinicaId) {
        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado."));
                
        if (!produto.getClinica().getId().equals(clinicaId)) {
            throw new IllegalArgumentException("Produto não pertence à clínica informada.");
        }

        return estoqueRepository.findAllByProdutoIdAndClinicaIdOrderByDataAddDesc(produtoId, clinicaId)
                .stream().map(this::toEstoqueResponseDTO).collect(Collectors.toList());
    }

    private ProdutoResponseDTO toProdutoResponseDTO(Produto p) {
        ProdutoResponseDTO dto = new ProdutoResponseDTO();
        dto.setId(p.getId());
        dto.setNome(p.getNome());
        dto.setTipo(p.getTipo());
        dto.setUnidadeMedida(p.getUnidadeMedida());
        dto.setEstoqueMinimo(p.getEstoqueMinimo());
        dto.setPrecoVenda(p.getPrecoVenda());
        dto.setAtivo(p.getAtivo());
        return dto;
    }

    private EstoqueMovimentoResponseDTO toEstoqueResponseDTO(EstoqueMovimento e) {
        EstoqueMovimentoResponseDTO dto = new EstoqueMovimentoResponseDTO();
        dto.setId(e.getId());
        dto.setProdutoId(e.getProduto().getId());
        dto.setTipoMovimento(e.getTipoMovimento());
        dto.setQuantidade(e.getQuantidade());
        dto.setLote(e.getLote());
        dto.setValidade(e.getValidade());
        dto.setReferenciaId(e.getReferenciaId());
        dto.setDataAdd(e.getDataAdd());
        
        EstoqueMovimentoResponseDTO.UsuarioResumoDTO user = new EstoqueMovimentoResponseDTO.UsuarioResumoDTO();
        user.setId(e.getUsuario().getId());
        user.setNome(e.getUsuario().getNome());
        dto.setUsuario(user);
        
        return dto;
    }
}

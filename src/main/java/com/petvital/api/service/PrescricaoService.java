package com.petvital.api.service;

import com.petvital.api.domain.model.*;
import com.petvital.api.domain.repository.ConsultaRepository;
import com.petvital.api.domain.repository.PrescricaoRepository;
import com.petvital.api.domain.repository.ProdutoRepository;
import com.petvital.api.domain.repository.UsuarioRepository;
import com.petvital.api.dto.PrescricaoRequestDTO;
import com.petvital.api.dto.PrescricaoResponseDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PrescricaoService {

    private final PrescricaoRepository prescricaoRepository;
    private final ConsultaRepository consultaRepository;
    private final ProdutoRepository produtoRepository;
    private final UsuarioRepository usuarioRepository;

    public PrescricaoService(PrescricaoRepository prescricaoRepository,
                             ConsultaRepository consultaRepository,
                             ProdutoRepository produtoRepository,
                             UsuarioRepository usuarioRepository) {
        this.prescricaoRepository = prescricaoRepository;
        this.consultaRepository = consultaRepository;
        this.produtoRepository = produtoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public PrescricaoResponseDTO criar(PrescricaoRequestDTO request, String emailVeterinario) {
        Usuario veterinario = usuarioRepository.findByEmail(emailVeterinario)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));

        // RN008: Apenas VETERINARIO pode prescrever receitas
        if (!"VETERINARIO".equalsIgnoreCase(veterinario.getPerfil())) {
            throw new SecurityException("RN008: Apenas usuários com perfil VETERINARIO podem emitir prescrições.");
        }

        Consulta consulta = consultaRepository.findByIdAndClinicaId(request.getConsultaId(), request.getClinicaId())
                .orElseThrow(() -> new IllegalArgumentException("Consulta não encontrada nesta clínica."));

        Prescricao prescricao = new Prescricao();
        prescricao.setClinica(consulta.getClinica());
        prescricao.setConsulta(consulta);
        prescricao.setVeterinario(veterinario);
        prescricao.setTipoReceita(request.getTipoReceita());
        prescricao.setInstrucoesGerais(request.getInstrucoesGerais());

        if (request.getItens() != null) {
            for (PrescricaoRequestDTO.ItemDTO itemDto : request.getItens()) {
                Produto produto = produtoRepository.findById(itemDto.getProdutoId())
                        .orElseThrow(() -> new IllegalArgumentException("Produto ID " + itemDto.getProdutoId() + " não encontrado."));
                
                if (!produto.getClinica().getId().equals(request.getClinicaId())) {
                    throw new IllegalArgumentException("O produto " + produto.getNome() + " não pertence a esta clínica.");
                }

                PrescricaoItem item = new PrescricaoItem();
                item.setPrescricao(prescricao);
                item.setProduto(produto);
                item.setDosagem(itemDto.getDosagem());
                item.setFrequencia(itemDto.getFrequencia());
                item.setDuracao(itemDto.getDuracao());
                item.setObservacoes(itemDto.getObservacoes());

                prescricao.getItens().add(item);
            }
        }

        return toResponseDTO(prescricaoRepository.save(prescricao));
    }

    @Transactional(readOnly = true)
    public List<PrescricaoResponseDTO> listarPorConsulta(Long consultaId, Long clinicaId) {
        consultaRepository.findByIdAndClinicaId(consultaId, clinicaId)
                .orElseThrow(() -> new IllegalArgumentException("Consulta não encontrada nesta clínica."));

        return prescricaoRepository.findAllByConsultaIdAndClinicaId(consultaId, clinicaId)
                .stream().map(this::toResponseDTO).collect(Collectors.toList());
    }

    private PrescricaoResponseDTO toResponseDTO(Prescricao p) {
        PrescricaoResponseDTO dto = new PrescricaoResponseDTO();
        dto.setId(p.getId());
        dto.setConsultaId(p.getConsulta().getId());
        dto.setTipoReceita(p.getTipoReceita());
        dto.setInstrucoesGerais(p.getInstrucoesGerais());
        dto.setDataAdd(p.getDataAdd());

        PrescricaoResponseDTO.VeterinarioResumoDTO vet = new PrescricaoResponseDTO.VeterinarioResumoDTO();
        vet.setId(p.getVeterinario().getId());
        vet.setNome(p.getVeterinario().getNome());
        dto.setVeterinario(vet);

        List<PrescricaoResponseDTO.ItemResponseDTO> itens = p.getItens().stream().map(i -> {
            PrescricaoResponseDTO.ItemResponseDTO itemDto = new PrescricaoResponseDTO.ItemResponseDTO();
            itemDto.setId(i.getId());
            itemDto.setProdutoId(i.getProduto().getId());
            itemDto.setNomeProduto(i.getProduto().getNome());
            itemDto.setDosagem(i.getDosagem());
            itemDto.setFrequencia(i.getFrequencia());
            itemDto.setDuracao(i.getDuracao());
            itemDto.setObservacoes(i.getObservacoes());
            return itemDto;
        }).collect(Collectors.toList());

        dto.setItens(itens);

        return dto;
    }
}

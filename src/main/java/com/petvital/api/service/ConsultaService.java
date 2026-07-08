package com.petvital.api.service;

import com.petvital.api.domain.model.*;
import com.petvital.api.domain.repository.*;
import com.petvital.api.dto.ConsultaHistoricoResponseDTO;
import com.petvital.api.dto.ConsultaRequestDTO;
import com.petvital.api.dto.ConsultaResponseDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ConsultaService {

    private final ConsultaRepository consultaRepository;
    private final ConsultaHistoricoRepository historicoRepository;
    private final ClinicaRepository clinicaRepository;
    private final AgendamentoRepository agendamentoRepository;
    private final UsuarioRepository usuarioRepository;

    public ConsultaService(ConsultaRepository consultaRepository,
                           ConsultaHistoricoRepository historicoRepository,
                           ClinicaRepository clinicaRepository,
                           AgendamentoRepository agendamentoRepository,
                           UsuarioRepository usuarioRepository) {
        this.consultaRepository = consultaRepository;
        this.historicoRepository = historicoRepository;
        this.clinicaRepository = clinicaRepository;
        this.agendamentoRepository = agendamentoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public ConsultaResponseDTO criar(ConsultaRequestDTO request) {
        Clinica clinica = clinicaRepository.findById(request.getClinicaId())
                .orElseThrow(() -> new IllegalArgumentException("Clínica não encontrada."));

        Usuario veterinario = usuarioRepository.findById(request.getVeterinarioId())
                .orElseThrow(() -> new IllegalArgumentException("Veterinário não encontrado."));

        Agendamento agendamento = null;
        if (request.getAgendamentoId() != null) {
            agendamento = agendamentoRepository.findByIdAndClinicaId(request.getAgendamentoId(), request.getClinicaId())
                    .orElseThrow(() -> new IllegalArgumentException("Agendamento não encontrado nesta clínica."));
        }

        Consulta consulta = new Consulta();
        consulta.setClinica(clinica);
        consulta.setAgendamento(agendamento);
        consulta.setVeterinario(veterinario);
        consulta.setMotivoConsulta(request.getMotivoConsulta());
        consulta.setAnamnese(request.getAnamnese());
        consulta.setExameFisico(request.getExameFisico());
        consulta.setDiagnostico(request.getDiagnostico());
        consulta.setConduta(request.getConduta());
        consulta.setHistoricoPrevio(request.getHistoricoPrevio());
        consulta.setVersao(1);

        return toResponseDTO(consultaRepository.save(consulta));
    }

    @Transactional
    public ConsultaResponseDTO atualizar(Long id, Long clinicaId, ConsultaRequestDTO request, String emailUsuarioLogado) {
        Consulta consulta = consultaRepository.findByIdAndClinicaId(id, clinicaId)
                .orElseThrow(() -> new IllegalArgumentException("Consulta não encontrada."));

        Usuario usuarioLogado = usuarioRepository.findByEmail(emailUsuarioLogado)
                .orElseThrow(() -> new IllegalArgumentException("Usuário logado não encontrado."));

        // RN006: Salvar estado ATUAL no histórico antes de alterar
        ConsultaHistorico historico = new ConsultaHistorico();
        historico.setClinica(consulta.getClinica());
        historico.setConsulta(consulta);
        historico.setUsuarioAlteracao(usuarioLogado);
        historico.setMotivoConsulta(consulta.getMotivoConsulta());
        historico.setAnamnese(consulta.getAnamnese());
        historico.setExameFisico(consulta.getExameFisico());
        historico.setDiagnostico(consulta.getDiagnostico());
        historico.setConduta(consulta.getConduta());
        historico.setHistoricoPrevio(consulta.getHistoricoPrevio());
        historico.setVersao(consulta.getVersao());
        historico.setDataAlteracao(LocalDateTime.now());
        historicoRepository.save(historico);

        // Atualizar consulta com novos dados
        if (request.getVeterinarioId() != null && !request.getVeterinarioId().equals(consulta.getVeterinario().getId())) {
            Usuario veterinario = usuarioRepository.findById(request.getVeterinarioId())
                    .orElseThrow(() -> new IllegalArgumentException("Veterinário não encontrado."));
            consulta.setVeterinario(veterinario);
        }
        
        consulta.setMotivoConsulta(request.getMotivoConsulta());
        consulta.setAnamnese(request.getAnamnese());
        consulta.setExameFisico(request.getExameFisico());
        consulta.setDiagnostico(request.getDiagnostico());
        consulta.setConduta(request.getConduta());
        consulta.setHistoricoPrevio(request.getHistoricoPrevio());
        consulta.setVersao(consulta.getVersao() + 1);

        return toResponseDTO(consultaRepository.save(consulta));
    }

    @Transactional(readOnly = true)
    public ConsultaResponseDTO buscarPorId(Long id, Long clinicaId) {
        return toResponseDTO(consultaRepository.findByIdAndClinicaId(id, clinicaId)
                .orElseThrow(() -> new IllegalArgumentException("Consulta não encontrada.")));
    }

    @Transactional(readOnly = true)
    public List<ConsultaResponseDTO> listarPorClinica(Long clinicaId) {
        return consultaRepository.findAllByClinicaIdOrderByDataAddDesc(clinicaId)
                .stream().map(this::toResponseDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ConsultaHistoricoResponseDTO> listarHistorico(Long id, Long clinicaId) {
        // Valida se a consulta existe
        consultaRepository.findByIdAndClinicaId(id, clinicaId)
                .orElseThrow(() -> new IllegalArgumentException("Consulta não encontrada."));

        return historicoRepository.findAllByConsultaIdAndClinicaIdOrderByVersaoDesc(id, clinicaId)
                .stream().map(this::toHistoricoResponseDTO).collect(Collectors.toList());
    }

    private ConsultaResponseDTO toResponseDTO(Consulta consulta) {
        ConsultaResponseDTO dto = new ConsultaResponseDTO();
        dto.setId(consulta.getId());
        dto.setClinicaId(consulta.getClinica().getId());
        dto.setAgendamentoId(consulta.getAgendamento() != null ? consulta.getAgendamento().getId() : null);
        
        ConsultaResponseDTO.VeterinarioResumoDTO vet = new ConsultaResponseDTO.VeterinarioResumoDTO();
        vet.setId(consulta.getVeterinario().getId());
        vet.setNome(consulta.getVeterinario().getNome());
        // vet.setCrmv(consulta.getVeterinario().getCrmv()); // CRMV removido anteriormente no modelo
        dto.setVeterinario(vet);
        
        dto.setMotivoConsulta(consulta.getMotivoConsulta());
        dto.setAnamnese(consulta.getAnamnese());
        dto.setExameFisico(consulta.getExameFisico());
        dto.setDiagnostico(consulta.getDiagnostico());
        dto.setConduta(consulta.getConduta());
        dto.setHistoricoPrevio(consulta.getHistoricoPrevio());
        dto.setVersao(consulta.getVersao());
        dto.setDataAdd(consulta.getDataAdd());
        dto.setDataAlt(consulta.getDataAlt());
        
        return dto;
    }

    private ConsultaHistoricoResponseDTO toHistoricoResponseDTO(ConsultaHistorico historico) {
        ConsultaHistoricoResponseDTO dto = new ConsultaHistoricoResponseDTO();
        dto.setId(historico.getId());
        dto.setConsultaId(historico.getConsulta().getId());
        
        ConsultaHistoricoResponseDTO.UsuarioAlteracaoDTO user = new ConsultaHistoricoResponseDTO.UsuarioAlteracaoDTO();
        user.setId(historico.getUsuarioAlteracao().getId());
        user.setNome(historico.getUsuarioAlteracao().getNome());
        dto.setUsuarioAlteracao(user);
        
        dto.setMotivoConsulta(historico.getMotivoConsulta());
        dto.setAnamnese(historico.getAnamnese());
        dto.setExameFisico(historico.getExameFisico());
        dto.setDiagnostico(historico.getDiagnostico());
        dto.setConduta(historico.getConduta());
        dto.setHistoricoPrevio(historico.getHistoricoPrevio());
        dto.setVersao(historico.getVersao());
        dto.setDataAlteracao(historico.getDataAlteracao());
        
        return dto;
    }
}

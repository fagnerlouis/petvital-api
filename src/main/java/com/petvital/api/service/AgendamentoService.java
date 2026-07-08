package com.petvital.api.service;

import com.petvital.api.domain.model.*;
import com.petvital.api.domain.repository.*;
import com.petvital.api.dto.AgendamentoRequestDTO;
import com.petvital.api.dto.AgendamentoResponseDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AgendamentoService {

    private final AgendamentoRepository agendamentoRepository;
    private final ClinicaRepository clinicaRepository;
    private final AnimalRepository animalRepository;
    private final UsuarioRepository usuarioRepository;

    public AgendamentoService(AgendamentoRepository agendamentoRepository,
                              ClinicaRepository clinicaRepository,
                              AnimalRepository animalRepository,
                              UsuarioRepository usuarioRepository) {
        this.agendamentoRepository = agendamentoRepository;
        this.clinicaRepository = clinicaRepository;
        this.animalRepository = animalRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public AgendamentoResponseDTO criar(AgendamentoRequestDTO request) {
        // Validação básica de horário
        if (!request.getDataHoraFim().isAfter(request.getDataHoraInicio())) {
            throw new IllegalArgumentException("O horário de fim deve ser posterior ao horário de início.");
        }

        Clinica clinica = clinicaRepository.findById(request.getClinicaId())
                .orElseThrow(() -> new IllegalArgumentException("Clínica não encontrada."));

        // Animal deve pertencer à mesma clínica
        Animal animal = animalRepository.findByIdAndClinicaId(request.getAnimalId(), request.getClinicaId())
                .orElseThrow(() -> new IllegalArgumentException("Animal não encontrado nesta clínica."));

        Agendamento agendamento = new Agendamento();
        agendamento.setClinica(clinica);
        agendamento.setAnimal(animal);
        agendamento.setDataHoraInicio(request.getDataHoraInicio());
        agendamento.setDataHoraFim(request.getDataHoraFim());
        agendamento.setTipoAtendimento(request.getTipoAtendimento());
        agendamento.setStatus("AGENDADO");
        agendamento.setObservacoes(request.getObservacoes());

        // Veterinário é opcional, mas se informado valida conflito de horário
        if (request.getVeterinarioId() != null) {
            Usuario veterinario = usuarioRepository.findById(request.getVeterinarioId())
                    .orElseThrow(() -> new IllegalArgumentException("Veterinário não encontrado."));

            List<Agendamento> conflitos = agendamentoRepository.findConflitosDeHorario(
                    veterinario.getId(), request.getDataHoraInicio(), request.getDataHoraFim());

            if (!conflitos.isEmpty()) {
                throw new IllegalArgumentException(
                    "O veterinário já possui um agendamento neste horário. Verifique a agenda.");
            }

            agendamento.setVeterinario(veterinario);
        }

        return toResponseDTO(agendamentoRepository.save(agendamento));
    }

    @Transactional
    public AgendamentoResponseDTO atualizarStatus(Long id, Long clinicaId, String novoStatus) {
        Agendamento agendamento = agendamentoRepository.findByIdAndClinicaId(id, clinicaId)
                .orElseThrow(() -> new IllegalArgumentException("Agendamento não encontrado."));

        List<String> statusValidos = List.of("AGENDADO", "CONFIRMADO", "ATENDIDO", "FALTOU", "CANCELADO");
        if (!statusValidos.contains(novoStatus)) {
            throw new IllegalArgumentException("Status inválido: " + novoStatus);
        }

        agendamento.setStatus(novoStatus);
        return toResponseDTO(agendamentoRepository.save(agendamento));
    }

    @Transactional(readOnly = true)
    public AgendamentoResponseDTO buscarPorId(Long id, Long clinicaId) {
        return toResponseDTO(agendamentoRepository.findByIdAndClinicaId(id, clinicaId)
                .orElseThrow(() -> new IllegalArgumentException("Agendamento não encontrado.")));
    }

    @Transactional(readOnly = true)
    public List<AgendamentoResponseDTO> listarPorClinica(Long clinicaId) {
        return agendamentoRepository.findAllByClinicaIdOrderByDataHoraInicioAsc(clinicaId)
                .stream().map(this::toResponseDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AgendamentoResponseDTO> listarPorAnimal(Long clinicaId, Long animalId) {
        return agendamentoRepository.findAllByClinicaIdAndAnimalIdOrderByDataHoraInicioAsc(clinicaId, animalId)
                .stream().map(this::toResponseDTO).collect(Collectors.toList());
    }

    private AgendamentoResponseDTO toResponseDTO(Agendamento agendamento) {
        AgendamentoResponseDTO dto = new AgendamentoResponseDTO();
        dto.setId(agendamento.getId());
        dto.setClinicaId(agendamento.getClinica().getId());
        dto.setDataHoraInicio(agendamento.getDataHoraInicio());
        dto.setDataHoraFim(agendamento.getDataHoraFim());
        dto.setTipoAtendimento(agendamento.getTipoAtendimento());
        dto.setStatus(agendamento.getStatus());
        dto.setObservacoes(agendamento.getObservacoes());
        dto.setDataAdd(agendamento.getDataAdd());

        AgendamentoResponseDTO.AnimalResumoDTO animalDTO = new AgendamentoResponseDTO.AnimalResumoDTO();
        animalDTO.setId(agendamento.getAnimal().getId());
        animalDTO.setNome(agendamento.getAnimal().getNome());
        animalDTO.setEspecie(agendamento.getAnimal().getEspecie());
        animalDTO.setTutorNome(agendamento.getAnimal().getTutorPrincipal().getNome());
        dto.setAnimal(animalDTO);

        if (agendamento.getVeterinario() != null) {
            AgendamentoResponseDTO.VeterinarioResumoDTO vetDTO = new AgendamentoResponseDTO.VeterinarioResumoDTO();
            vetDTO.setId(agendamento.getVeterinario().getId());
            vetDTO.setNome(agendamento.getVeterinario().getNome());
            vetDTO.setEmail(agendamento.getVeterinario().getEmail());
            dto.setVeterinario(vetDTO);
        }

        return dto;
    }
}

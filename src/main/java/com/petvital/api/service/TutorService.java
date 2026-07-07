package com.petvital.api.service;

import com.petvital.api.domain.model.*;
import com.petvital.api.domain.repository.ClinicaRepository;
import com.petvital.api.domain.repository.TutorRepository;
import com.petvital.api.dto.TutorRequestDTO;
import com.petvital.api.dto.TutorResponseDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TutorService {

    private final TutorRepository tutorRepository;
    private final ClinicaRepository clinicaRepository;

    public TutorService(TutorRepository tutorRepository, ClinicaRepository clinicaRepository) {
        this.tutorRepository = tutorRepository;
        this.clinicaRepository = clinicaRepository;
    }

    @Transactional
    public TutorResponseDTO criar(TutorRequestDTO request) {
        Clinica clinica = clinicaRepository.findById(request.getClinicaId())
                .orElseThrow(() -> new IllegalArgumentException("Clínica não encontrada com o ID: " + request.getClinicaId()));

        // CPF único dentro da mesma clínica (RN003 / Multitenancy)
        if (tutorRepository.existsByClinicaIdAndCpf(request.getClinicaId(), request.getCpf())) {
            throw new IllegalArgumentException("Já existe um tutor com este CPF cadastrado nesta clínica.");
        }

        Tutor tutor = new Tutor();
        tutor.setClinica(clinica);
        tutor.setNome(request.getNome());
        tutor.setCpf(request.getCpf());
        tutor.setEmail(request.getEmail());
        tutor.setDataNascimento(request.getDataNascimento());
        tutor.setAceitaComunicacaoInformativa(
                request.getAceitaComunicacaoInformativa() != null
                ? request.getAceitaComunicacaoInformativa()
                : false
        );
        tutor.setAtivo(true);

        if (request.getTelefones() != null) {
            request.getTelefones().forEach(telDto -> {
                TutorTelefone telefone = new TutorTelefone();
                telefone.setNumero(telDto.getNumero());
                telefone.setTipo(telDto.getTipo());
                telefone.setIsPrincipal(telDto.getIsPrincipal() != null ? telDto.getIsPrincipal() : false);
                tutor.addTelefone(telefone);
            });
        }

        if (request.getEnderecos() != null) {
            request.getEnderecos().forEach(endDto -> {
                TutorEndereco endereco = new TutorEndereco();
                endereco.setCep(endDto.getCep());
                endereco.setLogradouro(endDto.getLogradouro());
                endereco.setNumero(endDto.getNumero());
                endereco.setComplemento(endDto.getComplemento());
                endereco.setBairro(endDto.getBairro());
                endereco.setCidade(endDto.getCidade());
                endereco.setEstado(endDto.getEstado());
                tutor.addEndereco(endereco);
            });
        }

        return toResponseDTO(tutorRepository.save(tutor));
    }

    @Transactional(readOnly = true)
    public TutorResponseDTO buscarPorId(Long id, Long clinicaId) {
        Tutor tutor = tutorRepository.findByIdAndClinicaId(id, clinicaId)
                .orElseThrow(() -> new IllegalArgumentException("Tutor não encontrado."));
        return toResponseDTO(tutor);
    }

    @Transactional(readOnly = true)
    public List<TutorResponseDTO> listarPorClinica(Long clinicaId) {
        return tutorRepository.findAllByClinicaId(clinicaId)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    private TutorResponseDTO toResponseDTO(Tutor tutor) {
        TutorResponseDTO dto = new TutorResponseDTO();
        dto.setId(tutor.getId());
        dto.setNome(tutor.getNome());
        dto.setCpf(tutor.getCpf());
        dto.setEmail(tutor.getEmail());
        dto.setDataNascimento(tutor.getDataNascimento());
        dto.setAceitaComunicacaoInformativa(tutor.getAceitaComunicacaoInformativa());
        dto.setAtivo(tutor.getAtivo());
        dto.setClinicaId(tutor.getClinica().getId());
        dto.setDataAdd(tutor.getDataAdd());

        dto.setTelefones(tutor.getTelefones().stream().map(tel -> {
            TutorResponseDTO.TelefoneResponseDTO t = new TutorResponseDTO.TelefoneResponseDTO();
            t.setId(tel.getId());
            t.setNumero(tel.getNumero());
            t.setTipo(tel.getTipo());
            t.setIsPrincipal(tel.getIsPrincipal());
            return t;
        }).collect(Collectors.toList()));

        dto.setEnderecos(tutor.getEnderecos().stream().map(end -> {
            TutorResponseDTO.EnderecoResponseDTO e = new TutorResponseDTO.EnderecoResponseDTO();
            e.setId(end.getId());
            e.setCep(end.getCep());
            e.setLogradouro(end.getLogradouro());
            e.setNumero(end.getNumero());
            e.setComplemento(end.getComplemento());
            e.setBairro(end.getBairro());
            e.setCidade(end.getCidade());
            e.setEstado(end.getEstado());
            return e;
        }).collect(Collectors.toList()));

        return dto;
    }
}

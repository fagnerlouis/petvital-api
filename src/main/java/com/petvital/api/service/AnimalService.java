package com.petvital.api.service;

import com.petvital.api.domain.model.Animal;
import com.petvital.api.domain.model.Clinica;
import com.petvital.api.domain.model.Tutor;
import com.petvital.api.domain.repository.AnimalRepository;
import com.petvital.api.domain.repository.ClinicaRepository;
import com.petvital.api.domain.repository.TutorRepository;
import com.petvital.api.dto.AnimalRequestDTO;
import com.petvital.api.dto.AnimalResponseDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AnimalService {

    private final AnimalRepository animalRepository;
    private final ClinicaRepository clinicaRepository;
    private final TutorRepository tutorRepository;

    public AnimalService(AnimalRepository animalRepository,
                         ClinicaRepository clinicaRepository,
                         TutorRepository tutorRepository) {
        this.animalRepository = animalRepository;
        this.clinicaRepository = clinicaRepository;
        this.tutorRepository = tutorRepository;
    }

    @Transactional
    public AnimalResponseDTO criar(AnimalRequestDTO request) {
        Clinica clinica = clinicaRepository.findById(request.getClinicaId())
                .orElseThrow(() -> new IllegalArgumentException("Clínica não encontrada com o ID: " + request.getClinicaId()));

        // Validação de microchip único por clínica
        if (request.getMicrochip() != null && !request.getMicrochip().isBlank()) {
            if (animalRepository.existsByClinicaIdAndMicrochip(request.getClinicaId(), request.getMicrochip())) {
                throw new IllegalArgumentException("Já existe um animal com este microchip cadastrado nesta clínica.");
            }
        }

        // Tutor principal deve pertencer à mesma clínica (isolamento de dados)
        Tutor tutorPrincipal = tutorRepository.findByIdAndClinicaId(request.getTutorPrincipalId(), request.getClinicaId())
                .orElseThrow(() -> new IllegalArgumentException("Tutor principal não encontrado nesta clínica."));

        Animal animal = new Animal();
        animal.setClinica(clinica);
        animal.setTutorPrincipal(tutorPrincipal);
        animal.setNome(request.getNome());
        animal.setEspecie(request.getEspecie());
        animal.setRaca(request.getRaca());
        animal.setSexo(request.getSexo());
        animal.setCor(request.getCor());
        animal.setPelagem(request.getPelagem());
        animal.setDataNascimento(request.getDataNascimento());
        animal.setMicrochip(request.getMicrochip());
        animal.setAlergias(request.getAlergias());
        animal.setDoencasCronicas(request.getDoencasCronicas());
        animal.setAtivo(true);

        // Tutores secundários (N:N) — RN005
        if (request.getTutoresSecundariosIds() != null && !request.getTutoresSecundariosIds().isEmpty()) {
            Set<Tutor> tutoresSecundarios = new HashSet<>();
            for (Long tutorId : request.getTutoresSecundariosIds()) {
                Tutor tutorSec = tutorRepository.findByIdAndClinicaId(tutorId, request.getClinicaId())
                        .orElseThrow(() -> new IllegalArgumentException("Tutor secundário ID " + tutorId + " não encontrado nesta clínica."));
                tutoresSecundarios.add(tutorSec);
            }
            animal.setTutoresSecundarios(tutoresSecundarios);
        }

        return toResponseDTO(animalRepository.save(animal));
    }

    @Transactional(readOnly = true)
    public AnimalResponseDTO buscarPorId(Long id, Long clinicaId) {
        Animal animal = animalRepository.findByIdAndClinicaId(id, clinicaId)
                .orElseThrow(() -> new IllegalArgumentException("Animal não encontrado."));
        return toResponseDTO(animal);
    }

    @Transactional(readOnly = true)
    public List<AnimalResponseDTO> listarPorClinica(Long clinicaId) {
        return animalRepository.findAllByClinicaId(clinicaId)
                .stream().map(this::toResponseDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AnimalResponseDTO> listarPorTutor(Long clinicaId, Long tutorId) {
        return animalRepository.findAllByClinicaIdAndTutorPrincipalId(clinicaId, tutorId)
                .stream().map(this::toResponseDTO).collect(Collectors.toList());
    }

    private AnimalResponseDTO toResponseDTO(Animal animal) {
        AnimalResponseDTO dto = new AnimalResponseDTO();
        dto.setId(animal.getId());
        dto.setNome(animal.getNome());
        dto.setEspecie(animal.getEspecie());
        dto.setRaca(animal.getRaca());
        dto.setSexo(animal.getSexo());
        dto.setCor(animal.getCor());
        dto.setPelagem(animal.getPelagem());
        dto.setDataNascimento(animal.getDataNascimento());
        dto.setMicrochip(animal.getMicrochip());
        dto.setAlergias(animal.getAlergias());
        dto.setDoencasCronicas(animal.getDoencasCronicas());
        dto.setFotoUrl(animal.getFotoUrl());
        dto.setAtivo(animal.getAtivo());
        dto.setClinicaId(animal.getClinica().getId());
        dto.setDataAdd(animal.getDataAdd());

        // Tutor principal resumido
        AnimalResponseDTO.TutorResumoDTO tutorDTO = new AnimalResponseDTO.TutorResumoDTO();
        tutorDTO.setId(animal.getTutorPrincipal().getId());
        tutorDTO.setNome(animal.getTutorPrincipal().getNome());
        tutorDTO.setCpf(animal.getTutorPrincipal().getCpf());
        dto.setTutorPrincipal(tutorDTO);

        // Tutores secundários resumidos
        Set<AnimalResponseDTO.TutorResumoDTO> tutoresSecDTO = animal.getTutoresSecundarios().stream().map(t -> {
            AnimalResponseDTO.TutorResumoDTO ts = new AnimalResponseDTO.TutorResumoDTO();
            ts.setId(t.getId());
            ts.setNome(t.getNome());
            ts.setCpf(t.getCpf());
            return ts;
        }).collect(Collectors.toSet());
        dto.setTutoresSecundarios(tutoresSecDTO);

        return dto;
    }
}

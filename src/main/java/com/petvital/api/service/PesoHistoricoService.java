package com.petvital.api.service;

import com.petvital.api.domain.model.Animal;
import com.petvital.api.domain.model.Clinica;
import com.petvital.api.domain.model.PesoHistorico;
import com.petvital.api.domain.model.Usuario;
import com.petvital.api.domain.repository.AnimalRepository;
import com.petvital.api.domain.repository.ClinicaRepository;
import com.petvital.api.domain.repository.PesoHistoricoRepository;
import com.petvital.api.domain.repository.UsuarioRepository;
import com.petvital.api.dto.PesoHistoricoRequestDTO;
import com.petvital.api.dto.PesoHistoricoResponseDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PesoHistoricoService {

    private final PesoHistoricoRepository pesoRepository;
    private final AnimalRepository animalRepository;
    private final ClinicaRepository clinicaRepository;
    private final UsuarioRepository usuarioRepository;

    public PesoHistoricoService(PesoHistoricoRepository pesoRepository,
                                AnimalRepository animalRepository,
                                ClinicaRepository clinicaRepository,
                                UsuarioRepository usuarioRepository) {
        this.pesoRepository = pesoRepository;
        this.animalRepository = animalRepository;
        this.clinicaRepository = clinicaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public PesoHistoricoResponseDTO registrar(PesoHistoricoRequestDTO request, String emailUsuarioLogado) {
        Clinica clinica = clinicaRepository.findById(request.getClinicaId())
                .orElseThrow(() -> new IllegalArgumentException("Clínica não encontrada."));

        Animal animal = animalRepository.findByIdAndClinicaId(request.getAnimalId(), request.getClinicaId())
                .orElseThrow(() -> new IllegalArgumentException("Animal não encontrado nesta clínica."));

        Usuario usuarioLogado = usuarioRepository.findByEmail(emailUsuarioLogado)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));

        PesoHistorico peso = new PesoHistorico();
        peso.setClinica(clinica);
        peso.setAnimal(animal);
        peso.setPesoKg(request.getPesoKg());
        peso.setEcc(request.getEcc());
        peso.setUsuarioRegistro(usuarioLogado);

        return toResponseDTO(pesoRepository.save(peso));
    }

    @Transactional(readOnly = true)
    public List<PesoHistoricoResponseDTO> listarPorAnimal(Long animalId, Long clinicaId) {
        // Valida se o animal pertence à clínica
        animalRepository.findByIdAndClinicaId(animalId, clinicaId)
                .orElseThrow(() -> new IllegalArgumentException("Animal não encontrado nesta clínica."));

        return pesoRepository.findAllByAnimalIdAndClinicaIdOrderByDataAddDesc(animalId, clinicaId)
                .stream().map(this::toResponseDTO).collect(Collectors.toList());
    }

    private PesoHistoricoResponseDTO toResponseDTO(PesoHistorico peso) {
        PesoHistoricoResponseDTO dto = new PesoHistoricoResponseDTO();
        dto.setId(peso.getId());
        dto.setAnimalId(peso.getAnimal().getId());
        dto.setPesoKg(peso.getPesoKg());
        dto.setEcc(peso.getEcc());
        dto.setDataAdd(peso.getDataAdd());

        PesoHistoricoResponseDTO.UsuarioResumoDTO user = new PesoHistoricoResponseDTO.UsuarioResumoDTO();
        user.setId(peso.getUsuarioRegistro().getId());
        user.setNome(peso.getUsuarioRegistro().getNome());
        dto.setUsuarioRegistro(user);

        return dto;
    }
}

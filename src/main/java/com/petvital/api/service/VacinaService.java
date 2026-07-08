package com.petvital.api.service;

import com.petvital.api.domain.model.Animal;
import com.petvital.api.domain.model.Clinica;
import com.petvital.api.domain.model.Usuario;
import com.petvital.api.domain.model.VacinaAplicada;
import com.petvital.api.domain.repository.AnimalRepository;
import com.petvital.api.domain.repository.ClinicaRepository;
import com.petvital.api.domain.repository.UsuarioRepository;
import com.petvital.api.domain.repository.VacinaAplicadaRepository;
import com.petvital.api.dto.VacinaRequestDTO;
import com.petvital.api.dto.VacinaResponseDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class VacinaService {

    private final VacinaAplicadaRepository vacinaRepository;
    private final AnimalRepository animalRepository;
    private final ClinicaRepository clinicaRepository;
    private final UsuarioRepository usuarioRepository;

    public VacinaService(VacinaAplicadaRepository vacinaRepository,
                         AnimalRepository animalRepository,
                         ClinicaRepository clinicaRepository,
                         UsuarioRepository usuarioRepository) {
        this.vacinaRepository = vacinaRepository;
        this.animalRepository = animalRepository;
        this.clinicaRepository = clinicaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public VacinaResponseDTO registrar(VacinaRequestDTO request) {
        Clinica clinica = clinicaRepository.findById(request.getClinicaId())
                .orElseThrow(() -> new IllegalArgumentException("Clínica não encontrada."));

        Animal animal = animalRepository.findByIdAndClinicaId(request.getAnimalId(), request.getClinicaId())
                .orElseThrow(() -> new IllegalArgumentException("Animal não encontrado nesta clínica."));

        Usuario profissional = usuarioRepository.findById(request.getProfissionalId())
                .orElseThrow(() -> new IllegalArgumentException("Profissional não encontrado."));

        VacinaAplicada vacina = new VacinaAplicada();
        vacina.setClinica(clinica);
        vacina.setAnimal(animal);
        vacina.setProfissional(profissional);
        vacina.setNomeVacina(request.getNomeVacina());
        vacina.setDataAplicacao(request.getDataAplicacao());
        vacina.setDataProximoReforco(request.getDataProximoReforco());
        vacina.setLote(request.getLote());
        vacina.setFabricante(request.getFabricante());
        vacina.setValidadeVacina(request.getValidadeVacina());

        return toResponseDTO(vacinaRepository.save(vacina));
    }

    @Transactional(readOnly = true)
    public List<VacinaResponseDTO> listarPorAnimal(Long animalId, Long clinicaId) {
        animalRepository.findByIdAndClinicaId(animalId, clinicaId)
                .orElseThrow(() -> new IllegalArgumentException("Animal não encontrado nesta clínica."));

        return vacinaRepository.findAllByAnimalIdAndClinicaIdOrderByDataAplicacaoDesc(animalId, clinicaId)
                .stream().map(this::toResponseDTO).collect(Collectors.toList());
    }

    private VacinaResponseDTO toResponseDTO(VacinaAplicada vacina) {
        VacinaResponseDTO dto = new VacinaResponseDTO();
        dto.setId(vacina.getId());
        dto.setAnimalId(vacina.getAnimal().getId());
        dto.setNomeVacina(vacina.getNomeVacina());
        dto.setDataAplicacao(vacina.getDataAplicacao());
        dto.setDataProximoReforco(vacina.getDataProximoReforco());
        dto.setLote(vacina.getLote());
        dto.setFabricante(vacina.getFabricante());
        dto.setValidadeVacina(vacina.getValidadeVacina());
        dto.setDataAdd(vacina.getDataAdd());

        VacinaResponseDTO.ProfissionalResumoDTO prof = new VacinaResponseDTO.ProfissionalResumoDTO();
        prof.setId(vacina.getProfissional().getId());
        prof.setNome(vacina.getProfissional().getNome());
        dto.setProfissional(prof);

        return dto;
    }
}

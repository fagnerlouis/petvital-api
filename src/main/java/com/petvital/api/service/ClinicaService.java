package com.petvital.api.service;

import com.petvital.api.domain.model.Clinica;
import com.petvital.api.domain.model.ClinicaEndereco;
import com.petvital.api.domain.model.ClinicaTelefone;
import com.petvital.api.domain.repository.ClinicaRepository;
import com.petvital.api.dto.ClinicaRequestDTO;
import com.petvital.api.dto.ClinicaResponseDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.stream.Collectors;

@Service
public class ClinicaService {

    private final ClinicaRepository clinicaRepository;

    public ClinicaService(ClinicaRepository clinicaRepository) {
        this.clinicaRepository = clinicaRepository;
    }

    @Transactional
    public ClinicaResponseDTO criar(ClinicaRequestDTO request) {
        if (clinicaRepository.existsByDocumentoFiscal(request.getDocumentoFiscal())) {
            throw new IllegalArgumentException("Já existe uma clínica cadastrada com este documento fiscal.");
        }

        Clinica clinica = new Clinica();
        clinica.setNome(request.getNome());
        clinica.setNomeFantasia(request.getNomeFantasia());
        clinica.setTipoTenant(request.getTipoTenant());
        clinica.setDocumentoFiscal(request.getDocumentoFiscal());
        clinica.setEmail(request.getEmail());
        clinica.setAtivo(true);

        if (request.getTelefones() != null) {
            request.getTelefones().forEach(telDto -> {
                ClinicaTelefone telefone = new ClinicaTelefone();
                telefone.setNumero(telDto.getNumero());
                telefone.setTipo(telDto.getTipo());
                telefone.setIsPrincipal(telDto.getIsPrincipal() != null ? telDto.getIsPrincipal() : false);
                clinica.addTelefone(telefone);
            });
        }

        if (request.getEnderecos() != null) {
            request.getEnderecos().forEach(endDto -> {
                ClinicaEndereco endereco = new ClinicaEndereco();
                endereco.setCep(endDto.getCep());
                endereco.setLogradouro(endDto.getLogradouro());
                endereco.setNumero(endDto.getNumero());
                endereco.setComplemento(endDto.getComplemento());
                endereco.setBairro(endDto.getBairro());
                endereco.setCidade(endDto.getCidade());
                endereco.setEstado(endDto.getEstado());
                clinica.addEndereco(endereco);
            });
        }

        Clinica salva = clinicaRepository.save(clinica);
        return toResponseDTO(salva);
    }

    public ClinicaResponseDTO buscarPorId(Long id) {
        Clinica clinica = clinicaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Clínica não encontrada."));
        return toResponseDTO(clinica);
    }

    private ClinicaResponseDTO toResponseDTO(Clinica clinica) {
        ClinicaResponseDTO dto = new ClinicaResponseDTO();
        dto.setId(clinica.getId());
        dto.setNome(clinica.getNome());
        dto.setNomeFantasia(clinica.getNomeFantasia());
        dto.setTipoTenant(clinica.getTipoTenant());
        dto.setDocumentoFiscal(clinica.getDocumentoFiscal());
        dto.setEmail(clinica.getEmail());
        dto.setAtivo(clinica.getAtivo());
        dto.setDataAdd(clinica.getDataAdd());
        
        dto.setTelefones(clinica.getTelefones().stream().map(tel -> {
            ClinicaResponseDTO.TelefoneResponseDTO tDto = new ClinicaResponseDTO.TelefoneResponseDTO();
            tDto.setId(tel.getId());
            tDto.setNumero(tel.getNumero());
            tDto.setTipo(tel.getTipo());
            tDto.setIsPrincipal(tel.getIsPrincipal());
            return tDto;
        }).collect(Collectors.toList()));
        
        dto.setEnderecos(clinica.getEnderecos().stream().map(end -> {
            ClinicaResponseDTO.EnderecoResponseDTO eDto = new ClinicaResponseDTO.EnderecoResponseDTO();
            eDto.setId(end.getId());
            eDto.setCep(end.getCep());
            eDto.setLogradouro(end.getLogradouro());
            eDto.setNumero(end.getNumero());
            eDto.setComplemento(end.getComplemento());
            eDto.setBairro(end.getBairro());
            eDto.setCidade(end.getCidade());
            eDto.setEstado(end.getEstado());
            return eDto;
        }).collect(Collectors.toList()));

        return dto;
    }
}

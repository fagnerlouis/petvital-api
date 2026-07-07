package com.petvital.api.service;

import com.petvital.api.domain.model.Clinica;
import com.petvital.api.domain.model.Usuario;
import com.petvital.api.domain.repository.ClinicaRepository;
import com.petvital.api.domain.repository.UsuarioRepository;
import com.petvital.api.dto.UsuarioRequestDTO;
import com.petvital.api.dto.UsuarioResponseDTO;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final ClinicaRepository clinicaRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository,
                          ClinicaRepository clinicaRepository,
                          PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.clinicaRepository = clinicaRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UsuarioResponseDTO criar(UsuarioRequestDTO request) {
        // Regra: email deve ser único no sistema
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Já existe um usuário cadastrado com este email.");
        }

        boolean isMaster = Boolean.TRUE.equals(request.getMaster());

        // Regra: somente um usuário master logado pode criar outro usuário master
        if (isMaster) {
            Usuario usuarioLogado = getUsuarioLogado();
            if (!Boolean.TRUE.equals(usuarioLogado.getMaster())) {
                throw new IllegalArgumentException("Apenas usuários master podem criar outros usuários master.");
            }
        }

        Usuario usuario = new Usuario();
        usuario.setNome(request.getNome());
        usuario.setEmail(request.getEmail());
        usuario.setSenhaHash(passwordEncoder.encode(request.getSenha()));
        usuario.setPerfil(request.getPerfil());
        usuario.setMaster(isMaster);
        usuario.setAtivo(true);

        // Regra: se não for master, a clínica é obrigatória
        if (!isMaster) {
            if (request.getClinicaId() == null) {
                throw new IllegalArgumentException("O campo clinicaId é obrigatório para usuários não-master.");
            }
            Clinica clinica = clinicaRepository.findById(request.getClinicaId())
                    .orElseThrow(() -> new IllegalArgumentException("Clínica não encontrada com o ID: " + request.getClinicaId()));
            usuario.setClinica(clinica);
        }

        Usuario salvo = usuarioRepository.save(usuario);
        return toResponseDTO(salvo);
    }

    private Usuario getUsuarioLogado() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new IllegalArgumentException("Usuário não autenticado.");
        }
        return (Usuario) auth.getPrincipal();
    }

    private UsuarioResponseDTO toResponseDTO(Usuario usuario) {
        UsuarioResponseDTO dto = new UsuarioResponseDTO();
        dto.setId(usuario.getId());
        dto.setNome(usuario.getNome());
        dto.setEmail(usuario.getEmail());
        dto.setPerfil(usuario.getPerfil());
        dto.setMaster(usuario.getMaster());
        dto.setAtivo(usuario.getAtivo());
        dto.setDataAdd(usuario.getDataAdd());

        if (usuario.getClinica() != null) {
            dto.setClinicaId(usuario.getClinica().getId());
            dto.setClinicaNome(usuario.getClinica().getNome());
        }

        return dto;
    }
}

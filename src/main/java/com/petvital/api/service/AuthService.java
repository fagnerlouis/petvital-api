package com.petvital.api.service;

import com.petvital.api.domain.model.Usuario;
import com.petvital.api.domain.repository.UsuarioRepository;
import com.petvital.api.dto.AuthRequestDTO;
import com.petvital.api.dto.AuthResponseDTO;
import com.petvital.api.security.JwtService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UsuarioRepository usuarioRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public AuthResponseDTO login(AuthRequestDTO request) {
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Email ou senha inválidos."));

        if (!usuario.getAtivo()) {
            throw new BadCredentialsException("Usuário inativo. Contate o administrador.");
        }

        if (!passwordEncoder.matches(request.getSenha(), usuario.getSenhaHash())) {
            throw new BadCredentialsException("Email ou senha inválidos.");
        }

        String token = jwtService.gerarToken(usuario);

        return new AuthResponseDTO(token, usuario.getNome(), usuario.getPerfil(), usuario.getMaster());
    }
}

package com.petvital.api.config;

import com.petvital.api.domain.model.Usuario;
import com.petvital.api.domain.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Inicializa dados essenciais no banco de dados ao subir a aplicação.
 * Cria o usuário Master (Super Admin) se ele ainda não existir.
 *
 * IMPORTANTE: Altere a senha padrão imediatamente após o primeiro acesso!
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (!usuarioRepository.existsByEmail("admin@petvital.com")) {
            Usuario admin = new Usuario();
            admin.setNome("Admin PetVital");
            admin.setEmail("admin@petvital.com");
            admin.setSenhaHash(passwordEncoder.encode("petvital@2024")); // Senha inicial — TROQUE APÓS O PRIMEIRO LOGIN
            admin.setPerfil("ADMIN");
            admin.setMaster(true);
            admin.setAtivo(true);

            usuarioRepository.save(admin);
            System.out.println(">>> Usuário master criado: admin@petvital.com / petvital@2024");
        }
    }
}

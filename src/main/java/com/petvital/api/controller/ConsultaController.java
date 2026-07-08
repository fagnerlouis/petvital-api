package com.petvital.api.controller;

import com.petvital.api.dto.ConsultaHistoricoResponseDTO;
import com.petvital.api.dto.ConsultaRequestDTO;
import com.petvital.api.dto.ConsultaResponseDTO;
import com.petvital.api.service.ConsultaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/consultas")
public class ConsultaController {

    private final ConsultaService consultaService;

    public ConsultaController(ConsultaService consultaService) {
        this.consultaService = consultaService;
    }

    @PostMapping
    public ResponseEntity<ConsultaResponseDTO> criar(@Valid @RequestBody ConsultaRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(consultaService.criar(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ConsultaResponseDTO> atualizar(
            @PathVariable Long id,
            @RequestParam Long clinicaId,
            @Valid @RequestBody ConsultaRequestDTO request,
            Authentication authentication) {
        
        // Passa o email do usuário logado extraído do JWT para o service registrar quem alterou (RN006)
        String emailUsuarioLogado = authentication.getName();
        return ResponseEntity.ok(consultaService.atualizar(id, clinicaId, request, emailUsuarioLogado));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConsultaResponseDTO> buscarPorId(
            @PathVariable Long id,
            @RequestParam Long clinicaId) {
        return ResponseEntity.ok(consultaService.buscarPorId(id, clinicaId));
    }

    @GetMapping
    public ResponseEntity<List<ConsultaResponseDTO>> listar(@RequestParam Long clinicaId) {
        return ResponseEntity.ok(consultaService.listarPorClinica(clinicaId));
    }

    @GetMapping("/{id}/historico")
    public ResponseEntity<List<ConsultaHistoricoResponseDTO>> listarHistorico(
            @PathVariable Long id,
            @RequestParam Long clinicaId) {
        return ResponseEntity.ok(consultaService.listarHistorico(id, clinicaId));
    }
}

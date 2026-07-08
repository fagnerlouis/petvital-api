package com.petvital.api.controller;

import com.petvital.api.dto.AgendamentoRequestDTO;
import com.petvital.api.dto.AgendamentoResponseDTO;
import com.petvital.api.service.AgendamentoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/agendamentos")
public class AgendamentoController {

    private final AgendamentoService agendamentoService;

    public AgendamentoController(AgendamentoService agendamentoService) {
        this.agendamentoService = agendamentoService;
    }

    @PostMapping
    public ResponseEntity<AgendamentoResponseDTO> criar(@Valid @RequestBody AgendamentoRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(agendamentoService.criar(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AgendamentoResponseDTO> buscarPorId(
            @PathVariable Long id,
            @RequestParam Long clinicaId) {
        return ResponseEntity.ok(agendamentoService.buscarPorId(id, clinicaId));
    }

    @GetMapping
    public ResponseEntity<List<AgendamentoResponseDTO>> listar(
            @RequestParam Long clinicaId,
            @RequestParam(required = false) Long animalId) {
        if (animalId != null) {
            return ResponseEntity.ok(agendamentoService.listarPorAnimal(clinicaId, animalId));
        }
        return ResponseEntity.ok(agendamentoService.listarPorClinica(clinicaId));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<AgendamentoResponseDTO> atualizarStatus(
            @PathVariable Long id,
            @RequestParam Long clinicaId,
            @RequestBody Map<String, String> body) {
        String novoStatus = body.get("status");
        return ResponseEntity.ok(agendamentoService.atualizarStatus(id, clinicaId, novoStatus));
    }
}

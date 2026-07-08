package com.petvital.api.controller;

import com.petvital.api.dto.PesoHistoricoRequestDTO;
import com.petvital.api.dto.PesoHistoricoResponseDTO;
import com.petvital.api.service.PesoHistoricoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pesos")
public class PesoHistoricoController {

    private final PesoHistoricoService pesoService;

    public PesoHistoricoController(PesoHistoricoService pesoService) {
        this.pesoService = pesoService;
    }

    @PostMapping
    public ResponseEntity<PesoHistoricoResponseDTO> registrar(
            @Valid @RequestBody PesoHistoricoRequestDTO request,
            Authentication authentication) {
        String emailUsuario = authentication.getName();
        return ResponseEntity.status(HttpStatus.CREATED).body(pesoService.registrar(request, emailUsuario));
    }

    @GetMapping("/animal/{animalId}")
    public ResponseEntity<List<PesoHistoricoResponseDTO>> listarPorAnimal(
            @PathVariable Long animalId,
            @RequestParam Long clinicaId) {
        return ResponseEntity.ok(pesoService.listarPorAnimal(animalId, clinicaId));
    }
}

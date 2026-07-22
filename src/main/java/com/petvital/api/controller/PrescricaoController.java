package com.petvital.api.controller;

import com.petvital.api.dto.PrescricaoRequestDTO;
import com.petvital.api.dto.PrescricaoResponseDTO;
import com.petvital.api.service.PrescricaoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/prescricoes")
public class PrescricaoController {

    private final PrescricaoService prescricaoService;

    public PrescricaoController(PrescricaoService prescricaoService) {
        this.prescricaoService = prescricaoService;
    }

    @PostMapping
    public ResponseEntity<PrescricaoResponseDTO> criar(
            @Valid @RequestBody PrescricaoRequestDTO request,
            Authentication authentication) {
        
        String emailVet = authentication.getName();
        return ResponseEntity.status(HttpStatus.CREATED).body(prescricaoService.criar(request, emailVet));
    }

    @GetMapping("/consulta/{consultaId}")
    public ResponseEntity<List<PrescricaoResponseDTO>> listarPorConsulta(
            @PathVariable Long consultaId,
            @RequestParam Long clinicaId) {
        return ResponseEntity.ok(prescricaoService.listarPorConsulta(consultaId, clinicaId));
    }
}

package com.petvital.api.controller;

import com.petvital.api.dto.ClinicaRequestDTO;
import com.petvital.api.dto.ClinicaResponseDTO;
import com.petvital.api.service.ClinicaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/clinicas")
public class ClinicaController {

    private final ClinicaService clinicaService;

    public ClinicaController(ClinicaService clinicaService) {
        this.clinicaService = clinicaService;
    }

    @PostMapping
    public ResponseEntity<ClinicaResponseDTO> criar(@Valid @RequestBody ClinicaRequestDTO request) {
        ClinicaResponseDTO response = clinicaService.criar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClinicaResponseDTO> buscarPorId(@PathVariable Long id) {
        ClinicaResponseDTO response = clinicaService.buscarPorId(id);
        return ResponseEntity.ok(response);
    }
}

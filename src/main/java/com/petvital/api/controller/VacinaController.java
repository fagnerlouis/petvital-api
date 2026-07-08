package com.petvital.api.controller;

import com.petvital.api.dto.VacinaRequestDTO;
import com.petvital.api.dto.VacinaResponseDTO;
import com.petvital.api.service.VacinaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vacinas")
public class VacinaController {

    private final VacinaService vacinaService;

    public VacinaController(VacinaService vacinaService) {
        this.vacinaService = vacinaService;
    }

    @PostMapping
    public ResponseEntity<VacinaResponseDTO> registrar(@Valid @RequestBody VacinaRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(vacinaService.registrar(request));
    }

    @GetMapping("/animal/{animalId}")
    public ResponseEntity<List<VacinaResponseDTO>> listarPorAnimal(
            @PathVariable Long animalId,
            @RequestParam Long clinicaId) {
        return ResponseEntity.ok(vacinaService.listarPorAnimal(animalId, clinicaId));
    }
}

package com.petvital.api.controller;

import com.petvital.api.dto.AnimalRequestDTO;
import com.petvital.api.dto.AnimalResponseDTO;
import com.petvital.api.service.AnimalService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/animais")
public class AnimalController {

    private final AnimalService animalService;

    public AnimalController(AnimalService animalService) {
        this.animalService = animalService;
    }

    @PostMapping
    public ResponseEntity<AnimalResponseDTO> criar(@Valid @RequestBody AnimalRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(animalService.criar(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AnimalResponseDTO> buscarPorId(
            @PathVariable Long id,
            @RequestParam Long clinicaId) {
        return ResponseEntity.ok(animalService.buscarPorId(id, clinicaId));
    }

    @GetMapping
    public ResponseEntity<List<AnimalResponseDTO>> listar(
            @RequestParam Long clinicaId,
            @RequestParam(required = false) Long tutorId) {
        if (tutorId != null) {
            return ResponseEntity.ok(animalService.listarPorTutor(clinicaId, tutorId));
        }
        return ResponseEntity.ok(animalService.listarPorClinica(clinicaId));
    }
}

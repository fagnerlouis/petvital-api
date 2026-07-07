package com.petvital.api.controller;

import com.petvital.api.dto.TutorRequestDTO;
import com.petvital.api.dto.TutorResponseDTO;
import com.petvital.api.service.TutorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tutores")
public class TutorController {

    private final TutorService tutorService;

    public TutorController(TutorService tutorService) {
        this.tutorService = tutorService;
    }

    @PostMapping
    public ResponseEntity<TutorResponseDTO> criar(@Valid @RequestBody TutorRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tutorService.criar(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TutorResponseDTO> buscarPorId(
            @PathVariable Long id,
            @RequestParam Long clinicaId) {
        return ResponseEntity.ok(tutorService.buscarPorId(id, clinicaId));
    }

    @GetMapping
    public ResponseEntity<List<TutorResponseDTO>> listar(@RequestParam Long clinicaId) {
        return ResponseEntity.ok(tutorService.listarPorClinica(clinicaId));
    }
}

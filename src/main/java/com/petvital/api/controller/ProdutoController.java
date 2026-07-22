package com.petvital.api.controller;

import com.petvital.api.dto.EstoqueMovimentoRequestDTO;
import com.petvital.api.dto.EstoqueMovimentoResponseDTO;
import com.petvital.api.dto.ProdutoRequestDTO;
import com.petvital.api.dto.ProdutoResponseDTO;
import com.petvital.api.service.ProdutoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/produtos")
public class ProdutoController {

    private final ProdutoService produtoService;

    public ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    @PostMapping
    public ResponseEntity<ProdutoResponseDTO> criar(@Valid @RequestBody ProdutoRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(produtoService.criarProduto(request));
    }

    @GetMapping
    public ResponseEntity<List<ProdutoResponseDTO>> listar(@RequestParam Long clinicaId) {
        return ResponseEntity.ok(produtoService.listarProdutosAtivos(clinicaId));
    }

    @PostMapping("/movimentos")
    public ResponseEntity<EstoqueMovimentoResponseDTO> registrarMovimento(
            @Valid @RequestBody EstoqueMovimentoRequestDTO request,
            Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.status(HttpStatus.CREATED).body(produtoService.registrarMovimento(request, email));
    }

    @GetMapping("/{produtoId}/movimentos")
    public ResponseEntity<List<EstoqueMovimentoResponseDTO>> listarMovimentos(
            @PathVariable Long produtoId,
            @RequestParam Long clinicaId) {
        return ResponseEntity.ok(produtoService.listarMovimentos(produtoId, clinicaId));
    }
}

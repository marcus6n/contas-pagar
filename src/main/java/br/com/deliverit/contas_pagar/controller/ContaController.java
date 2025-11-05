package br.com.deliverit.contas_pagar.controller;

import br.com.deliverit.contas_pagar.dto.ContaRequestDTO;
import br.com.deliverit.contas_pagar.dto.ContaResponseDTO;
import br.com.deliverit.contas_pagar.service.ContaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contas")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ContaController {

    private final ContaService service;

    @PostMapping
    public ResponseEntity<ContaResponseDTO> criar(@Valid @RequestBody ContaRequestDTO dto) {
        ContaResponseDTO response = service.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<ContaResponseDTO>> listar() {
        List<ContaResponseDTO> contas = service.listar();
        return ResponseEntity.ok(contas);
    }
}
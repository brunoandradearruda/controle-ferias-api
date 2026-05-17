package br.gov.pb.seplag.controleferias.controller;

import br.gov.pb.seplag.controleferias.domain.PeriodoAquisitivo;
import br.gov.pb.seplag.controleferias.service.PeriodoAquisitivoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/periodos")
@CrossOrigin(origins = "http://localhost:5173") // Libera o React
@RequiredArgsConstructor
public class PeriodoAquisitivoController {

    private final PeriodoAquisitivoService periodoService;

    // Rota que vai devolver a lista para o nosso Dropdown no React
    @GetMapping
    public ResponseEntity<List<PeriodoAquisitivo>> listarTodos() {
        return ResponseEntity.ok(periodoService.listarTodos());
    }
}
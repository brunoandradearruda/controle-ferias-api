package br.gov.pb.seplag.controleferias.controller;

import br.gov.pb.seplag.controleferias.domain.PeriodoAquisitivo;
import br.gov.pb.seplag.controleferias.dto.PeriodoAquisitivoResponse;
import br.gov.pb.seplag.controleferias.service.PeriodoAquisitivoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/periodos")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class PeriodoAquisitivoController {

    private final PeriodoAquisitivoService periodoService;

    // Rota que vai devolver a lista para o Front-end
    @GetMapping
    public ResponseEntity<List<PeriodoAquisitivoResponse>> listarTodos() {

        // 1. Busca as entidades originais do banco
        List<PeriodoAquisitivo> periodos = periodoService.listarTodos();

        // 2. Converte as Entidades para o DTO (Response).
        // É aqui que a mágica acontece e a lista de férias é empacotada com segurança!
        List<PeriodoAquisitivoResponse> response = periodos.stream()
                .map(PeriodoAquisitivoResponse::new)
                .toList();

        return ResponseEntity.ok(response);
    }
}
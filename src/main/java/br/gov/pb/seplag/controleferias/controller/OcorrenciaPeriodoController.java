package br.gov.pb.seplag.controleferias.controller;

import br.gov.pb.seplag.controleferias.dto.OcorrenciaPeriodoRequest;
import br.gov.pb.seplag.controleferias.service.PeriodoAquisitivoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/periodos/{periodoId}")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class OcorrenciaPeriodoController {

    private final PeriodoAquisitivoService periodoService;

    @PostMapping("/suspender")
    public ResponseEntity<?> suspenderCiclo(
            @PathVariable Long periodoId,
            @RequestBody OcorrenciaPeriodoRequest request) {
        try {
            periodoService.suspenderCiclo(
                    periodoId,
                    request.dataEvento(),
                    request.numeroPbdoc(),
                    request.justificativa()
            );
            return ResponseEntity.ok("✅ Ciclo de aquisição suspenso com sucesso!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/retomar")
    public ResponseEntity<?> retomarCiclo(
            @PathVariable Long periodoId,
            @RequestBody OcorrenciaPeriodoRequest request) {
        try {
            periodoService.retomarCiclo(
                    periodoId,
                    request.dataEvento(),
                    request.numeroPbdoc(),
                    request.justificativa()
            );
            return ResponseEntity.ok("✅ Ciclo de aquisição retomado. Nova data calculada!");
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
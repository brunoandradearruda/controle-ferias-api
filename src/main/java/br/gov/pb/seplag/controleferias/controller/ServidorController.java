package br.gov.pb.seplag.controleferias.controller;

import br.gov.pb.seplag.controleferias.domain.Servidor;
import br.gov.pb.seplag.controleferias.domain.PeriodoAquisitivo; // <-- IMPORT ADICIONADO
import br.gov.pb.seplag.controleferias.service.ServidorService;
import br.gov.pb.seplag.controleferias.repository.PeriodoAquisitivoRepository; // <-- IMPORT ADICIONADO
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/servidores")
@CrossOrigin(origins = "http://localhost:5173") // Libera o acesso para o nosso React!
@RequiredArgsConstructor
public class ServidorController {

    private final ServidorService servidorService;
    private final PeriodoAquisitivoRepository periodoAquisitivoRepository; // <-- NOVO: Injetando o repositório

    // DTO rápido criado aqui mesmo para receber o texto do motivo do Front-end
    public record MotivoRequest(String motivo) {}

    // Rota para CRIAR um novo servidor
    @PostMapping
    public ResponseEntity<Servidor> cadastrar(@RequestBody Servidor servidor) {
        Servidor novoServidor = servidorService.cadastrar(servidor);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoServidor);
    }

    // Rota para LISTAR todos os servidores
    @GetMapping
    public ResponseEntity<List<Servidor>> listar() {
        return ResponseEntity.ok(servidorService.listarTodos());
    }

    // ---> ROTA ATUALIZADA: Agora recebe o MotivoRequest <---
    @PutMapping("/{id}/inativar")
    public ResponseEntity<Void> inativar(@PathVariable Long id, @RequestBody MotivoRequest request) {
        // Passa o ID e o motivo para o Service
        servidorService.inativarServidor(id, request.motivo());
        return ResponseEntity.ok().build();
    }

    // Rota para REATIVAR
    @PutMapping("/{id}/reativar")
    public ResponseEntity<Void> reativar(@PathVariable Long id) {
        servidorService.reativarServidor(id);
        return ResponseEntity.ok().build();
    }

    // DTO para receber o ano do Front-end
    public record PeriodoAcumuladoRequest(int anoReferencia) {}

    // ---> NOVA ROTA: Adicionar Férias Acumuladas <---
    @PostMapping("/{id}/periodos-acumulados")
    public ResponseEntity<Void> adicionarFériasAtrasadas(
            @PathVariable Long id,
            @RequestBody PeriodoAcumuladoRequest request) {

        servidorService.adicionarPeriodoAcumulado(id, request.anoReferencia());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // ==============================================================================
    // ---> NOVA ROTA: Buscar períodos já registrados do servidor (Resolve o 404) <--
    // ==============================================================================
    @GetMapping("/{id}/periodos")
    public ResponseEntity<List<PeriodoAquisitivo>> listarPeriodosDoServidor(@PathVariable Long id) {
        // Busca direto no banco todos os períodos atrelados a este servidor
        List<PeriodoAquisitivo> periodosJaRegistrados = periodoAquisitivoRepository.findByServidorId(id);
        return ResponseEntity.ok(periodosJaRegistrados);
    }

    // ENDPOINT TEMPORÁRIO PARA TESTES E SIMULAÇÕES (Pode apagar depois que for para produção)
    @PostMapping("/{id}/mock-historico")
    public ResponseEntity<String> gerarHistoricoMock(
            @PathVariable Long id,
            @RequestParam int anoInicio,
            @RequestParam int anoFim) {

        // Aqui chamamos o serviço para criar a massa de dados
        servidorService.gerarHistoricoGozadoSimulado(id, anoInicio, anoFim);

        return ResponseEntity.ok("✅ Histórico de " + anoInicio + " até " + anoFim + " gerado com sucesso!");
    }

}
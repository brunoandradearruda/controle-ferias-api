package br.gov.pb.seplag.controleferias.controller;

import br.gov.pb.seplag.controleferias.domain.Servidor;
import br.gov.pb.seplag.controleferias.service.ServidorService;
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

}
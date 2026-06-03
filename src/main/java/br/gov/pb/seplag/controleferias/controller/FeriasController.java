package br.gov.pb.seplag.controleferias.controller;

import br.gov.pb.seplag.controleferias.domain.SolicitacaoFerias;
import br.gov.pb.seplag.controleferias.dto.SolicitacaoFeriasRequest;
import br.gov.pb.seplag.controleferias.dto.SolicitacaoFeriasResponse;
import br.gov.pb.seplag.controleferias.service.FeriasService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1") // Ajustado para aceitar tanto /periodos quanto /solicitacoes de forma limpa
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class FeriasController {

    private final FeriasService feriasService;

    // Rota: POST http://localhost:8080/api/v1/periodos/{periodoId}/solicitacoes
    @PostMapping("/periodos/{periodoId}/solicitacoes")
    public ResponseEntity<?> solicitarFerias(
            @PathVariable Long periodoId,
            @RequestBody SolicitacaoFeriasRequest request) {

        try {
            // 1. Converte o DTO que veio da internet para a Entidade do banco
            SolicitacaoFerias novaSolicitacao = new SolicitacaoFerias();

            // ---> A LINHA QUE FALTAVA PARA SALVAR O SISTEMA <---
            novaSolicitacao.setModalidade(request.modalidade());

            novaSolicitacao.setDataInicioGozo(request.dataInicioGozo());
            novaSolicitacao.setDiasSolicitados(request.diasSolicitados());
            // Se o abono não for enviado, assume false por padrão
            novaSolicitacao.setAbonoPecuniario(request.abonoPecuniario() != null ? request.abonoPecuniario() : false);
            novaSolicitacao.setNumeroPbdoc(request.numeroPbdoc());

            // ---> MUDANÇA: Captura de forma segura a flag para o Modo Histórico Retroativo <---
            boolean modoRetroativo = Boolean.TRUE.equals(request.isRetroativo());

            // 2. Chama o serviço passando a nova flag para processar a regra de negócio
            SolicitacaoFerias solicitacaoSalva = feriasService.solicitarFracionamento(periodoId, novaSolicitacao, modoRetroativo);

            // 3. Converte a entidade salva de volta para DTO e retorna Status 201 (Created)
            SolicitacaoFeriasResponse response = new SolicitacaoFeriasResponse(solicitacaoSalva);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (IllegalArgumentException e) {
            // Se cair na nossa regra de "Saldo Insuficiente", retorna um erro 400 amigável
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // Rota: GET http://localhost:8080/api/v1/periodos/{periodoId}/solicitacoes
    @GetMapping("/periodos/{periodoId}/solicitacoes")
    public ResponseEntity<List<SolicitacaoFeriasResponse>> listarSolicitacoes(@PathVariable Long periodoId) {

        // 1. Busca as solicitações no banco usando o repositório através do service
        List<SolicitacaoFerias> solicitacoes = feriasService.listarPorPeriodo(periodoId);

        // 2. Converte a lista de Entidades para uma lista de DTOs usando Stream
        List<SolicitacaoFeriasResponse> responseList = solicitacoes.stream()
                .map(SolicitacaoFeriasResponse::new)
                .toList();

        // 3. Retorna Status 200 (OK) com a lista
        return ResponseEntity.ok(responseList);
    }

    // NOVO: Rota para APROVAR a solicitação de férias
    // Rota: PUT http://localhost:8080/api/v1/solicitacoes/{id}/aprovar
    @PutMapping("/solicitacoes/{id}/aprovar")
    public ResponseEntity<Void> aprovar(@PathVariable Long id) {
        feriasService.aprovar(id);
        return ResponseEntity.ok().build();
    }

    // NOVO: Rota para REJEITAR a solicitação de férias (devolvendo o saldo ao servidor)
    // Rota: PUT http://localhost:8080/api/v1/solicitacoes/{id}/rejeitar
    @PutMapping("/solicitacoes/{id}/rejeitar")
    public ResponseEntity<Void> reativar(@PathVariable Long id) {
        feriasService.rejeitar(id);
        return ResponseEntity.ok().build();
    }

    // NOVO: Rota para LISTAR TODAS as solicitações de todos os servidores (Painel Geral do RH)
    // Rota: GET http://localhost:8080/api/v1/solicitacoes
    @GetMapping("/solicitacoes")
    public ResponseEntity<List<SolicitacaoFeriasResponse>> listarTodasGerais() {
        // 1. Busca todas as solicitações existentes no banco
        List<SolicitacaoFerias> todas = feriasService.listarTodasGerais();

        // 2. Converte para DTO
        List<SolicitacaoFeriasResponse> responseList = todas.stream()
                .map(SolicitacaoFeriasResponse::new)
                .toList();

        return ResponseEntity.ok(responseList);
    }

    // NOVO: Rota para INTERROMPER a solicitação de férias (Art. 81)
    // Rota: PUT http://localhost:8080/api/v1/solicitacoes/{id}/interromper
    @PutMapping("/solicitacoes/{id}/interromper")
    public ResponseEntity<?> interromper(@PathVariable Long id) {
        try {
            feriasService.interromper(id);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            // Retorna o erro 400 se tentar interromper férias que já acabaram
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
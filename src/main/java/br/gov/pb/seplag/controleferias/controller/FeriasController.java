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
@RequestMapping("/api/v1/periodos")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class FeriasController {

    private final FeriasService feriasService;

    // Rota: POST http://localhost:8080/api/v1/periodos/{periodoId}/solicitacoes
    @PostMapping("/{periodoId}/solicitacoes")
    public ResponseEntity<?> solicitarFerias(
            @PathVariable Long periodoId,
            @RequestBody SolicitacaoFeriasRequest request) {

        try {
            // 1. Converte o DTO que veio da internet para a Entidade do banco
            SolicitacaoFerias novaSolicitacao = new SolicitacaoFerias();
            novaSolicitacao.setDataInicioGozo(request.dataInicioGozo());
            novaSolicitacao.setDiasSolicitados(request.diasSolicitados());
            // Se o abono não for enviado, assume false por padrão
            novaSolicitacao.setAbonoPecuniario(request.abonoPecuniario() != null ? request.abonoPecuniario() : false);

            // 2. Chama o serviço para processar a regra de negócio (matemática do saldo)
            SolicitacaoFerias solicitacaoSalva = feriasService.solicitarFracionamento(periodoId, novaSolicitacao);

            // 3. Converte a entidade salva de volta para DTO e retorna Status 201 (Created)
            SolicitacaoFeriasResponse response = new SolicitacaoFeriasResponse(solicitacaoSalva);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (IllegalArgumentException e) {
            // Se cair na nossa regra de "Saldo Insuficiente", retorna um erro 400 amigável
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // Rota: GET http://localhost:8080/api/v1/periodos/{periodoId}/solicitacoes
    @GetMapping("/{periodoId}/solicitacoes")
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
}
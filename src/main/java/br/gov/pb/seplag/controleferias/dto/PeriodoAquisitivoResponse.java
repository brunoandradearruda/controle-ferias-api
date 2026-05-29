package br.gov.pb.seplag.controleferias.dto;

import br.gov.pb.seplag.controleferias.domain.PeriodoAquisitivo;

import java.time.LocalDate;
import java.util.List;

public record PeriodoAquisitivoResponse(
        Long id,
        Integer anoReferencia,
        LocalDate dataInicio,
        LocalDate dataFim,
        Integer saldoDias,
        ServidorResumo servidor, // <-- ADICIONADO PARA O FRONT-END VOLTAR A FUNCIONAR
        List<OcorrenciaPeriodoResponse> ocorrencias,
        List<SolicitacaoFeriasResponse> solicitacoes
) {
    // Sub-record para enviar os dados essenciais do servidor para o Front-end ler
    public record ServidorResumo(Long id, String nome, String matricula, Boolean operadorRaioX) {}

    public PeriodoAquisitivoResponse(PeriodoAquisitivo periodo) {
        this(
                periodo.getId(),
                periodo.getAnoReferencia(),
                periodo.getDataInicio(),
                periodo.getDataFim(),
                periodo.getSaldoDias(),

                // Extrai os dados do servidor para o React conseguir filtrar
                periodo.getServidor() != null ? new ServidorResumo(
                        periodo.getServidor().getId(),
                        periodo.getServidor().getNome(),
                        periodo.getServidor().getMatricula(),
                        periodo.getServidor().getOperadorRaioX()
                ) : null,

                periodo.getOcorrencias() != null ?
                        periodo.getOcorrencias().stream().map(OcorrenciaPeriodoResponse::new).toList() : List.of(),

                periodo.getSolicitacoes() != null ?
                        periodo.getSolicitacoes().stream().map(SolicitacaoFeriasResponse::new).toList() : List.of()
        );
    }
}
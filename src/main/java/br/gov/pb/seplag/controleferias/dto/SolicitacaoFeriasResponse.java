package br.gov.pb.seplag.controleferias.dto;

import br.gov.pb.seplag.controleferias.domain.SolicitacaoFerias;
import java.time.LocalDate;

public record SolicitacaoFeriasResponse(
        Long id,
        LocalDate dataInicioGozo,
        Integer diasSolicitados,
        Boolean abonoPecuniario,
        String status
) {
    // Um construtor prático para converter a Entidade neste DTO
    public SolicitacaoFeriasResponse(SolicitacaoFerias entidade) {
        this(
                entidade.getId(),
                entidade.getDataInicioGozo(),
                entidade.getDiasSolicitados(),
                entidade.getAbonoPecuniario(),
                entidade.getStatus()
        );
    }
}
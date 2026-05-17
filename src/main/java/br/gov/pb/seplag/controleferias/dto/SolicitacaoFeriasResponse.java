package br.gov.pb.seplag.controleferias.dto;

import br.gov.pb.seplag.controleferias.domain.SolicitacaoFerias;
import java.time.LocalDate;

public record SolicitacaoFeriasResponse(
        Long id,
        LocalDate dataInicioGozo,
        Integer diasSolicitados,
        Boolean abonoPecuniario,
        String status,
        String servidorNome,
        String numeroPbdoc // 1. Adicionado o PBDOC aqui na estrutura do Record
) {
    public SolicitacaoFeriasResponse(SolicitacaoFerias entidade) {
        this(
                entidade.getId(),
                entidade.getDataInicioGozo(),
                entidade.getDiasSolicitados(),
                entidade.getAbonoPecuniario(),
                entidade.getStatus(),
                entidade.getPeriodoAquisitivo() != null && entidade.getPeriodoAquisitivo().getServidor() != null
                        ? entidade.getPeriodoAquisitivo().getServidor().getNome()
                        : "Não Informado",
                entidade.getNumeroPbdoc() // 2. Extrai o PBDOC da entidade e joga para o Front-end
        );
    }
}
package br.gov.pb.seplag.controleferias.dto;

import java.time.LocalDate;

public record SolicitacaoFeriasRequest(
        LocalDate dataInicioGozo,
        Integer diasSolicitados,
        Boolean abonoPecuniario,
        String numeroPbdoc
) {}
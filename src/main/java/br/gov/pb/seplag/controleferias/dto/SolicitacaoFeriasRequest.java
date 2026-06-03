package br.gov.pb.seplag.controleferias.dto;

import br.gov.pb.seplag.controleferias.domain.ModalidadeFerias;
import java.time.LocalDate;

public record SolicitacaoFeriasRequest(
        ModalidadeFerias modalidade, // <-- NOVO: Recebe GOZO ou INDENIZACAO do Front
        LocalDate dataInicioGozo,    // <-- Pode chegar nulo se for indenização
        Integer diasSolicitados,
        Boolean abonoPecuniario,
        String numeroPbdoc,
        Boolean isRetroativo         // <-- MANTIDO: Flag para receber o "Modo Histórico" do Front-end
) {}
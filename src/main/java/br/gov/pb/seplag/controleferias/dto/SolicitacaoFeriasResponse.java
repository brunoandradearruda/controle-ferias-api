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
        String numeroPbdoc,
        String lotacao,
        String matricula,
        Integer anoReferencia // <--- 1. NOVO CAMPO AQUI
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
                entidade.getNumeroPbdoc(),
                entidade.getPeriodoAquisitivo() != null && entidade.getPeriodoAquisitivo().getServidor() != null
                        ? java.util.Optional.ofNullable(entidade.getPeriodoAquisitivo().getServidor().getLotacao()).orElse("Não Informada")
                        : "Não Informada",
                entidade.getPeriodoAquisitivo() != null && entidade.getPeriodoAquisitivo().getServidor() != null
                        ? java.util.Optional.ofNullable(entidade.getPeriodoAquisitivo().getServidor().getMatricula()).orElse("-")
                        : "-",

                // 2. Extrai o ano de referência do período aquisitivo
                entidade.getPeriodoAquisitivo() != null ? entidade.getPeriodoAquisitivo().getAnoReferencia() : null
        );
    }
}
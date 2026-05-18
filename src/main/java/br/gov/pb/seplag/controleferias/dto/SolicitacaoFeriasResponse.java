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
        String matricula // 1. Adicionado o campo Matrícula no cabeçalho do Record
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

                // 2. Extrai a matrícula do Servidor de forma segura contra Nulls
                entidade.getPeriodoAquisitivo() != null && entidade.getPeriodoAquisitivo().getServidor() != null
                        ? java.util.Optional.ofNullable(entidade.getPeriodoAquisitivo().getServidor().getMatricula()).orElse("-")
                        : "-"
        );
    }
}
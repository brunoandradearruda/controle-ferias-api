package br.gov.pb.seplag.controleferias.dto;

import br.gov.pb.seplag.controleferias.domain.ModalidadeFerias; // <-- NOVO IMPORT
import br.gov.pb.seplag.controleferias.domain.SolicitacaoFerias;
import java.time.LocalDate;

public record SolicitacaoFeriasResponse(
        Long id,
        ModalidadeFerias modalidade, // <-- NOVO CAMPO AQUI
        LocalDate dataInicioGozo,
        Integer diasSolicitados,
        Boolean abonoPecuniario,
        String status,
        String servidorNome,
        String numeroPbdoc,
        String lotacao,
        String matricula,
        Integer anoReferencia
) {
    public SolicitacaoFeriasResponse(SolicitacaoFerias entidade) {
        this(
                entidade.getId(),
                entidade.getModalidade(), // <-- EXTRAINDO A MODALIDADE DA ENTIDADE
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

                // Extrai o ano de referência do período aquisitivo
                entidade.getPeriodoAquisitivo() != null ? entidade.getPeriodoAquisitivo().getAnoReferencia() : null
        );
    }
}
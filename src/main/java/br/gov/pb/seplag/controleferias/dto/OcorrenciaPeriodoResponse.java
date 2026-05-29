package br.gov.pb.seplag.controleferias.dto;

import br.gov.pb.seplag.controleferias.domain.OcorrenciaPeriodoAquisitivo;
import br.gov.pb.seplag.controleferias.domain.TipoOcorrenciaPeriodo;

import java.time.LocalDate;

public record OcorrenciaPeriodoResponse(
        Long id,
        TipoOcorrenciaPeriodo tipo,
        LocalDate dataEvento,
        String numeroPbdoc,
        String justificativa
) {
    public OcorrenciaPeriodoResponse(OcorrenciaPeriodoAquisitivo ocorrencia) {
        this(
                ocorrencia.getId(),
                ocorrencia.getTipo(),
                ocorrencia.getDataEvento(),
                ocorrencia.getNumeroPbdoc(),
                ocorrencia.getJustificativa()
        );
    }
}
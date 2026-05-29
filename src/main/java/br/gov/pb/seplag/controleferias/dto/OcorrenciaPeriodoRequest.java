package br.gov.pb.seplag.controleferias.dto;

import java.time.LocalDate;

public record OcorrenciaPeriodoRequest(
        LocalDate dataEvento,
        String numeroPbdoc,
        String justificativa
) {}
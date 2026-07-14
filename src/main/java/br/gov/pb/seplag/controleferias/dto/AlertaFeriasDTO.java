package br.gov.pb.seplag.controleferias.dto;

import java.time.LocalDate;

public record AlertaFeriasDTO(
        String servidorNome,
        String matricula,
        String lotacao,
        Integer anoReferencia,
        Integer saldoDias,
        LocalDate dataLimiteGozo,
        String nivelRisco // Pode ser "AMARELO" (23º mês) ou "VERMELHO" (Vencido)
) {}
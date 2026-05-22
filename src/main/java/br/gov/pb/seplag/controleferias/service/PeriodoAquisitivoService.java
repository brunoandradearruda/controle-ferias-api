package br.gov.pb.seplag.controleferias.service;

import br.gov.pb.seplag.controleferias.domain.PeriodoAquisitivo;
import br.gov.pb.seplag.controleferias.domain.Servidor;
import br.gov.pb.seplag.controleferias.repository.PeriodoAquisitivoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PeriodoAquisitivoService {

    private final PeriodoAquisitivoRepository periodoRepository;

    public List<PeriodoAquisitivo> listarTodos() {
        return periodoRepository.findByServidorAtivoTrue();
    }

    // ========================================================================
    // ---> LÓGICA DE CARGA DE PERÍODOS (O "ANO ZERO" INTELIGENTE) <---
    // ========================================================================
    public PeriodoAquisitivo gerarPeriodoPorAnoReferencia(Servidor servidor, int anoReferencia) {

        if (servidor.getDataAdmissao() == null) {
            throw new IllegalArgumentException("O servidor precisa ter uma data de admissão cadastrada para gerar o período aquisitivo.");
        }

        // 1. Projeta o aniversário de admissão para o ano de referência solicitado.
        // Dica: O 'withYear' lida perfeitamente com 29 de fevereiro em anos não-bissextos!
        LocalDate dataAniversarioAdmissao = servidor.getDataAdmissao().withYear(anoReferencia);

        // 2. A data final do ciclo (dataFim) é sempre 1 dia antes do aniversário de admissão naquele ano.
        LocalDate dataFim = dataAniversarioAdmissao.minusDays(1);

        // 3. A data de início do ciclo é 1 ano cravado antes do fechamento.
        LocalDate dataInicio = dataFim.minusYears(1).plusDays(1);

        // 4. Instancia e prepara o pote de férias para salvar no banco
        PeriodoAquisitivo novoPeriodo = new PeriodoAquisitivo();
        novoPeriodo.setServidor(servidor);
        novoPeriodo.setAnoReferencia(anoReferencia);
        novoPeriodo.setDataInicio(dataInicio);
        novoPeriodo.setDataFim(dataFim);
        novoPeriodo.setDataFimAquisicao(dataFim); // O marco inicial estatutário para liberação do saldo
        novoPeriodo.setSaldoDias(30); // Carga inicial cheia de 30 dias

        return periodoRepository.save(novoPeriodo);
    }
}
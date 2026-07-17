package br.gov.pb.seplag.controleferias.service;

import br.gov.pb.seplag.controleferias.dto.PeriodoAquisitivoDTO;
import br.gov.pb.seplag.controleferias.domain.Afastamento;
import br.gov.pb.seplag.controleferias.domain.PeriodoAquisitivo;
import br.gov.pb.seplag.controleferias.domain.Servidor;
import br.gov.pb.seplag.controleferias.repository.AfastamentoRepository;
import br.gov.pb.seplag.controleferias.repository.PeriodoAquisitivoRepository;
import br.gov.pb.seplag.controleferias.repository.SolicitacaoFeriasRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
public class CalculadoraPeriodoService {

    private final AfastamentoRepository afastamentoRepository;
    private final SolicitacaoFeriasRepository solicitacaoFeriasRepository;
    private final PeriodoAquisitivoRepository periodoAquisitivoRepository;

    public CalculadoraPeriodoService(
            AfastamentoRepository afastamentoRepository,
            SolicitacaoFeriasRepository solicitacaoFeriasRepository,
            PeriodoAquisitivoRepository periodoAquisitivoRepository) {
        this.afastamentoRepository = afastamentoRepository;
        this.solicitacaoFeriasRepository = solicitacaoFeriasRepository;
        this.periodoAquisitivoRepository = periodoAquisitivoRepository;
    }

    public List<PeriodoAquisitivoDTO> calcularPeriodosDisponiveis(Servidor servidor) {
        List<PeriodoAquisitivoDTO> periodos = new ArrayList<>();

        if (servidor.getDataAdmissao() == null) {
            return periodos;
        }

        // Busca os períodos que já existem no banco de dados para este servidor
        List<PeriodoAquisitivo> periodosNoBanco = periodoAquisitivoRepository.findByServidorId(servidor.getId());

        LocalDate inicioPeriodoAtual = servidor.getDataAdmissao();
        LocalDate hoje = LocalDate.now();

        while (!inicioPeriodoAtual.isAfter(hoje)) {

            final LocalDate dataInicioLoop = inicioPeriodoAtual; // Trava a variável para poder usar no lambda
            LocalDate previsaoFimPeriodo = inicioPeriodoAtual.plusYears(1).minusDays(1);

            List<Afastamento> afastamentos = afastamentoRepository.findAfastamentosQuePausamContagem(
                    servidor.getId(), dataInicioLoop, previsaoFimPeriodo
            );

            long diasPausados = 0;
            for (Afastamento af : afastamentos) {
                diasPausados += ChronoUnit.DAYS.between(af.getDataInicio(), af.getDataFim()) + 1;
            }

            LocalDate fimPeriodoReal = previsaoFimPeriodo.plusDays(diasPausados);

            Integer diasGozados = solicitacaoFeriasRepository.sumDiasGozadosPorPeriodo(servidor.getId(), dataInicioLoop, fimPeriodoReal);
            if (diasGozados == null) diasGozados = 0;

            int saldo = 30 - diasGozados;

            if (saldo > 0) {
                String refCalculada = dataInicioLoop.getYear() + "/" + fimPeriodoReal.getYear();

                // --- A MÁGICA DA SINCRONIZAÇÃO COM O BANCO ---
                PeriodoAquisitivo periodoReal = periodosNoBanco.stream()
                        .filter(p -> p.getDataInicio() != null && p.getDataInicio().equals(dataInicioLoop))
                        .findFirst()
                        .orElse(null);

                // Se o período calculado ainda não existe fisicamente no banco, cria e salva na hora!
                if (periodoReal == null) {
                    periodoReal = new PeriodoAquisitivo();
                    periodoReal.setServidor(servidor);
                    periodoReal.setDataInicio(dataInicioLoop);
                    periodoReal.setDataFim(fimPeriodoReal);

                    // Apenas insere o saldo de dias padrão para evitar o NullPointerException
                    periodoReal.setSaldoDias(30);

                    // Se na sua classe existir um campo numérico de ano, você pode usar:
                    // periodoReal.setAnoReferencia(dataInicioLoop.getYear());

                    periodoReal = periodoAquisitivoRepository.save(periodoReal);
                    periodosNoBanco.add(periodoReal); // Adiciona na memória para o próximo ciclo
                }
                // ===============================================================
                // ---> O SISTEMA DE AUTO-CURA QUE VAI RESOLVER O ERRO 500 <---
                // ===============================================================
                else if (periodoReal.getSaldoDias() == null) {
                    periodoReal.setSaldoDias(30);
                    periodoReal = periodoAquisitivoRepository.save(periodoReal);
                }

                PeriodoAquisitivoDTO dto = new PeriodoAquisitivoDTO();
                dto.setId(periodoReal.getId()); // <--- AGORA PASSA O ID VERDADEIRO PARA O REACT
                dto.setReferencia(refCalculada);
                dto.setDataInicio(dataInicioLoop);
                dto.setDataFim(fimPeriodoReal);
                dto.setSaldoDias(saldo);

                if (hoje.isBefore(fimPeriodoReal)) {
                    dto.setStatus("Em Andamento");
                } else if (hoje.isAfter(fimPeriodoReal.plusYears(1))) {
                    dto.setStatus("Acumulada / Vencida");
                } else {
                    dto.setStatus("Disponível");
                }

                if (!dto.getStatus().equals("Em Andamento")) {
                    periodos.add(dto);
                }
            }

            inicioPeriodoAtual = fimPeriodoReal.plusDays(1);
        }

        periodos.sort((p1, p2) -> p1.getDataInicio().compareTo(p2.getDataInicio()));
        return periodos;
    }
}
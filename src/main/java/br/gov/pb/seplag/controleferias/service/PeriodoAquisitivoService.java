package br.gov.pb.seplag.controleferias.service;

import br.gov.pb.seplag.controleferias.domain.OcorrenciaPeriodoAquisitivo;
import br.gov.pb.seplag.controleferias.domain.PeriodoAquisitivo;
import br.gov.pb.seplag.controleferias.domain.Servidor;
import br.gov.pb.seplag.controleferias.domain.TipoOcorrenciaPeriodo;
import br.gov.pb.seplag.controleferias.repository.OcorrenciaPeriodoAquisitivoRepository;
import br.gov.pb.seplag.controleferias.repository.PeriodoAquisitivoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PeriodoAquisitivoService {

    private final PeriodoAquisitivoRepository periodoRepository;
    private final OcorrenciaPeriodoAquisitivoRepository ocorrenciaRepository;

    /**
     * Gera um período aquisitivo padrão baseado na data de admissão do servidor
     */
    @Transactional
    public PeriodoAquisitivo gerarPeriodoPorAnoReferencia(Servidor servidor, int anoReferencia) {
        PeriodoAquisitivo periodo = new PeriodoAquisitivo();
        periodo.setServidor(servidor);
        periodo.setAnoReferencia(anoReferencia);

        LocalDate dataAdmissao = servidor.getDataAdmissao();
        LocalDate dataInicio = LocalDate.of(anoReferencia - 1, dataAdmissao.getMonth(), dataAdmissao.getDayOfMonth());
        LocalDate dataFim = dataInicio.plusYears(1).minusDays(1);

        periodo.setDataInicio(dataInicio);
        periodo.setDataFim(dataFim);
        periodo.setSaldoDias(30);

        return periodoRepository.save(periodo);
    }

    /**
     * Regra de Negócio: Suspender temporariamente a contagem de um ciclo de férias
     */
    @Transactional
    public void suspenderCiclo(Long periodoId, LocalDate dataSuspensao, String numeroPbdoc, String justificativa) {
        PeriodoAquisitivo periodo = periodoRepository.findById(periodoId)
                .orElseThrow(() -> new IllegalArgumentException("Período aquisitivo não encontrado."));

        if (dataSuspensao.isBefore(periodo.getDataInicio()) || dataSuspensao.isAfter(periodo.getDataFim())) {
            throw new IllegalArgumentException("A data de suspensão deve estar dentro do intervalo do período atual.");
        }

        OcorrenciaPeriodoAquisitivo ocorrencia = new OcorrenciaPeriodoAquisitivo();
        ocorrencia.setPeriodoAquisitivo(periodo);
        ocorrencia.setTipo(TipoOcorrenciaPeriodo.SUSPENSAO);
        ocorrencia.setDataEvento(dataSuspensao);
        ocorrencia.setNumeroPbdoc(numeroPbdoc);
        ocorrencia.setJustificativa(justificativa);

        ocorrenciaRepository.save(ocorrencia);
    }

    /**
     * Regra de Negócio: Retomar o ciclo e recalcular matematicamente a nova Data Fim
     */
    @Transactional
    public void retomarCiclo(Long periodoId, LocalDate dataRetomada, String numeroPbdoc, String justificativa) {
        PeriodoAquisitivo periodo = periodoRepository.findById(periodoId)
                .orElseThrow(() -> new IllegalArgumentException("Período aquisitivo não encontrado."));

        // Busca a última suspensão para saber quando o tempo foi congelado
        OcorrenciaPeriodoAquisitivo ultimaSuspensao = periodo.getOcorrencias().stream()
                .filter(o -> o.getTipo() == TipoOcorrenciaPeriodo.SUSPENSAO)
                .reduce((first, second) -> second)
                .orElseThrow(() -> new IllegalStateException("Não é possível retomar um ciclo que não possui histórico de suspensão."));

        if (dataRetomada.isBefore(ultimaSuspensao.getDataEvento())) {
            throw new IllegalArgumentException("A data de retomada não pode ser anterior à data de suspensão.");
        }

        // --- CÁLCULO DOS DIAS CONGELADOS ---
        long diasRestantes = ChronoUnit.DAYS.between(ultimaSuspensao.getDataEvento(), periodo.getDataFim());

        // A nova data fim empurra os dias restantes a partir da data de retorno ao trabalho
        LocalDate novaDataFim = dataRetomada.plusDays(diasRestantes);
        periodo.setDataFim(novaDataFim);

        // Registra o evento de retomada
        OcorrenciaPeriodoAquisitivo ocorrencia = new OcorrenciaPeriodoAquisitivo();
        ocorrencia.setPeriodoAquisitivo(periodo);
        ocorrencia.setTipo(TipoOcorrenciaPeriodo.RETOMADA);
        ocorrencia.setDataEvento(dataRetomada);
        ocorrencia.setNumeroPbdoc(numeroPbdoc);
        ocorrencia.setJustificativa(justificativa);

        ocorrenciaRepository.save(ocorrencia);
        periodoRepository.save(periodo);
    }

    /**
     * Retorna todos os períodos aquisitivos cadastrados no banco
     */
    public List<PeriodoAquisitivo> listarTodos() {
        return periodoRepository.findAll();
    }
}
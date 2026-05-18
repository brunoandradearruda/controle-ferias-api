package br.gov.pb.seplag.controleferias.service;

import br.gov.pb.seplag.controleferias.domain.PeriodoAquisitivo;
import br.gov.pb.seplag.controleferias.domain.Servidor;
import br.gov.pb.seplag.controleferias.domain.SolicitacaoFerias;
import br.gov.pb.seplag.controleferias.repository.PeriodoAquisitivoRepository;
import br.gov.pb.seplag.controleferias.repository.SolicitacaoFeriasRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor // O Lombok cria automaticamente o construtor para injetar os repositórios
public class FeriasService {

    private final PeriodoAquisitivoRepository periodoRepository;
    private final SolicitacaoFeriasRepository solicitacaoRepository;






    /**
     * Regra de Negócio Central: Fracionamento de Férias e Controle de Saldo
     */
    @Transactional
    public SolicitacaoFerias solicitarFracionamento(Long periodoId, SolicitacaoFerias novaSolicitacao) {

        PeriodoAquisitivo periodo = periodoRepository.findById(periodoId)
                .orElseThrow(() -> new IllegalArgumentException("Período Aquisitivo não encontrado."));

        if (novaSolicitacao.getDataInicioGozo().isBefore(java.time.LocalDate.now())) {
            throw new IllegalArgumentException("A data de início das férias não pode ser no passado.");
        }

        Servidor servidor = periodo.getServidor();

        // ========================================================================
        // ---> TRAVA FIFO (PRIORIDADE DO MAIS ANTIGO) <---
        // ========================================================================
        if (servidor != null) {
            boolean possuiPeriodoPendente = periodoRepository.existsPeriodoMaisAntigoComSaldo(
                    servidor.getId(),
                    periodo.getAnoReferencia()
            );

            if (possuiPeriodoPendente) {
                throw new IllegalArgumentException(
                        "Operação Bloqueada: O servidor ainda possui saldo em um período aquisitivo mais antigo. " +
                                "É obrigatório zerar o saldo dos períodos anteriores antes de utilizar este."
                );
            }
        }

        // ========================================================================
        // ---> TRAVA DE ADIANTAMENTO ESTATUTÁRIA (ART. 79, § 1º) <---
        // ========================================================================
        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy");

        // Usando o campo dataFim que já existe na sua classe!
        if (periodo.getDataFim() != null) {
            // O direito nasce exatamente 1 dia após o fim do ciclo de 12 meses (dataFim + 1 dia)
            java.time.LocalDate dataAquisicaoDireito = periodo.getDataFim().plusDays(1);

            if (novaSolicitacao.getDataInicioGozo().isBefore(dataAquisicaoDireito)) {
                throw new IllegalArgumentException(
                        "Operação Bloqueada: O servidor não completou os 12 meses de exercício deste período. " +
                                "O direito a estas férias só nasce em " + dataAquisicaoDireito.format(formatter) + "."
                );
            }
        } else {
            // FALLBACK: Se o período for legado e não possuir dataFim no banco, aplica a trava pelo ano civil
            if (novaSolicitacao.getDataInicioGozo().getYear() < periodo.getAnoReferencia()) {
                throw new IllegalArgumentException(
                        "Operação Bloqueada: Não é possível antecipar as férias. " +
                                "O período de " + periodo.getAnoReferencia() + " só pode ser usufruído a partir do ano de " + periodo.getAnoReferencia() + "."
                );
            }
        }
        // ========================================================================

        // ---> PREVENÇÃO DE CHOQUE DE DATAS <---
        if (servidor != null) {
            java.time.LocalDate novaDataInicio = novaSolicitacao.getDataInicioGozo();
            java.time.LocalDate novaDataFim = novaDataInicio.plusDays(novaSolicitacao.getDiasSolicitados() - 1);

            List<String> statusIgnorados = List.of("REJEITADA", "INTERROMPIDA");

            List<SolicitacaoFerias> feriasAtivas = solicitacaoRepository
                    .findByPeriodoAquisitivoServidorIdAndStatusNotIn(servidor.getId(), statusIgnorados);

            for (SolicitacaoFerias feriasAntiga : feriasAtivas) {
                java.time.LocalDate inicioAntiga = feriasAntiga.getDataInicioGozo();
                java.time.LocalDate fimAntiga = inicioAntiga.plusDays(feriasAntiga.getDiasSolicitados() - 1);

                if (!novaDataInicio.isAfter(fimAntiga) && !novaDataFim.isBefore(inicioAntiga)) {
                    throw new IllegalArgumentException(
                            String.format("Choque de datas: O servidor já possui férias registradas neste período (de %s a %s).",
                                    inicioAntiga.format(formatter),
                                    fimAntiga.format(formatter))
                    );
                }
            }
        }

        // ---> ART. 80 (RAIOS X) <---
        if (servidor != null && Boolean.TRUE.equals(servidor.getOperadorRaioX())) {
            if (novaSolicitacao.getDiasSolicitados() != 20) {
                throw new IllegalArgumentException(
                        "Pelo Art. 80 do Estatuto, servidores que operam Raios X devem gozar obrigatoriamente de 20 dias por semestre."
                );
            }
        }

        // Verifica se há saldo suficiente
        if (novaSolicitacao.getDiasSolicitados() > periodo.getSaldoDias()) {
            throw new IllegalArgumentException(
                    "Saldo insuficiente. Dias solicitados: " + novaSolicitacao.getDiasSolicitados() +
                            ", Saldo atual: " + periodo.getSaldoDias()
            );
        }

        // Subtrai os dias solicitados do saldo atual
        periodo.setSaldoDias(periodo.getSaldoDias() - novaSolicitacao.getDiasSolicitados());

        novaSolicitacao.setPeriodoAquisitivo(periodo);
        novaSolicitacao.setStatus("PENDENTE_CHEFIA");
        novaSolicitacao.setAbonoPecuniario(false);

        periodoRepository.save(periodo);
        return solicitacaoRepository.save(novaSolicitacao);
    }



    /**
     * Busca o histórico de solicitações de um período específico
     */
    public List<SolicitacaoFerias> listarPorPeriodo(Long periodoId) {
        return solicitacaoRepository.findByPeriodoAquisitivoId(periodoId);
    }

    /**
     * Regra de Negócio: Aprovação da Chefia
     */
    @Transactional
    public void aprovar(Long id) {
        SolicitacaoFerias solicitacao = solicitacaoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Solicitação não encontrada."));

        solicitacao.setStatus("APROVADA");
        solicitacaoRepository.save(solicitacao);
    }

    /**
     * Regra de Negócio: Rejeição da Chefia (Com devolução automática do saldo de dias)
     */
    @Transactional
    public void rejeitar(Long id) {
        SolicitacaoFerias solicitacao = solicitacaoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Solicitação não encontrada."));

        solicitacao.setStatus("REJEITADA");

        // Devolve de forma direta os dias solicitados de volta ao saldo
        PeriodoAquisitivo periodo = solicitacao.getPeriodoAquisitivo();
        periodo.setSaldoDias(periodo.getSaldoDias() + solicitacao.getDiasSolicitados());

        periodoRepository.save(periodo);
        solicitacaoRepository.save(solicitacao);
    }
    public List<SolicitacaoFerias> listarTodasGerais() {
        return solicitacaoRepository.findAll();
    }

    /**
     * Regra de Negócio: Interrupção de Férias (Art. 81)
     */
    @Transactional
    public void interromper(Long id) {
        SolicitacaoFerias solicitacao = solicitacaoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Solicitação não encontrada."));

        if (!"APROVADA".equals(solicitacao.getStatus())) {
            throw new IllegalArgumentException("Apenas férias aprovadas podem ser interrompidas.");
        }

        java.time.LocalDate hoje = java.time.LocalDate.now();
        java.time.LocalDate dataInicio = solicitacao.getDataInicioGozo();
        int diasSolicitados = solicitacao.getDiasSolicitados();
        java.time.LocalDate dataFim = dataInicio.plusDays(diasSolicitados);

        // Se a data de hoje já passou do fim das férias, não dá mais pra interromper
        if (hoje.isAfter(dataFim) || hoje.isEqual(dataFim)) {
            throw new IllegalArgumentException("Estas férias já terminaram e não podem ser interrompidas.");
        }

        int diasRestantesParaDevolver;

        // Se interrompeu ANTES de começar ou no exato primeiro dia
        if (hoje.isBefore(dataInicio) || hoje.isEqual(dataInicio)) {
            diasRestantesParaDevolver = diasSolicitados;
        } else {
            // Se interrompeu no MEIO das férias, calcula os dias que ele efetivamente ficou em casa
            long diasUsados = java.time.temporal.ChronoUnit.DAYS.between(dataInicio, hoje);
            diasRestantesParaDevolver = diasSolicitados - (int) diasUsados;
        }

        // Muda o status e devolve o saldo parcial ou total
        solicitacao.setStatus("INTERROMPIDA");

        PeriodoAquisitivo periodo = solicitacao.getPeriodoAquisitivo();
        periodo.setSaldoDias(periodo.getSaldoDias() + diasRestantesParaDevolver);

        periodoRepository.save(periodo);
        solicitacaoRepository.save(solicitacao);
    }

}
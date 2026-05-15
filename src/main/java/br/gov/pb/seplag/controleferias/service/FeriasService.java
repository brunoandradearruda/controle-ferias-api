package br.gov.pb.seplag.controleferias.service;

import br.gov.pb.seplag.controleferias.domain.PeriodoAquisitivo;
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
    @Transactional // Garante que, se der erro, nada é salvo no banco (Rollback)
    public SolicitacaoFerias solicitarFracionamento(Long periodoId, SolicitacaoFerias novaSolicitacao) {

        // 1. Busca o Período Aquisitivo no banco de dados
        PeriodoAquisitivo periodo = periodoRepository.findById(periodoId)
                .orElseThrow(() -> new IllegalArgumentException("Período Aquisitivo não encontrado."));

        // 2. Regra Principal: Verifica se há saldo suficiente para os dias solicitados
        if (novaSolicitacao.getDiasSolicitados() > periodo.getSaldoDias()) {
            throw new IllegalArgumentException(
                    "Saldo insuficiente. Dias solicitados: " + novaSolicitacao.getDiasSolicitados() +
                            ", Saldo atual: " + periodo.getSaldoDias()
            );
        }

        // 3. Subtrai os dias solicitados do saldo atual do período
        Integer novoSaldo = periodo.getSaldoDias() - novaSolicitacao.getDiasSolicitados();
        periodo.setSaldoDias(novoSaldo);

        // 4. Configura os dados complementares da solicitação
        novaSolicitacao.setPeriodoAquisitivo(periodo);
        novaSolicitacao.setStatus("PENDENTE_CHEFIA"); // Status inicial padrão

        // 5. Salva as atualizações no banco de dados
        periodoRepository.save(periodo); // Atualiza o saldo
        return solicitacaoRepository.save(novaSolicitacao); // Salva o fracionamento
    }

    /**
     * Busca o histórico de solicitações de um período específico
     */
    public List<SolicitacaoFerias> listarPorPeriodo(Long periodoId) {
        return solicitacaoRepository.findByPeriodoAquisitivoId(periodoId);
    }
}
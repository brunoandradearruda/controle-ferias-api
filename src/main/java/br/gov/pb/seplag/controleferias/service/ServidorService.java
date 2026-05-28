package br.gov.pb.seplag.controleferias.service;

import br.gov.pb.seplag.controleferias.domain.PeriodoAquisitivo;
import br.gov.pb.seplag.controleferias.domain.Servidor;
import br.gov.pb.seplag.controleferias.domain.SolicitacaoFerias;
import br.gov.pb.seplag.controleferias.repository.PeriodoAquisitivoRepository;
import br.gov.pb.seplag.controleferias.repository.ServidorRepository;
import br.gov.pb.seplag.controleferias.repository.SolicitacaoFeriasRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ServidorService {

    private final ServidorRepository servidorRepository;

    // Injetando o PeriodoAquisitivoService para usarmos a regra da admissão
    private final PeriodoAquisitivoService periodoService;

    // ---> CORREÇÃO: Repositórios injetados para a nossa "Máquina do Tempo" funcionarem <---
    private final PeriodoAquisitivoRepository periodoAquisitivoRepository;
    private final SolicitacaoFeriasRepository solicitacaoFeriasRepository;

    @Transactional
    public Servidor cadastrar(Servidor servidor) {
        // 1. Salva o servidor primeiro
        Servidor servidorSalvo = servidorRepository.save(servidor);

        int anoAtual = LocalDate.now().getYear(); // 2026
        int anoAdmissao = servidor.getDataAdmissao().getYear();

        // 2. REGRA DE OURO: Só automatiza o primeiro período se o servidor for NOVATO do ano atual
        if (anoAdmissao == anoAtual) {
            // Ex: Alan admitido em 2026 -> Gera automaticamente o período de 2026/2027
            int anoReferenciaPeriodoInicial = anoAtual + 1;
            periodoService.gerarPeriodoPorAnoReferencia(servidorSalvo, anoReferenciaPeriodoInicial);
        }
        // Se for VETERANO (anoAdmissao < anoAtual), o bloco acima é ignorado.
        // O servidor é salvo sem nenhum período automático, aguardando a inserção manual do RH.

        return servidorSalvo;
    }

    public List<Servidor> listarTodos() {
        return servidorRepository.findAll();
    }

    @Transactional
    public void inativarServidor(Long id, String motivo) {
        Servidor servidor = servidorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Servidor não encontrado."));

        servidor.setAtivo(false);
        servidor.setMotivoDesligamento(motivo);
        servidorRepository.save(servidor);
    }

    @Transactional
    public void reativarServidor(Long id) {
        Servidor servidor = servidorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Servidor não encontrado."));

        servidor.setAtivo(true);
        servidorRepository.save(servidor);
    }

    @Transactional
    public void adicionarPeriodoAcumulado(Long servidorId, int anoReferencia) {
        Servidor servidor = servidorRepository.findById(servidorId)
                .orElseThrow(() -> new IllegalArgumentException("Servidor não encontrado."));

        // Proteção contra duplicidade: verifica se o RH já cadastrou esse ano atrasado
        boolean jaExiste = servidor.getPeriodosAquisitivos().stream()
                .anyMatch(p -> p.getAnoReferencia() == anoReferencia);

        if (jaExiste) {
            throw new IllegalArgumentException("O período de " + anoReferencia + " já está cadastrado para este servidor.");
        }

        // Se não existir, gera o pote com a nossa mágica baseada na admissão
        periodoService.gerarPeriodoPorAnoReferencia(servidor, anoReferencia);
    }

    @Transactional
    public void gerarHistoricoGozadoSimulado(Long servidorId, int anoInicio, int anoFim) {
        Servidor servidor = servidorRepository.findById(servidorId)
                .orElseThrow(() -> new RuntimeException("Servidor não encontrado"));

        for (int ano = anoInicio; ano <= anoFim; ano++) {

            // 1. Cria o Período Aquisitivo (Ex: 2010/2011)
            PeriodoAquisitivo periodo = new PeriodoAquisitivo();
            periodo.setServidor(servidor);
            periodo.setAnoReferencia(ano + 1);
            periodo.setDataInicio(LocalDate.of(ano, servidor.getDataAdmissao().getMonth(), servidor.getDataAdmissao().getDayOfMonth()));
            periodo.setDataFim(periodo.getDataInicio().plusYears(1).minusDays(1));
            periodo.setSaldoDias(0);

            // Salvando no repositório correto
            PeriodoAquisitivo periodoSalvo = periodoAquisitivoRepository.save(periodo);

            // 2. Cria a Solicitação de Férias já GOZADA daquele período
            SolicitacaoFerias solicitacao = new SolicitacaoFerias();
            solicitacao.setPeriodoAquisitivo(periodoSalvo);
            solicitacao.setDiasSolicitados(30);

            // Simula que ele tirou as férias 1 mês depois que o período fechou
            LocalDate inicioGozo = periodoSalvo.getDataFim().plusMonths(1);
            solicitacao.setDataInicioGozo(inicioGozo);

            solicitacao.setStatus("APROVADA"); // Status final de quem já viajou
            solicitacao.setNumeroPbdoc("MOCK-" + ano + "/0000");

            // Salvando no repositório correto
            solicitacaoFeriasRepository.save(solicitacao);
        }
    }
}
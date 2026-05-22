package br.gov.pb.seplag.controleferias.service;

import br.gov.pb.seplag.controleferias.domain.Servidor;
import br.gov.pb.seplag.controleferias.repository.ServidorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ServidorService {

    private final ServidorRepository servidorRepository;

    // ---> NOVO: Injetando o PeriodoAquisitivoService para usarmos a regra da admissão <---
    private final PeriodoAquisitivoService periodoService;

    @Transactional // Se der erro na criação do período, ele desfaz o cadastro do servidor (Rollback)
    public Servidor cadastrar(Servidor servidor) {
        // 1. Salva o servidor no banco de dados (agora com a data de admissão)
        Servidor servidorSalvo = servidorRepository.save(servidor);

        // 2. A MÁGICA ATUALIZADA: Substituímos o código antigo por uma chamada limpa!
        // Gera automaticamente o "Ano Zero" (2026) ancorado no dia e mês da posse.
        periodoService.gerarPeriodoPorAnoReferencia(servidorSalvo, 2026);

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

}
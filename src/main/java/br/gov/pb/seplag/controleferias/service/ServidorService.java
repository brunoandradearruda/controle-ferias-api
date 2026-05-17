package br.gov.pb.seplag.controleferias.service;

import br.gov.pb.seplag.controleferias.domain.PeriodoAquisitivo;
import br.gov.pb.seplag.controleferias.domain.Servidor;
import br.gov.pb.seplag.controleferias.repository.PeriodoAquisitivoRepository;
import br.gov.pb.seplag.controleferias.repository.ServidorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ServidorService {

    private final ServidorRepository servidorRepository;
    private final PeriodoAquisitivoRepository periodoRepository;

    @Transactional // Se der erro na criação do período, ele desfaz o cadastro do servidor (Rollback)
    public Servidor cadastrar(Servidor servidor) {
        // 1. Salva o servidor no banco de dados
        Servidor servidorSalvo = servidorRepository.save(servidor);

        // 2. Mágica: Cria automaticamente o 1º período aquisitivo com 30 dias de saldo
        PeriodoAquisitivo periodo = new PeriodoAquisitivo();
        periodo.setServidor(servidorSalvo);
        periodo.setAnoReferencia(LocalDate.now().getYear());
        // Define o período de trabalho como o ano passado inteiro
        periodo.setDataInicio(LocalDate.now().minusYears(1).withDayOfYear(1));
        periodo.setDataFim(LocalDate.now().minusYears(1).withDayOfYear(365));
        periodo.setSaldoDias(30);

        periodoRepository.save(periodo);

        return servidorSalvo;
    }

    public List<Servidor> listarTodos() {
        return servidorRepository.findAll();
    }
}
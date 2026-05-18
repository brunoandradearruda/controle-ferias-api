package br.gov.pb.seplag.controleferias.repository;

import br.gov.pb.seplag.controleferias.domain.SolicitacaoFerias;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SolicitacaoFeriasRepository extends JpaRepository<SolicitacaoFerias, Long> {

    List<SolicitacaoFerias> findByPeriodoAquisitivoId(Long periodoAquisitivoId);

    // ---> NOVO MÉTODO COMPATÍVEL COM O SPRING DATA <---
    // Busca todas as férias de um servidor específico, ignorando as rejeitadas e interrompidas
    List<SolicitacaoFerias> findByPeriodoAquisitivoServidorIdAndStatusNotIn(Long servidorId, List<String> statusIgnorados);
}
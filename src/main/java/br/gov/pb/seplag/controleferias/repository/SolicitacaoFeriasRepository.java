package br.gov.pb.seplag.controleferias.repository;

import br.gov.pb.seplag.controleferias.domain.SolicitacaoFerias;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SolicitacaoFeriasRepository extends JpaRepository<SolicitacaoFerias, Long> {

    // O Spring faz a mágica de criar a consulta SQL só pelo nome do método!
    List<SolicitacaoFerias> findByPeriodoAquisitivoId(Long periodoAquisitivoId);

}
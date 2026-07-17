package br.gov.pb.seplag.controleferias.repository;

import br.gov.pb.seplag.controleferias.domain.SolicitacaoFerias;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SolicitacaoFeriasRepository extends JpaRepository<SolicitacaoFerias, Long> {

    List<SolicitacaoFerias> findByPeriodoAquisitivoId(Long periodoAquisitivoId);

    // Busca todas as férias de um servidor específico, ignorando as rejeitadas e interrompidas
    List<SolicitacaoFerias> findByPeriodoAquisitivoServidorIdAndStatusNotIn(Long servidorId, List<String> statusIgnorados);

    // =================================================================================
    // ---> MOTOR DE CÁLCULO DE PERÍODOS DISPONÍVEIS <---
    // =================================================================================
    @Query("SELECT SUM(s.diasSolicitados) FROM SolicitacaoFerias s " +
            "WHERE s.periodoAquisitivo.servidor.id = :servidorId " +
            "AND s.periodoAquisitivo.dataInicio = :inicio " +
            "AND s.periodoAquisitivo.dataFim = :fim " +
            "AND s.status NOT IN ('REJEITADA', 'CANCELADA')")
    Integer sumDiasGozadosPorPeriodo(
            @Param("servidorId") Long servidorId,
            @Param("inicio") LocalDate inicio,
            @Param("fim") LocalDate fim);
}
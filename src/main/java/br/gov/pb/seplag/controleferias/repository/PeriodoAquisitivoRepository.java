package br.gov.pb.seplag.controleferias.repository;

import br.gov.pb.seplag.controleferias.domain.PeriodoAquisitivo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PeriodoAquisitivoRepository extends JpaRepository<PeriodoAquisitivo, Long> {

    // Seu método original intacto
    List<PeriodoAquisitivo> findByServidorAtivoTrue();


    List<PeriodoAquisitivo> findByServidorId(Long servidorId);

    // ---> NOVA REGRA: Consulta de Trava FIFO <---

    // Adicione esta assinatura no seu PeriodoAquisitivoRepository
    @Query("SELECT p FROM PeriodoAquisitivo p JOIN FETCH p.servidor WHERE p.saldoDias > 0 AND p.dataFim IS NOT NULL")
    List<PeriodoAquisitivo> findPeriodosComSaldoPendentes();


    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END " +
            "FROM PeriodoAquisitivo p " +
            "WHERE p.servidor.id = :servidorId " +
            "AND p.anoReferencia < :anoReferencia " +
            "AND p.saldoDias > 0")
    boolean existsPeriodoMaisAntigoComSaldo(
            @Param("servidorId") Long servidorId,
            @Param("anoReferencia") Integer anoReferencia
    );
}
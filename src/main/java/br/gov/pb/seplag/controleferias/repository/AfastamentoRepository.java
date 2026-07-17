package br.gov.pb.seplag.controleferias.repository;

import br.gov.pb.seplag.controleferias.domain.Afastamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface AfastamentoRepository extends JpaRepository<Afastamento, Long> {

    // Método original preservado
    List<Afastamento> findByServidorId(Long servidorId);

    // Novo motor: Busca apenas os afastamentos que pausam a contagem de férias dentro de um período específico
    @Query("SELECT a FROM Afastamento a WHERE a.servidor.id = :servidorId " +
            "AND a.tipo IN ('LICENCA_SEM_VENCIMENTO', 'FALTAS_NAO_JUSTIFICADAS', 'SUSPENSAO_DISCIPLINAR', 'LICENCA_TRATO_INTERESSE_PARTICULAR') " +
            "AND a.dataInicio >= :inicio AND a.dataFim <= :fim")
    List<Afastamento> findAfastamentosQuePausamContagem(
            @Param("servidorId") Long servidorId,
            @Param("inicio") LocalDate inicio,
            @Param("fim") LocalDate fim);
}
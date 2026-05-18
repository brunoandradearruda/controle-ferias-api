package br.gov.pb.seplag.controleferias.repository;

import br.gov.pb.seplag.controleferias.domain.PeriodoAquisitivo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PeriodoAquisitivoRepository extends JpaRepository<PeriodoAquisitivo, Long> {
    List<PeriodoAquisitivo> findByServidorAtivoTrue();
}
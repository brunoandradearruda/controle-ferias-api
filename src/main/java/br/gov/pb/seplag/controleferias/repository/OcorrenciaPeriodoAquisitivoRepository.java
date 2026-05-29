package br.gov.pb.seplag.controleferias.repository;

import br.gov.pb.seplag.controleferias.domain.OcorrenciaPeriodoAquisitivo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OcorrenciaPeriodoAquisitivoRepository extends JpaRepository<OcorrenciaPeriodoAquisitivo, Long> {
}
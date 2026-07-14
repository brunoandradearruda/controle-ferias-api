package br.gov.pb.seplag.controleferias.repository;

import br.gov.pb.seplag.controleferias.domain.Afastamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AfastamentoRepository extends JpaRepository<Afastamento, Long> {
    List<Afastamento> findByServidorId(Long servidorId);
}
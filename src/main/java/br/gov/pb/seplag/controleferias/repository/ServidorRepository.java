package br.gov.pb.seplag.controleferias.repository;

import br.gov.pb.seplag.controleferias.domain.Servidor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServidorRepository extends JpaRepository<Servidor, Long> {
}
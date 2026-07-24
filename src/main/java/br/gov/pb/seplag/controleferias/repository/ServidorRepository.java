package br.gov.pb.seplag.controleferias.repository;

import br.gov.pb.seplag.controleferias.domain.Servidor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServidorRepository extends JpaRepository<Servidor, Long> {

    // 1. Usado na hora de CADASTRAR um novo servidor
    boolean existsByMatricula(String matricula);

    // 2. Usado na hora de EDITAR um servidor (ignora a matrícula dele mesmo)
    boolean existsByMatriculaAndIdNot(String matricula, Long id);
}
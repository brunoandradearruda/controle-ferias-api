package br.gov.pb.seplag.controleferias.service;

import br.gov.pb.seplag.controleferias.domain.PeriodoAquisitivo;
import br.gov.pb.seplag.controleferias.repository.PeriodoAquisitivoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PeriodoAquisitivoService {

    private final PeriodoAquisitivoRepository periodoRepository;

    public List<PeriodoAquisitivo> listarTodos() {
        return periodoRepository.findAll();
    }
}
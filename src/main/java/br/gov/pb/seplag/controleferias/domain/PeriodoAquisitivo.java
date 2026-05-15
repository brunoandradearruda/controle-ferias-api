package br.gov.pb.seplag.controleferias.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "periodo_aquisitivo")
@Getter
@Setter
public class PeriodoAquisitivo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "servidor_id")
    private Servidor servidor;

    private Integer anoReferencia;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private Integer saldoDias;

    @OneToMany(mappedBy = "periodoAquisitivo")
    private List<SolicitacaoFerias> solicitacoes;
}
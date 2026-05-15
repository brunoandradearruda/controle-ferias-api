package br.gov.pb.seplag.controleferias.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Entity
@Table(name = "solicitacao_ferias")
@Getter
@Setter
public class SolicitacaoFerias {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "periodo_aquisitivo_id")
    private PeriodoAquisitivo periodoAquisitivo;

    private LocalDate dataInicioGozo;
    private Integer diasSolicitados;
    private Boolean abonoPecuniario;
    private String status;
}
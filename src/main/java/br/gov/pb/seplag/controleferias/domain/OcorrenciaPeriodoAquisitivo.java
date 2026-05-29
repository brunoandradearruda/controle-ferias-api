package br.gov.pb.seplag.controleferias.domain;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Table(name = "ocorrencia_periodo_aquisitivo")
@Data
public class OcorrenciaPeriodoAquisitivo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "periodo_aquisitivo_id", nullable = false)
    private PeriodoAquisitivo periodoAquisitivo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoOcorrenciaPeriodo tipo; // SUSPENSAO ou RETOMADA

    @Column(nullable = false)
    private LocalDate dataEvento;

    @Column(nullable = false)
    private String numeroPbdoc;

    @Column(columnDefinition = "TEXT")
    private String justificativa;
}
package br.gov.pb.seplag.controleferias.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "afastamento")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Afastamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "servidor_id", nullable = false)
    private Servidor servidor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoAfastamento tipo;

    @Column(nullable = false)
    private LocalDate dataInicio;

    @Column(nullable = false)
    private LocalDate dataFim;

    @Column(nullable = false)
    private Integer quantidadeDias;

    // Método que calcula os dias automaticamente antes de salvar
    @PrePersist
    @PreUpdate
    public void calcularDias() {
        if (dataInicio != null && dataFim != null) {
            // Conta os dias incluindo o primeiro dia (por isso + 1)
            this.quantidadeDias = (int) ChronoUnit.DAYS.between(dataInicio, dataFim) + 1;
        }
    }
}
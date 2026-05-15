package br.gov.pb.seplag.controleferias.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Entity
@Table(name = "servidor")
@Getter
@Setter
public class Servidor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String matricula;
    private String nome;
    private String cargo;
    private String lotacao;

    @OneToMany(mappedBy = "servidor")
    private List<PeriodoAquisitivo> periodosAquisitivos;
}
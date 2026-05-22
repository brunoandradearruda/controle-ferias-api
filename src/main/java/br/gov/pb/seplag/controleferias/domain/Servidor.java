package br.gov.pb.seplag.controleferias.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate; // <-- Importação necessária para lidar com datas modernas no Java
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
    private Boolean operadorRaioX = false;
    private Boolean ativo = true;
    private String motivoDesligamento;

    // ---> NOVO CAMPO: A âncora temporal do servidor para cálculo estatutário <---
    @Column(name = "data_admissao")
    private LocalDate dataAdmissao;

    @JsonIgnore
    @OneToMany(mappedBy = "servidor")
    private List<PeriodoAquisitivo> periodosAquisitivos;
}
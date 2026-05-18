package br.gov.pb.seplag.controleferias.domain;

import com.fasterxml.jackson.annotation.JsonIgnore; // <-- Importação adicionada aqui
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
    private Boolean operadorRaioX = false;
    private Boolean ativo = true;
    private String motivoDesligamento;




    @JsonIgnore // <-- Anotação adicionada aqui para quebrar o loop do JSON
    @OneToMany(mappedBy = "servidor")
    private List<PeriodoAquisitivo> periodosAquisitivos;
}
package br.gov.pb.seplag.controleferias.domain;

import com.fasterxml.jackson.annotation.JsonIgnore; // <-- Importação adicionada
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.util.ArrayList;
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


    // ... seus atributos existentes (id, anoReferencia, dataInicio, etc) ...


    @OneToMany(mappedBy = "periodoAquisitivo", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("dataEvento ASC")
    private List<OcorrenciaPeriodoAquisitivo> ocorrencias = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "periodoAquisitivo")
    private List<SolicitacaoFerias> solicitacoes = new ArrayList<>(); // <-- Inicialização adicionada
    // ---> NOVO: Data que ele completou 12 meses de trabalho <---
    @Column(name = "data_fim_aquisicao")
    private LocalDate dataFimAquisicao;

    // ---> NOVO: Regra de Negócio calculada em tempo real (Não vai pro banco, vai pro JSON) <---
    @Transient
    public boolean isAlertaPrazo() {
        // Se não tiver data ou se ele já gastou todos os dias, não tem alerta
        if (this.dataFimAquisicao == null || this.saldoDias <= 0) {
            return false;
        }

        // Calcula a data limite do 23º mês após a aquisição (Art. 79, § 3º)
        LocalDate dataAlerta = this.dataFimAquisicao.plusMonths(23);

        // Retorna TRUE se a data de hoje já passou ou é igual ao limite do 23º mês
        return LocalDate.now().isAfter(dataAlerta) || LocalDate.now().isEqual(dataAlerta);
    }
}
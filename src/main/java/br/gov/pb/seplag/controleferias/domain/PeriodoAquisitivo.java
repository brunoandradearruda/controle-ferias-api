package br.gov.pb.seplag.controleferias.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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

    @Column(name = "ano_referencia")
    private Integer anoReferencia;

    @Column(name = "data_inicio")
    private LocalDate dataInicio;

    @Column(name = "data_fim")
    private LocalDate dataFim;

    @Column(name = "saldo_dias")
    private Integer saldoDias;

    @Column(name = "data_fim_aquisicao")
    private LocalDate dataFimAquisicao;

    @OneToMany(mappedBy = "periodoAquisitivo", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("dataEvento ASC")
    private List<OcorrenciaPeriodoAquisitivo> ocorrencias = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "periodoAquisitivo")
    private List<SolicitacaoFerias> solicitacoes = new ArrayList<>();

    // =========================================================================
    // GATILHO DE AUTOMAÇÃO JPA (Calcula o ano sozinho ao salvar no banco)
    // =========================================================================
    @PrePersist
    @PreUpdate
    public void calcularAnoReferenciaAutomatico() {
        if (this.anoReferencia == null && this.dataFim != null) {
            this.anoReferencia = this.dataFim.getYear();
        }
    }

    // =========================================================================
    // INTELIGÊNCIA ESTATUTÁRIA (LC 58/2003 - Art. 79)
    // As tags @JsonProperty forçam o Spring a injetar isso no JSON do Front-end
    // =========================================================================

    @Transient
    @JsonProperty("referencia")
    public String getReferencia() {
        if (this.anoReferencia != null) {
            return (this.anoReferencia - 1) + "/" + this.anoReferencia;
        }
        return "N/A";
    }

    @Transient
    @JsonProperty("descricaoAquisitiva")
    public String getDescricaoAquisitiva() {
        if (this.servidor == null || this.servidor.getDataAdmissao() == null || this.anoReferencia == null) {
            return "Ref: " + getReferencia();
        }
        int mes = this.servidor.getDataAdmissao().getMonthValue();
        int dia = this.servidor.getDataAdmissao().getDayOfMonth();

        LocalDate inicioAq = LocalDate.of(this.anoReferencia - 1, mes, dia);
        LocalDate fimAq = LocalDate.of(this.anoReferencia, mes, dia).minusDays(1);

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return String.format("Ref: %d/%d (%s a %s)",
                this.anoReferencia - 1, this.anoReferencia, inicioAq.format(fmt), fimAq.format(fmt));
    }

    @Transient
    @JsonProperty("inicioConcessivoFormatado")
    public String getInicioConcessivoFormatado() {
        if (this.servidor == null || this.servidor.getDataAdmissao() == null || this.anoReferencia == null) {
            return "--/--/----";
        }
        int mes = this.servidor.getDataAdmissao().getMonthValue();
        int dia = this.servidor.getDataAdmissao().getDayOfMonth();

        LocalDate inicioConc = LocalDate.of(this.anoReferencia, mes, dia);
        return inicioConc.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    @Transient
    @JsonProperty("fimConcessivoFormatado")
    public String getFimConcessivoFormatado() {
        if (this.servidor == null || this.servidor.getDataAdmissao() == null || this.anoReferencia == null) {
            return "--/--/----";
        }
        int mes = this.servidor.getDataAdmissao().getMonthValue();
        int dia = this.servidor.getDataAdmissao().getDayOfMonth();

        // Concessivo dura exatos 24 meses após a aquisição (Art. 79, § 2º)
        LocalDate fimConc = LocalDate.of(this.anoReferencia + 2, mes, dia).minusDays(1);
        return fimConc.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    @Transient
    @JsonProperty("status")
    public String getStatus() {
        if (this.saldoDias != null && this.saldoDias == 0) {
            return "Gozada";
        }
        if (isAlertaPrazo()) {
            return "Acumulada / Vencida";
        }
        if (this.anoReferencia != null && this.anoReferencia > LocalDate.now().getYear()) {
            return "Vincenda";
        }
        return "Disponível";
    }

    @Transient
    @JsonIgnore
    public boolean isAlertaPrazo() {
        if (this.servidor == null || this.servidor.getDataAdmissao() == null || this.anoReferencia == null || this.saldoDias == null || this.saldoDias <= 0) {
            return false;
        }

        int mes = this.servidor.getDataAdmissao().getMonthValue();
        int dia = this.servidor.getDataAdmissao().getDayOfMonth();

        // Fim da Aquisição
        LocalDate fimAq = LocalDate.of(this.anoReferencia, mes, dia).minusDays(1);

        // 23º mês após o Fim da Aquisição (Alerta do § 3º)
        LocalDate dataAlerta = fimAq.plusMonths(23);

        return LocalDate.now().isAfter(dataAlerta) || LocalDate.now().isEqual(dataAlerta);
    }
}
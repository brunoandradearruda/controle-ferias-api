package br.gov.pb.seplag.controleferias.dto;

import java.time.LocalDate;

public class PeriodoAquisitivoDTO {

    private Long id;
    private String referencia; // Ex: "2022/2023"
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private Integer saldoDias; // Ex: 30
    private String status; // Ex: "Disponível", "Acumulada / Vencida"

    public PeriodoAquisitivoDTO() {}

    public String getReferencia() { return referencia; }
    public void setReferencia(String referencia) { this.referencia = referencia; }

    public LocalDate getDataInicio() { return dataInicio; }
    public void setDataInicio(LocalDate dataInicio) { this.dataInicio = dataInicio; }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDataFim() { return dataFim; }
    public void setDataFim(LocalDate dataFim) { this.dataFim = dataFim; }

    public Integer getSaldoDias() { return saldoDias; }
    public void setSaldoDias(Integer saldoDias) { this.saldoDias = saldoDias; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
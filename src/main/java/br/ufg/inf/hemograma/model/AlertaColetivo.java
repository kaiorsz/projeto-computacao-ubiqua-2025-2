package br.ufg.inf.hemograma.model;

import br.ufg.inf.hemograma.model.enums.TipoParametro;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "alertas_coletivos")
public class AlertaColetivo {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_parametro", nullable = false)
    private TipoParametro tipoParametro;
    
    @Column(name = "total_desvios", nullable = false)
    private Integer totalDesvios;
    
    @Column(name = "janela_inicio", nullable = false)
    private LocalDateTime janelaInicio;
    
    @Column(name = "janela_fim", nullable = false)
    private LocalDateTime janelaFim;
    
    @Column(name = "percentual_afetados", nullable = false)
    private Double percentualAfetados;
    
    @Column(name = "descricao", length = 1000)
    private String descricao;
    
    @Column(name = "data_criacao", nullable = false)
    private LocalDateTime dataCriacao;
    
    @Column(name = "notificacao_enviada")
    private Boolean notificacaoEnviada = false;

    @PrePersist
    protected void onCreate() {
        dataCriacao = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TipoParametro getTipoParametro() {
        return tipoParametro;
    }

    public void setTipoParametro(TipoParametro tipoParametro) {
        this.tipoParametro = tipoParametro;
    }

    public Integer getTotalDesvios() {
        return totalDesvios;
    }

    public void setTotalDesvios(Integer totalDesvios) {
        this.totalDesvios = totalDesvios;
    }

    public LocalDateTime getJanelaInicio() {
        return janelaInicio;
    }

    public void setJanelaInicio(LocalDateTime janelaInicio) {
        this.janelaInicio = janelaInicio;
    }

    public LocalDateTime getJanelaFim() {
        return janelaFim;
    }

    public void setJanelaFim(LocalDateTime janelaFim) {
        this.janelaFim = janelaFim;
    }

    public Double getPercentualAfetados() {
        return percentualAfetados;
    }

    public void setPercentualAfetados(Double percentualAfetados) {
        this.percentualAfetados = percentualAfetados;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public Boolean getNotificacaoEnviada() {
        return notificacaoEnviada;
    }

    public void setNotificacaoEnviada(Boolean notificacaoEnviada) {
        this.notificacaoEnviada = notificacaoEnviada;
    }
}


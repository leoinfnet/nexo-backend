package br.com.infnet.nexobackend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "contas")
public class Conta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String descricao;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal valor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoConta tipo;

    @Column(nullable = false, length = 100)
    private String categoria;

    @Column(nullable = false)
    private LocalDate vencimento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusConta status;

    @Column(name = "data_pagamento")
    private LocalDate dataPagamento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RecorrenciaConta recorrencia;

    @Column(length = 1000)
    private String observacao;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "parceiro_id", nullable = false)
    private Parceiro parceiro;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lancamento_id", unique = true)
    private Lancamento lancamento;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private Instant criadoEm;

    protected Conta() {
    }

    public Conta(String descricao, BigDecimal valor, TipoConta tipo, String categoria,
                 LocalDate vencimento, RecorrenciaConta recorrencia, String observacao, Parceiro parceiro) {
        this.status = StatusConta.PENDENTE;
        atualizar(descricao, valor, tipo, categoria, vencimento, recorrencia, observacao, parceiro);
    }

    public void atualizar(String descricao, BigDecimal valor, TipoConta tipo, String categoria,
                          LocalDate vencimento, RecorrenciaConta recorrencia, String observacao, Parceiro parceiro) {
        this.descricao = descricao;
        this.valor = valor;
        this.tipo = tipo;
        this.categoria = categoria;
        this.vencimento = vencimento;
        this.recorrencia = recorrencia;
        this.observacao = observacao;
        this.parceiro = parceiro;
    }

    public void liquidar(LocalDate dataPagamento, Lancamento lancamento) {
        this.status = StatusConta.PAGA;
        this.dataPagamento = dataPagamento;
        this.lancamento = lancamento;
    }

    public void cancelar() {
        this.status = StatusConta.CANCELADA;
    }

    public StatusConta getStatusAtual() {
        if (status == StatusConta.PENDENTE && vencimento.isBefore(LocalDate.now())) {
            return StatusConta.VENCIDA;
        }
        return status;
    }

    @PrePersist
    void definirDataDeCriacao() {
        if (criadoEm == null) {
            criadoEm = Instant.now();
        }
    }

    public Long getId() {
        return id;
    }

    public String getDescricao() {
        return descricao;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public TipoConta getTipo() {
        return tipo;
    }

    public String getCategoria() {
        return categoria;
    }

    public LocalDate getVencimento() {
        return vencimento;
    }

    public StatusConta getStatus() {
        return status;
    }

    public LocalDate getDataPagamento() {
        return dataPagamento;
    }

    public RecorrenciaConta getRecorrencia() {
        return recorrencia;
    }

    public String getObservacao() {
        return observacao;
    }

    public Parceiro getParceiro() {
        return parceiro;
    }

    public Lancamento getLancamento() {
        return lancamento;
    }

    public Instant getCriadoEm() {
        return criadoEm;
    }
}

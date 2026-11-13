package br.com.infnet.nexobackend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "parceiros")
public class Parceiro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nome;

    @Column(nullable = false, unique = true, length = 20)
    private String documento;

    @Column(nullable = false, length = 150)
    private String email;

    @Column(length = 30)
    private String telefone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoParceiro tipo;

    @Column(nullable = false)
    private boolean ativo;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private Instant criadoEm;

    protected Parceiro() {
    }

    public Parceiro(String nome, String documento, String email, String telefone, TipoParceiro tipo) {
        this.ativo = true;
        atualizar(nome, documento, email, telefone, tipo, true);
    }

    public void atualizar(String nome, String documento, String email, String telefone,
                          TipoParceiro tipo, boolean ativo) {
        this.nome = nome;
        this.documento = documento;
        this.email = email;
        this.telefone = telefone;
        this.tipo = tipo;
        this.ativo = ativo;
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

    public String getNome() {
        return nome;
    }

    public String getDocumento() {
        return documento;
    }

    public String getEmail() {
        return email;
    }

    public String getTelefone() {
        return telefone;
    }

    public TipoParceiro getTipo() {
        return tipo;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public Instant getCriadoEm() {
        return criadoEm;
    }
}

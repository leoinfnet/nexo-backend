package br.com.infnet.nexobackend.dto;

import br.com.infnet.nexobackend.model.Parceiro;
import br.com.infnet.nexobackend.model.TipoParceiro;

import java.time.Instant;

public record ParceiroResponse(
        Long id,
        String nome,
        String documento,
        String email,
        String telefone,
        TipoParceiro tipo,
        boolean ativo,
        Instant criadoEm
) {
    public static ParceiroResponse from(Parceiro parceiro) {
        return new ParceiroResponse(
                parceiro.getId(),
                parceiro.getNome(),
                parceiro.getDocumento(),
                parceiro.getEmail(),
                parceiro.getTelefone(),
                parceiro.getTipo(),
                parceiro.isAtivo(),
                parceiro.getCriadoEm()
        );
    }
}

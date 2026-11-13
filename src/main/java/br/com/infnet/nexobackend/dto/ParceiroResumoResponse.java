package br.com.infnet.nexobackend.dto;

import br.com.infnet.nexobackend.model.Parceiro;
import br.com.infnet.nexobackend.model.TipoParceiro;

public record ParceiroResumoResponse(Long id, String nome, TipoParceiro tipo) {

    public static ParceiroResumoResponse from(Parceiro parceiro) {
        return new ParceiroResumoResponse(parceiro.getId(), parceiro.getNome(), parceiro.getTipo());
    }
}

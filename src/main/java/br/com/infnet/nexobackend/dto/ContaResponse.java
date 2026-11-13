package br.com.infnet.nexobackend.dto;

import br.com.infnet.nexobackend.model.Conta;
import br.com.infnet.nexobackend.model.RecorrenciaConta;
import br.com.infnet.nexobackend.model.StatusConta;
import br.com.infnet.nexobackend.model.TipoConta;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record ContaResponse(
        Long id,
        String descricao,
        BigDecimal valor,
        TipoConta tipo,
        String categoria,
        LocalDate vencimento,
        StatusConta status,
        LocalDate dataPagamento,
        RecorrenciaConta recorrencia,
        String observacao,
        ParceiroResumoResponse parceiro,
        Long lancamentoId,
        Instant criadoEm
) {
    public static ContaResponse from(Conta conta) {
        return new ContaResponse(
                conta.getId(),
                conta.getDescricao(),
                conta.getValor(),
                conta.getTipo(),
                conta.getCategoria(),
                conta.getVencimento(),
                conta.getStatusAtual(),
                conta.getDataPagamento(),
                conta.getRecorrencia(),
                conta.getObservacao(),
                ParceiroResumoResponse.from(conta.getParceiro()),
                conta.getLancamento() == null ? null : conta.getLancamento().getId(),
                conta.getCriadoEm()
        );
    }
}

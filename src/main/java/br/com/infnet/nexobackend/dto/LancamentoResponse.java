package br.com.infnet.nexobackend.dto;

import br.com.infnet.nexobackend.model.Lancamento;
import br.com.infnet.nexobackend.model.TipoLancamento;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record LancamentoResponse(
        Long id,
        String descricao,
        BigDecimal valor,
        TipoLancamento tipo,
        String categoria,
        LocalDate dataLancamento,
        String observacao,
        Instant criadoEm
) {
    public static LancamentoResponse from(Lancamento lancamento) {
        return new LancamentoResponse(
                lancamento.getId(),
                lancamento.getDescricao(),
                lancamento.getValor(),
                lancamento.getTipo(),
                lancamento.getCategoria(),
                lancamento.getDataLancamento(),
                lancamento.getObservacao(),
                lancamento.getCriadoEm()
        );
    }
}

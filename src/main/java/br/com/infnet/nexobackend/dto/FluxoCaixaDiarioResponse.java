package br.com.infnet.nexobackend.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record FluxoCaixaDiarioResponse(
        LocalDate data,
        BigDecimal receitas,
        BigDecimal despesas,
        BigDecimal saldo
) {
}

package br.com.infnet.nexobackend.dto;

import java.math.BigDecimal;

public record ResumoCategoriaResponse(
        String categoria,
        BigDecimal valorTotal,
        long quantidade
) {
}

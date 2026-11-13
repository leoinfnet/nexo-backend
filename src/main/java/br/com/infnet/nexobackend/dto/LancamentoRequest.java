package br.com.infnet.nexobackend.dto;

import br.com.infnet.nexobackend.model.TipoLancamento;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record LancamentoRequest(
        @NotBlank @Size(max = 200) String descricao,
        @NotNull @DecimalMin(value = "0.01") @Digits(integer = 17, fraction = 2) BigDecimal valor,
        @NotNull TipoLancamento tipo,
        @NotBlank @Size(max = 100) String categoria,
        @NotNull LocalDate dataLancamento,
        @Size(max = 1000) String observacao
) {
}

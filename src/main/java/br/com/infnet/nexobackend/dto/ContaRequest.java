package br.com.infnet.nexobackend.dto;

import br.com.infnet.nexobackend.model.RecorrenciaConta;
import br.com.infnet.nexobackend.model.TipoConta;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ContaRequest(
        @NotBlank @Size(max = 200) String descricao,
        @NotNull @DecimalMin("0.01") @Digits(integer = 17, fraction = 2) BigDecimal valor,
        @NotNull TipoConta tipo,
        @NotBlank @Size(max = 100) String categoria,
        @NotNull LocalDate vencimento,
        @NotNull RecorrenciaConta recorrencia,
        @Size(max = 1000) String observacao,
        @NotNull Long parceiroId
) {
}

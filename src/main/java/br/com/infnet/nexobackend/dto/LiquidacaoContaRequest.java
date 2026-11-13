package br.com.infnet.nexobackend.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record LiquidacaoContaRequest(@NotNull LocalDate dataPagamento) {
}

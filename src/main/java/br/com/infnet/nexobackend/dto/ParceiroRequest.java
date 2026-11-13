package br.com.infnet.nexobackend.dto;

import br.com.infnet.nexobackend.model.TipoParceiro;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ParceiroRequest(
        @NotBlank @Size(max = 150) String nome,
        @NotBlank @Size(max = 20) String documento,
        @NotBlank @Email @Size(max = 150) String email,
        @Size(max = 30) String telefone,
        @NotNull TipoParceiro tipo,
        Boolean ativo
) {
}

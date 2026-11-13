package br.com.infnet.nexobackend.controller;

import br.com.infnet.nexobackend.dto.ContaRequest;
import br.com.infnet.nexobackend.dto.ContaResponse;
import br.com.infnet.nexobackend.dto.LiquidacaoContaRequest;
import br.com.infnet.nexobackend.model.StatusConta;
import br.com.infnet.nexobackend.model.TipoConta;
import br.com.infnet.nexobackend.service.ContaService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;

import static org.springframework.data.domain.Sort.Direction.ASC;

@RestController
@RequestMapping("/api/v1/contas")
public class ContaController {

    private final ContaService service;

    public ContaController(ContaService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ContaResponse> criar(@Valid @RequestBody ContaRequest request) {
        ContaResponse response = service.criar(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping
    public Page<ContaResponse> listar(
            @RequestParam(required = false) TipoConta tipo,
            @RequestParam(required = false) StatusConta status,
            @RequestParam(required = false) Long parceiroId,
            @RequestParam(required = false) LocalDate vencimentoInicio,
            @RequestParam(required = false) LocalDate vencimentoFim,
            @PageableDefault(size = 20, sort = {"vencimento", "id"}, direction = ASC) Pageable pageable) {
        return service.listar(tipo, status, parceiroId, vencimentoInicio, vencimentoFim, pageable);
    }

    @GetMapping("/{id}")
    public ContaResponse buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @PutMapping("/{id}")
    public ContaResponse atualizar(@PathVariable Long id, @Valid @RequestBody ContaRequest request) {
        return service.atualizar(id, request);
    }

    @PostMapping("/{id}/liquidacao")
    public ContaResponse liquidar(@PathVariable Long id,
                                  @Valid @RequestBody LiquidacaoContaRequest request) {
        return service.liquidar(id, request);
    }

    @PostMapping("/{id}/cancelamento")
    public ContaResponse cancelar(@PathVariable Long id) {
        return service.cancelar(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}

package br.com.infnet.nexobackend.controller;

import br.com.infnet.nexobackend.dto.ParceiroRequest;
import br.com.infnet.nexobackend.dto.ParceiroResponse;
import br.com.infnet.nexobackend.model.TipoParceiro;
import br.com.infnet.nexobackend.service.ParceiroService;
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

@RestController
@RequestMapping("/api/v1/parceiros")
public class ParceiroController {

    private final ParceiroService service;

    public ParceiroController(ParceiroService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ParceiroResponse> criar(@Valid @RequestBody ParceiroRequest request) {
        ParceiroResponse response = service.criar(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping
    public Page<ParceiroResponse> listar(
            @RequestParam(required = false) TipoParceiro tipo,
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) Boolean ativo,
            @PageableDefault(size = 20, sort = "nome") Pageable pageable) {
        return service.listar(tipo, nome, ativo, pageable);
    }

    @GetMapping("/{id}")
    public ParceiroResponse buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @PutMapping("/{id}")
    public ParceiroResponse atualizar(@PathVariable Long id, @Valid @RequestBody ParceiroRequest request) {
        return service.atualizar(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}

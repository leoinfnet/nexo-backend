package br.com.infnet.nexobackend.controller;

import br.com.infnet.nexobackend.dto.FluxoCaixaDiarioResponse;
import br.com.infnet.nexobackend.dto.LancamentoResponse;
import br.com.infnet.nexobackend.dto.ResumoCategoriaResponse;
import br.com.infnet.nexobackend.dto.ResumoFinanceiroResponse;
import br.com.infnet.nexobackend.service.RelatorioFinanceiroService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/relatorios/financeiro")
public class RelatorioFinanceiroController {

    private final RelatorioFinanceiroService service;

    public RelatorioFinanceiroController(RelatorioFinanceiroService service) {
        this.service = service;
    }

    @GetMapping("/resumo")
    public ResumoFinanceiroResponse resumo(@RequestParam(required = false) LocalDate dataInicio,
                                           @RequestParam(required = false) LocalDate dataFim) {
        return service.resumo(dataInicio, dataFim);
    }

    @GetMapping("/despesas-por-categoria")
    public List<ResumoCategoriaResponse> despesasPorCategoria(
            @RequestParam(required = false) LocalDate dataInicio,
            @RequestParam(required = false) LocalDate dataFim) {
        return service.despesasPorCategoria(dataInicio, dataFim);
    }

    @GetMapping("/fluxo-diario")
    public List<FluxoCaixaDiarioResponse> fluxoDiario(
            @RequestParam(required = false) LocalDate dataInicio,
            @RequestParam(required = false) LocalDate dataFim) {
        return service.fluxoDiario(dataInicio, dataFim);
    }

    @GetMapping("/maiores-despesas")
    public List<LancamentoResponse> maioresDespesas(
            @RequestParam(required = false) LocalDate dataInicio,
            @RequestParam(required = false) LocalDate dataFim,
            @RequestParam(defaultValue = "10") int limite) {
        return service.maioresDespesas(dataInicio, dataFim, limite);
    }
}

package br.com.infnet.nexobackend.service;

import br.com.infnet.nexobackend.dto.FluxoCaixaDiarioResponse;
import br.com.infnet.nexobackend.dto.LancamentoResponse;
import br.com.infnet.nexobackend.dto.ResumoCategoriaResponse;
import br.com.infnet.nexobackend.dto.ResumoFinanceiroResponse;
import br.com.infnet.nexobackend.exception.RequisicaoInvalidaException;
import br.com.infnet.nexobackend.model.TipoLancamento;
import br.com.infnet.nexobackend.repository.LancamentoRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class RelatorioFinanceiroService {

    private static final BigDecimal CEM = BigDecimal.valueOf(100);
    private final LancamentoRepository repository;

    public RelatorioFinanceiroService(LancamentoRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public ResumoFinanceiroResponse resumo(LocalDate inicio, LocalDate fim) {
        Periodo periodo = periodo(inicio, fim);
        Map<TipoLancamento, BigDecimal> totais = totais(periodo.inicio(), periodo.fim());

        long dias = ChronoUnit.DAYS.between(periodo.inicio(), periodo.fim()) + 1;
        LocalDate fimAnterior = periodo.inicio().minusDays(1);
        LocalDate inicioAnterior = fimAnterior.minusDays(dias - 1);
        BigDecimal despesasAnteriores = totais(inicioAnterior, fimAnterior)
                .getOrDefault(TipoLancamento.DESPESA, BigDecimal.ZERO);
        BigDecimal despesas = totais.getOrDefault(TipoLancamento.DESPESA, BigDecimal.ZERO);
        BigDecimal variacao = despesasAnteriores.signum() == 0
                ? BigDecimal.ZERO
                : despesas.subtract(despesasAnteriores)
                .multiply(CEM)
                .divide(despesasAnteriores, 2, RoundingMode.HALF_UP);
        BigDecimal receitas = totais.getOrDefault(TipoLancamento.RECEITA, BigDecimal.ZERO);

        return new ResumoFinanceiroResponse(
                periodo.inicio(),
                periodo.fim(),
                receitas,
                despesas,
                receitas.subtract(despesas),
                despesasAnteriores,
                variacao
        );
    }

    @Transactional(readOnly = true)
    public List<ResumoCategoriaResponse> despesasPorCategoria(LocalDate inicio, LocalDate fim) {
        Periodo periodo = periodo(inicio, fim);
        return repository.resumirCategorias(TipoLancamento.DESPESA, periodo.inicio(), periodo.fim())
                .stream()
                .map(item -> new ResumoCategoriaResponse(
                        (String) item[0],
                        (BigDecimal) item[1],
                        (Long) item[2]
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<FluxoCaixaDiarioResponse> fluxoDiario(LocalDate inicio, LocalDate fim) {
        Periodo periodo = periodo(inicio, fim);
        Map<LocalDate, TotaisDia> dias = new LinkedHashMap<>();
        repository.resumirFluxoDiario(periodo.inicio(), periodo.fim()).forEach(item -> {
            LocalDate data = (LocalDate) item[0];
            TipoLancamento tipo = (TipoLancamento) item[1];
            BigDecimal valor = (BigDecimal) item[2];
            dias.computeIfAbsent(data, ignored -> new TotaisDia()).adicionar(tipo, valor);
        });
        return dias.entrySet().stream()
                .map(item -> new FluxoCaixaDiarioResponse(
                        item.getKey(),
                        item.getValue().receitas,
                        item.getValue().despesas,
                        item.getValue().receitas.subtract(item.getValue().despesas)
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<LancamentoResponse> maioresDespesas(LocalDate inicio, LocalDate fim, int limite) {
        Periodo periodo = periodo(inicio, fim);
        if (limite < 1 || limite > 100) {
            throw new RequisicaoInvalidaException("O limite deve estar entre 1 e 100.");
        }
        return repository.findByTipoAndDataLancamentoBetween(
                        TipoLancamento.DESPESA,
                        periodo.inicio(),
                        periodo.fim(),
                        PageRequest.of(0, limite, Sort.by(Sort.Direction.DESC, "valor"))
                )
                .map(LancamentoResponse::from)
                .getContent();
    }

    private Map<TipoLancamento, BigDecimal> totais(LocalDate inicio, LocalDate fim) {
        Map<TipoLancamento, BigDecimal> totais = new LinkedHashMap<>();
        repository.somarPorTipoNoPeriodo(inicio, fim)
                .forEach(item -> totais.put((TipoLancamento) item[0], (BigDecimal) item[1]));
        return totais;
    }

    private Periodo periodo(LocalDate inicio, LocalDate fim) {
        LocalDate dataFim = fim == null ? LocalDate.now() : fim;
        LocalDate dataInicio = inicio == null ? dataFim.minusDays(29) : inicio;
        if (dataInicio.isAfter(dataFim)) {
            throw new RequisicaoInvalidaException("A data inicial não pode ser posterior à data final.");
        }
        return new Periodo(dataInicio, dataFim);
    }

    private record Periodo(LocalDate inicio, LocalDate fim) {
    }

    private static final class TotaisDia {
        private BigDecimal receitas = BigDecimal.ZERO;
        private BigDecimal despesas = BigDecimal.ZERO;

        void adicionar(TipoLancamento tipo, BigDecimal valor) {
            if (tipo == TipoLancamento.RECEITA) {
                receitas = receitas.add(valor);
            } else {
                despesas = despesas.add(valor);
            }
        }
    }
}

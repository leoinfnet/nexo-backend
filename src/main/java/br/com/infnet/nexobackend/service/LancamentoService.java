package br.com.infnet.nexobackend.service;

import br.com.infnet.nexobackend.dto.LancamentoRequest;
import br.com.infnet.nexobackend.dto.LancamentoResponse;
import br.com.infnet.nexobackend.exception.RecursoNaoEncontradoException;
import br.com.infnet.nexobackend.model.Lancamento;
import br.com.infnet.nexobackend.model.TipoLancamento;
import br.com.infnet.nexobackend.repository.LancamentoRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;

@Service
public class LancamentoService {

    private final LancamentoRepository repository;

    public LancamentoService(LancamentoRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public LancamentoResponse criar(LancamentoRequest request) {
        Lancamento lancamento = new Lancamento(
                request.descricao(),
                request.valor(),
                request.tipo(),
                request.categoria(),
                request.dataLancamento(),
                request.observacao()
        );
        return LancamentoResponse.from(repository.saveAndFlush(lancamento));
    }

    @Transactional(readOnly = true)
    public Page<LancamentoResponse> listar(TipoLancamento tipo,
                                           String categoria,
                                           LocalDate dataInicio,
                                           LocalDate dataFim,
                                           BigDecimal valorMinimo,
                                           BigDecimal valorMaximo,
                                           Pageable pageable) {
        Specification<Lancamento> filtros = (root, query, builder) -> {
            var predicates = new ArrayList<Predicate>();
            if (tipo != null) {
                predicates.add(builder.equal(root.get("tipo"), tipo));
            }
            if (categoria != null && !categoria.isBlank()) {
                predicates.add(builder.equal(builder.lower(root.get("categoria")), categoria.trim().toLowerCase()));
            }
            if (dataInicio != null) {
                predicates.add(builder.greaterThanOrEqualTo(root.get("dataLancamento"), dataInicio));
            }
            if (dataFim != null) {
                predicates.add(builder.lessThanOrEqualTo(root.get("dataLancamento"), dataFim));
            }
            if (valorMinimo != null) {
                predicates.add(builder.greaterThanOrEqualTo(root.get("valor"), valorMinimo));
            }
            if (valorMaximo != null) {
                predicates.add(builder.lessThanOrEqualTo(root.get("valor"), valorMaximo));
            }
            return builder.and(predicates.toArray(Predicate[]::new));
        };
        return repository.findAll(filtros, pageable).map(LancamentoResponse::from);
    }

    @Transactional(readOnly = true)
    public LancamentoResponse buscarPorId(Long id) {
        return LancamentoResponse.from(buscarEntidade(id));
    }

    @Transactional
    public LancamentoResponse atualizar(Long id, LancamentoRequest request) {
        Lancamento lancamento = buscarEntidade(id);
        lancamento.atualizar(
                request.descricao(),
                request.valor(),
                request.tipo(),
                request.categoria(),
                request.dataLancamento(),
                request.observacao()
        );
        return LancamentoResponse.from(repository.saveAndFlush(lancamento));
    }

    @Transactional
    public void excluir(Long id) {
        repository.delete(buscarEntidade(id));
    }

    private Lancamento buscarEntidade(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Lançamento não encontrado: " + id));
    }
}

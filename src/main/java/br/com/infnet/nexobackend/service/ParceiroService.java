package br.com.infnet.nexobackend.service;

import br.com.infnet.nexobackend.dto.ParceiroRequest;
import br.com.infnet.nexobackend.dto.ParceiroResponse;
import br.com.infnet.nexobackend.exception.ConflitoException;
import br.com.infnet.nexobackend.exception.RecursoNaoEncontradoException;
import br.com.infnet.nexobackend.model.Parceiro;
import br.com.infnet.nexobackend.model.TipoParceiro;
import br.com.infnet.nexobackend.repository.ContaRepository;
import br.com.infnet.nexobackend.repository.ParceiroRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Service
public class ParceiroService {

    private final ParceiroRepository repository;
    private final ContaRepository contaRepository;

    public ParceiroService(ParceiroRepository repository, ContaRepository contaRepository) {
        this.repository = repository;
        this.contaRepository = contaRepository;
    }

    @Transactional
    public ParceiroResponse criar(ParceiroRequest request) {
        validarDocumentoNovo(request.documento());
        Parceiro parceiro = new Parceiro(
                request.nome(), request.documento(), request.email(), request.telefone(), request.tipo()
        );
        return ParceiroResponse.from(repository.saveAndFlush(parceiro));
    }

    @Transactional(readOnly = true)
    public Page<ParceiroResponse> listar(TipoParceiro tipo, String nome, Boolean ativo, Pageable pageable) {
        Specification<Parceiro> filtros = (root, query, builder) -> {
            var predicates = new ArrayList<Predicate>();
            if (tipo != null) {
                predicates.add(tipo == TipoParceiro.CLIENTE
                        ? root.get("tipo").in(TipoParceiro.CLIENTE, TipoParceiro.AMBOS)
                        : tipo == TipoParceiro.FORNECEDOR
                        ? root.get("tipo").in(TipoParceiro.FORNECEDOR, TipoParceiro.AMBOS)
                        : builder.equal(root.get("tipo"), TipoParceiro.AMBOS));
            }
            if (nome != null && !nome.isBlank()) {
                predicates.add(builder.like(builder.lower(root.get("nome")), "%" + nome.trim().toLowerCase() + "%"));
            }
            if (ativo != null) {
                predicates.add(builder.equal(root.get("ativo"), ativo));
            }
            return builder.and(predicates.toArray(Predicate[]::new));
        };
        return repository.findAll(filtros, pageable).map(ParceiroResponse::from);
    }

    @Transactional(readOnly = true)
    public ParceiroResponse buscarPorId(Long id) {
        return ParceiroResponse.from(buscarEntidade(id));
    }

    @Transactional
    public ParceiroResponse atualizar(Long id, ParceiroRequest request) {
        Parceiro parceiro = buscarEntidade(id);
        if (repository.existsByDocumentoIgnoreCaseAndIdNot(request.documento(), id)) {
            throw new ConflitoException("Já existe outro parceiro com o documento informado.");
        }
        parceiro.atualizar(
                request.nome(),
                request.documento(),
                request.email(),
                request.telefone(),
                request.tipo(),
                request.ativo() == null ? parceiro.isAtivo() : request.ativo()
        );
        return ParceiroResponse.from(repository.saveAndFlush(parceiro));
    }

    @Transactional
    public void excluir(Long id) {
        Parceiro parceiro = buscarEntidade(id);
        if (contaRepository.existsByParceiroId(id)) {
            throw new ConflitoException("O parceiro possui contas vinculadas e não pode ser excluído.");
        }
        repository.delete(parceiro);
    }

    @Transactional(readOnly = true)
    public Parceiro buscarEntidade(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Parceiro não encontrado: " + id));
    }

    private void validarDocumentoNovo(String documento) {
        if (repository.existsByDocumentoIgnoreCase(documento)) {
            throw new ConflitoException("Já existe um parceiro com o documento informado.");
        }
    }
}

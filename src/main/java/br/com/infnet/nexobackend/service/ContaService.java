package br.com.infnet.nexobackend.service;

import br.com.infnet.nexobackend.dto.ContaRequest;
import br.com.infnet.nexobackend.dto.ContaResponse;
import br.com.infnet.nexobackend.dto.LiquidacaoContaRequest;
import br.com.infnet.nexobackend.exception.ConflitoException;
import br.com.infnet.nexobackend.exception.RecursoNaoEncontradoException;
import br.com.infnet.nexobackend.exception.RequisicaoInvalidaException;
import br.com.infnet.nexobackend.model.Conta;
import br.com.infnet.nexobackend.model.Lancamento;
import br.com.infnet.nexobackend.model.Parceiro;
import br.com.infnet.nexobackend.model.StatusConta;
import br.com.infnet.nexobackend.model.TipoConta;
import br.com.infnet.nexobackend.model.TipoLancamento;
import br.com.infnet.nexobackend.model.TipoParceiro;
import br.com.infnet.nexobackend.repository.ContaRepository;
import br.com.infnet.nexobackend.repository.LancamentoRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;

@Service
public class ContaService {

    private final ContaRepository repository;
    private final LancamentoRepository lancamentoRepository;
    private final ParceiroService parceiroService;

    public ContaService(ContaRepository repository,
                        LancamentoRepository lancamentoRepository,
                        ParceiroService parceiroService) {
        this.repository = repository;
        this.lancamentoRepository = lancamentoRepository;
        this.parceiroService = parceiroService;
    }

    @Transactional
    public ContaResponse criar(ContaRequest request) {
        Parceiro parceiro = parceiroService.buscarEntidade(request.parceiroId());
        validarParceiro(request.tipo(), parceiro);
        Conta conta = new Conta(
                request.descricao(), request.valor(), request.tipo(), request.categoria(), request.vencimento(),
                request.recorrencia(), request.observacao(), parceiro
        );
        return ContaResponse.from(repository.saveAndFlush(conta));
    }

    @Transactional(readOnly = true)
    public Page<ContaResponse> listar(TipoConta tipo,
                                      StatusConta status,
                                      Long parceiroId,
                                      LocalDate vencimentoInicio,
                                      LocalDate vencimentoFim,
                                      Pageable pageable) {
        Specification<Conta> filtros = (root, query, builder) -> {
            var predicates = new ArrayList<Predicate>();
            if (tipo != null) {
                predicates.add(builder.equal(root.get("tipo"), tipo));
            }
            if (status == StatusConta.VENCIDA) {
                predicates.add(builder.equal(root.get("status"), StatusConta.PENDENTE));
                predicates.add(builder.lessThan(root.get("vencimento"), LocalDate.now()));
            } else if (status == StatusConta.PENDENTE) {
                predicates.add(builder.equal(root.get("status"), StatusConta.PENDENTE));
                predicates.add(builder.greaterThanOrEqualTo(root.get("vencimento"), LocalDate.now()));
            } else if (status != null) {
                predicates.add(builder.equal(root.get("status"), status));
            }
            if (parceiroId != null) {
                predicates.add(builder.equal(root.get("parceiro").get("id"), parceiroId));
            }
            if (vencimentoInicio != null) {
                predicates.add(builder.greaterThanOrEqualTo(root.get("vencimento"), vencimentoInicio));
            }
            if (vencimentoFim != null) {
                predicates.add(builder.lessThanOrEqualTo(root.get("vencimento"), vencimentoFim));
            }
            return builder.and(predicates.toArray(Predicate[]::new));
        };
        return repository.findAll(filtros, pageable).map(ContaResponse::from);
    }

    @Transactional(readOnly = true)
    public ContaResponse buscarPorId(Long id) {
        return ContaResponse.from(buscarEntidade(id));
    }

    @Transactional
    public ContaResponse atualizar(Long id, ContaRequest request) {
        Conta conta = buscarEntidade(id);
        validarPendente(conta, "Somente contas pendentes podem ser alteradas.");
        Parceiro parceiro = parceiroService.buscarEntidade(request.parceiroId());
        validarParceiro(request.tipo(), parceiro);
        conta.atualizar(
                request.descricao(), request.valor(), request.tipo(), request.categoria(), request.vencimento(),
                request.recorrencia(), request.observacao(), parceiro
        );
        return ContaResponse.from(repository.saveAndFlush(conta));
    }

    @Transactional
    public ContaResponse liquidar(Long id, LiquidacaoContaRequest request) {
        Conta conta = buscarEntidade(id);
        validarPendente(conta, "A conta já foi liquidada ou cancelada.");
        if (request.dataPagamento().isAfter(LocalDate.now())) {
            throw new RequisicaoInvalidaException("A data de pagamento não pode estar no futuro.");
        }
        TipoLancamento tipoLancamento = conta.getTipo() == TipoConta.PAGAR
                ? TipoLancamento.DESPESA
                : TipoLancamento.RECEITA;
        Lancamento lancamento = new Lancamento(
                conta.getDescricao(),
                conta.getValor(),
                tipoLancamento,
                conta.getCategoria(),
                request.dataPagamento(),
                "Liquidação da conta " + conta.getId() + " - " + conta.getParceiro().getNome()
        );
        lancamentoRepository.saveAndFlush(lancamento);
        conta.liquidar(request.dataPagamento(), lancamento);
        return ContaResponse.from(repository.saveAndFlush(conta));
    }

    @Transactional
    public ContaResponse cancelar(Long id) {
        Conta conta = buscarEntidade(id);
        validarPendente(conta, "Somente contas pendentes podem ser canceladas.");
        conta.cancelar();
        return ContaResponse.from(repository.saveAndFlush(conta));
    }

    @Transactional
    public void excluir(Long id) {
        Conta conta = buscarEntidade(id);
        if (conta.getStatus() == StatusConta.PAGA) {
            throw new ConflitoException("Uma conta liquidada não pode ser excluída.");
        }
        repository.delete(conta);
    }

    private Conta buscarEntidade(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Conta não encontrada: " + id));
    }

    private void validarPendente(Conta conta, String mensagem) {
        if (conta.getStatus() != StatusConta.PENDENTE) {
            throw new ConflitoException(mensagem);
        }
    }

    private void validarParceiro(TipoConta tipoConta, Parceiro parceiro) {
        if (!parceiro.isAtivo()) {
            throw new RequisicaoInvalidaException("O parceiro informado está inativo.");
        }
        boolean compativel = tipoConta == TipoConta.PAGAR
                ? parceiro.getTipo() == TipoParceiro.FORNECEDOR || parceiro.getTipo() == TipoParceiro.AMBOS
                : parceiro.getTipo() == TipoParceiro.CLIENTE || parceiro.getTipo() == TipoParceiro.AMBOS;
        if (!compativel) {
            throw new RequisicaoInvalidaException("O tipo do parceiro não é compatível com o tipo da conta.");
        }
    }
}

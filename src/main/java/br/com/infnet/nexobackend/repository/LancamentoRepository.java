package br.com.infnet.nexobackend.repository;

import br.com.infnet.nexobackend.model.Lancamento;
import br.com.infnet.nexobackend.model.TipoLancamento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface LancamentoRepository extends JpaRepository<Lancamento, Long>, JpaSpecificationExecutor<Lancamento> {

    @Query("""
            select l.tipo, sum(l.valor)
            from Lancamento l
            where l.dataLancamento between :inicio and :fim
            group by l.tipo
            """)
    List<Object[]> somarPorTipoNoPeriodo(@Param("inicio") LocalDate inicio, @Param("fim") LocalDate fim);

    @Query("""
            select l.categoria, sum(l.valor), count(l)
            from Lancamento l
            where l.tipo = :tipo and l.dataLancamento between :inicio and :fim
            group by l.categoria
            order by sum(l.valor) desc
            """)
    List<Object[]> resumirCategorias(@Param("tipo") TipoLancamento tipo,
                                     @Param("inicio") LocalDate inicio,
                                     @Param("fim") LocalDate fim);

    @Query("""
            select l.dataLancamento, l.tipo, sum(l.valor)
            from Lancamento l
            where l.dataLancamento between :inicio and :fim
            group by l.dataLancamento, l.tipo
            order by l.dataLancamento
            """)
    List<Object[]> resumirFluxoDiario(@Param("inicio") LocalDate inicio, @Param("fim") LocalDate fim);

    Page<Lancamento> findByTipoAndDataLancamentoBetween(
            TipoLancamento tipo,
            LocalDate inicio,
            LocalDate fim,
            Pageable pageable
    );
}

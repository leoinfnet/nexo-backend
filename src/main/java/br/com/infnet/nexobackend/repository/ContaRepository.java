package br.com.infnet.nexobackend.repository;

import br.com.infnet.nexobackend.model.Conta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ContaRepository extends JpaRepository<Conta, Long>, JpaSpecificationExecutor<Conta> {

    boolean existsByParceiroId(Long parceiroId);
}

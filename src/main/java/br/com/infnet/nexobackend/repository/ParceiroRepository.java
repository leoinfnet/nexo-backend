package br.com.infnet.nexobackend.repository;

import br.com.infnet.nexobackend.model.Parceiro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ParceiroRepository extends JpaRepository<Parceiro, Long>, JpaSpecificationExecutor<Parceiro> {

    boolean existsByDocumentoIgnoreCase(String documento);

    boolean existsByDocumentoIgnoreCaseAndIdNot(String documento, Long id);
}

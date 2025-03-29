package br.com.banco.dao;

import br.com.banco.model.Conta;
import br.com.banco.model.Transferencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface ContaDao extends JpaRepository<Conta, Long> {

    Transferencia trasferir(Long idOrigem, Long idDestino, BigDecimal valor);

    Optional<Long> buscarIdConta(String chavePix);

    Optional<Long> buscarIdConta(int agencia, int numero);

}

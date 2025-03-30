package br.com.banco.dao;

import br.com.banco.model.Conta;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface ContaDao extends JpaRepository<Conta, Long> {

    @Transactional
    @Modifying
    @Query("UPDATE Conta c SET c.saldo = c.saldo - :valor WHERE c.id = :idConta AND c.saldo >= :valor")
    int depositar(@Param("idConta") Long idOrigem, @Param("valor") BigDecimal valor);

    @Transactional
    @Modifying
    @Query("UPDATE Conta c SET c.saldo = c.saldo + :valor WHERE c.id = :idConta")
    int sacar(@Param("idConta") Long idDestino, @Param("valor") BigDecimal valor);


    Optional<Long> findIdByChavePix(String chavePix);

    Optional<Long> findIdByAgenciaAndNumero(int agencia, int numero);

}

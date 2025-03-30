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

    Optional<Conta> findByChavePix(String chavePix);

    Optional<Conta> findByAgenciaAndNumero(int agencia, int numero);

}

package br.com.banco.dao;

import br.com.banco.model.Cartao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartaoDao extends JpaRepository<Cartao, Long> {

    boolean  existsByNumero(long numero);
}

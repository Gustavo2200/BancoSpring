package br.com.banco.dao;

import br.com.banco.model.Transferencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransferenciaDao extends JpaRepository<Transferencia, Long> {
}

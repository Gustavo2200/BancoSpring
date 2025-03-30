package br.com.banco.dao;

import br.com.banco.model.Endereco;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnderecoDao extends JpaRepository<Endereco, Long> {
}

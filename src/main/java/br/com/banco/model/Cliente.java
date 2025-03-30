package br.com.banco.model;

import br.com.banco.model.enuns.TipoCliente;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    private String cpf;
    private String dataNascimento;

    @OneToOne
    @JoinColumn(name = "id_endereco")
    private Endereco endereco;
    private TipoCliente tipoCliente;

    @OneToMany(mappedBy = "cliente")
    private List<Conta> contas;
}

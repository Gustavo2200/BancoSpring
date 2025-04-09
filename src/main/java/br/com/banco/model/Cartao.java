package br.com.banco.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE) // Todos na mesma tabela
@DiscriminatorColumn(name = "tipo_cartao", discriminatorType = DiscriminatorType.STRING)
public class Cartao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private long numero;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "id_conta")
    private Conta conta;

    private int cvv;
    private int senha;

    private boolean ativo;

}

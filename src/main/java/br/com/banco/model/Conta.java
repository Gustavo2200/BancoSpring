package br.com.banco.model;

import br.com.banco.model.enuns.TipoConta;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
public class Conta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int numero;
    private BigDecimal saldo;
    private int agencia;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "id_cliente")
    private Cliente cliente;
    private int senha;
    private TipoConta tipoConta;
    private String chavePix;

    @OneToMany
    @JsonManagedReference
    private List<Cartao> cartoes;
}

package br.com.banco.model;

import br.com.banco.model.enuns.TipoConta;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

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
    @JoinColumn(name = "id_cliente")
    private Cliente cliente;
    private int senha;
    private TipoConta tipoConta;
    private String chavePix;
}

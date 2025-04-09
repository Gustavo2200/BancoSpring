package br.com.banco.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@DiscriminatorValue("DEBITO")
public class CartaoDebito extends Cartao {

    private BigDecimal limiteDiario;
}

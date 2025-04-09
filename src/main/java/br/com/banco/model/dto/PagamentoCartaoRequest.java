package br.com.banco.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PagamentoCartaoRequest {

    private BigDecimal valor;
    private int senha;
}

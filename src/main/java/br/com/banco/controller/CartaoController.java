package br.com.banco.controller;

import br.com.banco.model.Cartao;
import br.com.banco.model.CartaoCredito;
import br.com.banco.model.CartaoDebito;
import br.com.banco.model.dto.PagamentoCartaoRequest;
import br.com.banco.service.CartaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/cartoes")
public class CartaoController {

    @Autowired
    private CartaoService service;

    @PostMapping("/criar-cartao-credito")
    public ResponseEntity<?> criarCartao(@RequestBody CartaoCredito cartao) {
        service.criarCartao(cartao);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
    @PostMapping("/criar-cartao-debito")
    public ResponseEntity<?> criarCartao(@RequestBody CartaoDebito cartao) {
        service.criarCartao(cartao);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/buscar/{id}")
    public ResponseEntity<Cartao> buscarCartao(@PathVariable Long id) {
        return new ResponseEntity<>(service.buscarCartao(id), HttpStatus.OK);
    }

    @PostMapping("/{id}/pagamento")
    public ResponseEntity<?> realizarPagamento(@PathVariable Long id, @RequestBody PagamentoCartaoRequest pagamento) {
        service.realizarPagamento(id, pagamento);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/{id}/limite-cartao")
    public ResponseEntity<?> atualizarLimiteCartao(@PathVariable Long id, @RequestBody BigDecimal valor) {
        service.atualizarLimiteCartao(id, valor);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/ativar-desativar/{id}")
    public ResponseEntity<?> ativarDesativarCartao(@PathVariable Long id) {
        service.ativarDesativarCartao(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/{id}/alterar-senha")
    public ResponseEntity<?> alterarSenha(@PathVariable Long id, @RequestBody int senha) {
        service.alterarSenha(id, senha);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/{id}/fatura")
    public ResponseEntity<?> consultarFatura(@PathVariable Long id) {
        return new ResponseEntity<>(service.consultarFatura(id), HttpStatus.OK);
    }

    @PostMapping("/{id}/pagar-fatura")
    public ResponseEntity<?> pagarFatura(@PathVariable Long id) {
        service.pagarFatura(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/{id}/alterar-limite")
    public ResponseEntity<?> alterarLimite(@PathVariable Long id, @RequestBody BigDecimal valor) {
        service.atualizarLimiteDiario(id, valor);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}

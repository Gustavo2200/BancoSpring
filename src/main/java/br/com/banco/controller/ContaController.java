package br.com.banco.controller;

import br.com.banco.model.Conta;
import br.com.banco.model.dto.TrasferenciaPixRequest;
import br.com.banco.model.dto.TrasferenciaTedRequest;
import br.com.banco.service.ContaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/contas")
public class ContaController {

    @Autowired
    private ContaService service;

    @PostMapping
    public ResponseEntity<?> criar(@RequestBody Conta conta) {
        service.criarConta(conta);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping("/trasferencia-pix")
    public ResponseEntity<?> transferir(@RequestBody TrasferenciaPixRequest request) {
        return new ResponseEntity<>(service.transferenciaPix(request), HttpStatus.OK);
    }

    @PostMapping("/trasferencia-ted")
    public ResponseEntity<?> transferir(@RequestBody TrasferenciaTedRequest request) {
        return new ResponseEntity<>(service.transferenciaTed(request), HttpStatus.OK);
    }

    @PutMapping("/{id}/deposito")
    public ResponseEntity<?> depositar(@PathVariable Long id, @RequestBody BigDecimal valor) {
        service.depositar(id, valor);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}

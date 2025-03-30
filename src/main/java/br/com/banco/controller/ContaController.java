package br.com.banco.controller;

import br.com.banco.model.Conta;
import br.com.banco.service.ContaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/conta")
public class ContaController {

    @Autowired
    private ContaService service;

    @PostMapping("/criar")
    public ResponseEntity<?> criar(@RequestBody Conta conta) {
        service.criarConta(conta);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

}

package br.com.banco.controller;

import br.com.banco.model.Cliente;
import br.com.banco.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cliente")
public class ClienteController {

    @Autowired
    private ClienteService service;

    @PostMapping("/cadastrar")
    public ResponseEntity<?> cadastrar(@RequestBody Cliente cliente){
        service.cadastrar(cliente);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}

package br.com.banco.controller;

import br.com.banco.model.Cliente;
import br.com.banco.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clientes")
public class ClienteController {

    @Autowired
    private ClienteService service;

    @PostMapping
    public ResponseEntity<?> cadastrar(@RequestBody Cliente cliente){
        service.cadastrar(cliente);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cliente> buscarClientePorId(@PathVariable Long id){
        return new ResponseEntity<>(service.buscarPorId(id), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarCliente(@PathVariable Long id, @RequestBody Cliente cliente){
        service.atualizarCliente(id, cliente);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> apagarClientePorId(@PathVariable Long id){
        service.apagarClientePorId(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    @GetMapping
    public ResponseEntity<List<Cliente>> listarClientes(){
        return new ResponseEntity<>(service.listarClientes(), HttpStatus.OK);
    }
}

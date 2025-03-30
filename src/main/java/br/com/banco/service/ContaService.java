package br.com.banco.service;

import br.com.banco.dao.ContaDao;
import br.com.banco.dao.TransferenciaDao;
import br.com.banco.model.Conta;
import br.com.banco.model.Transferencia;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class ContaService {

    @Autowired
    private ContaDao contaDao;

    @Autowired
    private TransferenciaDao transferenciaDao;

    public void criarConta(Conta conta) {
        verificarContaExiste(conta);
        contaDao.save(conta);
    }
    @Transactional
    public Transferencia transferenciaPix(String chavePix, Long idContaOrigem, BigDecimal valor){
        varificarValor(valor);
        Optional<Conta> contaOrigemOptional = buscarConta(idContaOrigem);
        if(contaOrigemOptional.isPresent()) {
            verificarSaldo(contaOrigemOptional.get().getSaldo(), valor);

            Optional<Conta> contaDestinoOptional = buscarConta(buscarIdConta(chavePix));
            if(contaDestinoOptional.isPresent()) {
                Long idContaDestino = contaDestinoOptional.get().getId();

                sacar(contaOrigemOptional.get().getId(), valor);
                depositar(contaDestinoOptional.get().getId(), valor);

                return registrarTransferencia(idContaOrigem, contaDestinoOptional.get().getId(), valor);
            }
            throw new RuntimeException("ContaDestino não encontrada");
        }
        throw new RuntimeException("ContaOrigem não encontrada");
    }
    @Transactional
    public Transferencia transferenciaTed(int agencia, int numero, Long idContaOrigem, BigDecimal valor){
        varificarValor(valor);
        Optional<Conta> contaOrigemOptional = buscarConta(idContaOrigem);
        if(contaOrigemOptional.isPresent()) {
            verificarSaldo(contaOrigemOptional.get().getSaldo(), valor);

            Optional<Conta> contaDestinoOptional = buscarConta(buscarIdConta(agencia, numero));
            if(contaDestinoOptional.isPresent()) {

                sacar(contaOrigemOptional.get().getId(), valor);
                depositar(contaDestinoOptional.get().getId(), valor);

                return registrarTransferencia(idContaOrigem, contaDestinoOptional.get().getId(), valor);
            }
            throw new RuntimeException("ContaDestino não encontrada");
        }
        throw new RuntimeException("ContaOrigem não encontrada");
    }

    public void fecharConta(Long id) {
        Optional<Conta> conta = buscarConta(id);
        if (conta.isPresent()) {
            verificarContaExiste(conta.get());
            if(conta.get().getSaldo().compareTo(BigDecimal.ZERO) == 0) {
                contaDao.deleteById(id);
            }
            else{
                throw new RuntimeException("Conta com saldo não pode ser fechada");
            }
        }
    }
    public BigDecimal exibirSldo(Long id) {
        Optional<Conta> conta = buscarConta(id);
        if (conta.isPresent()) {
            return conta.get().getSaldo();
        }
        throw new RuntimeException("Conta não encontrada");
    }

    public  void depositar(Long id, BigDecimal valor) {
        varificarValor(valor);
        Optional<Conta> contaOptional = buscarConta(id);
        if (contaOptional.isPresent()) {
            contaDao.depositar(id, valor);
        }
    }

    public void sacar(Long id, BigDecimal valor) {
        varificarValor(valor);
        Optional<Conta> contaOptional = buscarConta(id);
        if (contaOptional.isPresent()) {
            Conta conta = contaOptional.get();
            verificarSaldo(conta.getSaldo(), valor);;
            contaDao.sacar(id, valor);
        }
    }

    private Transferencia registrarTransferencia(Long idContaOrigem, Long idContaDestino, BigDecimal valor) {
        Transferencia transferencia = new Transferencia();
        transferencia.setIdContaOrigem(idContaOrigem);
        transferencia.setIdContaDestino(idContaDestino);
        transferencia.setValor(valor);
        transferencia.setDataTransferencia(LocalDateTime.now());
        transferenciaDao.save(transferencia);
        return transferencia;
    }

    private Optional<Conta> buscarConta(Long id) {

        return contaDao.findById(id);

    }
    private Long buscarIdConta(String chavePix) {
        Optional<Long> idConta = contaDao.findIdByChavePix(chavePix);
        if(idConta.isPresent()){
            return idConta.get();
        }
        throw new RuntimeException("Conta não encontrada");
    }
    private Long buscarIdConta(int agencia, int numero) {
        Optional<Long> idConta = contaDao.findIdByAgenciaAndNumero(agencia, numero);
        if(idConta.isPresent()){
            return idConta.get();
        }
        throw new RuntimeException("Conta não encontrada");
    }
    private void verificarContaExiste(Conta conta) {

        Optional<Long> idConta = contaDao.findIdByAgenciaAndNumero(conta.getAgencia(), conta.getNumero());

        if (idConta.isPresent()) {
            throw new RuntimeException("Conta já cadastrada");
        }

    }
    private void varificarValor(BigDecimal valor){
        if(valor.compareTo(BigDecimal.ZERO) < 0){
            throw new RuntimeException("Valor não pode ser negativo");
        }
    }

    private void verificarSaldo(BigDecimal saldo, BigDecimal valor){
        if(saldo.compareTo(valor) < 0){
            throw new RuntimeException("Saldo insuficiente");
        }
    }

}

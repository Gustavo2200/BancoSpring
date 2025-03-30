package br.com.banco.service;

import br.com.banco.dao.ContaDao;
import br.com.banco.dao.TransferenciaDao;
import br.com.banco.model.Conta;
import br.com.banco.model.Transferencia;
import br.com.banco.model.dto.TrasferenciaPixRequest;
import br.com.banco.model.dto.TrasferenciaTedRequest;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
public class ContaService {

    @Autowired
    private ContaDao contaDao;

    @Autowired
    private TransferenciaDao transferenciaDao;

    private Random random = new Random();

    public void criarConta(Conta conta) {
        verificarContaExiste(conta);
        popularDadosConta(conta);
        contaDao.save(conta);
    }

    private void popularDadosConta(Conta conta) {
        conta.setSaldo(BigDecimal.ZERO);
        conta.setNumero(random.nextInt(900000) + 100000);
        conta.setAgencia(1221);
    }

    @Transactional
    public Transferencia transferenciaPix(TrasferenciaPixRequest request){
        varificarValor(request.getValor());
        Optional<Conta> contaOrigemOptional = buscarConta(request.getIdContaOrigem());
        if(contaOrigemOptional.isPresent()) {
            verificarSaldo(contaOrigemOptional.get().getSaldo(), request.getValor());

            Optional<Conta> contaDestinoOptional = buscarConta(buscarIdConta(request.getChavePix()));
            if(contaDestinoOptional.isPresent()) {

                if(contaOrigemOptional.get().getSenha() != request.getSenha()){
                    throw new RuntimeException("Senha incorreta");
                }
                sacar(contaOrigemOptional.get().getId(), request.getValor());
                depositar(contaDestinoOptional.get().getId(), request.getValor());

                return registrarTransferencia(request.getIdContaOrigem(), contaDestinoOptional.get().getId(), request.getValor());
            }
            throw new RuntimeException("ContaDestino não encontrada");
        }
        throw new RuntimeException("ContaOrigem não encontrada");
    }
    @Transactional
    public Transferencia transferenciaTed(TrasferenciaTedRequest request){
        varificarValor(request.getValor());
        Optional<Conta> contaOrigemOptional = buscarConta(request.getIdOrigem());
        if(contaOrigemOptional.isPresent()) {
            verificarSaldo(contaOrigemOptional.get().getSaldo(), request.getValor());

            Optional<Conta> contaDestinoOptional = buscarConta(buscarIdConta(request.getAgenciaDestino(), request.getNumeroContaDestino()));
            if(contaDestinoOptional.isPresent()) {
                if(contaOrigemOptional.get().getSenha() != request.getSenha()){
                    throw new RuntimeException("Senha incorreta");
                }

                sacar(contaOrigemOptional.get().getId(), request.getValor());
                depositar(contaDestinoOptional.get().getId(), request.getValor());

                return registrarTransferencia(request.getIdOrigem(), contaDestinoOptional.get().getId(), request.getValor());
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
            Conta conta = contaOptional.get();
            conta.setSaldo(conta.getSaldo().add(valor));
            contaDao.save(conta);
        }
    }

    public void sacar(Long id, BigDecimal valor) {
        varificarValor(valor);
        Optional<Conta> contaOptional = buscarConta(id);
        if (contaOptional.isPresent()) {
            Conta conta = contaOptional.get();
            verificarSaldo(conta.getSaldo(), valor);
            conta.setSaldo(conta.getSaldo().subtract(valor));
            contaDao.save(conta);
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
        Optional<Conta> conta = contaDao.findByChavePix(chavePix);
        if(conta.isPresent()){
            return conta.get().getId();
        }
        throw new RuntimeException("Conta não encontrada");
    }
    private Long buscarIdConta(int agencia, int numero) {
        Optional<Conta> conta = contaDao.findByAgenciaAndNumero(agencia, numero);
        if(conta.isPresent()){
            return conta.get().getId();
        }
        throw new RuntimeException("Conta não encontrada");
    }
    private void verificarContaExiste(Conta conta) {

        Optional<Conta> contaOptional = contaDao.findByAgenciaAndNumero(conta.getAgencia(), conta.getNumero());

        if (contaOptional.isPresent()) {
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

package br.com.banco.service;

import br.com.banco.dao.ContaDao;
import br.com.banco.dao.TransferenciaDao;
import br.com.banco.model.Conta;
import br.com.banco.model.Transferencia;
import br.com.banco.model.dto.TrasferenciaPixRequest;
import br.com.banco.model.dto.TrasferenciaTedRequest;
import br.com.banco.model.enuns.TipoCliente;
import br.com.banco.model.enuns.TipoConta;
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

    private final Random random = new Random();

    public void criarConta(Conta conta) {
        verificarContaExiste(conta);
        popularDadosConta(conta);
        contaDao.save(conta);
    }

    @Transactional
    public Transferencia transferenciaPix(TrasferenciaPixRequest request){
        varificarValor(request.getValor());
        Conta contaOrigem = buscarConta(request.getIdContaOrigem());
        verificarSaldo(contaOrigem.getSaldo(), request.getValor());

        Conta contaDestino = buscarConta(buscarIdConta(request.getChavePix()));

        if(contaOrigem.getSenha() != request.getSenha()){
            throw new RuntimeException("Senha incorreta");
        }
        sacar(contaOrigem.getId(), request.getValor());
        depositar(contaDestino.getId(), request.getValor());

        return registrarTransferencia(request.getIdContaOrigem(), contaDestino.getId(), request.getValor());
    }
    @Transactional
    public Transferencia transferenciaTed(TrasferenciaTedRequest request){
        varificarValor(request.getValor());

        Conta contaOrigem = buscarConta(request.getIdOrigem());
        verificarSaldo(contaOrigem.getSaldo(), request.getValor());

        Conta contaDestino = buscarConta(buscarIdConta(request.getAgenciaDestino(), request.getNumeroContaDestino()));
        if(contaOrigem.getSenha() != request.getSenha()){
            throw new RuntimeException("Senha incorreta");
        }

        sacar(contaOrigem.getId(), request.getValor());
        depositar(contaDestino.getId(), request.getValor());

        return registrarTransferencia(request.getIdOrigem(), contaDestino.getId(), request.getValor());
    }

    public void fecharConta(Long id) {
        Conta conta = buscarConta(id);
        verificarContaExiste(conta);
        if(conta.getSaldo().compareTo(BigDecimal.ZERO) == 0) {
            contaDao.deleteById(id);
        }
        else{
            throw new RuntimeException("Conta com saldo não pode ser fechada");
        }
    }
    public BigDecimal exibirSldo(Long id) {
        Conta conta = buscarConta(id);
            return conta.getSaldo();
    }

    public  void depositar(Long id, BigDecimal valor) {
        varificarValor(valor);
        Conta conta = buscarConta(id);
        conta.setSaldo(conta.getSaldo().add(valor));
        contaDao.save(conta);

    }

    public void sacar(Long id, BigDecimal valor) {
        varificarValor(valor);
        Conta conta = buscarConta(id);
        verificarSaldo(conta.getSaldo(), valor);
        conta.setSaldo(conta.getSaldo().subtract(valor));
        contaDao.save(conta);

    }
    public Conta buscarConta(Long id) {
        Optional<Conta> conta = contaDao.findById(id);
        if (conta.isPresent()) {
            return conta.get();
        }
        throw new RuntimeException("Conta não encontrada");

    }

    public void aplicarTaxaManutencao(Long id){
        Conta conta = buscarConta(id);
        if(conta.getTipoConta() == TipoConta.CORRENTE){
            if(conta.getCliente().getTipoCliente() == TipoCliente.COMUM){
                conta.setSaldo(conta.getSaldo().subtract(new BigDecimal("12.00")));
            }
            else if(conta.getCliente().getTipoCliente() == TipoCliente.SUPER){
                conta.setSaldo(conta.getSaldo().subtract(new BigDecimal("8.00")));
            }
            contaDao.save(conta);
        }
        else{
            throw new RuntimeException("Essa conta não é uma conta corrente");
        }
    }

    public void aplicarRendimento(Long id){
        Conta conta = buscarConta(id);
        if(conta.getTipoConta() == TipoConta.POUPANCA){
            if(conta.getCliente().getTipoCliente() == TipoCliente.COMUM){
                conta.setSaldo(conta.getSaldo().add(conta.getSaldo().multiply(new BigDecimal("0.005"))));
            }
            else if(conta.getCliente().getTipoCliente() == TipoCliente.SUPER){
                conta.setSaldo(conta.getSaldo().add(conta.getSaldo().multiply(new BigDecimal("0.007"))));
            } else if (conta.getCliente().getTipoCliente() == TipoCliente.PREMIUM) {
                conta.setSaldo(conta.getSaldo().add(conta.getSaldo().multiply(new BigDecimal("0.009"))));
            }
            contaDao.save(conta);
        }
        else{
            throw new RuntimeException("Essa conta não é uma conta poupança");
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
    private void popularDadosConta(Conta conta) {
        conta.setSaldo(BigDecimal.ZERO);
        conta.setNumero(random.nextInt(900000) + 100000);
        conta.setAgencia(1221);

        String regexCpf = "\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}";
        if(conta.getChavePix() == null || conta.getChavePix().isEmpty() || !conta.getChavePix().matches(regexCpf)){
            conta.setChavePix(generateChavePix(30));
        }
    }

    private String generateChavePix(int valor) {
        final String CARACTERES = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < valor; i++) {
            char randomChar = CARACTERES.charAt(random.nextInt(CARACTERES.length()));
            builder.append(randomChar);
        }
        return builder.toString();
    }

}

package br.com.banco.service;

import br.com.banco.dao.CartaoDao;
import br.com.banco.dao.ContaDao;
import br.com.banco.model.Cartao;
import br.com.banco.model.CartaoCredito;
import br.com.banco.model.CartaoDebito;
import br.com.banco.model.Conta;
import br.com.banco.model.dto.PagamentoCartaoRequest;
import br.com.banco.model.enuns.TipoCliente;
import br.com.banco.model.enuns.TipoConta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Random;

@Service
public class CartaoService {

    @Autowired
    private CartaoDao cartaoDao;

    @Autowired
    private ContaDao contaDao;

    private Random random = new Random();

    public void criarCartao(Cartao cartao) {
        validarTipoConta(cartao);
        verificarCartaoExiste(cartao.getNumero());
        popularDadosCartao(cartao);
        cartaoDao.save(cartao);
    }

    private void validarTipoConta(Cartao cartao) {

        Optional<Conta> contaOptional = contaDao.findById(cartao.getConta().getId());

        if (contaOptional.isPresent()) {
            Conta conta = contaOptional.get();
            if(conta.getTipoConta() == TipoConta.POUPANCA && cartao instanceof CartaoCredito){
                throw new RuntimeException("Conta poupança não pode ter cartão de crédito");
            }
        }
        else{
            throw new RuntimeException("Conta não encontrada");
        }
    }

    public Cartao buscarCartao(Long id) {
        Optional<Cartao> cartao = cartaoDao.findById(id);
        if (cartao.isPresent()) {
            return cartao.get();
        }
        throw new RuntimeException("Cartão não encontrado");
    }

    public void realizarPagamento(Long id, PagamentoCartaoRequest pagamento) {

        Cartao cartao = buscarCartao(id);

        verificarCartaoAtivo(cartao.isAtivo());
        validarPagamento(cartao, pagamento);

        if (cartao instanceof CartaoCredito) {
            if(((CartaoCredito) cartao).getFatura().compareTo(pagamento.getValor()) > 0){
                throw new RuntimeException("Limite excedido");

            }
            ((CartaoCredito) cartao).setFatura(((CartaoCredito) cartao).getFatura().add(pagamento.getValor()));
        }
        else if (cartao instanceof CartaoDebito){
            if(((CartaoDebito) cartao).getLimiteDiario().compareTo(pagamento.getValor()) < 0){
                throw new RuntimeException("Limite excedido");
            }
            else if(cartao.getConta().getSaldo().compareTo(pagamento.getValor()) < 0){
                throw new RuntimeException("Saldo insuficiente");
            }
            ((CartaoDebito) cartao).setLimiteDiario(((CartaoDebito) cartao).getLimiteDiario().subtract(pagamento.getValor()));
            cartao.getConta().setSaldo(cartao.getConta().getSaldo().subtract(pagamento.getValor()));
            contaDao.save(cartao.getConta());
        }

        cartaoDao.save(cartao);
    }

    public void atualizarLimiteCartao(Long id, BigDecimal valor) {

        Cartao cartao = buscarCartao(id);

        if (cartao instanceof CartaoCredito) {
            ((CartaoCredito) cartao).setLimite(valor);
            cartaoDao.save(cartao);
        }
        else{
            throw new RuntimeException("Cartão não é de crédito");
        }
    }

    public void ativarDesativarCartao(Long id) {

        Cartao cartao = buscarCartao(id);
        cartao.setAtivo(!cartao.isAtivo());
        cartaoDao.save(cartao);
    }

    public void alterarSenha(Long id, int senha) {

        Cartao cartao = buscarCartao(id);
        cartao.setSenha(senha);
        cartaoDao.save(cartao);
    }

    public BigDecimal consultarFatura(Long id) {

        Cartao cartao = buscarCartao(id);
        if (cartao instanceof CartaoCredito) {
            return ((CartaoCredito) cartao).getFatura();
        }
        else {
            throw new RuntimeException("Cartão não é de crédito");
        }
    }

    public void pagarFatura(Long id) {

        Cartao cartao = buscarCartao(id);

        if (cartao instanceof CartaoCredito cartaoCredito) {

            if(cartaoCredito.getFatura().compareTo(BigDecimal.ZERO) == 0){
                throw new RuntimeException("Fatura está zerada");
            }
            else if(cartaoCredito.getFatura().compareTo(cartaoCredito.getLimite().multiply(new BigDecimal("0.8"))) > 0){
                cartaoCredito.setFatura(cartaoCredito.getFatura().add(cartaoCredito.getFatura().multiply(new BigDecimal("0.05"))));
            }

            if(cartaoCredito.getConta().getSaldo().compareTo(cartaoCredito.getFatura()) < 0){
                throw new RuntimeException("Saldo insuficiente para pagar a fatura");
            }

            cartaoCredito.getConta().setSaldo(cartao.getConta().getSaldo().subtract(cartaoCredito.getFatura()));
            contaDao.save(cartaoCredito.getConta());

            cartaoCredito.setFatura(BigDecimal.ZERO);
            cartaoDao.save(cartao);
        }
        else {
            throw new RuntimeException("Cartão não é de crédito");
        }
    }

    public void atualizarLimiteDiario(Long id, BigDecimal valor) {

        Cartao cartao = buscarCartao(id);

        if (cartao instanceof CartaoDebito) {
            if(valor.compareTo(BigDecimal.ZERO) < 0){
                throw new RuntimeException("Valor não pode ser negativo");
            }
            ((CartaoDebito) cartao).setLimiteDiario(valor);
            cartaoDao.save(cartao);
        }
        else {
            throw new RuntimeException("Cartão não é de débito");
        }
    }

    private void verificarCartaoAtivo(boolean isAtivo) {
        if (!isAtivo) {
            throw new RuntimeException("Cartão não está ativo");
        }
    }


    private void validarPagamento(Cartao cartao, PagamentoCartaoRequest pagamento) {

        if(pagamento.getValor().compareTo(BigDecimal.ZERO) < 0){
            throw new RuntimeException("Valor não pode ser negativo");
        }
        if(cartao instanceof CartaoDebito){
            if(pagamento.getValor().compareTo(((CartaoDebito) cartao).getLimiteDiario()) > 0){
                throw new RuntimeException("Limite diário excedido");
            }
        }
        else if (cartao instanceof CartaoCredito){
            if(pagamento.getValor().compareTo(((CartaoCredito) cartao).getLimite()) > 0){
                throw new RuntimeException("Limite excedido");
            }
        }

        if(cartao.getSenha() != pagamento.getSenha()){
            throw new RuntimeException("Senha incorreta");
        }
    }

    private void popularDadosCartao(Cartao cartao) {

        cartao.setAtivo(true);
        cartao.setNumero(random.nextInt(900000) + 100000);
        cartao.setCvv(random.nextInt(900) + 100);

        if (cartao instanceof CartaoCredito) {

            if (cartao.getConta().getCliente().getTipoCliente() == TipoCliente.COMUM) {
                ((CartaoCredito) cartao).setLimite(new BigDecimal(1000));

            } else if (cartao.getConta().getCliente().getTipoCliente() == TipoCliente.SUPER) {
                ((CartaoCredito) cartao).setLimite(new BigDecimal(5000));
            } else if (cartao.getConta().getCliente().getTipoCliente() == TipoCliente.PREMIUM) {
                ((CartaoCredito) cartao).setLimite(new BigDecimal(10000));
            }
            ((CartaoCredito) cartao).setFatura(BigDecimal.ZERO);
        }
        else if (cartao instanceof CartaoDebito) {
            ((CartaoDebito) cartao).setLimiteDiario(new BigDecimal(1000));
        }

    }

    private void verificarCartaoExiste(long numero) {
        if (cartaoDao.existsByNumero(numero)) {
            throw new RuntimeException("Cartão já cadastrado");
        }
    }
}

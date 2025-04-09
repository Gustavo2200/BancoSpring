package br.com.banco.service;

import br.com.banco.dao.ClienteDao;
import br.com.banco.dao.EnderecoDao;
import br.com.banco.model.Cliente;
import br.com.banco.model.Endereco;
import br.com.banco.utils.MensagensDeErroUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ClienteService {

    @Autowired
    private ClienteDao dao;

    @Autowired
    private EnderecoDao enderecoDao;

    public void cadastrar(Cliente cliente) {
        verificarDadosCliente(cliente);
        dao.save(cliente);
    }

    public Cliente buscarPorId(Long id) {
        Optional<Cliente> cliente = dao.findById(id);
        if(cliente.isPresent()){
            return cliente.get();
        }
        throw new RuntimeException(MensagensDeErroUtils.CLIENTE_NAO_ENCONTRADO);
    }

    public void atualizarCliente(Long id, Cliente cliente) {
        Cliente clienteBanco = buscarPorId(id);

        try {
            validarEndereco(cliente.getEndereco());
            validarNome(cliente.getNome());
            validarCpf(cliente.getCpf());
            validarDataNascimento(cliente.getDataNascimento());

            clienteBanco.setNome(cliente.getNome());
            clienteBanco.setCpf(cliente.getCpf());
            clienteBanco.setDataNascimento(cliente.getDataNascimento());
            clienteBanco.setEndereco(cliente.getEndereco());
        }catch (Exception e) {

            throw new RuntimeException(MensagensDeErroUtils.CLIENTE_NAO_ATUALIZADO + e.getMessage());
        }

        dao.save(clienteBanco);
    }
    public void apagarClientePorId(Long id) {
        if(buscarPorId(id) != null){
            dao.deleteById(id);
        }
        else {
            throw new RuntimeException(MensagensDeErroUtils.CLIENTE_NAO_ENCONTRADO);
        }
    }
    public List<Cliente> listarClientes() {
        return dao.findAll();
    }

    private void verificarDadosCliente(Cliente cliente) {

        verificarClienteExiste(cliente.getCpf());
        validarNome(cliente.getNome());
        validarCpf(cliente.getCpf());
        validarDataNascimento(cliente.getDataNascimento());
        validarEndereco(cliente.getEndereco());
    }

    private void verificarClienteExiste(String cpf) {

        if(dao.existsByCpf(cpf)){
            throw new RuntimeException(MensagensDeErroUtils.CPF_JA_CADASTRADO);
        }
    }

    private void validarEndereco(Endereco endereco) {
        String regexCep = "^\\d{5}-\\d{3}$";
        if(!endereco.getCep().matches(regexCep)){
            throw new RuntimeException(MensagensDeErroUtils.CEP_INVALIDO);
        }
        if((endereco.getCidade() == null || endereco.getCidade().isEmpty()) ||
            (endereco.getEstado() == null || endereco.getEstado().isEmpty()) ||
            (endereco.getRua() == null || endereco.getRua().isEmpty()) ||
            (endereco.getNumero() == null || endereco.getNumero().isEmpty())) {

            throw new RuntimeException(MensagensDeErroUtils.ENDERECO_INVALIDO);
        }
        enderecoDao.save(endereco);
    }

    private void validarDataNascimento(String dataNascimento) {

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        sdf.setLenient(false);

        Date dataNascimentoDate = null;
        try {
            dataNascimentoDate = sdf.parse(dataNascimento);

        }catch (Exception e){
            throw new RuntimeException(MensagensDeErroUtils.FORMATO_DATA_INVALIDO);
        }

        Date dataAtual = new Date();

        int idade = dataAtual.getYear() - dataNascimentoDate.getYear();

        if (dataAtual.getMonth() < dataNascimentoDate.getMonth() ||
                (dataAtual.getMonth() == dataNascimentoDate.getMonth() && dataAtual.getDay() < dataNascimentoDate.getDay())) {
            idade--;
        }

        if(idade < 18) {
            throw new RuntimeException(MensagensDeErroUtils.MENSAGEM_MENOR_DE_IDADE);
        }

    }

    private void validarCpf(String CPF) {
        String regex = "\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}";

        if(!CPF.matches(regex)){
            throw new RuntimeException("Formato de CPF inválido");
        }
        CPF = CPF.replace(".", "").replace("-", "");

        if (CPF.equals("00000000000") ||
                CPF.equals("11111111111") ||
                CPF.equals("22222222222") || CPF.equals("33333333333") ||
                CPF.equals("44444444444") || CPF.equals("55555555555") ||
                CPF.equals("66666666666") || CPF.equals("77777777777") ||
                CPF.equals("88888888888") || CPF.equals("99999999999") ||
                (CPF.length() != 11)){
            throw new RuntimeException(MensagensDeErroUtils.CPF_INVALIDO);
        }

        char dig10, dig11;
        int sm, i, r, num, peso;

        // "try" - protege o codigo para eventuais erros de conversao de tipo (int)
        try {
            // Calculo do 1o. Digito Verificador
            sm = 0;
            peso = 10;
            for (i=0; i<9; i++) {
                // converte o i-esimo caractere do CPF em um numero:
                // por exemplo, transforma o caractere "0" no inteiro 0
                // (48 eh a posicao de "0" na tabela ASCII)
                num = (int)(CPF.charAt(i) - 48);
                sm = sm + (num * peso);
                peso = peso - 1;
            }

            r = 11 - (sm % 11);
            if ((r == 10) || (r == 11))
                dig10 = '0';
            else dig10 = (char)(r + 48); // converte no respectivo caractere numerico

            // Calculo do 2o. Digito Verificador
            sm = 0;
            peso = 11;
            for(i=0; i<10; i++) {
                num = (int)(CPF.charAt(i) - 48);
                sm = sm + (num * peso);
                peso = peso - 1;
            }

            r = 11 - (sm % 11);
            if ((r == 10) || (r == 11))
                dig11 = '0';
            else dig11 = (char)(r + 48);

            // Verifica se os digitos calculados conferem com os digitos informados.
            if ((dig10 != CPF.charAt(9)) && (dig11 != CPF.charAt(10))) {
                throw new RuntimeException(MensagensDeErroUtils.CPF_INVALIDO);
            }
        } catch (Exception erro) {
            throw new RuntimeException(MensagensDeErroUtils.CPF_INVALIDO);
        }

    }

    private void validarNome(String nome) {

        String regex = "^[A-Za-zÀ-ÖØ-öø-ÿ ]+$";

        if(!nome.matches(regex)){
            throw new RuntimeException(MensagensDeErroUtils.FORMATO_NOME_INVALIDO);
        }
        else if(nome.length() < 2 || nome.length() > 100) {
            throw new RuntimeException(MensagensDeErroUtils.TAMANHO_NOME_INVALIDO);
        }
    }
}

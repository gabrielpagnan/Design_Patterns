package strategy;

import java.util.Objects;

public class Pedido {
    private final double valor;
    private final double peso;
    private final String regiao;
    private RegraCliente regraCliente;

    public Pedido(double valor, double peso, String regiao, RegraCliente regraCliente) {
        this.valor = valor;
        this.peso = peso;
        this.regiao = regiao;
        definirRegraCliente(regraCliente);
    }

    public double getValor() {
        return valor;
    }

    public double getPeso() {
        return peso;
    }

    public String getRegiao() {
        return regiao;
    }

    public RegraCliente getRegraCliente() {
        return regraCliente;
    }

    public void definirRegraCliente(RegraCliente regraCliente) {
        this.regraCliente = Objects.requireNonNull(regraCliente, "A regra do cliente é obrigatória.");
    }
}

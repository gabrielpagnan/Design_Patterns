package strategy;

import java.util.Locale;

public class RelatorioPedido {
    public String formatar(Pedido pedido) {
        RegraCliente regra = pedido.getRegraCliente();
        double desconto = regra.calcularDesconto(pedido);
        double frete = regra.calcularFrete(pedido);
        double total = pedido.getValor() - desconto + frete;

        return String.format(Locale.ROOT,
                "%s | valor: %.2f | desconto: %.2f | frete: %.2f | total: %.2f",
                regra.getDescricao(), pedido.getValor(), desconto, frete, total);
    }
}

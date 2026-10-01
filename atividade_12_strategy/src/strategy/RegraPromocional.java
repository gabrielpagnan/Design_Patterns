package strategy;

import java.util.Locale;
import java.util.Objects;

public class RegraPromocional implements RegraCliente {
    private final RegraCliente regraPadrao;
    private final double percentualDesconto;

    public RegraPromocional(RegraCliente regraPadrao, double percentualDesconto) {
        this.regraPadrao = Objects.requireNonNull(regraPadrao, "A regra padrão é obrigatória.");
        if (percentualDesconto < 0.0 || percentualDesconto > 1.0) {
            throw new IllegalArgumentException("O percentual deve estar entre 0 e 1.");
        }
        this.percentualDesconto = percentualDesconto;
    }

    @Override
    public String getDescricao() {
        return String.format(Locale.ROOT, "Promoção especial (%.0f%% de desconto)", percentualDesconto * 100);
    }

    @Override
    public double calcularDesconto(Pedido pedido) {
        return pedido.getValor() * percentualDesconto;
    }

    @Override
    public double calcularFrete(Pedido pedido) {
        return regraPadrao.calcularFrete(pedido);
    }
}

package strategy;

public class RegraClienteVip extends RegraClienteBase {
    @Override
    public String getDescricao() {
        return "Cliente VIP (10% de desconto)";
    }

    @Override
    public double calcularDesconto(Pedido pedido) {
        return pedido.getValor() * 0.10;
    }

    @Override
    public double calcularFrete(Pedido pedido) {
        return calcularFreteComBase(pedido, 12.0);
    }
}

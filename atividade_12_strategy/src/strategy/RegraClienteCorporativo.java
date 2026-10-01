package strategy;

public class RegraClienteCorporativo extends RegraClienteBase {
    @Override
    public String getDescricao() {
        return "Cliente corporativo (20% de desconto)";
    }

    @Override
    public double calcularDesconto(Pedido pedido) {
        return pedido.getValor() * 0.20;
    }

    @Override
    public double calcularFrete(Pedido pedido) {
        return calcularFreteComBase(pedido, 0.0);
    }
}

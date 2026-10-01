package strategy;

public class RegraClienteComum extends RegraClienteBase {
    @Override
    public String getDescricao() {
        return "Cliente comum";
    }

    @Override
    public double calcularDesconto(Pedido pedido) {
        return 0.0;
    }

    @Override
    public double calcularFrete(Pedido pedido) {
        return calcularFreteComBase(pedido, 25.0);
    }
}

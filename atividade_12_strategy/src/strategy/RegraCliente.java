package strategy;

public interface RegraCliente {
    String getDescricao();

    double calcularDesconto(Pedido pedido);

    double calcularFrete(Pedido pedido);
}

package strategy;

abstract class RegraClienteBase implements RegraCliente {
    private static final double FRETE_POR_KG = 3.0;
    private static final double ADICIONAL_NORTE = 30.0;

    protected double calcularFreteComBase(Pedido pedido, double freteBase) {
        double adicionalRegiao = "norte".equalsIgnoreCase(pedido.getRegiao())
                ? ADICIONAL_NORTE
                : 0.0;
        return freteBase + pedido.getPeso() * FRETE_POR_KG + adicionalRegiao;
    }
}

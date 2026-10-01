package strategy;

public class StrategyTest {

    public static void main(String[] args) {
        deveCalcularAsRegrasPadrao();
        deveTrocarAPromocaoEVoltarParaARegraOriginal();
        deveFormatarUsandoAAbstracaoDaRegra();

        System.out.println("StrategyTest: todos os testes passaram.");
    }

    private static void deveCalcularAsRegrasPadrao() {
        assertValores(
                new Pedido(200.0, 2.0, "sul", new RegraClienteComum()),
                0.0, 31.0, 231.0);
        assertValores(
                new Pedido(200.0, 2.0, "sul", new RegraClienteVip()),
                20.0, 18.0, 198.0);
        assertValores(
                new Pedido(200.0, 2.0, "norte", new RegraClienteCorporativo()),
                40.0, 36.0, 196.0);
    }

    private static void deveTrocarAPromocaoEVoltarParaARegraOriginal() {
        RegraCliente regraVip = new RegraClienteVip();
        Pedido pedido = new Pedido(200.0, 2.0, "sul", regraVip);

        pedido.definirRegraCliente(new RegraPromocional(regraVip, 0.30));
        assertValores(pedido, 60.0, 18.0, 158.0);

        pedido.definirRegraCliente(regraVip);
        assertValores(pedido, 20.0, 18.0, 198.0);
    }

    private static void deveFormatarUsandoAAbstracaoDaRegra() {
        RegraCliente regra = new RegraCliente() {
            @Override
            public String getDescricao() {
                return "Regra de teste";
            }

            @Override
            public double calcularDesconto(Pedido pedido) {
                return 15.0;
            }

            @Override
            public double calcularFrete(Pedido pedido) {
                return 7.0;
            }
        };

        String relatorio = new RelatorioPedido()
                .formatar(new Pedido(100.0, 1.0, "sul", regra));

        assert relatorio.startsWith("Regra de teste | valor: 100.00") : relatorio;
        assert relatorio.contains("desconto: 15.00") : relatorio;
        assert relatorio.endsWith("total: 92.00") : relatorio;
    }

    private static void assertValores(Pedido pedido, double descontoEsperado,
                                      double freteEsperado, double totalEsperado) {
        RegraCliente regra = pedido.getRegraCliente();
        double desconto = regra.calcularDesconto(pedido);
        double frete = regra.calcularFrete(pedido);
        double total = pedido.getValor() - desconto + frete;

        assert Math.abs(desconto - descontoEsperado) < 0.0001 : desconto;
        assert Math.abs(frete - freteEsperado) < 0.0001 : frete;
        assert Math.abs(total - totalEsperado) < 0.0001 : total;
    }
}

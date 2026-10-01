package strategy;

public class Main {
    public static void main(String[] args) {
        RelatorioPedido relatorio = new RelatorioPedido();

        Pedido comum = new Pedido(200.0, 2.0, "sul", new RegraClienteComum());
        RegraCliente regraVip = new RegraClienteVip();
        Pedido vip = new Pedido(200.0, 2.0, "sul", regraVip);
        Pedido corporativo = new Pedido(200.0, 2.0, "norte", new RegraClienteCorporativo());

        System.out.println(relatorio.formatar(comum));
        System.out.println(relatorio.formatar(vip));
        System.out.println(relatorio.formatar(corporativo));

        System.out.println("\nAplicando promoção temporária ao pedido VIP:");
        vip.definirRegraCliente(new RegraPromocional(regraVip, 0.30));
        System.out.println(relatorio.formatar(vip));

        System.out.println("Voltando à regra VIP padrão:");
        vip.definirRegraCliente(regraVip);
        System.out.println(relatorio.formatar(vip));
    }
}

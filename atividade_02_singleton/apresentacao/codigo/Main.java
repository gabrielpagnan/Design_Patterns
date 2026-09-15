public class Main {

    public static void main(String[] args) {
        CentralDeAlertas centralDaPolicia = CentralDeAlertas.getInstancia();
        CentralDeAlertas centralDosBombeiros = CentralDeAlertas.getInstancia();
        CentralDeAlertas centralDoSamu = CentralDeAlertas.getInstancia();

        centralDaPolicia.registrar("POLICIA: acidente na rodovia");
        centralDosBombeiros.registrar("BOMBEIROS: incêndio controlado");
        centralDoSamu.registrar("SAMU: ambulância enviada");

        boolean mesmaInstancia = centralDaPolicia == centralDosBombeiros
                && centralDosBombeiros == centralDoSamu;

        if (!mesmaInstancia) {
            throw new AssertionError("Os órgãos receberam centrais diferentes.");
        }
        if (centralDaPolicia.getHistorico().size() != 3) {
            throw new AssertionError("O histórico compartilhado deveria conter três alertas.");
        }

        System.out.println("=== CENTRAL DE ALERTAS ===");
        for (String alerta : centralDaPolicia.getHistorico()) {
            System.out.println(alerta);
        }
        System.out.println();
        System.out.println("Todas as referências apontam para o mesmo objeto: "
                + mesmaInstancia);
        System.out.println("Total de alertas compartilhados: "
                + centralDaPolicia.getHistorico().size());
    }
}

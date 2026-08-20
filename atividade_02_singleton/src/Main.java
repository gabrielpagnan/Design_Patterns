import java.util.ArrayList;
import java.util.List;

/**
 * Demonstracao do Sistema Central de Alertas de Emergencia.
 *
 * Prova tres pontos:
 *  1. Orgaos diferentes enxergam o MESMO historico de alertas;
 *  2. Todos apontam para a MESMA instancia de CentralDeAlertas (hashCode identico);
 *  3. A implementacao aguenta varias threads enviando alertas simultaneamente.
 */
public class Main {

    public static void main(String[] args) throws InterruptedException {

        System.out.println("=========================================================");
        System.out.println("   SISTEMA CENTRAL DE ALERTAS DE EMERGENCIA");
        System.out.println("=========================================================\n");

        // 1) Cada orgao tem seu proprio modulo (objetos distintos)...
        Policia policia = new Policia();
        Bombeiros bombeiros = new Bombeiros();
        Samu samu = new Samu();

        // 2) ...mas todos conversam com a mesma central.
        System.out.println("\n--- Envio de alertas ---");
        policia.enviarAlerta("Assalto a mao armada na Av. Centenario, 1200");
        bombeiros.enviarAlerta("Incendio em residencia no bairro Pinheirinho");
        samu.enviarAlerta("Acidente com vitima na BR-101, km 372");
        policia.enviarAlerta("Furto de veiculo no estacionamento do shopping");

        // 3) Dois orgaos diferentes consultando o historico: veem tudo, de todos.
        policia.imprimirAlertas();
        samu.imprimirAlertas();

        // 4) Prova de instancia unica pelo hashCode.
        System.out.println("\n--- Prova de instancia unica (hashCode) ---");
        System.out.println("   Central vista pela Policia ....: " + policia.hashCodeDaCentral());
        System.out.println("   Central vista pelos Bombeiros .: " + bombeiros.hashCodeDaCentral());
        System.out.println("   Central vista pelo SAMU .......: " + samu.hashCodeDaCentral());
        System.out.println("   Central obtida aqui no Main ...: "
                + CentralDeAlertas.getInstancia().hashCode());

        boolean mesmaInstancia =
                CentralDeAlertas.getInstancia() == CentralDeAlertas.getInstancia();
        System.out.println("   getInstancia() == getInstancia() ? " + mesmaInstancia
                + "  (comparacao por referencia, com ==)");

        // 5) Teste de concorrencia: 3 threads x 100 alertas cada.
        testarConcorrencia();

        System.out.println("\n=========================================================");
    }

    /**
     * Dispara varios modulos em paralelo para mostrar que nenhum alerta se perde
     * e que a central continua sendo uma unica instancia.
     */
    private static void testarConcorrencia() throws InterruptedException {
        System.out.println("\n--- Teste de concorrencia (3 threads x 100 alertas) ---");

        int totalAntes = CentralDeAlertas.getInstancia().totalDeAlertas();

        List<OrgaoDeSeguranca> orgaos = List.of(new Policia(), new Bombeiros(), new Samu());
        List<Thread> threads = new ArrayList<>();

        for (OrgaoDeSeguranca orgao : orgaos) {
            Thread t = new Thread(() -> {
                for (int i = 1; i <= 100; i++) {
                    orgao.enviarAlertaSilencioso("ocorrencia automatica " + i);
                }
            }, "thread-" + orgao.getNome());
            threads.add(t);
            t.start();
        }

        for (Thread t : threads) {
            t.join();
        }

        int totalDepois = CentralDeAlertas.getInstancia().totalDeAlertas();
        int registrados = totalDepois - totalAntes;

        System.out.println("   Alertas esperados ..: 300");
        System.out.println("   Alertas registrados : " + registrados);
        System.out.println("   Resultado ..........: "
                + (registrados == 300 ? "OK - nenhum alerta perdido" : "FALHA - houve perda"));
    }
}

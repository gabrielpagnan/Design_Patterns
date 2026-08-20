import java.util.List;

/**
 * Comportamento comum a todos os orgaos ligados a central.
 *
 * Note que a referencia para a central e obtida por CentralDeAlertas.getInstancia(),
 * nunca por "new": o orgao apenas se conecta a central que ja existe.
 */
public abstract class OrgaoDeSeguranca {

    private final String nome;
    protected final CentralDeAlertas central = CentralDeAlertas.getInstancia();

    protected OrgaoDeSeguranca(String nome) {
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }

    /** Encaminha a mensagem para a central, identificando o orgao emissor. */
    public void enviarAlerta(String mensagem) {
        central.enviarAlerta(nome, mensagem);
        System.out.println("[" + nome + "] alerta enviado: " + mensagem);
    }

    /** Igual ao enviarAlerta, porem sem imprimir no console (usado no teste de carga). */
    public void enviarAlertaSilencioso(String mensagem) {
        central.enviarAlerta(nome, mensagem);
    }

    /** @return historico completo registrado na central (visto por todos os orgaos). */
    public List<String> listarAlertas() {
        return central.getAlertas();
    }

    /** Imprime o historico completo a partir da visao deste orgao. */
    public void imprimirAlertas() {
        System.out.println("\n--- Historico consultado por " + nome
                + " (central hashCode: " + central.hashCode() + ") ---");
        for (String alerta : listarAlertas()) {
            System.out.println("   " + alerta);
        }
        System.out.println("   Total: " + listarAlertas().size() + " alerta(s)");
    }

    /** Prova de identidade: o hashCode da central vista por este orgao. */
    public int hashCodeDaCentral() {
        return central.hashCode();
    }
}

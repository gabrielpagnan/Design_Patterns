import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Central única compartilhada por todos os órgãos de emergência.
 */
public final class CentralDeAlertas {

    // 1. A própria classe cria e guarda sua única instância.
    private static final CentralDeAlertas INSTANCIA = new CentralDeAlertas();

    private final List<String> historico = new ArrayList<>();

    // 2. O construtor privado impede o uso de "new" fora da classe.
    private CentralDeAlertas() {
    }

    // 3. Este é o ponto de acesso global e controlado.
    public static CentralDeAlertas getInstancia() {
        return INSTANCIA;
    }

    public void registrar(String alerta) {
        historico.add(alerta);
    }

    public List<String> getHistorico() {
        return Collections.unmodifiableList(historico);
    }
}

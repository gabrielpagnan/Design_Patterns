import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Centro de controle unico do Sistema Central de Alertas de Emergencia.
 *
 * Singleton implementado pela tecnica do "holder" (initialization-on-demand holder):
 * a classe interna CentralHolder so e carregada pela JVM na primeira chamada de
 * getInstancia(), e a propria especificacao da linguagem garante que a inicializacao
 * de uma classe acontece uma unica vez, mesmo com varias threads chamando ao mesmo
 * tempo. Ou seja: temos criacao preguicosa (lazy) e thread-safe sem pagar o custo de
 * synchronized a cada chamada.
 */
public class CentralDeAlertas {

    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm:ss");

    /** Lista thread-safe: cada escrita copia o array interno, e as leituras nunca travam. */
    private final List<String> mensagens = new CopyOnWriteArrayList<>();

    /** Numero sequencial do alerta; incremento atomico, seguro entre threads. */
    private final AtomicInteger contador = new AtomicInteger(0);

    /** Construtor privado: ninguem de fora consegue chamar "new CentralDeAlertas()". */
    private CentralDeAlertas() {
        System.out.println(">> [CentralDeAlertas] instancia unica criada (hashCode: "
                + System.identityHashCode(this) + ")");
    }

    /** Carregada apenas quando getInstancia() e chamado pela primeira vez. */
    private static class CentralHolder {
        private static final CentralDeAlertas INSTANCIA = new CentralDeAlertas();
    }

    /** Unico ponto de acesso a central. */
    public static CentralDeAlertas getInstancia() {
        return CentralHolder.INSTANCIA;
    }

    /**
     * Registra um novo alerta na central.
     *
     * @param orgao   nome do orgao que esta enviando (Policia, Bombeiros, SAMU...)
     * @param mensagem descricao da ocorrencia
     */
    public void enviarAlerta(String orgao, String mensagem) {
        if (orgao == null || orgao.isBlank()) {
            throw new IllegalArgumentException("O orgao emissor e obrigatorio.");
        }
        if (mensagem == null || mensagem.isBlank()) {
            throw new IllegalArgumentException("A mensagem do alerta e obrigatoria.");
        }

        int numero = contador.incrementAndGet();
        String registro = String.format("#%03d [%s] %s: %s",
                numero, LocalTime.now().format(HORA), orgao, mensagem);

        mensagens.add(registro);
    }

    /**
     * @return historico completo de alertas, em modo somente-leitura para que
     *         nenhum chamador consiga alterar a lista interna da central.
     */
    public List<String> getAlertas() {
        return Collections.unmodifiableList(mensagens);
    }

    /** @return quantidade de alertas registrados ate agora. */
    public int totalDeAlertas() {
        return mensagens.size();
    }
}

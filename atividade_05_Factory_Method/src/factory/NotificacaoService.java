package factory;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Creator: concentra o fluxo comum de notificacao.
 *
 * O fluxo (montar mensagem -> criar o notificador -> enviar -> registrar log)
 * esta escrito UMA UNICA VEZ aqui. A unica coisa que varia entre os canais e
 * qual objeto o metodo fabrica devolve.
 */
public abstract class NotificacaoService {

    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm:ss");

    /**
     * METODO FABRICA.
     * Cada subclasse decide qual Notificador concreto sera criado.
     */
    protected abstract Notificador criarNotificador();

    /** Fluxo fixo, igual para todos os canais. */
    public void notificar(String destinatario, String mensagem) {
        // 1. montar a mensagem
        String textoFinal = montarMensagem(mensagem);

        // 2. escolher o canal (delegado a subclasse)
        Notificador notificador = criarNotificador();

        // 3. enviar
        notificador.enviar(destinatario, textoFinal);

        // 4. registrar no log
        registrarLog(notificador.getCanal(), destinatario);
    }

    /** Parte comum da mensagem: assinatura do portal. */
    protected String montarMensagem(String mensagem) {
        return mensagem + " -- Portal Academico";
    }

    private void registrarLog(String canal, String destinatario) {
        System.out.println("[LOG " + LocalTime.now().format(HORA) + "] "
                + "canal=" + canal + " destinatario=" + destinatario + " status=ENVIADO");
    }
}

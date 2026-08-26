package factory;

/**
 * Product: contrato que todos os canais de notificacao seguem.
 * O fluxo de envio conversa apenas com esta interface.
 */
public interface Notificador {

    void enviar(String destinatario, String mensagem);

    /** Nome do canal, usado no log. */
    String getCanal();
}

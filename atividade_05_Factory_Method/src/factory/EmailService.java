package factory;

/** ConcreteCreator: fabrica notificadores de e-mail. */
public class EmailService extends NotificacaoService {

    @Override
    protected Notificador criarNotificador() {
        return new EmailNotificador();
    }
}

package factory;

/** ConcreteCreator: fabrica notificadores push. */
public class PushService extends NotificacaoService {

    @Override
    protected Notificador criarNotificador() {
        return new PushNotificador();
    }
}

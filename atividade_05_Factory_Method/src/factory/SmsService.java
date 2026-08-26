package factory;

/** ConcreteCreator: fabrica notificadores de SMS. */
public class SmsService extends NotificacaoService {

    @Override
    protected Notificador criarNotificador() {
        return new SmsNotificador();
    }
}

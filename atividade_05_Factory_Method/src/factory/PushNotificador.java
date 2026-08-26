package factory;

/** ConcreteProduct: entrega por notificacao push no aplicativo. */
public class PushNotificador implements Notificador {

    @Override
    public void enviar(String destinatario, String mensagem) {
        System.out.println("[PUSH] Buscando o token do dispositivo do aluno " + destinatario);
        System.out.println("[PUSH] Disparando notificacao: " + mensagem);
    }

    @Override
    public String getCanal() {
        return "Push";
    }
}

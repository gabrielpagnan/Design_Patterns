package factory;

/** ConcreteProduct: entrega por e-mail. */
public class EmailNotificador implements Notificador {

    @Override
    public void enviar(String destinatario, String mensagem) {
        System.out.println("[EMAIL] Conectando ao servidor SMTP...");
        System.out.println("[EMAIL] Para: " + destinatario);
        System.out.println("[EMAIL] Corpo: " + mensagem);
    }

    @Override
    public String getCanal() {
        return "E-mail";
    }
}

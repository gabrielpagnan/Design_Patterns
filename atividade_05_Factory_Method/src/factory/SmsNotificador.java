package factory;

/** ConcreteProduct: entrega por SMS. */
public class SmsNotificador implements Notificador {

    private static final int LIMITE_SMS = 160;

    @Override
    public void enviar(String destinatario, String mensagem) {
        String texto = mensagem.length() > LIMITE_SMS
                ? mensagem.substring(0, LIMITE_SMS)
                : mensagem;

        System.out.println("[SMS] Enviando pela operadora para o numero " + destinatario);
        System.out.println("[SMS] Texto (" + texto.length() + "/160): " + texto);
    }

    @Override
    public String getCanal() {
        return "SMS";
    }
}

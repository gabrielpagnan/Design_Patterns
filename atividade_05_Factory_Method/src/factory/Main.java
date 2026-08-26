package factory;

/**
 * Cliente / classe de teste.
 *
 * Repare que o Main nunca da "new EmailNotificador()", "new SmsNotificador()"
 * ou "new PushNotificador()". Ele so escolhe qual service usar e chama sempre
 * o mesmo metodo notificar(...): quem cria o objeto concreto e o metodo
 * fabrica de cada subclasse.
 */
public class Main {

    public static void main(String[] args) {
        String mensagem = "Sua nota de Design Patterns foi lancada.";

        // O cliente trabalha com o tipo abstrato NotificacaoService.
        NotificacaoService email = new EmailService();
        NotificacaoService sms = new SmsService();
        NotificacaoService push = new PushService();

        // Mesma mensagem, mesma chamada, tres canais diferentes.
        executar(email, "gabriel.pagnan@aluno.satc.edu.br", mensagem);
        executar(sms, "(48) 99999-1234", mensagem);
        executar(push, "aluno-2023001", mensagem);

        System.out.println("O fluxo (montar -> criar -> enviar -> log) foi o mesmo nos 3 casos.");
        System.out.println("So mudou o Notificador devolvido pelo metodo fabrica.");
    }

    private static void executar(NotificacaoService servico, String destinatario, String mensagem) {
        System.out.println("---------------------------------------------");
        System.out.println("Usando: " + servico.getClass().getSimpleName());
        System.out.println("---------------------------------------------");

        servico.notificar(destinatario, mensagem);

        System.out.println();
    }
}

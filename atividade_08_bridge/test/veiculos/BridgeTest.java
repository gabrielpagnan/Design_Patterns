package veiculos;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class BridgeTest {

    public static void main(String[] args) {
        Motor gasolina = new MotorGasolina();
        Motor eletrico = new MotorEletrico();

        // O mesmo veículo funciona com motores diferentes.
        String sedanGasolina = executar(new Sedan(gasolina));
        String sedanEletrico = executar(new Sedan(eletrico));

        assert sedanGasolina.contains("Dirigindo um Sedan");
        assert sedanEletrico.contains("Dirigindo um Sedan");
        assert sedanGasolina.contains("Motor a gasolina ligado");
        assert sedanEletrico.contains("Motor elétrico ligado");
        assert !sedanGasolina.equals(sedanEletrico);

        // O mesmo motor funciona em veículos diferentes.
        String suvGasolina = executar(new Suv(gasolina));

        assert suvGasolina.contains("Dirigindo um SUV");
        assert suvGasolina.contains("Motor a gasolina ligado");
        assert suvGasolina.contains("Motor a gasolina acelerando");

        // A abstração só conhece o tipo Motor, nunca as classes concretas.
        Veiculo suvEletrico = new Suv(eletrico);
        assert suvEletrico instanceof Veiculo;
        assert executar(suvEletrico).contains("Motor elétrico acelerando");

        System.out.println("Todos os testes passaram.");
    }

    private static String executar(Veiculo veiculo) {
        PrintStream saidaOriginal = System.out;
        ByteArrayOutputStream saidaCapturada = new ByteArrayOutputStream();
        System.setOut(new PrintStream(saidaCapturada));

        veiculo.dirigir();
        veiculo.acelerar();

        System.setOut(saidaOriginal);
        return saidaCapturada.toString();
    }
}

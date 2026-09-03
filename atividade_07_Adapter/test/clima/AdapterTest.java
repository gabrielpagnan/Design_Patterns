package clima;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class AdapterTest {

    public static void main(String[] args) {
        PrevisaoService openWeather = new OpenWeatherAdapter(new OpenWeatherApi());
        PrevisaoService accuWeather = new AccuWeatherAdapter(new AccuWeatherApi());

        assert openWeather.obterTemperatura("Criciúma") == 25;
        assert accuWeather.obterTemperatura("Criciúma") == 25;

        PrintStream saidaOriginal = System.out;
        ByteArrayOutputStream saidaCapturada = new ByteArrayOutputStream();
        System.setOut(new PrintStream(saidaCapturada));

        new PainelClima(openWeather).exibir("Criciúma");
        new PainelClima(accuWeather).exibir("Criciúma");

        System.setOut(saidaOriginal);
        String saida = saidaCapturada.toString();
        assert saida.equals("Temperatura em Criciúma: 25°C\n"
                + "Temperatura em Criciúma: 25°C\n");

        System.out.println("Todos os testes passaram.");
    }
}

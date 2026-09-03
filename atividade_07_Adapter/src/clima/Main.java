package clima;

public class Main {

    public static void main(String[] args) {
        PainelClima painelOpenWeather = new PainelClima(
                new OpenWeatherAdapter(new OpenWeatherApi()));

        PainelClima painelAccuWeather = new PainelClima(
                new AccuWeatherAdapter(new AccuWeatherApi()));

        System.out.println("Usando OpenWeather:");
        painelOpenWeather.exibir("Criciúma");

        System.out.println("\nUsando AccuWeather:");
        painelAccuWeather.exibir("Criciúma");

        // Para adicionar outro fornecedor, basta criar outro Adapter que
        // implemente PrevisaoService. O PainelClima continua igual.
    }
}

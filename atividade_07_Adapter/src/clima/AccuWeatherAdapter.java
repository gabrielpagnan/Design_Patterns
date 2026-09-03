package clima;

public class AccuWeatherAdapter implements PrevisaoService {

    private final AccuWeatherApi api;

    public AccuWeatherAdapter(AccuWeatherApi api) {
        this.api = api;
    }

    @Override
    public int obterTemperatura(String cidade) {
        int fahrenheit = api.getTemperature(cidade);
        return (fahrenheit - 32) * 5 / 9;
    }
}

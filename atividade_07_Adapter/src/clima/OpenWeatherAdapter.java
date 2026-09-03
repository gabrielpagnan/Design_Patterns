package clima;

public class OpenWeatherAdapter implements PrevisaoService {

    private final OpenWeatherApi api;

    public OpenWeatherAdapter(OpenWeatherApi api) {
        this.api = api;
    }

    @Override
    public int obterTemperatura(String cidade) {
        return (int) api.temperatura(cidade);
    }
}

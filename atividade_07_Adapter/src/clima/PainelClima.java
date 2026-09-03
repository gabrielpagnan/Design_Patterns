package clima;

public class PainelClima {

    private final PrevisaoService previsaoService;

    public PainelClima(PrevisaoService previsaoService) {
        this.previsaoService = previsaoService;
    }

    public void exibir(String cidade) {
        int temperatura = previsaoService.obterTemperatura(cidade);
        System.out.println("Temperatura em " + cidade + ": " + temperatura + "°C");
    }
}

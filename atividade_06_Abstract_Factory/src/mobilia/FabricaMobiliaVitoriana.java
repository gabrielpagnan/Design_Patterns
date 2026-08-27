package mobilia;

/** ConcreteFactory: produz apenas moveis vitorianos. */
public class FabricaMobiliaVitoriana implements FabricaMobilia {

    @Override
    public Cadeira criarCadeira() {
        return new CadeiraVitoriana();
    }

    @Override
    public Sofa criarSofa() {
        return new SofaVitoriano();
    }

    @Override
    public MesaDeCentro criarMesaDeCentro() {
        return new MesaDeCentroVitoriana();
    }

    @Override
    public String getEstilo() {
        return "Vitoriano";
    }
}

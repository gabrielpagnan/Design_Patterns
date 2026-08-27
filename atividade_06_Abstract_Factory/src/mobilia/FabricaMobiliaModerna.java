package mobilia;

/** ConcreteFactory: produz apenas moveis modernos. */
public class FabricaMobiliaModerna implements FabricaMobilia {

    @Override
    public Cadeira criarCadeira() {
        return new CadeiraModerna();
    }

    @Override
    public Sofa criarSofa() {
        return new SofaModerno();
    }

    @Override
    public MesaDeCentro criarMesaDeCentro() {
        return new MesaDeCentroModerna();
    }

    @Override
    public String getEstilo() {
        return "Moderno";
    }
}

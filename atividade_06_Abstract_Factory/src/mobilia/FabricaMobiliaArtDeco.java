package mobilia;

/**
 * ConcreteFactory: produz apenas moveis art deco.
 *
 * Esta e a familia extra do enunciado, e serve para mostrar o OCP: ela foi
 * acrescentada sem alterar uma linha das outras fabricas nem do
 * ConfiguradorDeSala.
 */
public class FabricaMobiliaArtDeco implements FabricaMobilia {

    @Override
    public Cadeira criarCadeira() {
        return new CadeiraArtDeco();
    }

    @Override
    public Sofa criarSofa() {
        return new SofaArtDeco();
    }

    @Override
    public MesaDeCentro criarMesaDeCentro() {
        return new MesaDeCentroArtDeco();
    }

    @Override
    public String getEstilo() {
        return "Art Deco";
    }
}

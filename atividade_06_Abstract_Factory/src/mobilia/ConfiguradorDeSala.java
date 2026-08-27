package mobilia;

/**
 * Client: monta o kit de sala de estar.
 *
 * Repare que esta classe nao tem nenhum "new CadeiraModerna()" da vida. Ela so
 * conhece a FabricaMobilia e as tres interfaces de produto, entao nem teria
 * como misturar estilos: as pecas vem todas da mesma fabrica que ela recebeu.
 */
public class ConfiguradorDeSala {

    private final Cadeira cadeira;
    private final Sofa sofa;
    private final MesaDeCentro mesaDeCentro;
    private final String estiloDaFabrica;

    public ConfiguradorDeSala(FabricaMobilia fabrica) {
        this.cadeira = fabrica.criarCadeira();
        this.sofa = fabrica.criarSofa();
        this.mesaDeCentro = fabrica.criarMesaDeCentro();
        this.estiloDaFabrica = fabrica.getEstilo();
    }

    /** Mostra as tres pecas do kit. */
    public void exibir() {
        System.out.println("Kit de sala de estar - estilo " + estiloDaFabrica);

        cadeira.assentar();
        sofa.deitar();
        mesaDeCentro.apoiar();

        System.out.println("  Conferencia: cadeira=" + cadeira.getEstilo()
                + ", sofa=" + sofa.getEstilo()
                + ", mesa=" + mesaDeCentro.getEstilo()
                + " -> " + (estaCoerente() ? "kit coerente" : "KIT MISTURADO!"));
    }

    /** So para provar no console que as tres pecas sairam da mesma familia. */
    public boolean estaCoerente() {
        return cadeira.getEstilo().equals(sofa.getEstilo())
                && sofa.getEstilo().equals(mesaDeCentro.getEstilo());
    }
}

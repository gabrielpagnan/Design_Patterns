package mobilia;

/** ConcreteProduct: sofa da familia moderna. */
public class SofaModerno implements Sofa {

    @Override
    public void deitar() {
        System.out.println("  [SOFA] Moderno: linhas retas, tecido cinza e encosto baixo.");
    }

    @Override
    public String getEstilo() {
        return "Moderno";
    }
}

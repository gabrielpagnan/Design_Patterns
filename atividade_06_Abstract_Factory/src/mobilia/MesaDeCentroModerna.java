package mobilia;

/** ConcreteProduct: mesa de centro da familia moderna. */
public class MesaDeCentroModerna implements MesaDeCentro {

    @Override
    public void apoiar() {
        System.out.println("  [MESA] Moderna: tampo de vidro temperado com base minimalista.");
    }

    @Override
    public String getEstilo() {
        return "Moderno";
    }
}

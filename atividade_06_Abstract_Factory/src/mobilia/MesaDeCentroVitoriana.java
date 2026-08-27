package mobilia;

/** ConcreteProduct: mesa de centro da familia vitoriana. */
public class MesaDeCentroVitoriana implements MesaDeCentro {

    @Override
    public void apoiar() {
        System.out.println("  [MESA] Vitoriana: mogno macico com detalhes em marchetaria.");
    }

    @Override
    public String getEstilo() {
        return "Vitoriano";
    }
}

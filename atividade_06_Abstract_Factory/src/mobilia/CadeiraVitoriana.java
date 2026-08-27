package mobilia;

/** ConcreteProduct: cadeira da familia vitoriana. */
public class CadeiraVitoriana implements Cadeira {

    @Override
    public void assentar() {
        System.out.println("  [CADEIRA] Vitoriana: madeira entalhada, estofado em veludo bordo.");
    }

    @Override
    public String getEstilo() {
        return "Vitoriano";
    }
}

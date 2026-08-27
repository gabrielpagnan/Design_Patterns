package mobilia;

/** ConcreteProduct: sofa da familia vitoriana. */
public class SofaVitoriano implements Sofa {

    @Override
    public void deitar() {
        System.out.println("  [SOFA] Vitoriano: bracos curvos, capitone e pes torneados.");
    }

    @Override
    public String getEstilo() {
        return "Vitoriano";
    }
}

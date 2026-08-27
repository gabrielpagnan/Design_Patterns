package mobilia;

/** ConcreteProduct: cadeira da familia moderna. */
public class CadeiraModerna implements Cadeira {

    @Override
    public void assentar() {
        System.out.println("  [CADEIRA] Moderna: assento de couro branco sobre pes de aco escovado.");
    }

    @Override
    public String getEstilo() {
        return "Moderno";
    }
}

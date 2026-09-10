package veiculos;

/** Abstração refinada: sedan. */
public class Sedan extends Veiculo {

    public Sedan(Motor motor) {
        super(motor);
    }

    @Override
    public void dirigir() {
        System.out.println("Dirigindo um Sedan (porta-malas separado, foco em conforto):");
        super.dirigir();
    }
}

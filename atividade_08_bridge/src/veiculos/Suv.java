package veiculos;

/** Abstração refinada: SUV. */
public class Suv extends Veiculo {

    public Suv(Motor motor) {
        super(motor);
    }

    @Override
    public void dirigir() {
        System.out.println("Dirigindo um SUV (altura elevada, tração nas quatro rodas):");
        super.dirigir();
    }
}

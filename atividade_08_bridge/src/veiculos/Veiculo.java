package veiculos;

/**
 * Abstração (Abstraction) do padrão Bridge.
 *
 * O veículo não sabe como a força é gerada: ele guarda uma referência a um
 * Motor e delega a ele. Essa referência é a "ponte" entre as duas hierarquias.
 */
public abstract class Veiculo {

    protected Motor motor;

    protected Veiculo(Motor motor) {
        this.motor = motor;
    }

    public void dirigir() {
        motor.ligar();
    }

    public void acelerar() {
        motor.acelerar();
    }
}

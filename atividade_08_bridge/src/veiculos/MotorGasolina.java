package veiculos;

/** Implementação concreta: motor a combustão. */
public class MotorGasolina implements Motor {

    @Override
    public void ligar() {
        System.out.println("  Motor a gasolina ligado: ignição e marcha lenta em 800 rpm.");
    }

    @Override
    public void acelerar() {
        System.out.println("  Motor a gasolina acelerando: trocando marchas até 4000 rpm.");
    }
}

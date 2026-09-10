package veiculos;

/** Implementação concreta: motor elétrico. */
public class MotorEletrico implements Motor {

    @Override
    public void ligar() {
        System.out.println("  Motor elétrico ligado: sistema pronto, sem ruído.");
    }

    @Override
    public void acelerar() {
        System.out.println("  Motor elétrico acelerando: torque instantâneo, sem trocas de marcha.");
    }
}

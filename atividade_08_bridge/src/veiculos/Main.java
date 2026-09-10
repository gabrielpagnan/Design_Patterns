package veiculos;

public class Main {

    public static void main(String[] args) {
        // Os dois lados da ponte são criados separadamente...
        Motor gasolina = new MotorGasolina();
        Motor eletrico = new MotorEletrico();

        // ...e combinados na hora (composição), sem uma classe por combinação.
        Veiculo[] frota = {
                new Sedan(gasolina),
                new Sedan(eletrico),
                new Suv(gasolina),
                new Suv(eletrico)
        };

        for (Veiculo veiculo : frota) {
            veiculo.dirigir();
            veiculo.acelerar();
            System.out.println();
        }

        System.out.println("O mesmo Sedan roda com motor a gasolina e com motor elétrico;");
        System.out.println("o mesmo MotorGasolina roda em Sedan e em SUV.");

        // Para adicionar um MotorHibrido, basta criar uma classe que implemente
        // Motor: nenhum veículo é alterado. Para adicionar uma Picape, basta
        // estender Veiculo: nenhum motor é alterado.
    }
}

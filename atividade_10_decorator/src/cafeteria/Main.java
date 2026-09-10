package cafeteria;

import java.util.List;
import java.util.Locale;

public class Main {

    private static final Locale BR = Locale.of("pt", "BR");

    public static void main(String[] args) {
        // Todas as combinações são montadas empilhando decorators sobre o Cafe.
        Bebida cafePuro = new Cafe();
        Bebida cafeComLeite = new Leite(new Cafe());
        Bebida cafeComLeiteEChantilly = new Chantilly(new Leite(new Cafe()));

        System.out.println("=== Combinações ===");
        // A variável é sempre do tipo Bebida: o cliente não sabe quantas camadas existem.
        for (Bebida bebida : List.of(cafePuro, cafeComLeite, cafeComLeiteEChantilly)) {
            exibir(bebida);
        }

        System.out.println();
        System.out.println("=== A ordem dos complementos aparece na descrição ===");
        exibir(new Chantilly(new Leite(new Cafe())));
        exibir(new Leite(new Chantilly(new Cafe())));

        System.out.println();
        System.out.println("=== O custo soma camada por camada ===");
        System.out.println("Cafe                    = " + formatar(cafePuro.custo()));
        System.out.println("Leite(Cafe)             = " + formatar(cafeComLeite.custo())
                + "  (5,00 + 1,50)");
        System.out.println("Chantilly(Leite(Cafe))  = " + formatar(cafeComLeiteEChantilly.custo())
                + "  (5,00 + 1,50 + 2,00)");

        System.out.println();
        System.out.println("=== Complemento novo, sem alterar as classes antigas ===");
        exibir(new CaldaDeCaramelo(new Chantilly(new Leite(new Cafe()))));
    }

    private static void exibir(Bebida bebida) {
        System.out.println(bebida.getDescricao() + " -> " + formatar(bebida.custo()));
    }

    private static String formatar(double valor) {
        return String.format(BR, "R$ %.2f", valor);
    }
}

package restaurante;

import java.util.List;
import java.util.Locale;

public class Main {

    private static final Locale BR = Locale.of("pt", "BR");

    public static void main(String[] args) {
        // Folhas: pratos individuais.
        ItemMenu hamburguer = new Prato("Hambúrguer", 18.00);
        ItemMenu batataFrita = new Prato("Batata frita", 12.00);
        ItemMenu refrigerante = new Prato("Refrigerante", 8.00);

        // Composto: um combo formado só por pratos.
        Combo comboDuplo = new Combo("Combo Duplo");
        comboDuplo.adicionar(hamburguer);
        comboDuplo.adicionar(hamburguer);
        comboDuplo.adicionar(refrigerante);

        // Composto que contém outro composto: aqui aparece a recursão.
        Combo comboFamilia = new Combo("Combo Família");
        comboFamilia.adicionar(comboDuplo);
        comboFamilia.adicionar(batataFrita);
        comboFamilia.adicionar(batataFrita);
        comboFamilia.adicionar(refrigerante);
        comboFamilia.adicionar(refrigerante);

        System.out.println("=== O mesmo getPreco() para prato e para combo ===");
        // A lista mistura folha e composto, mas o laço só conhece ItemMenu.
        for (ItemMenu item : List.of(hamburguer, comboDuplo, comboFamilia)) {
            exibir(item);
        }

        System.out.println();
        System.out.println("=== Estrutura do Combo Família ===");
        comboFamilia.imprimir("");

        System.out.println();
        System.out.println("=== O total do Combo Família já inclui o sub-combo ===");
        System.out.println("Combo Duplo dentro do Combo Família = " + formatar(comboDuplo.getPreco())
                + "  (18,00 + 18,00 + 8,00)");
        System.out.println("Combo Família                       = " + formatar(comboFamilia.getPreco())
                + "  (44,00 do sub-combo + 12,00 + 12,00 + 8,00 + 8,00)");

        comboFamilia.remover(comboDuplo);
        System.out.println("Sem o sub-combo                     = " + formatar(comboFamilia.getPreco())
                + "  (o total caiu exatamente os 44,00 do Combo Duplo)");
        comboFamilia.adicionar(comboDuplo);

        System.out.println();
        System.out.println("=== Item novo (BebidaAlcoolica) sem alterar Combo nem os pratos ===");
        Combo happyHour = new Combo("Combo Happy Hour");
        happyHour.adicionar(new BebidaAlcoolica("Chopp", 14.00));
        happyHour.adicionar(batataFrita);
        happyHour.imprimir("");
        exibir(happyHour);
    }

    private static void exibir(ItemMenu item) {
        System.out.println(item.getNome() + " -> " + formatar(item.getPreco()));
    }

    private static String formatar(double valor) {
        return String.format(BR, "R$ %.2f", valor);
    }
}

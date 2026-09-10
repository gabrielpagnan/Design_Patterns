package restaurante;

import java.util.Locale;

/**
 * Folha (Leaf) do Composite: não tem filhos, então o preço é o dele mesmo e a
 * recursão termina aqui.
 */
public class Prato implements ItemMenu {

    private final String nome;
    private final double preco;

    public Prato(String nome, double preco) {
        this.nome = nome;
        this.preco = preco;
    }

    @Override
    public String getNome() {
        return nome;
    }

    @Override
    public double getPreco() {
        return preco;
    }

    @Override
    public void imprimir(String recuo) {
        System.out.printf(Locale.of("pt", "BR"), "%s- %s: R$ %.2f%n", recuo, nome, getPreco());
    }
}

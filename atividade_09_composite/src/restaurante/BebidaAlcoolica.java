package restaurante;

import java.util.Locale;

/**
 * Folha adicionada depois, para mostrar a extensibilidade do padrão.
 *
 * O preço tem uma regra própria (imposto sobre a bebida), mas nem Combo nem
 * Main precisaram ser alterados: basta implementar ItemMenu.
 */
public class BebidaAlcoolica implements ItemMenu {

    private static final double IMPOSTO = 0.25;

    private final String nome;
    private final double precoBase;

    public BebidaAlcoolica(String nome, double precoBase) {
        this.nome = nome;
        this.precoBase = precoBase;
    }

    @Override
    public String getNome() {
        return nome;
    }

    @Override
    public double getPreco() {
        return precoBase * (1 + IMPOSTO);
    }

    @Override
    public void imprimir(String recuo) {
        System.out.printf(Locale.of("pt", "BR"), "%s- %s: R$ %.2f (base R$ %.2f + imposto de %.0f%%)%n",
                recuo, nome, getPreco(), precoBase, IMPOSTO * 100);
    }
}

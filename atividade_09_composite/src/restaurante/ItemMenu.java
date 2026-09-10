package restaurante;

/**
 * Component do padrão Composite.
 *
 * É o tipo único que o cliente enxerga: tanto um prato (folha) quanto um combo
 * (composto) são um ItemMenu, então nenhum código de fora precisa perguntar
 * "isso é um prato ou um combo?" antes de pedir o preço.
 */
public interface ItemMenu {

    String getNome();

    double getPreco();

    /**
     * Extra (além do que o enunciado pede): imprime o item na tela.
     *
     * Está aqui pelo mesmo motivo de getPreco(): cada tipo sabe se imprimir, e
     * o cliente não precisa de instanceof para percorrer a árvore.
     */
    void imprimir(String recuo);
}

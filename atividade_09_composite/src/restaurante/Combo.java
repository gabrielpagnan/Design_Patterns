package restaurante;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Composto (Composite): guarda uma lista de ItemMenu, que pode conter pratos,
 * outros combos ou qualquer folha criada no futuro.
 *
 * A recursão mora aqui dentro: getPreco() chama getPreco() dos filhos sem saber
 * o tipo concreto de cada um.
 */
public class Combo implements ItemMenu {

    private final String nome;
    private final List<ItemMenu> itens = new ArrayList<>();

    public Combo(String nome) {
        this.nome = nome;
    }

    public void adicionar(ItemMenu item) {
        itens.add(item);
    }

    public void remover(ItemMenu item) {
        itens.remove(item);
    }

    @Override
    public String getNome() {
        return nome;
    }

    @Override
    public double getPreco() {
        double total = 0;
        for (ItemMenu item : itens) {
            // Se o filho for um sub-combo, ele repete esse mesmo laço nos filhos dele.
            total += item.getPreco();
        }
        return total;
    }

    @Override
    public void imprimir(String recuo) {
        System.out.printf(Locale.of("pt", "BR"), "%s+ %s (total R$ %.2f)%n", recuo, nome, getPreco());
        for (ItemMenu item : itens) {
            item.imprimir(recuo + "   ");
        }
    }
}

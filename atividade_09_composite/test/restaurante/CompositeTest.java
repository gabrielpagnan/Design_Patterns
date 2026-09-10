package restaurante;

public class CompositeTest {

    public static void main(String[] args) {
        ItemMenu hamburguer = new Prato("Hambúrguer", 18.00);
        ItemMenu batataFrita = new Prato("Batata frita", 12.00);
        ItemMenu refrigerante = new Prato("Refrigerante", 8.00);

        // Folha: o preço é o dela mesma.
        assert hamburguer.getNome().equals("Hambúrguer");
        assert hamburguer.getPreco() == 18.00;

        Combo comboDuplo = new Combo("Combo Duplo");
        comboDuplo.adicionar(hamburguer);
        comboDuplo.adicionar(hamburguer);
        comboDuplo.adicionar(refrigerante);

        assert comboDuplo.getNome().equals("Combo Duplo");
        assert comboDuplo.getPreco() == 44.00;

        Combo comboFamilia = new Combo("Combo Família");
        comboFamilia.adicionar(comboDuplo);
        comboFamilia.adicionar(batataFrita);
        comboFamilia.adicionar(batataFrita);
        comboFamilia.adicionar(refrigerante);
        comboFamilia.adicionar(refrigerante);

        // O total do composto inclui o preço do sub-combo (recursão).
        assert comboFamilia.getPreco() == 84.00;

        comboFamilia.remover(comboDuplo);
        assert comboFamilia.getPreco() == 40.00;
        comboFamilia.adicionar(comboDuplo);
        assert comboFamilia.getPreco() == 84.00;

        // Combo vazio custa zero e continua sendo um ItemMenu válido.
        ItemMenu comboVazio = new Combo("Combo Vazio");
        assert comboVazio.getPreco() == 0.0;

        // Folha nova entra na árvore sem alterar Combo.
        ItemMenu chopp = new BebidaAlcoolica("Chopp", 14.00);
        assert chopp.getPreco() == 17.50;

        Combo happyHour = new Combo("Combo Happy Hour");
        happyHour.adicionar(chopp);
        happyHour.adicionar(batataFrita);
        assert happyHour.getPreco() == 29.50;

        // Combos aninhados em mais de um nível continuam somando.
        Combo festa = new Combo("Combo Festa");
        festa.adicionar(comboFamilia);
        festa.adicionar(happyHour);
        assert festa.getPreco() == 113.50;

        System.out.println("Todos os testes passaram.");
    }
}

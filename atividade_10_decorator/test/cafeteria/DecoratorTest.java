package cafeteria;

public class DecoratorTest {

    public static void main(String[] args) {
        Bebida cafe = new Cafe();
        assert cafe.getDescricao().equals("Café");
        assert cafe.custo() == 5.0;

        Bebida comLeite = new Leite(new Cafe());
        assert comLeite.getDescricao().equals("Café com leite");
        assert comLeite.custo() == 6.5;

        Bebida comLeiteEChantilly = new Chantilly(new Leite(new Cafe()));
        assert comLeiteEChantilly.getDescricao().equals("Café com leite com chantilly");
        assert comLeiteEChantilly.custo() == 8.5;

        // A ordem muda a descrição, mas não o custo total.
        Bebida ordemInvertida = new Leite(new Chantilly(new Cafe()));
        assert ordemInvertida.getDescricao().equals("Café com chantilly com leite");
        assert ordemInvertida.custo() == comLeiteEChantilly.custo();

        // Complemento adicionado depois continua funcionando sobre qualquer Bebida.
        Bebida completo = new CaldaDeCaramelo(comLeiteEChantilly);
        assert completo.getDescricao().equals("Café com leite com chantilly com calda de caramelo");
        assert completo.custo() == 11.5;

        // Decorators podem ser empilhados repetidas vezes.
        Bebida leiteDobrado = new Leite(new Leite(new Cafe()));
        assert leiteDobrado.custo() == 8.0;

        System.out.println("Todos os testes passaram.");
    }
}

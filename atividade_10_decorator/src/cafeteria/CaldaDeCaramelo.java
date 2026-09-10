package cafeteria;

// Complemento adicionado depois dos outros. Para criá-lo não foi preciso
// alterar Cafe, Leite, Chantilly nem a interface Bebida.
public class CaldaDeCaramelo extends Complemento {

    private static final double PRECO = 3.0;

    public CaldaDeCaramelo(Bebida bebida) {
        super(bebida);
    }

    @Override
    public String getDescricao() {
        return bebida.getDescricao() + " com calda de caramelo";
    }

    @Override
    public double custo() {
        return bebida.custo() + PRECO;
    }
}

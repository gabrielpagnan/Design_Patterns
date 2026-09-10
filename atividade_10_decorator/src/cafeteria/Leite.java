package cafeteria;

public class Leite extends Complemento {

    private static final double PRECO = 1.5;

    public Leite(Bebida bebida) {
        super(bebida);
    }

    @Override
    public String getDescricao() {
        return bebida.getDescricao() + " com leite";
    }

    @Override
    public double custo() {
        return bebida.custo() + PRECO;
    }
}

package cafeteria;

public class Chantilly extends Complemento {

    private static final double PRECO = 2.0;

    public Chantilly(Bebida bebida) {
        super(bebida);
    }

    @Override
    public String getDescricao() {
        return bebida.getDescricao() + " com chantilly";
    }

    @Override
    public double custo() {
        return bebida.custo() + PRECO;
    }
}

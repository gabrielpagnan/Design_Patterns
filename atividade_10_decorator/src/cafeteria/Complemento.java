package cafeteria;

public abstract class Complemento implements Bebida {

    protected final Bebida bebida;

    protected Complemento(Bebida bebida) {
        this.bebida = bebida;
    }

    @Override
    public abstract String getDescricao();

    @Override
    public abstract double custo();
}

package cafeteria;

public class Cafe implements Bebida {

    private static final double PRECO_BASE = 5.0;

    @Override
    public String getDescricao() {
        return "Café";
    }

    @Override
    public double custo() {
        return PRECO_BASE;
    }
}

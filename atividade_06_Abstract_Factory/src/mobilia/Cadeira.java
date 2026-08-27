package mobilia;

/**
 * AbstractProduct: contrato da cadeira.
 * O cliente conhece apenas este tipo, nunca CadeiraModerna ou CadeiraVitoriana.
 */
public interface Cadeira {

    void assentar();

    /** Estilo da peca, usado so para conferir a coerencia do kit. */
    String getEstilo();
}

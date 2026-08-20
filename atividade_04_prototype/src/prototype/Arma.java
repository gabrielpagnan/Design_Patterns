package prototype;

/**
 * Objeto MUTAVEL usado como campo de Inimigo.
 * E justamente por ser mutavel que ele exige copia PROFUNDA:
 * se dois inimigos compartilharem a mesma instancia, mexer em um
 * altera o outro (aliasing).
 */
public class Arma {

    private String nome;
    private double bonusDano;

    public Arma(String nome, double bonusDano) {
        this.nome = nome;
        this.bonusDano = bonusDano;
    }

    /** Construtor de copia da Arma. */
    public Arma(Arma base) {
        this.nome = base.nome;
        this.bonusDano = base.bonusDano;
    }

    /** Devolve uma Arma NOVA e independente do original. */
    public Arma clonar() {
        return new Arma(this);
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public double getBonusDano() {
        return bonusDano;
    }

    public void setBonusDano(double bonusDano) {
        this.bonusDano = bonusDano;
    }

    @Override
    public String toString() {
        return String.format("%s (+%.0f dano) [id=%s]", nome, bonusDano, idDe(this));
    }

    /** Identidade da instancia, para provar na saida quem e quem. */
    static String idDe(Object o) {
        return o == null ? "null" : Integer.toHexString(System.identityHashCode(o));
    }
}

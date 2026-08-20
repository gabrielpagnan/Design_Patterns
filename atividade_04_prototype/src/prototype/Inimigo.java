package prototype;

/**
 * ConcretePrototype: sabe se copiar sozinho.
 * A logica de copia mora AQUI dentro, e nao espalhada pelo cliente.
 */
public class Inimigo implements InimigoPrototype {

    private String tipo;
    private double vida;
    private double dano;
    private Arma arma;

    /** Construtor normal, usado so para montar os prototipos-base. */
    public Inimigo(String tipo, double vida, double dano, Arma arma) {
        this.tipo = tipo;
        this.vida = vida;
        this.dano = dano;
        this.arma = arma;
    }

    /**
     * CONSTRUTOR DE COPIA.
     * Campos simples sao copiados por valor; a Arma, por ser um objeto
     * mutavel, e clonada (COPIA PROFUNDA) para que o clone nao divida
     * a mesma instancia com o original.
     */
    public Inimigo(Inimigo base) {
        this.tipo = base.tipo;
        this.vida = base.vida;
        this.dano = base.dano;
        this.arma = (base.arma == null) ? null : base.arma.clonar();
    }

    /** Prototype: delega ao construtor de copia. */
    @Override
    public Inimigo clonar() {
        return new Inimigo(this);
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public double getVida() {
        return vida;
    }

    public void setVida(double vida) {
        this.vida = vida;
    }

    public double getDano() {
        return dano;
    }

    public void setDano(double dano) {
        this.dano = dano;
    }

    public Arma getArma() {
        return arma;
    }

    public void setArma(Arma arma) {
        this.arma = arma;
    }

    @Override
    public String toString() {
        return String.format("Inimigo[id=%s] %-9s vida=%-6.0f dano=%-5.0f arma=%s",
                Arma.idDe(this), tipo, vida, dano, arma);
    }
}

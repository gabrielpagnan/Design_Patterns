package veiculos;

/**
 * Implementador (Implementor) do padrão Bridge.
 *
 * Representa a dimensão "motor", que varia de forma independente da
 * dimensão "tipo de veículo".
 */
public interface Motor {

    void ligar();

    void acelerar();
}

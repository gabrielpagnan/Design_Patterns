package mobilia;

/**
 * AbstractFactory: declara a criacao da familia inteira de moveis.
 *
 * A garantia do padrao esta aqui: quem implementa esta interface e obrigado a
 * responder pelos tres metodos, e cada implementacao devolve so pecas do seu
 * proprio estilo. O cliente pede "uma cadeira, um sofa e uma mesa" sem nunca
 * dizer de qual estilo.
 */
public interface FabricaMobilia {

    Cadeira criarCadeira();

    Sofa criarSofa();

    MesaDeCentro criarMesaDeCentro();

    /** Nome do estilo, so para exibicao. */
    String getEstilo();
}

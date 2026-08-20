package prototype;

/**
 * Contrato de clonagem do padrao Prototype (o "Prototype" do GoF).
 * O cliente conversa SO com esta interface: pede uma copia sem
 * saber qual e a classe concreta que sera instanciada.
 */
public interface InimigoPrototype {

    /** Retorna uma copia independente deste objeto, pronta para uso. */
    InimigoPrototype clonar();
}

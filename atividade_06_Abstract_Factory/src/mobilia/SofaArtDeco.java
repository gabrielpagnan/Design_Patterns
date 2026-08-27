package mobilia;

/** ConcreteProduct: sofa da familia art deco. */
public class SofaArtDeco implements Sofa {

    @Override
    public void deitar() {
        System.out.println("  [SOFA] Art Deco: veludo verde esmeralda e formas geometricas.");
    }

    @Override
    public String getEstilo() {
        return "Art Deco";
    }
}

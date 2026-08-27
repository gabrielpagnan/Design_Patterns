package mobilia;

/** ConcreteProduct: mesa de centro da familia art deco. */
public class MesaDeCentroArtDeco implements MesaDeCentro {

    @Override
    public void apoiar() {
        System.out.println("  [MESA] Art Deco: tampo em marmore preto com base espelhada.");
    }

    @Override
    public String getEstilo() {
        return "Art Deco";
    }
}

package mobilia;

/** ConcreteProduct: cadeira da familia art deco (familia extra). */
public class CadeiraArtDeco implements Cadeira {

    @Override
    public void assentar() {
        System.out.println("  [CADEIRA] Art Deco: encosto em leque com acabamento dourado.");
    }

    @Override
    public String getEstilo() {
        return "Art Deco";
    }
}

package mobilia;

/**
 * Classe de teste.
 *
 * E se alguem quisesse misturar os estilos (uma cadeira moderna com um sofa
 * vitoriano)? Passando pelo ConfiguradorDeSala isso simplesmente nao acontece:
 * ele recebe UMA fabrica e pega as tres pecas dela. Para misturar seria preciso
 * voltar a dar "new" nas classes concretas espalhado pelo codigo - que e
 * exatamente o que o Abstract Factory veio tirar do caminho.
 */
public class Main {

    public static void main(String[] args) {
        // O mesmo codigo cliente montando as tres familias.
        montarSala(new FabricaMobiliaModerna());
        montarSala(new FabricaMobiliaVitoriana());
        montarSala(new FabricaMobiliaArtDeco());

        // ------------------------------------------------------------------
        // Troca de estilo da vitrine: UMA linha muda, o resto do sistema nao.
        // Era assim quando a vitrine era moderna:
        //     FabricaMobilia fabricaDaVitrine = new FabricaMobiliaModerna();
        // ------------------------------------------------------------------
        FabricaMobilia fabricaDaVitrine = new FabricaMobiliaVitoriana();

        System.out.println("=== Vitrine da loja ===");
        ConfiguradorDeSala vitrine = new ConfiguradorDeSala(fabricaDaVitrine);
        vitrine.exibir();
        System.out.println();

        System.out.println("O ConfiguradorDeSala e o Main nao dao 'new' em nenhuma peca.");
        System.out.println("Trocar a fabrica troca o kit inteiro de uma vez.");
    }

    private static void montarSala(FabricaMobilia fabrica) {
        System.out.println("=== Fabrica: " + fabrica.getClass().getSimpleName() + " ===");

        ConfiguradorDeSala sala = new ConfiguradorDeSala(fabrica);
        sala.exibir();

        System.out.println();
    }
}

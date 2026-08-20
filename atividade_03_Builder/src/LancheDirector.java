/**
 * DIRECTOR do padrao Builder.
 *
 * Encapsula as "receitas" do cardapio: sequencias conhecidas de passos sobre o
 * mesmo Builder. O cliente pede um lanche pelo nome e nao precisa conhecer a
 * combinacao de ingredientes. Cada receita usa um Builder novo, entao os
 * lanches produzidos sao independentes entre si.
 */
public class LancheDirector {

    /** Misto quente: o basico do cardapio. */
    public Lanche criarMisto() {
        return new Lanche.Builder()
                .setPao("Pao de forma")
                .setProteina("Presunto")
                .setQueijo("Mussarela")
                .build();
    }

    /** X-Salada: hamburguer com queijo, salada e maionese. */
    public Lanche criarXSalada() {
        return new Lanche.Builder()
                .setPao("Pao brioche")
                .setProteina("Hamburguer bovino 150g")
                .setQueijo("Cheddar")
                .addVegetal("Alface")
                .addVegetal("Tomate")
                .addVegetal("Cebola")
                .setMolho("Maionese da casa")
                .build();
    }

    /** Especial da casa: usa todos os passos disponiveis do Builder. */
    public Lanche criarEspecialDaCasa() {
        return new Lanche.Builder()
                .setPao("Pao australiano")
                .setProteina("Costela desfiada")
                .setQueijo("Gorgonzola")
                .addVegetal("Rucula")
                .addVegetal("Tomate seco")
                .addVegetal("Cebola caramelizada")
                .setMolho("Barbecue defumado")
                .setBemPassado(true)
                .setObservacoes("Servir com farofa de bacon a parte")
                .build();
    }

    /**
     * Mesma receita do X-Salada, porem vegetariana: demonstra que variar o
     * resultado nao exige um processo de montagem novo, apenas um preenchimento
     * diferente dos mesmos passos.
     */
    public Lanche criarVeggie() {
        return new Lanche.Builder()
                .setPao("Pao integral")
                .setProteina("Hamburguer de grao-de-bico")
                .addVegetal("Alface")
                .addVegetal("Tomate")
                .setMolho("Mostarda e mel")
                .setObservacoes("Sem queijo (vegano)")
                .build();
    }
}

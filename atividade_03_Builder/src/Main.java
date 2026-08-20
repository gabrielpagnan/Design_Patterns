import java.util.List;

/**
 * Classe de teste / demonstracao do padrao Builder.
 *
 * 1. Monta lanches pela API fluente (so obrigatorios e com varios opcionais)
 * 2. Monta lanches pelo Director e imprime a descricao de cada um
 * 3. Tenta montar sem campo obrigatorio e mostra o IllegalStateException
 * 4. Comprova a imutabilidade do produto
 */
public class Main {

    public static void main(String[] args) {
        titulo("1. MONTAGEM PELA API FLUENTE");

        // apenas os campos obrigatorios
        Lanche simples = new Lanche.Builder()
                .setPao("Pao frances")
                .setProteina("File de frango")
                .build();
        imprimir("Lanche simples (so obrigatorios)", simples);

        // varios opcionais, em ordem livre
        Lanche completo = new Lanche.Builder()
                .setPao("Pao de hamburguer")
                .setProteina("Hamburguer bovino 180g")
                .setBemPassado(true)
                .setQueijo("Cheddar duplo")
                .addVegetal("Alface americana")
                .addVegetal("Tomate")
                .addVegetal("Picles")
                .setMolho("Molho especial")
                .setObservacoes("Cortar ao meio")
                .build();
        imprimir("Lanche completo (todos os opcionais)", completo);

        // combinacao intermediaria: molho e observacao, sem queijo e sem vegetais.
        // Com construtores telescopicos isso exigiria passar null nas posicoes do
        // meio; aqui basta nao chamar os metodos correspondentes.
        Lanche intermediario = new Lanche.Builder()
                .setPao("Ciabatta")
                .setProteina("Pernil")
                .setMolho("Vinagrete")
                .setObservacoes("Sem sal adicional")
                .build();
        imprimir("Lanche intermediario (combinacao parcial)", intermediario);

        titulo("2. MONTAGEM PELO DIRECTOR");

        LancheDirector director = new LancheDirector();
        List<Lanche> cardapio = List.of(
                director.criarMisto(),
                director.criarXSalada(),
                director.criarEspecialDaCasa(),
                director.criarVeggie());

        String[] nomes = { "Misto", "X-Salada", "Especial da Casa", "Veggie" };
        for (int i = 0; i < cardapio.size(); i++) {
            imprimir(nomes[i], cardapio.get(i));
        }

        titulo("3. VALIDACAO DOS CAMPOS OBRIGATORIOS");

        // sem proteina
        try {
            new Lanche.Builder()
                    .setPao("Pao de forma")
                    .setQueijo("Prato")
                    .build();
            System.out.println("ERRO: deveria ter lancado excecao!");
        } catch (IllegalStateException e) {
            System.out.println("[OK] Tentativa sem proteina barrada pelo build():");
            System.out.println("     " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }

        // sem pao
        try {
            new Lanche.Builder()
                    .setProteina("File de frango")
                    .addVegetal("Alface")
                    .build();
            System.out.println("ERRO: deveria ter lancado excecao!");
        } catch (IllegalStateException e) {
            System.out.println("\n[OK] Tentativa sem pao barrada pelo build():");
            System.out.println("     " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }

        // string em branco tambem nao vale
        try {
            new Lanche.Builder()
                    .setPao("   ")
                    .setProteina("Costela")
                    .build();
            System.out.println("ERRO: deveria ter lancado excecao!");
        } catch (IllegalStateException e) {
            System.out.println("\n[OK] Pao em branco barrado pelo build():");
            System.out.println("     " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }

        titulo("4. IMUTABILIDADE DO PRODUTO");

        try {
            completo.getVegetais().add("Bacon");
            System.out.println("ERRO: a lista deveria ser imutavel!");
        } catch (UnsupportedOperationException e) {
            System.out.println("[OK] Nao foi possivel alterar a lista de vegetais do lanche");
            System.out.println("     pronto (UnsupportedOperationException). O produto e imutavel.");
        }

        // o Builder pode ser reutilizado sem contaminar o lanche ja construido
        Lanche.Builder builder = new Lanche.Builder()
                .setPao("Pao frances")
                .setProteina("Mortadela")
                .addVegetal("Alface");

        Lanche antes = builder.build();
        builder.addVegetal("Tomate").setMolho("Mostarda");
        Lanche depois = builder.build();

        System.out.println("\n[OK] Reuso do Builder apos o build():");
        System.out.println("     lanche construido antes  -> vegetais " + antes.getVegetais()
                + ", molho " + antes.getMolho());
        System.out.println("     lanche construido depois -> vegetais " + depois.getVegetais()
                + ", molho " + depois.getMolho());
        System.out.println("     O primeiro lanche nao foi afetado (copia defensiva).");

        System.out.println();
    }

    private static void titulo(String texto) {
        System.out.println();
        System.out.println("=".repeat(64));
        System.out.println(" " + texto);
        System.out.println("=".repeat(64));
        System.out.println();
    }

    private static void imprimir(String nome, Lanche lanche) {
        System.out.println("--- " + nome + " ---");
        System.out.print(lanche.getDescricao());
        System.out.println();
    }
}

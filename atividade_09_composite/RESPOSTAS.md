# Atividade 09 — Composite

## Exercício 1 — Aplicações

### 1. Explorador de arquivos

**Faz sentido usar Composite.** A estrutura é recursiva parte-todo: uma pasta contém arquivos ou outras pastas, sem limite de profundidade. O tamanho de uma pasta é a soma do tamanho dos filhos, então arquivo e pasta podem implementar o mesmo `getTamanho()` e o cliente não precisa saber com qual dos dois está lidando.

### 2. Cardápio digital

**Faz sentido usar Composite.** Uma seção pode conter itens ou subseções, o que é exatamente a hierarquia recursiva do padrão. Perguntar as calorias de uma seção resolve a árvore inteira sozinha, porque cada seção soma os filhos e cada item devolve o próprio valor.

### 3. Motor de interface gráfica

**Faz sentido usar Composite.** Painéis contêm controles simples ou outros painéis aninhados, e o requisito é justamente tratar todo mundo da mesma forma ao desenhar ou ocultar. Chamar `desenhar()` na janela principal repassa a chamada para toda a árvore de componentes.

### 4. Cadastro de produtos plano

**Não faz sentido usar Composite.** A lista é plana e o enunciado garante que nunca haverá itens compostos nem hierarquia. Criar interface comum, folha e composto para uma lista de nome, preço e estoque seria overengineering: uma `List<Produto>` resolve.

### 5. Rede de lojas

**Faz sentido usar Composite.** Loja, região, estado e país formam uma hierarquia parte-todo em que a loja é a folha e os demais níveis são compostos que agregam os filhos. Como é preciso somar o faturamento a partir de qualquer nó, todos os níveis implementam o mesmo `getFaturamento()` e a soma acontece recursivamente, sem `if` para cada nível.

## Exercício 2 — Analogia

Uma playlist de aplicativo de música. Uma playlist pode conter músicas soltas e também outras playlists: a playlist "Casamento" pode ter duas músicas avulsas e, dentro dela, as playlists "Cerimônia" e "Festa" — e "Festa" pode conter outra playlist menor de "Encerramento".

Para quem usa, música e playlist se comportam do mesmo jeito: existe um botão de play que funciona nos dois, e a duração aparece nos dois. A diferença é que a música responde com o próprio tempo e a playlist responde com a soma do que está dentro dela, incluindo as sub-playlists.

Isso simplifica a vida de quem usa porque ninguém precisa abrir a playlist para descobrir se ali dentro tem música ou outra playlist. Basta perguntar a duração da playlist "Casamento" que ela desce a árvore inteira e devolve o total, e o mesmo vale para arrastar, tocar ou compartilhar qualquer um dos dois.

## Exercício 3 — Anti-pattern

### a) Por que calcular o total fora dos objetos é um problema?

A lógica da árvore está no `Pedido`, e não nas classes que realmente formam a árvore. `Caixa` guarda os filhos, mas não sabe percorrê-los nem somar nada, então quem tem os dados não tem o comportamento.

Além disso, `List<Object>` aceita qualquer coisa e obriga o `Pedido` a descobrir o tipo com `instanceof` a cada item. Se outro trecho do sistema precisar percorrer a mesma estrutura — imprimir a nota, contar itens, aplicar desconto, calcular frete —, cada um vai reescrever a mesma recursão com a mesma cadeia de `if`. É a travessia duplicada em vários lugares, e basta um deles ficar desatualizado para os números divergirem.

### b) O que acontece ao adicionar um novo tipo?

Ao criar um `ProdutoComDesconto` ou um serviço de montagem, é preciso caçar todos os `instanceof` do sistema e acrescentar mais um ramo em cada um. O compilador não ajuda: nada avisa que um método ficou para trás.

O bug mais perigoso é o `return 0` no final do `calcularTotal`. Um tipo esquecido não gera erro, exceção nem aviso — ele simplesmente entra na conta valendo zero, e o pedido fecha com o total errado. Também é fácil o `calcularTotal` ser atualizado e o `imprimir` não, aí a nota mostra um item que não foi cobrado. Somando a isso a `List<Object>`, nada impede alguém de colocar uma `String` dentro de uma caixa.

### c) Solução usando Composite

```java
public interface ItemPedido {          // Component
    String getNome();
    double getPreco();
}

public class Produto implements ItemPedido {   // Leaf
    private final String nome;
    private final double preco;

    public Produto(String nome, double preco) {
        this.nome = nome;
        this.preco = preco;
    }

    public String getNome() { return nome; }

    public double getPreco() { return preco; }   // a recursão termina aqui
}

public class Caixa implements ItemPedido {     // Composite
    private final String nome;
    private final List<ItemPedido> itens = new ArrayList<>();

    public Caixa(String nome) { this.nome = nome; }

    public void adicionar(ItemPedido item) { itens.add(item); }

    public void remover(ItemPedido item) { itens.remove(item); }

    public String getNome() { return nome; }

    public double getPreco() {
        double total = 0;
        for (ItemPedido item : itens) {
            total += item.getPreco();   // não interessa se é produto ou caixa
        }
        return total;
    }
}
```

Os papéis ficam assim:

- **Component (`ItemPedido`)**: o contrato comum, o único tipo que o cliente enxerga;
- **Leaf (`Produto`)**: não tem filhos e devolve o próprio preço;
- **Composite (`Caixa`)**: guarda uma lista de `ItemPedido` e soma os filhos.

A recursão passa a morar dentro do `Caixa.getPreco()`, junto com os dados que ela percorre. O `Pedido` vira apenas `item.getPreco()`, sem `instanceof` e sem laço, porque a lista agora é de `ItemPedido` e cada objeto sabe responder por si. Para acrescentar um `ProdutoComDesconto`, basta criar mais uma classe que implemente `ItemPedido`: nem `Caixa` nem `Pedido` mudam.

## Exercício 4 — Implementação

O código está na pasta `src/restaurante`:

- `ItemMenu`: **Component**, com `getNome()`, `getPreco()` e `imprimir()`;
- `Prato`: **Leaf**, guarda nome e preço e devolve o próprio preço;
- `Combo`: **Composite**, guarda uma `List<ItemMenu>`, tem `adicionar()` / `remover()` e soma os filhos recursivamente;
- `BebidaAlcoolica`: folha extra, criada para demonstrar a extensibilidade;
- `Main`: monta a árvore e chama sempre `getPreco()` sobre `ItemMenu`.

O método `imprimir(String recuo)` não foi pedido no enunciado. Ele está na interface pelo mesmo motivo do `getPreco()`: assim a impressão da árvore também acontece dentro dos objetos, e a `Main` não precisa de `instanceof` para saber se deve descer um nível.

Para compilar e executar:

```bash
javac -encoding UTF-8 -d out src/restaurante/*.java
java -cp out restaurante.Main
```

Para rodar os testes:

```bash
javac -encoding UTF-8 -d out src/restaurante/*.java test/restaurante/*.java
java -ea -cp out restaurante.CompositeTest
```

A saída completa está em [`saida-execucao.txt`](saida-execucao.txt). O Combo Família custa R$ 84,00 e já inclui os R$ 44,00 do Combo Duplo que está dentro dele — ao remover o sub-combo, o total cai para R$ 40,00, mostrando que a soma desceu um nível na árvore.

### Como adicionar um novo tipo de item

Um item novo precisa apenas implementar `ItemMenu`. Foi o que a `BebidaAlcoolica` fez: ela tem uma regra própria de preço (25% de imposto sobre o preço base), mas para o resto do sistema continua sendo um `ItemMenu` como qualquer outro.

Nenhuma linha de `Combo` foi alterada, porque ele trabalha com a interface e não com os tipos concretos. A `Main` também não precisou mudar para que o combo somasse a bebida — só foi acrescentado um trecho no final para demonstrar o caso. É o Open/Closed na prática: o sistema fica aberto para novos tipos de item e fechado para alteração dos que já existem.

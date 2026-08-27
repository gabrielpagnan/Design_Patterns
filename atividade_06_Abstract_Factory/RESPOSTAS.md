# Atividade 06 — Padrão Abstract Factory

---

## Exercício 1 — Aplicações

### 1. Interface gráfica que troca de plataforma (Windows, macOS, Linux)
**Faz sentido usar Abstract Factory.**
Existe uma família inteira de produtos (botão, checkbox, janela) que precisa vir toda da mesma plataforma, e o próprio enunciado diz que misturar quebra a interface — é o caso clássico do padrão. Com uma fábrica por plataforma, o código que monta a tela pede as peças a uma única fábrica e nunca consegue combinar um botão do Windows com um checkbox do Linux.

### 2. Classe `Ponto(x, y)` instanciada dezenas de vezes por segundo
**Não faz sentido usar Abstract Factory.**
Não existe família nenhuma: é um objeto só, com dois campos, sem variantes a manter coerentes. Criar interface de produto, fábrica abstrata e fábrica concreta para devolver `new Ponto(x, y)` seria overengineering puro — e ainda por cima em um trecho sensível a desempenho, onde o construtor direto é o caminho mais barato.

### 3. Módulo de acesso a bancos com famílias por fornecedor
**Faz sentido usar Abstract Factory.**
Conexão, comando e transação formam uma família que **só funciona junta**: uma `TransacaoPostgreSQL` não tem como controlar uma `ConexaoMySQL`. A fábrica abstrata (algo como `FabricaBanco` com `criarConexao()`, `criarComando()` e `criarTransacao()`) garante que os três objetos venham sempre do mesmo fornecedor, e trocar de banco vira trocar a fábrica em um ponto só.

### 4. Loja de kits de móveis por estilo (moderno, vitoriano, art déco)
**Faz sentido usar Abstract Factory.**
Cadeira, sofá e mesa de centro são a família; moderno, vitoriano e art déco são as variantes. O cliente espera que as três peças combinem, então a criação não pode ficar espalhada em `new` pelo sistema — é justamente o exemplo implementado no Exercício 5.

### 5. Classe `Produto` com três campos, criada em um único ponto
**Não faz sentido usar Abstract Factory.**
Só existe um tipo de `Produto`, sem variantes e sem outros objetos que precisem ser coerentes com ele. Como a criação acontece em um lugar só, não há nem acoplamento a resolver nem troca de variante a prever: o construtor tradicional já é a solução certa.

**Resumindo:** o Abstract Factory se justifica quando há **famílias de objetos relacionados** e **mais de uma variante** dessas famílias (itens 1, 3 e 4). Nos itens 2 e 5 falta o requisito básico — não existe família, existe uma classe só —, então o padrão só acrescentaria classes sem resolver problema nenhum.

---

## Exercício 2 — Analogia

### A reforma de um banheiro com metais e louças da mesma linha

Quem já foi a uma loja de materiais de construção comprar as peças de um banheiro conhece a situação. Não se compra "uma torneira" solta: compra-se **uma linha** — torneira do lavatório, ducha, acabamento do registro, cuba, saboneteira e papeleira. A loja vende essas peças agrupadas em linhas: uma linha cromada mais clássica, uma linha preta fosca mais moderna, uma linha dourada.

Duas coisas acontecem se alguém resolve montar o banheiro pegando cada peça de uma linha diferente:

- **O visual não fecha.** Torneira dourada, ducha cromada e registro preto no mesmo box: cada peça é bonita sozinha, o conjunto fica esquisito.
- **Tem peça que nem encaixa.** O acabamento do registro é feito para a base daquele fabricante e daquela linha; pegar o acabamento de uma linha e tentar rosquear na base de outra simplesmente não serve, e a reforma para no meio.

Por isso o vendedor experiente não pergunta peça por peça. Ele pergunta **"qual linha você quer?"** e, a partir daí, monta o kit inteiro dentro dela. O cliente pede "torneira, ducha e acabamento", nunca "torneira dourada modelo tal".

Ligando com o padrão:

- a **loja** é a fábrica abstrata: sabe entregar torneira, ducha e acabamento, sem dizer de qual linha;
- **cada linha** (cromada, preta fosca, dourada) é uma fábrica concreta, e só entrega peça dela mesma;
- **torneira, ducha e acabamento** são os produtos abstratos — o cliente pensa nessas categorias, não nos modelos;
- o **cliente da reforma** escolhe a linha **uma vez** e recebe um banheiro coerente; trocar de linha é uma decisão só, não vinte;
- **misturar linhas** é o resultado incoerente que o padrão evita — e o pior é que, como cada peça funciona sozinha, o problema só aparece quando tudo já está instalado.

---

## Exercício 3 — Anti-pattern

### a) Por que criar os componentes com `new` aqui é um problema de design?

A `Aplicacao` deveria só **montar e exibir a tela**, mas o construtor dela virou também o lugar que decide qual sistema operacional está rodando e instancia as classes de cada plataforma. Os problemas:

- **Acoplamento com todas as classes concretas de uma vez.** Para compilar, a `Aplicacao` precisa conhecer `BotaoWindows`, `CheckboxWindows`, `BotaoLinux` e `CheckboxLinux`, mesmo que uma execução use apenas uma dessas famílias.
- **Duas responsabilidades na mesma classe** (fere o SRP): ela muda quando a tela muda *e* quando entra ou sai uma plataforma.
- **O `if/else` cresce sem parar** e, pior, tende a se repetir. Se em outro ponto do sistema existir um `if` parecido (para criar a janela, o menu, o ícone), cada plataforma nova tem que ser lembrada em todos eles.
- **A plataforma é uma `String` solta.** `"windows"`, `"Windows"` ou `"win"` compilam igual; o compilador não ajuda em nada.
- **Nada garante a coerência da família.** Como `BotaoWindows` e `BotaoLinux` são só "um `Botao`", o compilador aceita numa boa qualquer combinação — inclusive a errada, que é o que está no código do enunciado.

### b) Que bug a mistura do `linux` causa? E ao adicionar o `mac`?

**O bug da mistura.** No ramo do Linux, o código cria `BotaoLinux` mas `CheckboxWindows` (era para ser `CheckboxLinux`). Isso **compila sem um aviso sequer** e não lança exceção nenhuma: a tela simplesmente sai errada em produção. O usuário de Linux vê um botão com o visual do sistema dele e, ao lado, um checkbox com desenho, fonte, cores e tamanho do Windows. Além do visual quebrado, componentes de plataforma costumam depender de recursos do sistema (temas, fontes, APIs nativas, atalhos de teclado): o `CheckboxWindows` pode tentar usar algo que não existe no Linux e aí o erro aparece só em tempo de execução, na máquina do cliente. É o pior tipo de bug — silencioso, e que passa por qualquer revisão desatenta porque a linha errada é praticamente idêntica à certa.

**Adicionando o mac.** Seria preciso criar `BotaoMac` e `CheckboxMac` e **editar a `Aplicacao`**, acrescentando mais um `else if`. Isso viola o **OCP**: uma classe que já funcionava e já estava testada é reaberta por causa de uma plataforma que não tem nada a ver com ela, com risco de quebrar Windows ou Linux sem querer. E existe um problema que já está lá hoje: se a string não bater com nenhum `if` (um `"mac"` antes de a alteração ser feita, ou um `"Linux"` com maiúscula), `botao` e `checkbox` ficam `null` e o `exibir()` estoura um `NullPointerException` — que é o único erro do código que aparece na hora, e ainda assim longe da causa real.

### c) Solução com Abstract Factory

**1. Produtos abstratos** — as interfaces `Botao` (com `renderizar()`), `Checkbox` (com `alternar()`) e `Janela`. É só com esses tipos que a `Aplicacao` passa a trabalhar.

**2. Fábrica abstrata** — `GUIFactory`, declarando `criarBotao()`, `criarCheckbox()` e `criarJanela()`. Ela representa "uma família de componentes", sem dizer de qual sistema.

**3. Fábricas concretas** — `GUIFactoryWindows`, `GUIFactoryLinux` e `GUIFactoryMac`. Cada uma implementa os três métodos devolvendo **só as peças do seu SO**. A coerência da família fica concentrada em uma classe pequena, fácil de conferir de bater o olho — bem diferente de procurar `new` espalhado pelo sistema.

**4. O cliente recebe a fábrica pronta** (injeção de dependência), em vez de escolher com `if`:

```java
public class Aplicacao {

    private final Botao botao;
    private final Checkbox checkbox;

    public Aplicacao(GUIFactory fabrica) {
        this.botao = fabrica.criarBotao();
        this.checkbox = fabrica.criarCheckbox();
    }

    public void exibir() {
        botao.renderizar();
        checkbox.alternar();
    }
}
```

A escolha do sistema operacional sai da `Aplicacao` e vai para **um único ponto** de inicialização (o `main`, um arquivo de configuração ou um container de DI):

```java
GUIFactory fabrica = new GUIFactoryLinux();
new Aplicacao(fabrica).exibir();
```

Com isso: o `if/else` e a `String` somem; a mistura Windows + Linux deixa de ser possível, porque as duas peças vêm obrigatoriamente da mesma fábrica; e adicionar o mac passa a ser **três arquivos novos** (`BotaoMac`, `CheckboxMac`, `GUIFactoryMac`) sem tocar em nada que já existe — OCP respeitado.

---

## Exercício 4 — Exemplo real: `iluwatar/java-design-patterns`

Código conferido em `abstract-factory/src/main/java/com/iluwatar/abstractfactory/`.

```java
public interface KingdomFactory {

  Castle createCastle();

  King createKing();

  Army createArmy();
}
```

```java
public class ElfKingdomFactory implements KingdomFactory {

  @Override
  public Castle createCastle() {
    return new ElfCastle();
  }

  @Override
  public King createKing() {
    return new ElfKing();
  }

  @Override
  public Army createArmy() {
    return new ElfArmy();
  }
}
```

### a) Que família a `KingdomFactory` produz?

`KingdomFactory` é a **AbstractFactory** e produz a família de produtos que forma um reino: um **castelo**, um **rei** e um **exército**. Os métodos de criação são:

| Método | Produto abstrato devolvido |
|---|---|
| `createCastle()` | `Castle` |
| `createKing()` | `King` |
| `createArmy()` | `Army` |

Os três retornos são **abstrações** (interfaces), nunca `ElfCastle` ou `OrcKing` — é isso que deixa o cliente independente do reino escolhido. É a mesma estrutura da `FabricaMobilia` da aula: um método de criação por produto da família.

### b) O que o `ElfKingdomFactory` cria? E se um método devolvesse um produto orc?

Cada método devolve a peça élfica correspondente: `createCastle()` → `new ElfCastle()`, `createKing()` → `new ElfKing()`, `createArmy()` → `new ElfArmy()`. A fábrica concreta é a **única responsável pela coerência da família**.

Se, por engano, `createCastle()` devolvesse um `OrcCastle`, o código **compilaria normalmente** — `OrcCastle` também é um `Castle`, então a assinatura continua válida e o compilador não tem como perceber o erro. Não haveria exceção nenhuma em tempo de execução: o reino simplesmente sairia incoerente, com um rei elfo e um exército elfo comandando um castelo orc, e o `getDescription()` do castelo imprimindo `"This is the orcish castle!"` no meio das descrições élficas. É exatamente o mesmo bug silencioso do Exercício 3, e mostra bem onde mora a garantia do padrão: **ela é uma responsabilidade da fábrica concreta, não do sistema de tipos**. Por isso o repositório tem testes (`ElfKingdomFactoryTest`) conferindo peça por peça — é o único jeito de travar esse tipo de troca.

### c) O que muda no cliente para trocar de reino? Relação com o OCP

No exemplo, o cliente é o `App`, e o método que monta o reino é:

```java
public void createKingdom(final Kingdom.FactoryMaker.KingdomType kingdomType) {
  final KingdomFactory kingdomFactory = Kingdom.FactoryMaker.makeFactory(kingdomType);
  kingdom.setKing(kingdomFactory.createKing());
  kingdom.setCastle(kingdomFactory.createCastle());
  kingdom.setArmy(kingdomFactory.createArmy());
}
```

Para trocar de elfo para orc, muda **um argumento**: `createKingdom(KingdomType.ELF)` vira `createKingdom(KingdomType.ORC)`. Todo o corpo do método continua igual, porque ele conversa só com a interface `KingdomFactory` — não sabe e não precisa saber que existe `ElfKing` ou `OrcArmy`. Os três `set` seguem valendo para qualquer reino.

Isso é o **OCP** na prática. Para acrescentar um reino de anões, bastaria criar as classes novas (`DwarfCastle`, `DwarfKing`, `DwarfArmy` e `DwarfKingdomFactory`): nem o `App`, nem o `createKingdom`, nem as fábricas élfica e orc precisariam ser alterados — código *aberto para extensão e fechado para modificação*. Vale a ressalva honesta de que sobraria **um** ponto a editar: o `FactoryMaker`, que tem o `enum KingdomType` e o `switch` que decide qual fábrica instanciar. Mas repare na diferença para o Exercício 3: ali o `if/else` estava dentro da classe que monta a tela e se repetia por todo lado; aqui ele está isolado num único lugar, cuja função é justamente escolher a fábrica — e num sistema real esse ponto normalmente sai do código e vai para um arquivo de configuração ou um container de injeção de dependência.

---

## Exercício 5 — Implementação

Código em `src/mobilia/`. Saída da execução em `saida-execucao.txt`.

| Arquivo | Papel no padrão |
|---|---|
| `Cadeira.java`, `Sofa.java`, `MesaDeCentro.java` | AbstractProducts — `assentar()`, `deitar()` e `apoiar()` |
| `CadeiraModerna`, `SofaModerno`, `MesaDeCentroModerna` | ConcreteProducts — família moderna |
| `CadeiraVitoriana`, `SofaVitoriano`, `MesaDeCentroVitoriana` | ConcreteProducts — família vitoriana |
| `CadeiraArtDeco`, `SofaArtDeco`, `MesaDeCentroArtDeco` | ConcreteProducts — família art déco (a extra) |
| `FabricaMobilia.java` | AbstractFactory — `criarCadeira()`, `criarSofa()`, `criarMesaDeCentro()` |
| `FabricaMobiliaModerna`, `FabricaMobiliaVitoriana`, `FabricaMobiliaArtDeco` | ConcreteFactories — cada uma só produz o seu estilo |
| `ConfiguradorDeSala.java` | Cliente — recebe a fábrica no construtor e monta o kit |
| `Main.java` | Classe de teste — monta as três salas e troca a vitrine de estilo |

Além dos métodos pedidos, cada produto tem um `getEstilo()`. Ele não faz parte do padrão: serve só para o `ConfiguradorDeSala` imprimir no fim a linha de conferência mostrando que as três peças saíram mesmo da mesma família.

### Como executar

```bash
javac -encoding UTF-8 -d out src/mobilia/*.java
java -cp out mobilia.Main
```

(No macOS, com o OpenJDK do Homebrew fora do PATH: `export PATH="/opt/homebrew/opt/openjdk/bin:$PATH"`.)

### O que a saída mostra

As três salas são montadas pelo **mesmo** `ConfiguradorDeSala`, com o mesmo `exibir()`, e todas saem coerentes — a linha de conferência fecha com "kit coerente" nos três casos. A troca da vitrine de moderna para vitoriana acontece em **uma única linha** do `Main`:

```java
FabricaMobilia fabricaDaVitrine = new FabricaMobiliaVitoriana();
```

Nenhuma outra classe é tocada, porque nem o `Main` nem o `ConfiguradorDeSala` dão `new` em peça nenhuma.

### E se alguém tentasse misturar os estilos?

Se as peças fossem criadas com `new` espalhado (`new CadeiraModerna()` aqui, `new SofaVitoriano()` ali), **nada impediria a mistura**: o código compilaria, não haveria exceção, e o cliente da loja receberia em casa uma cadeira moderna com um sofá vitoriano — a loja só descobriria o problema na reclamação. É o mesmo bug silencioso do checkbox do Exercício 3 e do `OrcCastle` do Exercício 4.

O Abstract Factory evita isso porque **muda quem decide**. O `ConfiguradorDeSala` não conhece nenhuma classe concreta: ele recebe **uma** `FabricaMobilia` e tira dela as três peças. Para acabar com um kit misturado seria preciso ter duas fábricas ao mesmo tempo e pedir uma peça a cada uma — algo que nem passa perto do código que existe. A garantia de coerência fica concentrada em uma classe de vinte linhas (`FabricaMobiliaVitoriana`, por exemplo), que é fácil de revisar e testar, em vez de depender de cada desenvolvedor lembrar do estilo certo toda vez que precisar de um móvel.

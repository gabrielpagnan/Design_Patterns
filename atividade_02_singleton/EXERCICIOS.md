# Atividade 02 — Padrão Singleton

Respostas dos exercícios 1 a 4. O exercício 5 (implementação) está no [README.md](README.md)
e no código em [`src/`](src/).

---

## Exercício 1 — Aplicações

### 1.1 Serviço de configurações que carrega `application.properties`

**Faz sentido usar Singleton.**

As configurações são o exemplo clássico de estado global legítimo: o arquivo é único, o
conteúdo é imutável em tempo de execução e ler o disco uma vez por módulo seria desperdício
de I/O. Como o objeto é somente-leitura após a carga, não há problema de concorrência —
várias threads podem ler o mesmo mapa sem sincronização. A ressalva é para os testes:
convém expor as configurações por uma interface e injetá-la, para que um teste possa
substituir os valores sem depender do arquivo real.

### 1.2 Componente que gerencia conexões HTTP com timeouts e autenticação distintos

**Não faz sentido usar Singleton.**

O próprio enunciado já mata a ideia: se cada parte do sistema precisa de timeout e
credenciais diferentes, então não existe *uma* configuração única — existem várias, e
unicidade é justamente a premissa do Singleton. Forçar uma instância única levaria ou a um
objeto cheio de `if`s por chamador, ou a mutação de configuração compartilhada (uma parte do
sistema trocando o timeout e quebrando a outra). O correto é ter um cliente configurado por
contexto de uso (um por integração/API), possivelmente compartilhando por baixo um pool de
conexões — esse *pool*, sim, pode ser único.

### 1.3 Logger central usado por dezenas de classes

**Faz sentido usar Singleton.**

O logger disputa um recurso realmente único (o arquivo/console de saída), e centralizar a
escrita evita intercalação corrompida de linhas e múltiplos *file handles* sobre o mesmo
arquivo. Além disso é um serviço transversal, chamado de qualquer ponto do código, onde
injetar dependência em toda classe traria pouco benefício. É exatamente por isso que
frameworks como Log4j e SLF4J expõem o logger por método estático de fábrica — mas a
implementação precisa ser thread-safe, já que dezenas de classes escrevem em paralelo.

### 1.4 Classe que representa o usuário autenticado atual (sistema web multiusuário)

**Não faz sentido usar Singleton.**

O usuário autenticado é único **por sessão/requisição**, não por aplicação: em um servidor web
existem centenas de usuários simultâneos dentro da mesma JVM. Um Singleton faria o login do
último usuário sobrescrever o dos demais, com pessoas vendo dados umas das outras — uma falha
grave de segurança, não só um bug. O escopo correto é o da requisição (`HttpSession`,
`SecurityContextHolder` com `ThreadLocal`, ou um token propagado explicitamente).

### 1.5 Cache em memória compartilhado entre serviços do back-end

**Faz sentido usar Singleton** (dentro de uma única instância da aplicação).

O valor do cache vem justamente do compartilhamento: se cada serviço tivesse o seu, haveria
duplicação de memória e cada um pagaria seu próprio *cache miss*, anulando o ganho. Como é um
objeto mutável acessado concorrentemente, a implementação precisa ser thread-safe de verdade
(`ConcurrentHashMap`, ou uma biblioteca como Caffeine). Limitação importante: o Singleton só
garante unicidade dentro de uma JVM — se a aplicação roda em várias instâncias/pods, cada uma
terá seu próprio cache e podem divergir; nesse caso o caminho é um cache distribuído (Redis).

---

## Exercício 2 — Analogia

### A torre de controle do aeroporto

Um aeroporto grande tem dezenas de aeronaves manobrando ao mesmo tempo: umas pousando, outras
decolando, outras taxiando. Todas elas — e também os carros de bombeiros, o pessoal de solo e
as companhias aéreas — falam com **uma única torre de controle**, sintonizando a mesma
frequência de rádio. Não importa de onde o piloto está chamando: ele não "cria" uma torre,
ele apenas sintoniza a frequência conhecida e fala com a que já existe.

**Por que representa bem a instância única:** o aeroporto pode ter muitos aviões, mas só pode
existir *uma* torre coordenando aquela pista. Se houvesse duas torres autorizando pousos sem
saber uma da outra, cada uma teria uma visão parcial do tráfego e liberariam dois aviões para
a mesma pista — é exatamente o tipo de inconsistência que o Singleton evita ao garantir um
único ponto de verdade sobre um estado compartilhado.

**Por que representa bem o ponto de acesso global:** a frequência do rádio funciona como o
`getInstance()`. Ninguém precisa receber uma referência da torre nem saber como ela foi
construída; basta conhecer o canal público e ele leva sempre ao mesmo lugar. O acesso é
global, mas a criação é controlada — só a autoridade aeronáutica constrói a torre, assim como
só a própria classe pode chamar seu construtor privado.

**Limitação da analogia:** a torre é única *por aeroporto*, e existem milhares de aeroportos
no mundo — ou seja, ela é um Singleton de escopo local, enquanto o Singleton de código é
único em toda a JVM. Isso na verdade expõe uma armadilha real do padrão: quando a aplicação
sobe em várias instâncias/servidores, cada uma tem o "seu" Singleton, e a unicidade global que
se imaginava simplesmente não existe. A analogia também não captura o problema de testes: é
fácil simular um aeroporto novo, mas difícil "resetar" um Singleton estático entre testes.

---

## Exercício 3 — Anti-pattern: `CarrinhoDeComprasSingleton`

### 3.1 Por que é um problema arquitetural

O erro central é confundir **"existe um único ponto de acesso"** com **"existe uma única
instância"**. Um carrinho de compras é, por definição, um dado *por usuário e por sessão de
compra* — é estado conversacional, não estado global. Ao torná-lo Singleton, o escopo do
objeto foi amarrado ao ciclo de vida da aplicação inteira, quando deveria estar amarrado ao
ciclo de vida de uma sessão.

Enquanto a aplicação era desktop monousuário, os dois escopos coincidiam por acidente, e o
código "funcionava". A migração para multiusuário só revela um defeito de modelagem que já
estava lá. Outros agravantes:

- **Acoplamento oculto:** qualquer classe pode chamar `getInstancia()` e mutar o carrinho, sem
  que isso apareça em nenhuma assinatura de método. Não dá para saber, olhando a API, quem
  altera o quê.
- **Vazamento de encapsulamento:** `getItens()` devolve a própria `List` interna, então
  qualquer chamador pode limpar ou alterar o carrinho de qualquer usuário pelas costas da
  classe.
- **Testabilidade:** o estado estático sobrevive entre testes. Um teste que adiciona itens
  contamina o próximo, gerando falhas que dependem da ordem de execução.
- **Ciclo de vida errado:** o carrinho nunca é liberado, mesmo depois da compra finalizada.

### 3.2 Bugs esperados com vários usuários simultâneos

- **Vazamento de dados entre contas:** o cliente A adiciona uma TV; o cliente B abre o carrinho
  e vê a TV do A. É a falha mais grave — quebra de privacidade e de integridade da venda.
- **Compra do item errado:** B finaliza a compra e paga por itens que A colocou (ou A tem seu
  carrinho esvaziado quando B faz checkout, porque a lista é a mesma).
- **Condições de corrida na criação (`getInstancia()` não é thread-safe):** duas threads podem
  passar simultaneamente pelo `if (instancia == null)` e criar dois carrinhos; uma delas escreve
  em um objeto que será descartado, e seus itens desaparecem sem erro nenhum.
- **Corrupção da lista (`ArrayList` não é sincronizado):** `adicionarItem` concorrente pode
  perder itens, sobrescrever posições ou lançar `ArrayIndexOutOfBoundsException` durante o
  redimensionamento interno do array.
- **`ConcurrentModificationException`:** uma thread itera sobre a lista devolvida por
  `getItens()` (para calcular o total) enquanto outra adiciona um item.
- **Problemas de visibilidade de memória:** sem `volatile`, uma thread pode enxergar
  `instancia` já preenchida mas com o objeto parcialmente construído.
- **Bugs não reproduzíveis:** todos os anteriores dependem de timing, então aparecem em
  produção sob carga e somem no ambiente de desenvolvimento.

### 3.3 Abordagem alternativa (sem Singleton)

Tornar o carrinho um objeto comum, com identidade e ciclo de vida próprios, e mover a
responsabilidade de "achar o carrinho certo" para um componente à parte:

```java
// Entidade comum: instanciável, com dono explícito e estado encapsulado.
public class Carrinho {
    private final String idUsuario;
    private final List<Item> itens = new ArrayList<>();

    public Carrinho(String idUsuario) { this.idUsuario = idUsuario; }

    public void adicionarItem(Item item) { itens.add(item); }

    public List<Item> getItens() {
        return Collections.unmodifiableList(itens); // não vaza a lista interna
    }
}

// Responsável por localizar/criar o carrinho de cada usuário.
public interface CarrinhoRepository {
    Carrinho carrinhoDe(String idUsuario);
    void salvar(Carrinho carrinho);
    void remover(String idUsuario);
}
```

**Como as instâncias devem ser gerenciadas:**

1. **Uma instância por usuário/sessão**, nunca por aplicação. O carrinho é criado no primeiro
   item adicionado e destruído no checkout, no logout ou por expiração de sessão.
2. **Um registro central indexado por usuário**, e não uma variável estática solta — em memória
   um `ConcurrentHashMap<String, Carrinho>`, ou persistido em banco/Redis se o carrinho precisa
   sobreviver a reinícios. Note que esse *repositório* pode até ser único na aplicação (aí o
   Singleton seria legítimo): o que não pode ser único é o carrinho.
3. **Injeção de dependência em vez de acesso estático:** os serviços recebem o
   `CarrinhoRepository` pelo construtor. Isso torna a dependência explícita e permite trocá-la
   por uma implementação falsa nos testes.
4. **Escopo gerenciado pelo framework, quando houver:** em Spring, por exemplo, o carrinho seria
   um bean `@SessionScope`, e o container cuida de criar e descartar um por sessão.
5. **Confinamento em vez de sincronização:** como cada carrinho pertence a um único usuário, a
   concorrência praticamente desaparece; basta o mapa ser thread-safe. É mais simples e mais
   rápido do que sincronizar um carrinho global.

---

## Exercício 4 — Exemplo real: `JavaRecoverableNetworkWordCount` (Apache Spark)

Arquivo: `examples/src/main/java/org/apache/spark/examples/streaming/JavaRecoverableNetworkWordCount.java`

### 4.1 Funcionalidades implementadas

O exemplo é um contador de palavras em *streaming* que sobrevive a falhas do driver:

- **Leitura de um socket TCP:** recebe texto de um host/porta (`socketTextStream`) em janelas de
  1 segundo, quebra as linhas em palavras e agrega com `reduceByKey`.
- **Checkpointing e recuperação:** usa `JavaStreamingContext.getOrCreate(checkpointDirectory, ...)`.
  Se o diretório de checkpoint já existe, o contexto é **reconstruído a partir dele**, retomando
  o processamento de onde parou; se não existe, uma função de fábrica cria um contexto novo. É
  por isso que o exemplo se chama "recoverable".
- **Lista de exclusão distribuída (`Broadcast`):** uma lista de palavras a ignorar (`"a"`, `"b"`,
  `"c"`) é distribuída para todos os executores via variável de *broadcast*, evitando reenviá-la
  a cada tarefa.
- **Contador distribuído (`Accumulator`):** um `LongAccumulator` soma quantas ocorrências foram
  descartadas por estarem na lista de exclusão — agregando valores vindos de vários nós.
- **Filtragem e saída:** dentro de `foreachRDD`, cada par palavra/contagem é filtrado pela lista
  de exclusão, o contador é incrementado para os descartados, e o resultado (com o total
  acumulado de palavras excluídas) é impresso e **anexado a um arquivo de saída**.
- **Tolerância a falhas real:** o exemplo é acompanhado do script `run-example` com
  `yarn --supervise`, demonstrando reinício automático do driver.

### 4.2 Quais classes podem ser consideradas Singletons

Duas das três classes do arquivo são Singletons:

| Classe | É Singleton? | Por quê |
|---|---|---|
| `JavaWordExcludeList` | **Sim** | Campo `private static volatile` guardando a instância + `getInstance()` estático que controla a criação. |
| `JavaDroppedWordsCounter` | **Sim** | Mesma estrutura, guardando o `LongAccumulator`. |
| `JavaRecoverableNetworkWordCount` | **Não** | É apenas a classe com o `main`; não guarda nem controla instância alguma. |

```java
class JavaWordExcludeList {

  private static volatile Broadcast<List<String>> instance = null;

  public static Broadcast<List<String>> getInstance(JavaSparkContext jsc) {
    if (instance == null) {
      synchronized (JavaWordExcludeList.class) {
        if (instance == null) {
          List<String> wordExcludeList = Arrays.asList("a", "b", "c");
          instance = jsc.broadcast(wordExcludeList);
        }
      }
    }
    return instance;
  }
}

class JavaDroppedWordsCounter {

  private static volatile LongAccumulator instance = null;

  public static LongAccumulator getInstance(JavaSparkContext jsc) {
    if (instance == null) {
      synchronized (JavaDroppedWordsCounter.class) {
        if (instance == null) {
          instance = jsc.sc().longAccumulator("DroppedWordsCounter");
        }
      }
    }
    return instance;
  }
}
```

**Justificativa:** as duas seguem o *lazy initialization* com **double-checked locking**, que é
a forma clássica do padrão vista em aula — instância estática privada, criação encapsulada em um
método estático e inicialização adiada até o primeiro uso.

Há duas diferenças em relação ao Singleton "de livro" que valem menção:

- O **construtor não é privado** (as classes nem declaram construtor, então ficam com o padrão
  *package-private*). Elas não são Singletons de si mesmas: são *holders* estáticos que garantem
  uma única instância de **outro** objeto (o `Broadcast` e o `LongAccumulator`).
- O `getInstance()` **recebe um parâmetro** (`JavaSparkContext`), necessário para construir o
  objeto na primeira chamada.

O motivo de existirem é específico do Spark Streaming: quando o driver cai e é recuperado a
partir do checkpoint, as variáveis de broadcast e os acumuladores **não são restaurados** com o
contexto. Envolvê-los em um *lazy singleton* faz com que sejam recriados automaticamente na
primeira utilização após o reinício — e uma única vez por JVM, evitando redistribuir a lista ou
duplicar o contador a cada micro-batch.

### 4.3 As soluções são thread-safe?

**Sim.** A combinação de dois mecanismos garante isso:

1. **`synchronized (Classe.class)`** — garante **exclusão mútua**: apenas uma thread por vez
   entra no bloco de criação. Sem ele, duas threads poderiam passar juntas pelo primeiro `if`
   e criar dois broadcasts (ou dois acumuladores), o que no Spark significaria contagens
   perdidas.
2. **`volatile`** — garante **visibilidade e ordenação**. Sem ele, o double-checked locking é
   comprovadamente quebrado: por causa do reordenamento de instruções permitido pela JVM, a
   referência `instance` pode ser publicada *antes* de o objeto estar totalmente construído, e
   outra thread — que lê fora do `synchronized` — enxergaria um objeto pela metade. O `volatile`
   também assegura que a escrita feita por uma thread seja imediatamente visível às demais, em
   vez de ficar presa em cache de processador.

Isso importa muito aqui porque o Spark é um framework distribuído e **multithread**: o driver
processa vários batches e jobs concorrentemente, então `getInstance()` é realmente chamado por
threads diferentes ao mesmo tempo.

### 4.4 Por que dois `if (instance == null)`? É desperdício de recursos?

As duas verificações têm papéis distintos:

- **O primeiro `if` (fora do `synchronized`) é uma otimização de performance.** Depois que a
  instância existe — o que é o caso em 99,99% das chamadas —, ele retorna imediatamente **sem
  adquirir o lock**. Se o `synchronized` fosse aplicado ao método inteiro, toda chamada, para
  sempre, pagaria o custo de sincronização e serializaria as threads em um ponto quentíssimo do
  código.
- **O segundo `if` (dentro do `synchronized`) é o que garante a corretude.** Entre a primeira
  verificação e a aquisição do lock, outra thread pode ter criado a instância. Sem a recheca, a
  segunda thread a entrar no bloco criaria uma **segunda** instância, sobrescrevendo a primeira.
  Cenário concreto: threads A e B veem `instance == null`; A entra no bloco, cria o acumulador e
  sai; B então adquire o lock e, sem o segundo `if`, criaria outro acumulador — todos os valores
  já somados no primeiro seriam perdidos.

**Não é desperdício.** Pelo contrário: é uma troca deliberada de uma verificação de referência
`null` — uma instrução barata, sem lock, sobre um campo já em cache — pela eliminação da
aquisição de monitor em todas as chamadas subsequentes. O custo extra existe apenas nas
primeiras chamadas concorrentes (as poucas que disputam a criação); no regime permanente, o
caminho executado é só `if` + `return`. A única penalidade real é a leitura `volatile`, que
impede o cacheamento agressivo do campo pela CPU — ainda assim, muito mais barata que
sincronizar.

Vale registrar que, em Java moderno, existem alternativas mais simples com a mesma garantia e
sem a verbosidade do double-checked locking:

- **Initialization-on-demand holder** (classe interna estática) — a JVM garante a inicialização
  única de forma *lazy*, sem `volatile` nem `synchronized`.
- **`enum` Singleton** — a forma mais segura, imune inclusive a serialização e reflexão.

Nenhuma das duas serve ao caso do Spark, porém, porque `getInstance()` precisa receber o
`JavaSparkContext` como parâmetro e ser recriado após a recuperação do checkpoint — por isso o
double-checked locking continua sendo a escolha adequada ali.

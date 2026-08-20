# Atividade 03 — Padrão Builder

Aluno: Gabriel Matiola Pagnan
Disciplina: Design Patterns

---

## Exercício 1: Aplicações

### 1.1 `ConfiguracaoServidor` (host, porta, timeout, tentativas, SSL, proxy)

**Faz sentido usar Builder.**

São seis ou mais campos em que só `host` e `porta` são realmente obrigatórios, e cada
ambiente (dev, homologação, produção) usa um subconjunto diferente dos opcionais. Com
construtor tradicional isso viraria um construtor telescópico ou uma chamada cheia de
`null` e `0` para "não quero configurar isso". O Builder permite valores padrão sensatos
(timeout de 30s, SSL ligado, sem proxy) e o chamador sobrescreve apenas o que muda,
deixando explícito no código *qual* configuração está sendo alterada.

### 1.2 Montador de requisições HTTP (`Request.Builder` do OkHttp)

**Faz sentido usar Builder.**

É o caso canônico do padrão. A requisição tem uma parte obrigatória (a URL) e um número
grande de partes opcionais e repetíveis (vários cabeçalhos, corpo, método, tags), e o
encadeamento fluente faz o código de chamada ser lido quase como a própria requisição
HTTP. Além disso, o Builder é o que garante a **imutabilidade** do objeto final: toda a
mutação acontece no Builder e o produto nasce pronto e congelado, o que é essencial em uma
biblioteca onde a mesma requisição é compartilhada entre threads, cache e retentativas.

### 1.3 `Ponto` com dois campos obrigatórios em um sistema CAD

**Não faz sentido usar Builder.**

Com dois campos, ambos obrigatórios e de significado óbvio, `new Ponto(x, y)` já é mais
legível do que `new Ponto.Builder().setX(10).setY(20).build()`. Aplicar o padrão aqui é
**overengineering**: triplica o código sem resolver problema nenhum, já que não existem
opcionais nem explosão de combinações. Há também um custo secundário — cada `build()` cria
um objeto Builder descartável, o que é desperdício em um tipo instanciado com muita
frequência (embora, sendo honesto, "dezenas de vezes por segundo" seja um volume baixo
demais para que a alocação seja o argumento principal; o argumento principal é a
simplicidade).

### 1.4 `RelatorioFinanceiro` (título, período, filtros, ordenação, formato, marca d'água, rodapé)

**Faz sentido usar Builder.**

São muitos campos opcionais e independentes entre si, e o enunciado descreve exatamente o
problema que o padrão resolve: **módulos diferentes geram combinações diferentes sem
alterar o código de montagem**. Cada módulo monta seu relatório escolhendo os passos que
interessam, e o processo de montagem permanece um só. É também um caso em que o `Director`
se justifica, encapsulando receitas recorrentes (`relatorioMensalPDF()`,
`extratoResumidoCSV()`) como combinações nomeadas do mesmo Builder.

### 1.5 `Produto` com três campos obrigatórios, criado em um único ponto

**Não faz sentido usar Builder.**

Todos os campos são obrigatórios, então não existe combinação a variar — o Builder não
eliminaria nenhum parâmetro da chamada, apenas a espalharia por três linhas. Com um único
ponto de criação no sistema, o ganho de legibilidade também é nulo. O construtor tradicional
comunica melhor a intenção: "não existe Produto sem esses três dados". Vale reavaliar se no
futuro surgirem campos opcionais (categoria, descrição, imagens, dimensões) — aí o Builder
passa a se pagar.

**Resumo:** Builder nos itens **1, 2 e 4**; construtor tradicional nos itens **3 e 5**. O
critério que separa os dois grupos é a presença de campos opcionais em combinações variadas
— não a quantidade bruta de campos.

---

## Exercício 2: Analogia

### A confeitaria que monta bolos sob encomenda

Você chega em uma confeitaria e pede um bolo personalizado. A atendente pega uma **ficha de
pedido** e vai preenchendo com você, passo a passo, sempre na mesma ordem:

1. **Massa** — obrigatório (baunilha, chocolate, red velvet)
2. **Recheio** — obrigatório (brigadeiro, doce de leite, mousse)
3. Cobertura — opcional
4. Decoração — opcional (confeitos, frutas, pasta americana)
5. Topo de bolo — opcional
6. Mensagem escrita — opcional

Sem massa e sem recheio a ficha **não é aceita** — a atendente devolve o pedido e diz "falta
escolher a massa". Os outros itens você preenche ou deixa em branco à vontade. No fim, ela
leva a ficha para a cozinha e de lá sai **o bolo**.

A confeitaria também tem **combos prontos no cardápio**: "Bolo Infantil" (massa de chocolate
+ brigadeiro + confeitos + topo), "Bolo de Casamento" (red velvet + mousse + pasta americana),
"Bolo Simples" (baunilha + doce de leite, sem mais nada). Esses combos não são um processo
novo: são a mesma ficha, já preenchida de um jeito conhecido.

### Por que a analogia representa bem o padrão

**Separação entre o que é montado (Produto) e como é montado (Builder).** A ficha de pedido
não é o bolo. Ela é rabiscada, alterada e corrigida à vontade enquanto o pedido está sendo
feito; o bolo só existe depois que a ficha vai para a cozinha, e sai de lá pronto — ninguém
troca o recheio de um bolo já assado. É exatamente a divisão entre o Builder (mutável,
temporário, descartado depois) e o Produto (imutável, o resultado).

**Montagem em passos claros.** Cada campo da ficha é um método de configuração. Você não
precisa decorar a ordem dos ingredientes nem entender de confeitaria: a ficha guia a
conversa, um passo de cada vez, e cada passo tem nome. Comparando com o construtor
telescópico, seria como pedir o bolo dizendo `("chocolate", "brigadeiro", null, "confeitos",
null, null)` e torcer para a atendente lembrar qual posição é o quê.

**Variar combinações sem criar um processo do zero.** A confeitaria não precisa de uma
esteira de produção nova para cada bolo diferente — é sempre a mesma ficha, os mesmos passos,
e o que muda é o preenchimento. Os combos do cardápio são o papel do **Director**: receitas
nomeadas que reutilizam a ficha, sem que o cliente precise conhecer cada opção.

### Limitação da analogia

A validação do mundo real é mais esperta que a do padrão. Na confeitaria, se você tentar
pular a massa, a atendente reclama **na hora**, no passo em que o erro acontece. No Builder,
o objeto só é validado no `build()` — você pode encadear dez chamadas e só descobrir o campo
faltante na última, em tempo de execução. Um construtor tradicional, ao contrário, garante os
obrigatórios em **tempo de compilação**: o código nem compila. Essa é a troca real que o
Builder faz — ganha legibilidade e flexibilidade, perde a garantia do compilador.

Um detalhe secundário que também não encaixa: um bolo físico ainda pode ser mexido depois de
pronto (dá para espetar uma vela), enquanto o produto do Builder é imutável de verdade.

---

## Exercício 3: Anti-pattern (construtores telescópicos)

### 3.1 Por que esse uso de construtores sobrecarregados é um problema de design

**A chamada perde o significado.** Em `new Pedido("Ana", "Rua X, 10", itens, 0.1, "NATAL10",
15.0, "sem cebola")` nenhum valor está identificado. Quem lê precisa abrir a classe `Pedido`
e contar posições para saber que `0.1` é o desconto e `15.0` é o frete. O código de chamada
vira dependente da *ordem* dos parâmetros, e não do nome deles.

**As combinações são impostas, não escolhidas.** Os construtores formam uma cadeia rígida:
para informar `frete`, você é obrigado a passar `desconto` e `cupom` antes, mesmo que o
pedido não tenha nenhum dos dois. O resultado é a proliferação de valores "de mentira"
(`0.0`, `null`, `""`) só para chegar ao parâmetro que interessa — e aí `0.0` de desconto
passa a ser ambíguo: significa "sem desconto" ou "desconto de zero por cento calculado pela
regra"?

**Manutenção e validação duplicadas.** Cada construtor precisa preencher os campos que não
recebeu e, idealmente, repetir as validações. Com seis construtores, uma regra nova ("frete
não pode ser negativo") precisa ser lembrada em seis lugares, ou o objeto fica meio válido
em alguns caminhos e não em outros.

**A API pública fica engessada.** Como assinatura de construtor é contrato público, mudar a
ordem ou o tipo de um parâmetro quebra todo mundo que já usa a classe.

### 3.2 Bugs e confusões prováveis

**Troca silenciosa de argumentos do mesmo tipo.** `cliente`, `endereco`, `cupom` e
`observacoes` são todos `String`. Inverter `endereco` e `cupom` **compila sem nenhum aviso** e
gera um pedido entregue no endereço "NATAL10". O mesmo vale para `desconto` e `frete`, ambos
`double`: inverter os dois no construtor de sete argumentos produz um pedido com 10% de frete
e R$ 15,00 de desconto — e o erro só aparece na fatura do cliente, não no compilador.

**Escolha do overload errado.** Um desenvolvedor com pressa chama o construtor mais curto que
"parece servir" e cria um `Pedido` sem itens ou sem endereço, com os demais campos em valores
default silenciosos. O objeto existe, passa por todo o sistema e só explode lá na frente, com
um `NullPointerException` distante da causa.

**Estados inválidos representáveis.** Nada impede `new Pedido("Ana")` — um pedido sem
endereço e sem itens, que não deveria existir em momento algum. A classe permite construir
objetos que violam as próprias regras de negócio.

**Ambiguidade ao evoluir.** Adicionar um construtor `(String, String, List<String>, double)`
para um campo novo pode colidir com um já existente ou, pior, fazer o Java escolher um
overload diferente do que era escolhido antes — mudando o comportamento de código que não foi
tocado.

### 3.3 O que acontece a cada novo campo opcional

Cada campo novo exige **pelo menos mais um construtor**, e todos os anteriores continuam
existindo para não quebrar quem já usa a classe. A lista cresce indefinidamente, a duplicação
de inicialização e validação cresce junto, e o risco de colisão de assinaturas aumenta a cada
campo (um `double frete` novo colidindo com o `double desconto` já existente).

**Quantos construtores para N campos opcionais?**

- Na forma **telescópica encadeada** do exemplo — cada construtor acrescenta o próximo campo
  em uma ordem fixa — são **N + 1 construtores** (o mínimo obrigatório, mais um por campo).
  Mas essa economia tem um preço: só é possível expressar os *prefixos* dessa ordem fixa.
  "Quero cliente, endereço, itens e observações, sem desconto, sem cupom e sem frete" **não é
  representável** sem passar valores falsos.
- Para cobrir **todas as combinações possíveis** de presença/ausência dos opcionais seriam
  **2^N construtores** — crescimento exponencial. Com 5 opcionais isso já passa de 30
  construtores, e a maior parte deles sequer seria declarável em Java por conflito de
  assinatura (`(String, double)` para desconto e `(String, double)` para frete são o *mesmo*
  método para o compilador).

Ou seja: ou você aceita combinações limitadas, ou cai numa explosão combinatória que a
linguagem nem permite escrever. É esse impasse que o Builder desfaz.

### 3.4 Abordagem alternativa com Builder

A ideia é inverter a responsabilidade: em vez de a classe oferecer N caminhos de criação, ela
oferece **um só**, e delega a coleta dos dados a um objeto auxiliar.

**Estrutura:**

- `Pedido` passa a ter **construtor privado**, recebendo o Builder inteiro:
  `private Pedido(Builder builder)`. Fora da classe, `new Pedido(...)` deixa de ser possível —
  o Builder vira o único caminho de criação.
- Todos os campos viram `final`, sem setters, e as coleções (`itens`) são copiadas
  defensivamente e devolvidas como `unmodifiableList`. O `Pedido` nasce **imutável**.
- `Pedido.Builder` é uma **classe interna estática** (estática porque não precisa de uma
  instância de `Pedido` para existir — ela é justamente quem vai criar essa instância).
- Os campos realmente obrigatórios (`cliente`, `endereco`, `itens`) entram no **construtor do
  Builder**, o que devolve ao compilador a garantia de que não podem ser esquecidos. Os
  opcionais ganham **métodos fluentes** — `desconto(...)`, `cupom(...)`, `frete(...)`,
  `observacoes(...)` — que atribuem o campo e retornam `this`.
- `build()` executa as validações de invariante (itens não vazio, desconto entre 0 e 1, frete
  não negativo, cupom e desconto não simultâneos) e só então chama `new Pedido(this)`. Todas
  as regras ficam concentradas em **um único ponto**.

**Esqueleto:**

```java
public class Pedido {
    private final String cliente;
    private final String endereco;
    private final List<String> itens;
    private final double desconto;
    private final String cupom;
    private final double frete;
    private final String observacoes;

    private Pedido(Builder b) {          // só o Builder constrói
        this.cliente     = b.cliente;
        this.endereco    = b.endereco;
        this.itens       = List.copyOf(b.itens);   // cópia defensiva
        this.desconto    = b.desconto;
        this.cupom       = b.cupom;
        this.frete       = b.frete;
        this.observacoes = b.observacoes;
    }

    // apenas getters, nenhum setter

    public static class Builder {
        private final String cliente;                 // obrigatórios: no construtor
        private final String endereco;
        private final List<String> itens;
        private double desconto = 0.0;                // opcionais: default explícito
        private String cupom;
        private double frete = 0.0;
        private String observacoes;

        public Builder(String cliente, String endereco, List<String> itens) {
            this.cliente = cliente;
            this.endereco = endereco;
            this.itens = itens;
        }

        public Builder desconto(double d)      { this.desconto = d;    return this; }
        public Builder cupom(String c)         { this.cupom = c;       return this; }
        public Builder frete(double f)         { this.frete = f;       return this; }
        public Builder observacoes(String o)   { this.observacoes = o; return this; }

        public Pedido build() {
            if (cliente == null || cliente.isBlank())
                throw new IllegalStateException("Pedido exige um cliente.");
            if (itens == null || itens.isEmpty())
                throw new IllegalStateException("Pedido exige ao menos um item.");
            if (desconto < 0 || desconto > 1)
                throw new IllegalStateException("Desconto deve estar entre 0 e 1.");
            return new Pedido(this);
        }
    }
}
```

**Como a criação passa a funcionar:**

```java
Pedido pedido = new Pedido.Builder("Ana", "Rua X, 10", itens)
        .frete(15.0)
        .observacoes("sem cebola")
        .build();
```

O pedido acima informa frete e observações **sem precisar mencionar desconto nem cupom** —
exatamente a combinação que os construtores telescópicos não conseguiam expressar. Cada valor
está identificado pelo nome do método, trocar dois `double` de lugar deixa de ser possível, e
um campo opcional novo custa **um método** no Builder, sem quebrar nenhum código existente.

---

## Exercício 4: Exemplo real — `Request.kt` (OkHttp)

Arquivo analisado:
`okhttp/src/commonJvmAndroid/kotlin/okhttp3/Request.kt` (branch `master`, OkHttp 5.x)
https://github.com/square/okhttp/blob/master/okhttp/src/commonJvmAndroid/kotlin/okhttp3/Request.kt

### 4.1 Por que `Request` é imutável e qual o papel do Builder nessa garantia

O próprio KDoc da classe declara a intenção:

```kotlin
/**
 * An HTTP request. Instances of this class are immutable if their [body] is null or itself
 * immutable.
 */
class Request internal constructor(
  builder: Builder,
) {
```

A imutabilidade existe porque uma `Request` no OkHttp **não é usada em um lugar só**: ela
atravessa a cadeia de interceptors, é usada como chave de cache, é reenviada em
redirecionamentos e retentativas, e circula entre threads do pool de conexões. Se qualquer um
desses pontos pudesse alterá-la, a requisição efetivamente enviada poderia divergir daquela
que o usuário montou, e a chave de cache poderia mudar depois de calculada. Sendo imutável,
ela é segura para compartilhar sem sincronização e o comportamento é previsível.

O Builder é o que torna isso viável. Repare em três decisões no código:

- O construtor principal é `internal constructor(builder: Builder)` — em Kotlin, `internal`
  significa visível apenas dentro do módulo. **Fora do OkHttp não existe forma de construir
  uma `Request` a não ser passando por um `Builder`.**
- Todo o estado mutável fica no Builder (`internal var url`, `internal var method`,
  `internal var headers: Headers.Builder`...), e o produto declara tudo como `val`, sem setters.
- No momento da construção, o estado do Builder é **congelado**, não apenas referenciado:
  `val headers: Headers = builder.headers.build()`. Mexer no Builder depois não afeta a
  `Request` já criada.

Vale notar um detalhe interessante: a classe também expõe um construtor secundário público
`Request(url, headers, method, body)` para os casos triviais — mas ele **delega para o
Builder**:

```kotlin
constructor(
  url: HttpUrl,
  headers: Headers = headersOf(),
  method: String = /* sentinela */,
  body: RequestBody? = null,
) : this(
  Builder()
    .url(url)
    .headers(headers)
    .method(..., body),
)
```

Ou seja, mesmo o atalho passa pelo Builder, e as invariantes continuam concentradas em um
único caminho. Há ainda o caminho inverso — `fun newBuilder(): Builder = Builder(this)` — que
é como se "modifica" um objeto imutável: cria-se um Builder pré-preenchido a partir da
requisição existente e monta-se uma **cópia alterada**, sem tocar na original.

### 4.2 O que os métodos de configuração têm em comum no tipo de retorno

**Todos retornam `Builder` — a própria instância.** Alguns exemplos do arquivo:

```kotlin
open fun url(url: HttpUrl): Builder = apply { this.url = url }
open fun url(url: String): Builder = url(canonicalUrl(url).toHttpUrl())
open fun header(name: String, value: String) = apply { ... }
open fun headers(headers: Headers) = apply { this.headers = headers.newBuilder() }
open fun method(method: String, body: RequestBody?): Builder = apply { ... }
open fun get(): Builder = method("GET", null)
open fun post(body: RequestBody): Builder = method("POST", body)
```

O `apply { ... }` é a função da biblioteca padrão do Kotlin que executa o bloco e **retorna o
próprio receptor** — é o equivalente idiomático do `return this;` que escrevemos em Java.
Métodos como `get()` e `post(body)` são açúcar sintático: delegam para `method(...)` e, por
consequência, também devolvem o `Builder`.

É justamente isso que habilita o **encadeamento fluente** visto em aula: como cada chamada
devolve o mesmo objeto de volta, a chamada seguinte pode ser aplicada diretamente sobre o
resultado da anterior, formando uma expressão única:

```kotlin
val request = Request.Builder()
    .url("https://api.exemplo.com/usuarios")
    .header("Authorization", "Bearer $token")
    .post(body)
    .build()
```

Se esses métodos retornassem `Unit`/`void`, cada configuração exigiria uma linha separada
repetindo a variável do builder — funcionaria, mas perderia a legibilidade que é o principal
argumento a favor do padrão aqui.

### 4.3 O método `build()` e a validação dos campos obrigatórios

No arquivo, o `build()` é enxuto:

```kotlin
open fun build(): Request = Request(this)
```

E a verificação do campo obrigatório está na inicialização do produto:

```kotlin
val url: HttpUrl = checkNotNull(builder.url) { "url == null" }
```

O efeito é o mesmo da validação vista em aula, e por um motivo importante: como o construtor
de `Request` é `internal` e `build()` é o caminho de criação, **essa checagem só pode ser
disparada por um `build()`**. O `checkNotNull` do Kotlin lança `IllegalStateException` — a
mesma exceção que usamos no `build()` do exercício de implementação, e pela mesma razão
semântica: "o builder está em um estado inválido para produzir o objeto".

A relação com o que vimos em aula é direta:

| Em aula | No OkHttp |
|---|---|
| `if (pao == null) throw new IllegalStateException(...)` | `checkNotNull(builder.url) { "url == null" }` |
| Validação disparada por `build()` | Validação disparada por `build()` (via construtor `internal`) |
| `IllegalStateException` | `IllegalStateException` (lançada por `checkNotNull`) |
| Campo opcional tem default (`bemPassado = false`) | `method` tem default `"GET"`, `body` default `null` |

A diferença é apenas de **onde o código foi escrito**: o OkHttp coloca a checagem na
inicialização do produto em vez de literalmente dentro do `build()`. Isso mantém a validação
junto do campo que ela protege e faz o compilador Kotlin enxergar `url` como não-nulo dali em
diante. A garantia para quem usa a biblioteca é idêntica — campo obrigatório ausente significa
exceção na construção, e nunca um objeto meio-montado circulando pelo sistema (*fail-fast*).

### 4.4 O Builder é uma classe interna — comparação com o Builder interno `static` da aula

```kotlin
class Request internal constructor(builder: Builder) {
  ...
  open class Builder {
```

**Sim, a relação é a mesma que vimos em aula.** Em Kotlin, uma classe aninhada é **estática
por padrão**: `Request.Builder` não guarda referência à instância externa e pode ser
instanciada sozinha com `Request.Builder()`. Para ter o comportamento de classe interna
não-estática do Java seria preciso a palavra-chave `inner`, que **não** aparece aqui.
Portanto `class Builder` dentro de `Request` em Kotlin é exatamente o `public static class
Builder` dentro de `Request` em Java.

E isso é obrigatório, não estilístico: se o Builder dependesse de uma instância de `Request`
para existir, seria impossível usá-lo para *criar* a primeira `Request` — a dependência seria
circular. O aninhamento em si serve para exprimir o acoplamento forte entre os dois (o Builder
não faz sentido fora do contexto de `Request`) e para dar ao Builder acesso privilegiado ao
construtor restrito do produto.

Os pontos em que o OkHttp vai além do exemplo da aula:

- **`open class Builder`** — a classe e seus métodos são abertos para herança (em Kotlin,
  classes são `final` por padrão). É uma escolha típica de biblioteca pública, permitindo que
  subclasses estendam a montagem; no nosso exercício, o Builder é `final` na prática.
- **`internal` em vez de `private`** — o construtor precisa ser acessível a outras classes do
  módulo `okhttp3` (a `Request` é reconstruída internamente em redirecionamentos e
  retentativas), mas continua inacessível para quem consome a biblioteca. Java não tem esse
  nível de visibilidade; o equivalente mais próximo seria *package-private*.
- **`newBuilder()`** — o caminho Produto → Builder, que a aula não cobriu. É o que permite
  "editar" um objeto imutável produzindo uma cópia modificada, padrão muito comum nos
  interceptors do OkHttp.

O núcleo do padrão, porém, é idêntico ao da aula: **produto imutável com construtor restrito +
Builder aninhado estático que acumula o estado + métodos fluentes que retornam o próprio
Builder + `build()` que valida e entrega o objeto pronto.**

---

## Exercício 5: Implementação

Código-fonte em `src/`:

| Arquivo | Papel no padrão |
|---|---|
| `src/Lanche.java` | **Produto** (imutável, construtor privado) + **Builder** (classe interna estática) |
| `src/LancheDirector.java` | **Director** — três receitas prontas reutilizando o Builder |
| `src/Main.java` | Classe de teste/demonstração |

### Como compilar e executar

```bash
javac -d out src/*.java
java -cp out Main
```

A saída completa da execução está em `SAIDA.txt`.

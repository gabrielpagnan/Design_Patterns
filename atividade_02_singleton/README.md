# Sistema Central de Alertas de Emergência — Padrão Singleton

Atividade 02 — Design Patterns (SATC).

- **Exercícios 1 a 4** (aplicações, analogia, anti-pattern e exemplo real no Apache Spark):
  [EXERCICIOS.md](EXERCICIOS.md)
- **Exercício 5** (implementação): código em [`src/`](src/) + as respostas abaixo.

---

## Como executar

Requer JDK 11 ou superior (o código usa `String.isBlank()` e `List.of`).

```bash
javac -d out src/*.java
java -cp out Main
```

Ou, de uma vez só:

```bash
./run.sh
```

> Neste Mac o JDK foi instalado via Homebrew (`brew install openjdk`) e é *keg-only*, ou seja, não
> entra no `PATH` automaticamente. Se `javac` não for encontrado, rode antes:
> ```bash
> export PATH="/opt/homebrew/opt/openjdk/bin:$PATH"
> ```
> (para deixar permanente, acrescente essa linha ao `~/.zshrc`)

## Estrutura

| Arquivo | Papel |
|---|---|
| `src/CentralDeAlertas.java` | **O Singleton.** Guarda o histórico e recebe os alertas. |
| `src/OrgaoDeSeguranca.java` | Classe base dos órgãos; obtém a central via `getInstancia()`. |
| `src/Policia.java` | Módulo da Polícia. |
| `src/Bombeiros.java` | Módulo do Corpo de Bombeiros. |
| `src/Samu.java` | Módulo do SAMU. |
| `src/Main.java` | Demonstração + prova de instância única + teste de concorrência. |

## O que a demonstração mostra

1. Três órgãos independentes enviam alertas diferentes.
2. Polícia e SAMU consultam o histórico e **veem as mensagens de todos**, inclusive as dos outros
   órgãos.
3. O `hashCode()` da central é impresso a partir de quatro pontos distintos do código
   (Polícia, Bombeiros, SAMU e `Main`) e é **sempre o mesmo número**, provando que se trata de um
   único objeto — reforçado pela comparação por referência `getInstancia() == getInstancia()`.
4. Um teste dispara 3 threads enviando 100 alertas cada; ao final, os 300 alertas estão
   registrados, sem perdas.

Saída esperada (resumida):

```
>> [CentralDeAlertas] instancia unica criada (hashCode: 1735600054)

--- Envio de alertas ---
[POLICIA] alerta enviado: Assalto a mao armada na Av. Centenario, 1200
[BOMBEIROS] alerta enviado: Incendio em residencia no bairro Pinheirinho
[SAMU] alerta enviado: Acidente com vitima na BR-101, km 372
...

--- Historico consultado por POLICIA (central hashCode: 1735600054) ---
   #001 [14:32:07] POLICIA: Assalto a mao armada na Av. Centenario, 1200
   #002 [14:32:07] BOMBEIROS: Incendio em residencia no bairro Pinheirinho
   #003 [14:32:07] SAMU: Acidente com vitima na BR-101, km 372
   #004 [14:32:07] POLICIA: Furto de veiculo no estacionamento do shopping
   Total: 4 alerta(s)

--- Historico consultado por SAMU (central hashCode: 1735600054) ---
   ...mesmos 4 alertas...

--- Prova de instancia unica (hashCode) ---
   Central vista pela Policia ....: 1735600054
   Central vista pelos Bombeiros .: 1735600054
   Central vista pelo SAMU .......: 1735600054
   Central obtida aqui no Main ...: 1735600054
   getInstancia() == getInstancia() ? true

--- Teste de concorrencia (3 threads x 100 alertas) ---
   Alertas esperados ..: 300
   Alertas registrados : 300
   Resultado ..........: OK - nenhum alerta perdido
```

*(o `hashCode` muda a cada execução — o que importa é ele ser idêntico nas quatro linhas)*

---

## Respostas do Exercício 5

### 1. Por que faz sentido que a `CentralDeAlertas` seja um Singleton neste contexto?

Porque o domínio já exige unicidade: no mundo real existe **um** centro de controle de
emergências para uma região, e é justamente essa centralização que dá sentido ao sistema. O
histórico de alertas é um recurso compartilhado por natureza — Polícia, Bombeiros e SAMU precisam
enxergar exatamente a mesma lista de ocorrências para coordenar o atendimento. Um acidente grave
comunicado pela Polícia tem que estar visível ao SAMU no instante seguinte, sem nenhuma sincronia
extra entre módulos.

Além disso:

- **Consistência do estado:** existe uma única fonte de verdade sobre o que já foi registrado, e a
  numeração sequencial dos alertas (`#001`, `#002`, ...) só é confiável porque um único contador
  os gera.
- **Ponto de acesso global legítimo:** os módulos dos órgãos podem estar em qualquer camada do
  sistema; o `getInstancia()` os conecta à central sem que ninguém precise passar a referência de
  mão em mão. É um serviço de infraestrutura transversal, como um logger.
- **Controle de criação:** o construtor privado impede que alguém, por engano, crie uma "segunda
  central" — o compilador barra `new CentralDeAlertas()`.
- **Economia de recursos:** um único buffer de mensagens em memória, em vez de uma cópia por
  módulo.

Vale a comparação com o Exercício 3: o carrinho de compras é dado **por usuário**, então o
Singleton foi um erro. Aqui o dado é **por aplicação** — todos os órgãos compartilham a mesma
central deliberadamente. É essa diferença de escopo que separa o bom uso do anti-pattern.

### 2. Que problemas aconteceriam se cada órgão tivesse sua própria instância de central?

O sistema deixaria de ser "central" e viraria três sistemas isolados que apenas parecem um só:

- **Históricos divergentes:** o SAMU consultaria os alertas e veria só os seus. O incêndio
  comunicado pelos Bombeiros seria invisível para a Polícia — exatamente o oposto do requisito.
- **Falha de coordenação operacional:** em uma ocorrência que exige os três órgãos (um acidente
  com vítimas e princípio de incêndio, por exemplo), cada um agiria com informação parcial. Num
  sistema de emergência, isso custa vidas, não apenas dados.
- **Numeração duplicada:** existiriam três alertas `#001` diferentes, tornando impossível
  referenciar uma ocorrência sem ambiguidade em rádios, relatórios ou auditorias.
- **Impossibilidade de auditoria e estatística:** não haveria como responder "quantos alertas
  foram emitidos hoje?" sem consolidar três fontes, e a ordem cronológica real entre elas se
  perderia.
- **Desperdício de recursos:** N cópias da mesma estrutura em memória, e cada nova unidade
  cadastrada multiplicaria o custo.
- **Bugs sutis de "quase-funcionamento":** o pior cenário é o sistema não quebrar — ele apenas
  mostra menos informação do que deveria, em silêncio, e o problema só aparece quando a informação
  faltante já fez falta.

### 3. Sua implementação é thread-safe?

**Sim, em duas frentes distintas** — vale separá-las, porque muita implementação de Singleton
protege só a primeira e esquece a segunda.

#### (a) Criação da instância — *initialization-on-demand holder*

```java
private CentralDeAlertas() { ... }               // ninguém cria de fora

private static class CentralHolder {             // carregada só no 1º getInstancia()
    private static final CentralDeAlertas INSTANCIA = new CentralDeAlertas();
}

public static CentralDeAlertas getInstancia() {
    return CentralHolder.INSTANCIA;
}
```

A classe interna `CentralHolder` só é carregada pela JVM no momento em que é referenciada pela
primeira vez — ou seja, na primeira chamada de `getInstancia()`. E a **Java Language Specification
(§12.4.2)** garante que a inicialização de uma classe é feita **uma única vez**, sob um lock
interno da própria JVM, mesmo que várias threads a disparem simultaneamente.

Isso entrega as duas propriedades desejadas de uma vez:

- **Lazy:** a central não é criada no *class loading* da aplicação, só quando o primeiro órgão
  precisa dela;
- **Thread-safe sem custo:** não há `synchronized` nem `volatile` no caminho de leitura, então as
  chamadas seguintes são apenas uma leitura de campo estático — mais rápido que o
  *double-checked locking* usado no exemplo do Spark (Exercício 4) e sem o risco de esquecer o
  `volatile`, que é o erro clássico daquela abordagem.

A prova prática está no `Main`: o teste dispara três threads e a mensagem
`">> [CentralDeAlertas] instancia unica criada"`, impressa dentro do construtor, aparece
**uma única vez** na saída.

#### (b) Estado interno — coleção e contador concorrentes

Garantir uma instância única não basta: como essa única instância é acessada por várias threads
ao mesmo tempo, os campos mutáveis também precisam ser seguros.

```java
private final List<String> mensagens = new CopyOnWriteArrayList<>();
private final AtomicInteger contador = new AtomicInteger(0);
```

- **`CopyOnWriteArrayList`** em vez de `ArrayList`: escritas concorrentes não corrompem o array
  interno nem perdem elementos, e a iteração nunca lança `ConcurrentModificationException` (ela
  percorre um *snapshot*). É a escolha ideal aqui porque o padrão de uso é "escreve pouco, lê
  muito" — os órgãos consultam o histórico com muito mais frequência do que registram alertas.
- **`AtomicInteger.incrementAndGet()`**: incremento atômico. Com um `int` comum, a operação
  `contador++` são na verdade três passos (ler, somar, gravar), e duas threads poderiam ler o
  mesmo valor e gerar dois alertas com o número `#007`.
- **`Collections.unmodifiableList`** em `getAlertas()`: nenhum órgão consegue alterar a lista
  interna da central por fora — evita o vazamento de encapsulamento apontado no Exercício 3.
- Os dois campos são **`final`**, o que garante sua publicação segura entre threads.

O teste de concorrência do `Main` comprova: 3 threads × 100 alertas = **300 registrados, zero
perdidos**.

#### O que aconteceria se *não* fosse thread-safe

Se a implementação fosse a ingênua do Exercício 3 (`if (instancia == null) { ... }` com
`ArrayList`), os riscos seriam:

1. Duas threads passando juntas pelo `if` e criando **duas centrais**, com alertas indo para um
   objeto que seria descartado;
2. Perda silenciosa de alertas por escrita concorrente no `ArrayList`;
3. `ArrayIndexOutOfBoundsException` durante o redimensionamento interno do array;
4. `ConcurrentModificationException` ao listar enquanto outro órgão registra;
5. Números de alerta duplicados.

#### Alternativas para tornar segura uma implementação que não fosse

Caso a versão ingênua precisasse ser corrigida, as opções seriam:

| Abordagem | Como | Trade-off |
|---|---|---|
| `synchronized` no método | `public static synchronized CentralDeAlertas getInstancia()` | Correto e trivial, mas serializa **todas** as chamadas, inclusive as que só leem uma instância já criada. |
| *Eager initialization* | `private static final CentralDeAlertas INSTANCIA = new CentralDeAlertas();` | Seguro pela JVM e simples, mas cria o objeto sempre, mesmo que nunca seja usado. |
| *Double-checked locking* | Campo `volatile` + dois `if` em torno de um bloco `synchronized` | Rápido e lazy — é o que o Spark usa —, porém verboso e **quebrado se o `volatile` for esquecido**. |
| **Holder (o adotado)** | Classe interna estática | Lazy + thread-safe + sem lock na leitura. Não serve quando a criação precisa de parâmetros. |
| `enum` Singleton | `public enum CentralDeAlertas { INSTANCIA; ... }` | O mais seguro de todos (resistente a serialização e reflexão), mas não permite herança e é menos convencional para quem lê. |

Em todos os casos, o estado mutável interno continuaria precisando de proteção própria — a escolha
acima resolve apenas a criação da instância.

---

## Observação sobre o limite do Singleton

O `getInstancia()` garante uma única central **por JVM**. Se este sistema fosse distribuído em
vários servidores, cada um teria a sua central e os históricos voltariam a divergir — o mesmo
problema descrito na pergunta 2, só que em outra escala. Em produção, a unicidade real viria de um
armazenamento compartilhado (banco de dados, Redis, fila de mensagens), e o Singleton passaria a
ser apenas o ponto de acesso local a esse recurso.

# Atividade 04 — Padrão Prototype

---

## Exercício 1 — Aplicações

### 1. Relatórios financeiros a partir de um template base
**Faz sentido usar Prototype.**
Existe uma base pronta e cara de montar (cabeçalho, rodapé, seções padrão) que é idêntica todo mês, e só os valores mudam — ou seja, é o caso clássico de "variações sobre uma mesma base". Clonar o template já configurado evita remontar toda a estrutura a cada mês e garante que nenhum relatório saia com uma seção faltando. O protótipo fica guardado intacto e cada mês trabalha sobre a sua própria cópia.

### 2. Classe `Ponto(x, y)` instanciada dezenas de vezes por segundo
**Não faz sentido usar Prototype.**
A criação é trivial e barata: dois campos obrigatórios, nenhuma configuração para replicar e nenhuma variação sobre uma base. `new Ponto(x, y)` é mais curto, mais legível e mais rápido do que clonar um modelo e depois ajustar `x` e `y` com setters. Aplicar o padrão aqui é *overengineering*: adiciona interface, método `clonar()` e indireção sem resolver problema nenhum — e ainda obrigaria a classe a ter setters, quando um `Ponto` normalmente deveria ser imutável.

### 3. Inimigos de jogo com muita configuração
**Faz sentido usar Prototype.**
É o caso canônico do GoF: o objeto tem muita configuração (estatísticas, equipamentos, habilidades) e várias fases repetem o mesmo código de construção só para criar variações do mesmo guerreiro. Clonar um guerreiro já montado e mexer em dois ou três campos elimina essa duplicação e centraliza o "como se monta um guerreiro" em um lugar só. Se o balanceamento mudar, muda-se o protótipo e todas as fases acompanham.

### 4. Cliente pede a cópia pelo nome ("guerreiro", "mago") através de um registro
**Faz sentido usar Prototype.**
Essa é exatamente a variação *Prototype Registry*: o cliente depende apenas da interface de clonagem e de uma `String`, sem nunca citar a classe concreta — acoplamento baixo. Além disso, registrar um novo modelo passa a ser configuração, não alteração de código: nenhum `if/switch` do cliente precisa ser tocado para surgir um "necromante".

### 5. Classe `Produto` com três campos, criada em um único ponto e sem variações
**Não faz sentido usar Prototype.**
Não há repetição de código de construção (é um único ponto do sistema), não há configuração cara e não existem variações sobre uma base — os três motivos que justificariam o padrão estão ausentes. O construtor tradicional já resolve, e introduzir clonagem só traria custo: mais uma interface, um construtor de cópia para manter e o risco permanente de esquecer um campo novo na cópia.

> **Regra prática dos 5 itens:** Prototype se paga quando (a) montar o objeto do zero é caro ou repetitivo, (b) existem muitas variações de uma mesma base, ou (c) o cliente não deve conhecer as classes concretas. Nos itens 2 e 5 nenhuma dessas condições aparece.

---

## Exercício 2 — Analogia

### A massa madre (fermento natural) da padaria

Uma padaria mantém um pote de **massa madre** viva na geladeira. Ela levou dias para ficar pronta: farinha, água, tempo e alimentação diária até virar um fermento maduro e estável — esse é o "custo de construção".

Quando o padeiro vai assar, ele **não começa outra massa madre do zero**. Ele tira um pedaço do pote e esse pedaço **já nasce vivo e pronto para uso**, com toda a colônia de fermento e o sabor da matriz. É o objeto pronto participando da criação da cópia.

A partir do mesmo pedaço, mudando só alguns detalhes, saem pães diferentes: junta azeitona e vira pão de azeitona; troca a farinha e vira integral; muda o formato e vira baguete. **Mesma base, variações pequenas** — sem refazer o fermento em nenhuma delas.

E o detalhe que fecha a analogia: o padeiro **nunca entrega o pote original**. Ele sempre tira um pedaço e devolve o pote para a geladeira — se ele usasse a matriz direto, a padaria ficaria sem base para o dia seguinte.

| Na padaria | No padrão Prototype |
|---|---|
| Pote de massa madre maduro, guardado na geladeira | O objeto protótipo, já configurado |
| Prateleira com potes rotulados ("pão", "panetone") | O registro de protótipos (`Map<String, Prototype>`) |
| Tirar um pedaço do pote | `clonar()` |
| O pedaço já vem vivo, sem esperar dias | A cópia nasce completa, sem reconstruir do zero |
| Juntar azeitona, trocar a farinha | Ajustar poucos campos após a cópia |
| Nunca entregar o pote original | O registro devolve o clone, nunca o protótipo |
| Se o padeiro emprestasse o **próprio pote** e o vizinho o estragasse, todos os pães seguintes iriam junto | **Cópia rasa**: clone e original compartilham o mesmo objeto interno |

Repare no contraste importante: a **receita escrita** na parede não é o protótipo — ela é a *classe*. O protótipo é o pote pronto. Fazer pão pela receita é `new`; tirar um pedaço do pote é `clonar()`.

---

## Exercício 3 — Anti-pattern

```java
Inimigo copia = new Inimigo();
copia.setTipo(antigo.getTipo());
copia.setVida(antigo.getVida());
copia.setDano(antigo.getDano());
copia.setArma(antigo.getArma());   // <-- a linha problemática
return copia;
```

### a) Por que copiar campo a campo é um problema de design?

1. **A lógica de cópia está na classe errada.** Quem sabe do que um `Inimigo` é feito deveria ser o próprio `Inimigo`, não a `Fase`. Do jeito atual, a `Fase` precisa conhecer a lista completa de campos da outra classe — inclusive os que são detalhe interno. Isso quebra o encapsulamento e cria um acoplamento forte entre as duas classes.

2. **Obriga a classe a ser "aberta".** Para o código funcionar, `Inimigo` precisa de um construtor sem argumentos e de um setter público para **todo** campo. A classe perde a chance de validar invariantes na construção (nada impede um inimigo com vida negativa) e nunca poderá ter campos `final` nem ser imutável.

3. **Duplicação garantida.** Qualquer outro lugar que precise copiar um inimigo (o editor de fases, os testes, o save do jogo) vai reescrever a mesma sequência de setters. O mesmo conhecimento passa a existir em N cópias que precisam ser mantidas em sincronia — e não vão ser.

4. **Não funciona com herança (*object slicing*).** Se amanhã existir `InimigoVoador extends Inimigo` com o campo `altitude`, `criarCopia` continua devolvendo um `Inimigo` puro: o subtipo se perde e os campos extras somem. Um `clonar()` polimórfico dentro de cada classe resolveria isso naturalmente.

### b) Que bugs aparecem se um campo novo for adicionado e `criarCopia` não for atualizado?

O ponto grave é que **nada avisa**: o código compila normalmente e nenhum erro é lançado. O bug é silencioso.

- O campo novo fica no **valor padrão** — `null` para objetos, `0` / `0.0` para números, `false` para booleanos. Um inimigo copiado sai com `nivel = 0`, `resistencias = null`, `habilidades = null`.
- Isso gera **objetos parcialmente inicializados** circulando pelo jogo: o clone "quase funciona", até alguém chamar `inimigo.getHabilidades().size()` e tomar um `NullPointerException` — muitas vezes longe dali, no meio da fase, sem pista de onde o objeto foi criado.
- O sintoma é **assimétrico e confuso de depurar**: o inimigo original funciona perfeitamente, só a cópia falha. Todos os testes que constroem o inimigo pelo construtor passam; o defeito só aparece nas fases que usam o clone.
- E o problema **se acumula**: cada campo novo é uma nova chance de alguém esquecer. Com o tempo, `criarCopia` vira uma lista desatualizada que ninguém confia — mas que todo mundo usa.

Com um construtor de cópia dentro da própria classe o risco não desaparece de todo, mas cai muito: o campo novo é declarado a três linhas de distância de onde ele deveria ser copiado, e se o campo for `final` o compilador chega a exigir a atribuição.

### c) O que acontece em `copia.setArma(antigo.getArma())`?

`getArma()` não devolve uma arma nova — devolve **a referência para a mesma instância**. Depois dessa linha, o original e o clone apontam para o mesmíssimo objeto `Arma` na memória (*aliasing*).

Como `Arma` é mutável, a consequência é imediata:

```java
clone.getArma().setBonusDano(999);   // o jogador achou um upgrade
// ...e a arma do inimigo original também virou +999
```

Encantar a arma de um inimigo encanta a de todos os outros que vieram da mesma cópia. Pior: se o objeto copiado for o **modelo guardado no registro**, contaminar a arma dele envenena todos os inimigos que forem criados dali para frente — o bug se propaga para o resto da partida e some quando o jogo reinicia, que é o tipo de defeito mais difícil de reproduzir.

Isso ilustra exatamente a diferença vista em aula:

| | Cópia rasa (*shallow*) | Cópia profunda (*deep*) |
|---|---|---|
| Campos primitivos (`double vida`) | copiados por valor — independentes | copiados por valor — independentes |
| Campos imutáveis (`String tipo`) | referência compartilhada, mas **sem risco** (não dá para alterar) | idem |
| Campos mutáveis (`Arma arma`) | **referência compartilhada — os dois mudam juntos** | `arma.clonar()` — cada um com a sua |

O código do enunciado faz uma cópia rasa. A correção é o clone pedir uma cópia da arma também: `copia.setArma(antigo.getArma().clonar())` — de preferência dentro de um construtor de cópia na própria classe `Inimigo`, e não na `Fase`.

---

## Exercício 4 — Exemplo real: `Object.clone()` e `Cloneable` no OpenJDK

Trechos verificados no código-fonte do JDK (`src.zip` → `java.base/java/lang/`):

```java
// Object.java, linha 237
@IntrinsicCandidate
protected native Object clone() throws CloneNotSupportedException;
```

```java
// Cloneable.java — a interface inteira
public interface Cloneable {
}
```

### a) Qual mecanismo está mais próximo da ideia de Prototype do GoF?

**O contrato visto em aula (interface com `clonar()` + construtor de cópia) está claramente mais próximo do GoF.** Motivos:

1. **`Cloneable` não é um contrato — é uma marcação.** O próprio Javadoc admite: *"Note that this interface does **not** contain the clone method. Therefore, it is not possible to clone an object merely by virtue of the fact that it implements this interface."* No GoF, `Prototype` é uma **interface com a operação `Clone()`** que o cliente chama polimorficamente. Com `Cloneable` isso é impossível: `Cloneable c = ...; c.clone();` nem compila, porque `clone()` é `protected` em `Object` e não está declarado na interface. Ou seja, falta justamente a abstração que define o padrão.

2. **A assinatura vaza detalhes para o cliente.** Retornar `Object` obriga a um *cast* (a covariância desde o Java 5 ameniza, mas o contrato-base continua `Object`), e `CloneNotSupportedException` é uma exceção *checked* que o chamador precisa tratar mesmo sabendo que ela nunca vai acontecer. Um `InimigoPrototype clonar()` devolve o tipo certo, sem cast e sem `try/catch`: o cliente simplesmente pede uma cópia.

3. **Quem controla a cópia é a VM, não o objeto.** O `clone()` nativo faz uma cópia campo a campo direto na memória e **não chama construtor nenhum** — invariantes não são validadas e campos `final` não podem ser reatribuídos depois do `super.clone()`, o que faz o mecanismo brigar com imutabilidade. No GoF, o protótipo é responsável por saber se copiar, inclusive decidindo o que precisa de cópia profunda. O construtor de cópia devolve essa responsabilidade ao objeto e mantém a construção normal do Java.

4. **A cadeia de `super.clone()` é frágil.** Uma classe que chame `new MinhaClasse()` em vez de `super.clone()` quebra a garantia `x.clone().getClass() == x.getClass()` para todas as subclasses — um contrato que o compilador não verifica e que só falha em tempo de execução.

**Ressalva honesta, em favor do `clone()` nativo:** ele preserva o tipo dinâmico automaticamente. `super.clone()` chamado em uma subclasse já devolve uma instância da subclasse, sem escrever nada. Um construtor de cópia precisa ser reimplementado em cada subclasse, senão ocorre *slicing* (o mesmo problema do Exercício 3.a). Essa é a única vantagem real do mecanismo — e é por isso que a recomendação usual (Bloch, *Effective Java*, item "Override clone judiciously") é preferir **construtor de cópia ou fábrica de cópia**, exatamente o modelo visto em aula.

### b) O `clone()` nativo faz cópia rasa ou profunda?

**Cópia rasa (*shallow copy*)** — e o Javadoc do `Object.java` diz isso com todas as letras:

> *"this method creates a new instance of the class of this object and initializes all its fields with exactly the contents of the corresponding fields of this object, **as if by assignment**; the contents of the fields are not themselves cloned. Thus, this method performs a **"shallow copy"** of this object, not a "deep copy" operation."*

Na prática, é o mesmo comportamento do `copia.setArma(antigo.getArma())` do Exercício 3: os campos de referência são copiados como **ponteiros**, então o clone e o original passam a compartilhar os mesmos objetos internos (a `Arma`, uma lista, um array). Para obter cópia profunda é preciso sobrescrever `clone()` e clonar os campos mutáveis na mão:

```java
@Override
public Inimigo clone() {
    try {
        Inimigo copia = (Inimigo) super.clone();  // rasa
        copia.arma = this.arma.clonar();          // a parte profunda é responsabilidade sua
        return copia;
    } catch (CloneNotSupportedException e) {
        throw new AssertionError(e);              // não acontece: a classe é Cloneable
    }
}
```

Um exemplo real disso na própria biblioteca: `ArrayList.clone()` copia o array interno (senão as duas listas cresceriam juntas), mas **os elementos dentro do array continuam compartilhados** — a cópia é profunda em um nível e rasa no nível seguinte. Reforça a lição: no Java, cópia profunda nunca vem de graça, sempre é decisão explícita de quem escreve a classe.

---

## Exercício 5 — Implementação

Código completo em `src/prototype/`:

| Arquivo | Papel no padrão |
|---|---|
| `Arma.java` | Objeto mutável interno, com `clonar()` — é o que exige a cópia profunda |
| `InimigoPrototype.java` | O contrato `Prototype` do GoF: `InimigoPrototype clonar()` |
| `Inimigo.java` | `ConcretePrototype`: construtor de cópia (com `arma.clonar()`) + `clonar()` |
| `RegistroDePrototipos.java` | *Prototype Registry*: `Map<String, InimigoPrototype>` que devolve **sempre um clone** |
| `Main.java` | Cliente: pede pelo nome, cria o elite, prova identidade e cópia profunda |

### Como executar

```bash
javac -encoding UTF-8 -d out src/prototype/*.java
java -cp out prototype.Main
```

(No macOS com o OpenJDK do Homebrew fora do PATH: `export PATH="/opt/homebrew/opt/openjdk/bin:$PATH"`.)

### O que a saída prova

1. **Criação pelo nome** — o cliente chama `registro.getPrototipo("mago")`, sem `new Inimigo(...)` em lugar nenhum.
2. **Variação sobre a base** — o "Guerreiro Elite" sai de 120/18 para 300/45 e o protótipo do registro continua 120/18 com a Espada Longa.
3. **Clones distintos** — `mago1 == mago2` é `false`, e os `identityHashCode` impressos são todos diferentes.
4. **Cópia profunda** — cada clone tem sua própria `Arma` (ids diferentes na saída); transformar a arma do `mago1` em "Cajado Amaldiçoado (+999)" não toca no `mago2` nem no protótipo.
5. **Contraprova com cópia rasa** — `copiaRasa()` passa a mesma instância de `Arma`; ao alterar a arma do `arqueiroB`, a do `arqueiroA` **muda sozinha**, reproduzindo o bug do Exercício 3.

A saída real da execução está em `saida-execucao.txt`.

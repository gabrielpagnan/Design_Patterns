# Atividade 08 — Bridge

## Exercício 1 — Aplicações

### 1. Notificações (tipo de conteúdo × canal de envio)

**Faz sentido usar Bridge.** São duas dimensões ortogonais: o conteúdo (informativo, alerta, promoção) e o canal (SMS, e-mail, push) variam por motivos diferentes e sem relação entre si. Sem o padrão, seriam 3 × 3 = 9 classes (`AlertaPorSms`, `AlertaPorEmail`…); com Bridge, a notificação recebe um `CanalEnvio` no construtor e ficam 3 + 3 = 6 classes, que crescem somadas e não multiplicadas.

### 2. Aplicação multiplataforma sobre APIs de Windows, Linux e macOS

**Faz sentido usar Bridge.** Esse é o caso de origem do padrão: a lógica da aplicação é a abstração e cada API de sistema operacional é uma implementação. A interface da aplicação depende de uma interface estreita (`ApiPlataforma`, por exemplo), então adicionar um novo sistema operacional não cria uma variante nova de cada tela.

### 3. Repositório que funciona com MySQL ou PostgreSQL, com troca em tempo de execução

**Faz sentido usar Bridge.** A lógica de repositório é uma dimensão e o banco é outra, e a troca em tempo de execução só é possível porque a ligação é por composição — com herança o par ficaria fixo na compilação. Basta o repositório guardar uma referência a `ConexaoBanco` e trocar o objeto apontado para mudar de banco com o sistema no ar.

### 4. Framework de testes em diferentes ambientes com diferentes configurações de hardware

**Não faz sentido usar Bridge.** Configuração de hardware (memória, número de núcleos, resolução) é *dado*, não comportamento: isso se resolve com parâmetros ou um objeto de configuração, e criar uma hierarquia de classes para representar valores seria overengineering. Só valeria a pena se os ambientes exigissem mecanismos de execução realmente distintos (local, contêiner, nuvem) — aí o executor seria a abstração e o mecanismo, a implementação; o hardware continuaria sendo configuração.

## Exercício 2 — Analogia

Uma furadeira e suas brocas. De um lado existem as máquinas: furadeira com fio, parafusadeira a bateria, furadeira de bancada. Do outro existem as pontas: broca para madeira, para concreto, para metal, bit de parafuso. Quem faz o furo é a ponta — a máquina só entrega rotação e força e **delega** o corte a ela.

Fabricar uma máquina pronta para cada combinação seria inviável: com 3 máquinas e 10 pontas seriam 30 ferramentas diferentes na loja (e uma nova ponta obrigaria a lançar outras 3). Como existe um encaixe padrão, o mandril, são só 3 + 10 = 13 produtos, e a combinação é feita na hora pelo usuário. Cada lado evolui sozinho: o fabricante pode lançar uma furadeira mais leve sem mexer em nenhuma broca, e uma broca nova de cerâmica funciona nas máquinas que já estão na prateleira. O mandril é a ponte; a rosca que ele aceita é a interface.

## Exercício 3 — Anti-pattern

### a) Por que a hierarquia é um problema?

Forma e cor são duas dimensões independentes, mas o código juntou as duas em uma única hierarquia de herança: cada classe é uma combinação fixa, decidida na compilação. Isso gera duplicação nos dois sentidos — a lógica de desenhar círculo aparece em `CirculoVermelho` e `CirculoAzul`, e a lógica da cor vermelha aparece em `CirculoVermelho` e `QuadradoVermelho`.

O resultado é que qualquer mudança se espalha. Corrigir o cálculo do círculo obriga a editar todas as classes de círculo, e mudar o tom de vermelho obriga a editar todas as classes vermelhas — com risco de esquecer uma e deixar o editor inconsistente.

### b) O que acontece ao adicionar uma forma ou uma cor?

Adicionar `Triangulo` exige uma classe por cor existente (`TrianguloVermelho`, `TrianguloAzul`). Adicionar `Verde` exige uma classe por forma existente (`CirculoVerde`, `QuadradoVerde`, `TrianguloVerde`).

Para **N** formas e **M** cores seriam **N × M** classes: 2 × 2 = 4 hoje, 3 × 3 = 9 depois das duas inclusões, 100 classes com 10 formas e 10 cores. Com Bridge o total passa a ser **N + M** (10 + 10 = 20), porque cada lado cresce por conta própria.

### c) Solução usando Bridge

A cor deixa de ser um tipo de forma e passa a ser uma hierarquia própria, a implementação:

```java
public interface Cor {
    void aplicar();
}

public class Vermelho implements Cor {
    @Override
    public void aplicar() {
        System.out.println("preenchendo com vermelho");
    }
}
```

A forma continua sendo a abstração, mas guarda uma referência a uma `Cor` e delega a ela a parte do desenho que é cor:

```java
public abstract class Forma {

    protected Cor cor;

    protected Forma(Cor cor) {
        this.cor = cor;
    }

    public abstract void desenhar();
}

public class Circulo extends Forma {

    public Circulo(Cor cor) {
        super(cor);
    }

    @Override
    public void desenhar() {
        System.out.print("Desenhando círculo: ");
        cor.aplicar();
    }
}
```

O campo `cor` é a ponte. A lógica do círculo existe em um lugar só, a do vermelho também, e as combinações são montadas em tempo de execução (`new Circulo(new Verde())`) — inclusive trocando a cor de uma forma já criada, o que a herança não permitia. Cada lado evolui de forma independente: uma forma nova implica uma classe que estende `Forma` e não toca em nenhuma cor; uma cor nova implica uma classe que implementa `Cor` e não toca em nenhuma forma.

## Exercício 4 — Exemplo real: JavaFX (OpenJFX)

Código consultado: [Skin.java](https://github.com/openjdk/jfx/blob/master/modules/javafx.controls/src/main/java/javafx/scene/control/Skin.java) e [Button.java](https://github.com/openjdk/jfx/blob/master/modules/javafx.controls/src/main/java/javafx/scene/control/Button.java).

### a) As duas hierarquias

A **abstração** é a hierarquia de controles: `Control`, que é declarado como `public abstract class Control extends Region implements Skinnable`, e suas abstrações refinadas — `Button extends ButtonBase` (que por sua vez desce de `Labeled` e `Control`), `CheckBox`, `Slider`, `ListView` e os demais. É o lado do comportamento: propriedades, eventos, ações.

A **implementação** é a hierarquia de aparência, definida pela interface `Skin<C extends Skinnable>`, com métodos enxutos: `getSkinnable()`, `getNode()`, `install()` e `dispose()`. Dela descendem `SkinBase`, `LabeledSkinBase`, `ButtonSkin`, `CheckBoxSkin` e as outras skins.

A ponte é o campo de skin do `Control`: `private ObjectProperty<Skin<?>> skin`, exposto por `skinProperty()` / `getSkin()` / `setSkin()`. O controle não desenha nada: ele delega a renderização ao nó devolvido pela skin, exatamente como o `ControleRemoto` delega ao `Dispositivo` — com a vantagem de que, sendo uma *property* observável, a skin pode ser trocada com a aplicação rodando.

### b) O `createDefaultSkin()` do `Button`

Em `Button.java` o método é curto:

```java
@Override protected Skin<?> createDefaultSkin() {
    return new ButtonSkin(this);
}
```

Ele devolve uma nova instância de `ButtonSkin` ligada àquele botão (`this`). Quem chama é o próprio `Control`, em `doProcessCSS()`, e somente quando `getSkin() == null` — ou seja, quando ninguém definiu a skin pelo CSS (`-fx-skin`) nem por `setSkin(...)`. Em `Control` o método é só um gancho que retorna `null`; cada controle refinado responde com a sua skin padrão.

O `Button` delega a aparência em vez de embutir o desenho por três motivos. Primeiro, separa o que muda por motivos diferentes: comportamento (o que o botão faz quando é acionado) de um lado, renderização (fundo, borda, sombra, fonte, posição do ícone) do outro. Segundo, permite trocar o visual sem tocar no controle — os temas Modena e Caspian, o CSS e skins próprias mudam a aparência de todos os botões sem nenhuma subclasse de `Button`; se o desenho estivesse dentro do controle, cada tema exigiria um `BotaoModena`, um `BotaoCaspian` e assim por diante, a mesma explosão do Exercício 3. Terceiro, porque o contrato com a aparência é estreito (quatro métodos em `Skin`): o `Button` depende de uma interface pequena, e não das dezenas de detalhes de layout que vivem dentro de `ButtonSkin`.

## Exercício 5 — Implementação

O código está em `src/veiculos`, com os papéis do padrão separados assim:

| Papel no Bridge | Classe |
|---|---|
| Implementador | `Motor` (interface com `ligar()` e `acelerar()`) |
| Implementações concretas | `MotorGasolina`, `MotorEletrico` |
| Abstração | `Veiculo` (campo `protected Motor motor`, recebido no construtor) |
| Abstrações refinadas | `Sedan`, `Suv` |
| Cliente | `Main` |

`Veiculo.dirigir()` liga o motor e `Veiculo.acelerar()` delega ao motor; `Sedan` e `Suv` imprimem o próprio tipo antes de chamar `super.dirigir()`. Nenhuma das duas classes de veículo menciona `MotorGasolina` ou `MotorEletrico`: elas conhecem apenas o tipo `Motor`.

### Como executar

```bash
javac -encoding UTF-8 -d out src/veiculos/*.java
java -cp out veiculos.Main
```

(No macOS, com o OpenJDK do Homebrew fora do PATH: `export PATH="/opt/homebrew/opt/openjdk/bin:$PATH"`.)

Teste de verificação (`test/veiculos/BridgeTest.java`, precisa do `-ea` para ligar os `assert`):

```bash
javac -encoding UTF-8 -cp out -d out test/veiculos/*.java
java -ea -cp out veiculos.BridgeTest
```

A saída completa do `Main` está em `saida-execucao.txt`.

### O que a saída mostra

As quatro combinações são montadas no `Main` a partir de **dois** motores e **duas** classes de veículo — nenhuma classe `SedanGasolina` existe:

```java
Motor gasolina = new MotorGasolina();
Motor eletrico = new MotorEletrico();

Veiculo[] frota = {
        new Sedan(gasolina),
        new Sedan(eletrico),
        new Suv(gasolina),
        new Suv(eletrico)
};
```

O mesmo `Sedan` aparece ligando um motor a combustão e um motor elétrico, e o mesmo objeto `gasolina` é reaproveitado no `Sedan` e no `Suv` — prova de que os dois lados são mesmo independentes. O laço final trata todos como `Veiculo`, sem saber qual motor está dentro.

### Como adicionar um novo motor ou um novo veículo

Para um `MotorHibrido`, basta uma classe que implemente `Motor`:

```java
public class MotorHibrido implements Motor {
    @Override public void ligar() { /* ... */ }
    @Override public void acelerar() { /* ... */ }
}
```

`Veiculo`, `Sedan` e `Suv` continuam intactos, e `new Sedan(new MotorHibrido())` já funciona.

Para uma `Picape`, basta uma classe que estenda `Veiculo`:

```java
public class Picape extends Veiculo {
    public Picape(Motor motor) { super(motor); }
    @Override public void dirigir() { /* imprime o tipo */ super.dirigir(); }
}
```

Nenhum motor é alterado, e a picape já nasce funcionando com os três motores. É a diferença entre somar e multiplicar: com herança, 3 motores × 3 veículos exigiriam 9 classes e cada inclusão criaria várias outras; com Bridge são 3 + 3 = 6 classes, e cada inclusão custa **uma**.

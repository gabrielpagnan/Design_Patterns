# Atividade 05 — Padrão Factory Method

---

## Exercício 1 — Aplicações

### 1. Serviço de notificações (email, SMS, push)
**Faz sentido usar Factory Method.**
O fluxo de envio é fixo e só a criação do canal varia — que é exatamente a situação que o padrão resolve. Deixar a criação em um método fábrica evita que o serviço principal dependa das classes concretas de cada canal, e como novos canais entram com frequência, cada um vira apenas uma nova subclasse.

### 2. Classe `Ponto(x, y)` instanciada dezenas de vezes por segundo
**Não faz sentido usar Factory Method.**
Não existe variação nenhuma na criação: sempre é o mesmo tipo, com os mesmos dois campos. Colocar um criador abstrato e uma subclasse só para chamar `new Ponto(x, y)` seria overengineering — mais classes, mais indireção e nenhum problema resolvido, ainda por cima em um trecho sensível a desempenho.

### 3. Framework de exportação de relatórios (PDF, CSV, Excel)
**Faz sentido usar Factory Method.**
O enunciado descreve o padrão quase literalmente: o módulo principal não pode conhecer as classes concretas e novos formatos devem entrar só com novas subclasses. O método fábrica isola o `new` nas subclasses, então o fluxo de geração de relatório continua trabalhando apenas com a abstração do exportador.

### 4. Cliente HTTP mock nos testes e real (OkHttp) em produção
**Faz sentido usar Factory Method.**
O que muda entre os dois ambientes é justamente qual implementação concreta é criada, e o resto do código precisa continuar igual. Um método fábrica sobrescrito na versão de teste devolve o mock, e o código cliente segue dependendo só da interface do cliente HTTP.

### 5. Classe `Produto` com três campos, criada em um único ponto
**Não faz sentido usar Factory Method.**
Não há variação de tipo a decidir, nem extensibilidade prevista, nem cliente que precise ser desacoplado — a criação acontece em um lugar só. O construtor tradicional já resolve, e o padrão aqui só acrescentaria uma hierarquia inútil de criadores.

**Resumindo:** o Factory Method se justifica quando existe mais de um tipo concreto possível e o cliente não deve escolher qual é (itens 1, 3 e 4). Nos itens 2 e 5 existe um tipo só e um jeito só de criar, então o padrão vira custo puro.

---

## Exercício 2 — Analogia

### Uma rede de gráficas rápidas de personalizados

Imagine uma rede de lojas de brindes personalizados espalhada em vários shoppings. O **atendimento é idêntico em todas as unidades**: o cliente chega com a arte no pen drive, o atendente confere a imagem, a loja produz o item na hora, embala e entrega com a nota fiscal.

O que muda é **o que cada unidade produz**. A unidade de camisetas estampa uma camiseta; a de canecas produz uma caneca; a de banners imprime um banner em lona. O cliente não precisa saber nada sobre prensa térmica ou plotter — ele entrega a arte e recebe **um item personalizado pronto**, seja ele qual for.

Quando a rede abre uma **unidade de adesivos**, nada do roteiro de atendimento muda: o gerente da nova loja só define "aqui o item produzido é um adesivo" e o mesmo processo de sempre passa a valer. Ninguém precisa reescrever o manual de atendimento da rede.

Ligando com o padrão:

- o **manual de atendimento da rede** é o criador abstrato, com o fluxo fixo;
- **"produzir o item"** é o método fábrica: está previsto no manual, mas quem define o que sai é cada unidade;
- **cada unidade** (camiseta, caneca, banner) é um criador concreto;
- o **item personalizado** é o produto, e o cliente lida só com essa ideia genérica, nunca com "camiseta" ou "caneca" especificamente;
- a **nova unidade de adesivos** mostra a extensibilidade: entra uma loja nova, o processo continua o mesmo.

---

## Exercício 3 — Anti-pattern

### a) Por que o if/else é um problema de design?

O `NotificadorService` fica **acoplado a todas as classes concretas** ao mesmo tempo. Ele dá `new EmailNotificador()`, `new SmsNotificador()` e `new PushNotificador()`, ou seja, precisa conhecer o nome de cada canal existente no sistema, mesmo que uma execução use um só.

Além disso:

- **A classe tem mais de um motivo para mudar** (fere o SRP): ela muda quando o fluxo de notificação muda *e* quando um canal novo aparece.
- **A cadeia de `if/else` cresce indefinidamente.** Cada canal novo é mais um `else if`, e o método vai virando um bloco longo que ninguém quer tocar.
- **O canal é uma `String` solta.** `canal.equals("email")` não é verificado pelo compilador: um `"e-mail"` ou `"EMAIL"` compila normalmente e falha silenciosamente em tempo de execução — repare que, se nenhum `if` bater, o método simplesmente **não faz nada e não avisa ninguém**.
- **As classes concretas nem têm um contrato comum.** `EmailNotificador`, `SmsNotificador` e `PushNotificador` não implementam nenhuma interface; têm apenas métodos com a mesma assinatura por coincidência. Não dá para tratá-las polimorficamente nem para escrever um teste com um notificador falso.

### b) O que muda para adicionar o WhatsApp? Que riscos isso traz?

Seria necessário criar a classe `WhatsAppNotificador` e **editar o `NotificadorService`**, acrescentando mais um `else if (canal.equals("whatsapp"))`.

Os riscos vêm justamente de mexer numa classe que já estava pronta e testada:

- **Viola o OCP.** A classe deveria estar fechada para modificação, mas todo canal novo obriga a abrir e alterar o mesmo arquivo.
- **Risco de regressão.** Ao editar o método, é possível quebrar por acidente um canal que já funcionava (um `else` no lugar errado, um retorno esquecido). Todos os testes de e-mail, SMS e push precisam ser rodados de novo por causa de uma mudança que, em tese, não tinha nada a ver com eles.
- **Conflito de versionamento.** Se duas pessoas adicionarem canais diferentes na mesma semana, ambas alteram exatamente o mesmo método — conflito de merge garantido.
- **O problema se repete em todo lugar.** Se em outro ponto do sistema existir um `if/else` parecido (por exemplo, para escolher o template da mensagem), o canal novo tem que ser lembrado em cada um deles. Esquecer um só gera um bug silencioso.

### c) Solução com Factory Method

A ideia é tirar o `new` de dentro do fluxo e empurrá-lo para subclasses:

**1. Interface do produto** — `Notificador`, com o contrato `void enviar(String destinatario, String mensagem)`. Todos os canais passam a implementá-la, e o fluxo de envio conversa só com esse tipo.

**2. Criador abstrato** — `NotificacaoService`, contendo:
- o método público `notificar(...)` com o fluxo comum (montar mensagem, obter o notificador, enviar, registrar log), escrito uma única vez;
- o método fábrica `protected abstract Notificador criarNotificador();`, que é o "buraco" deixado para as subclasses preencherem.

**3. Criadores concretos** — `EmailService`, `SmsService` e `PushService`, cada um estendendo `NotificacaoService` e sobrescrevendo `criarNotificador()` para devolver o notificador do seu canal.

Com isso, a `String canal` e o `if/else` deixam de existir: o cliente escolhe qual service instanciar (ou recebe um por injeção de dependência) e chama sempre `notificar(...)`. Para adicionar o WhatsApp, basta criar `WhatsAppNotificador` e `WhatsAppService` — **nenhuma classe existente é alterada**, que é exatamente o que o OCP pede.

---

## Exercício 4 — Exemplo real: `iluwatar/java-design-patterns`

Código conferido em `factory-method/src/main/java/com/iluwatar/factory/method/`.

```java
public interface Blacksmith {
  Weapon manufactureWeapon(WeaponType weaponType);
}
```

```java
public class ElfBlacksmith implements Blacksmith {

  private static final Map<WeaponType, ElfWeapon> ELFARSENAL;

  static {
    ELFARSENAL = new EnumMap<>(WeaponType.class);
    Arrays.stream(WeaponType.values()).forEach(type -> ELFARSENAL.put(type, new ElfWeapon(type)));
  }

  @Override
  public Weapon manufactureWeapon(WeaponType weaponType) {
    return ELFARSENAL.get(weaponType);
  }

  @Override
  public String toString() {
    return "The elf blacksmith";
  }
}
```

### a) Qual o papel da interface `Blacksmith`?

`Blacksmith` é o **Creator** do padrão: é a abstração que declara o método fábrica e à qual o cliente se liga. O método fábrica declarado é `manufactureWeapon(WeaponType weaponType)` e o **tipo de retorno é `Weapon`**, ou seja, também uma abstração.

É o mesmo papel da `Logistica` vista em aula, só que aqui o criador é uma interface em vez de uma classe abstrata — o padrão admite as duas formas. A diferença é que a interface não pode conter o fluxo comum, então neste exemplo o `Blacksmith` só declara a criação.

### b) O que o `manufactureWeapon(...)` do `ElfBlacksmith` retorna? Por que o retorno é `Weapon`?

Ele retorna um **`ElfWeapon`** — uma arma élfica do tipo pedido (lança, machado etc.). Vale notar que a versão atual do repositório monta um `EnumMap` estático com uma arma de cada tipo e devolve a instância já pronta; o efeito para o cliente é o mesmo, é o ferreiro élfico quem decide qual objeto concreto sai dali.

O tipo declarado é `Weapon`, e não `ElfWeapon`, porque é isso que garante o **desacoplamento**: quem chama recebe "uma arma", sem saber que é élfica. Se a assinatura devolvesse `ElfWeapon`, o cliente ficaria preso ao ferreiro élfico e não conseguiria trocar por um orc sem alterar código. Além disso, a assinatura tem que ser compatível com a da interface — declarar o retorno concreto quebraria a ideia de tratar todos os ferreiros pelo mesmo tipo.

### c) O que seria necessário para adicionar um ferreiro anão? Relação com o OCP

Bastaria criar duas classes novas:

1. `DwarfWeapon implements Weapon` — o produto concreto;
2. `DwarfBlacksmith implements Blacksmith` — o criador concreto, sobrescrevendo `manufactureWeapon(...)` para devolver armas anãs.

E só. **Nenhuma classe existente precisa ser modificada**: `Blacksmith`, `Weapon`, `ElfBlacksmith` e `OrcBlacksmith` continuam iguais. O único ponto que muda é a linha onde se decide qual ferreiro instanciar — na `App`, o `new OrcBlacksmith()`. Num sistema real esse ponto normalmente sai do código de negócio e vai para a configuração ou para um container de injeção de dependência.

Isso é o **OCP (Open/Closed Principle)** na prática: o sistema fica *aberto para extensão* (dá para acrescentar quantos ferreiros e armas se quiser) e *fechado para modificação* (o código que já funciona e já foi testado não é tocado). O contraste com o Exercício 3 é direto: lá, um canal novo obrigava a editar o `if/else` de uma classe já testada; aqui, um ferreiro novo é só um arquivo novo.

---

## Exercício 5 — Implementação

Código em `src/factory/`. Saída da execução em `saida-execucao.txt`.

| Arquivo | Papel no padrão |
|---|---|
| `Notificador.java` | Product — contrato `enviar(destinatario, mensagem)` |
| `EmailNotificador.java`, `SmsNotificador.java`, `PushNotificador.java` | ConcreteProducts — cada um entrega do seu jeito |
| `NotificacaoService.java` | Creator — fluxo comum em `notificar(...)` + método fábrica abstrato |
| `EmailService.java`, `SmsService.java`, `PushService.java` | ConcreteCreators — sobrescrevem `criarNotificador()` |
| `Main.java` | Cliente — usa os três services com a mesma chamada |

### Como executar

```bash
javac -encoding UTF-8 -d out src/factory/*.java
java -cp out factory.Main
```

(No macOS, com o OpenJDK do Homebrew fora do PATH: `export PATH="/opt/homebrew/opt/openjdk/bin:$PATH"`.)

### O que a saída mostra

Os três canais passam pelas mesmas quatro etapas — a mensagem recebe a assinatura "-- Portal Academico", o notificador é criado, o envio acontece e o log sai no mesmo formato. A única diferença nas linhas impressas é o canal que efetivamente entregou. O `Main` não instancia nenhum `Notificador` diretamente.

### Como adicionar um novo canal (WhatsApp)

Duas classes novas, nenhuma alteração no que já existe:

```java
public class WhatsAppNotificador implements Notificador {
    @Override
    public void enviar(String destinatario, String mensagem) {
        System.out.println("[WHATSAPP] Enviando para " + destinatario + ": " + mensagem);
    }

    @Override
    public String getCanal() {
        return "WhatsApp";
    }
}

public class WhatsAppService extends NotificacaoService {
    @Override
    protected Notificador criarNotificador() {
        return new WhatsAppNotificador();
    }
}
```

Depois disso, `new WhatsAppService().notificar(...)` já funciona. O método `notificar(...)` da `NotificacaoService` **não é tocado**, e os canais que já estavam testados continuam intactos — de novo, o OCP.

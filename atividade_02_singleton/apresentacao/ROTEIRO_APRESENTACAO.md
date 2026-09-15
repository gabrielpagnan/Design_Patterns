# Roteiro de apresentação do Singleton

Tempo previsto: **4 minutos e 35 segundos**

Objetivo: explicar o padrão com clareza, mostrar o código essencial e concluir quando ele faz sentido.

## Slide 1 — Singleton e a Central de Alertas — 30 segundos

> Imaginem que Polícia, Bombeiros e SAMU atendem a mesma cidade. Agora pensem no que aconteceria se cada órgão trabalhasse com uma central diferente. Um deles poderia saber de um acidente enquanto os outros não receberiam a informação. O padrão Singleton resolve problemas desse tipo quando o sistema realmente precisa de um único objeto compartilhado. Vou mostrar como ele funciona usando uma Central de Alertas.

**Dica:** comece olhando para a turma. Não leia o título do slide.

## Slide 2 — O problema de várias centrais — 40 segundos

> Sem controle, cada órgão poderia usar `new` e criar sua própria central. Assim, cada objeto teria um histórico diferente. A Polícia registraria o acidente em uma central, os Bombeiros consultariam outra, e o SAMU poderia acessar uma terceira. O programa continuaria executando, mas cada parte enxergaria apenas uma parte da verdade. A analogia mostra o requisito principal: todos precisam conversar com a mesma central.

**Frase-chave:** várias centrais produzem informações desencontradas.

## Slide 3 — A ideia do Singleton — 45 segundos

> Singleton é um padrão de criação. Ele garante que uma classe tenha uma única instância e fornece um ponto conhecido para acessar esse objeto. Para isso, a própria classe guarda sua instância, bloqueia a criação externa e oferece o método `getInstancia`. No exemplo, os órgãos não constroem centrais. Eles apenas pedem à classe a central que já existe. Toda chamada devolve exatamente o mesmo objeto.

**Se precisar simplificar:** uma classe controla a criação do seu único objeto.

## Slide 4 — A estrutura em Java — 70 segundos

> O código tem três partes importantes. Primeiro, o atributo `INSTANCIA` é estático. Isso faz com que ele pertença à classe e não a cada objeto. A própria classe cria a única central. Segundo, o construtor é privado. Por isso, outra classe não consegue escrever `new CentralDeAlertas`. Terceiro, o método público e estático `getInstancia` devolve a instância guardada. A inicialização usada aqui é antecipada: a JVM cria o objeto quando inicializa a classe. Essa solução é curta e segura para a criação da instância. Depois disso, métodos normais, como `registrar`, trabalham com o histórico compartilhado.

**Aponte para o código nesta ordem:** `static`, `private` e `getInstancia()`.

## Slide 5 — Uma central compartilhada — 50 segundos

> No programa principal, criamos três variáveis: uma para a Polícia, uma para os Bombeiros e outra para o SAMU. Todas chamam `getInstancia`. Cada órgão registra um alerta. Quando consultamos o histórico por qualquer variável, aparecem as três mensagens. A comparação com dois sinais de igual retorna `true`, pois as variáveis apontam para o mesmo objeto na memória. Essa é a prova prática do Singleton: acessos diferentes chegam à mesma instância e ao mesmo estado.

**Dica:** destaque o resultado `true` antes de falar do histórico.

## Slide 6 — Quando usar e quando evitar — 40 segundos

> O Singleton funciona bem quando a unicidade faz parte do problema, como uma configuração da aplicação, um registro central ou um gerenciador de recursos. Ele deve ser evitado quando cada usuário ou contexto precisa do próprio estado. Um carrinho de compras, por exemplo, precisa existir por cliente. O padrão também pode criar dependências globais e dificultar testes. Portanto, a regra final é simples: use Singleton quando o sistema precisa provar que existe uma única instância compartilhada, e não apenas por conveniência.

**Fechamento:** uma única instância, acessada de forma controlada.

## Perguntas prováveis do professor

### Por que o construtor precisa ser privado?

Porque um construtor público permitiria `new CentralDeAlertas()` e criaria outras instâncias. O construtor privado entrega o controle da criação à própria classe.

### Por que o atributo é `static`?

Porque a instância precisa pertencer à classe. Assim, `getInstancia()` consegue acessá-la sem depender de outro objeto de `CentralDeAlertas`.

### Singleton e classe estática são a mesma coisa?

Não. O Singleton ainda é um objeto, pode implementar interfaces e pode ser passado como referência. Uma classe estática reúne membros ligados diretamente à classe e não possui uma instância controlada.

### Essa implementação é segura com várias threads?

A criação da instância é segura, porque a JVM inicializa o campo estático uma única vez. Porém, o `ArrayList` usado para simplificar o exemplo não protege alterações simultâneas. Em um sistema concorrente, o estado interno também precisaria de uma coleção thread-safe ou sincronização.

### Existe uma única instância no sistema inteiro?

Existe uma instância por carregador de classes e, normalmente, por JVM. Se a aplicação roda em vários servidores, cada processo terá seu próprio Singleton. Um banco de dados ou serviço compartilhado resolve a unicidade entre servidores.

### Qual é a principal vantagem?

Manter um recurso realmente único e oferecer um acesso controlado a ele.

### Qual é a principal desvantagem?

O acesso global esconde dependências e o estado pode sobreviver entre testes, causando acoplamento e dificultando o isolamento.

### Quando Singleton seria uma escolha errada?

Quando o dado deve existir por usuário, sessão ou tarefa. Exemplos: carrinho de compras, usuário autenticado e rascunho de documento.

### Quais exemplos comuns podem usar Singleton?

Configurações da aplicação, registro de eventos e um gerenciador local de recursos. A escolha só faz sentido quando todos realmente precisam compartilhar a mesma instância.

## Demonstração opcional

Na pasta `codigo`, execute:

```bash
javac -encoding UTF-8 CentralDeAlertas.java Main.java
java Main
```

Mostre apenas as duas últimas linhas se o tempo estiver curto:

```text
Todas as referências apontam para o mesmo objeto: true
Total de alertas compartilhados: 3
```

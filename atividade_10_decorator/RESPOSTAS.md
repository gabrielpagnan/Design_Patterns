# Atividade 10 — Decorator

## Exercício 1 — Aplicações

### 1. Serviço de envio de e-mails com log e compressão de anexos

**Faz sentido usar Decorator.** Log e compressão são responsabilidades opcionais que precisam ser combinadas de formas diferentes em cada fluxo. Com decorators, cada uma vira uma classe e o fluxo escolhe quais camadas envolvem o serviço de envio, o que evita criar `EnvioComLog`, `EnvioComCompressao` e `EnvioComLogECompressao`.

### 2. Editor de imagens com filtros em cadeia

**Faz sentido usar Decorator.** Cada filtro recebe uma imagem, aplica seu efeito e devolve algo que continua sendo uma imagem, então as camadas podem ser empilhadas em qualquer ordem e quantidade. Uma classe por combinação de brilho, contraste e sépia seria impossível de manter.

### 3. Repositório de produtos com cache em um único caso de uso

**Faz sentido usar Decorator.** O decorator de cache implementa a mesma interface do repositório e guarda o repositório real dentro dele, então a interface que o resto do sistema usa não muda. Só o caso de uso de alta leitura recebe a versão decorada, e os demais consumidores continuam recebendo o repositório sem cache.

### 4. Catálogo de livros com título, autor e preço

**Não faz sentido usar Decorator.** Não existe comportamento opcional para somar: `Livro` só guarda dados e a estrutura é estável. Aplicar o padrão aqui só criaria interface, decorator abstrato e camadas para nada, o que é overengineering.

### 5. Concessionária em que todo carro sai igual

**Não faz sentido usar Decorator.** Se o pacote opcional é sempre o mesmo, não há combinação para montar em tempo de execução e o comportamento pode continuar dentro da própria classe `Carro`. O padrão só passaria a valer se a concessionária começasse a vender opcionais escolhidos pelo cliente.

## Exercício 2 — Analogia

Uma analogia é a pessoa se vestindo por camadas antes de sair de casa. A camiseta é a base, e por cima dela podem entrar um moletom, uma jaqueta corta-vento, um cachecol ou uma capa de chuva, em qualquer ordem e quantidade.

Cada peça envolve a anterior e soma alguma proteção, mas nenhuma delas muda o fato de que continua sendo a mesma pessoa vestida saindo pela porta. Quem olha de fora não precisa saber quantas camadas existem embaixo.

Fabricar uma peça única para cada combinação seria inviável: a loja precisaria de um produto para "camiseta com moletom", outro para "camiseta com moletom e capa de chuva", outro para "camiseta com capa de chuva e cachecol", e assim por diante. É mais simples vender as peças separadas e deixar quem veste combinar na hora.

## Exercício 3 — Anti-pattern

### a) Por que as flags booleanas são um problema de design?

A classe `Cafe` acumula o preço base e também as regras de todos os complementos, então ela deixa de ter uma responsabilidade só. As combinações ficam presas ao que foi previsto no código: só é possível montar um café com o que já virou atributo da classe.

A cada novo complemento é preciso alterar `Cafe` de novo, adicionando um campo booleano e mais um `if` dentro de `custo()` e outro dentro de `getDescricao()`. Isso quebra o princípio aberto/fechado, porque a classe nunca fica pronta, e os dois métodos crescem juntos a cada mudança.

### b) Bugs e confusões que esse código tende a gerar

O erro mais provável é atualizar um método e esquecer do outro: o caramelo entra no preço e não aparece na descrição, ou o contrário, e o cliente recebe um recibo que não bate com o valor cobrado. Como a ordem dos `if` é fixa, a descrição também sempre sai na mesma sequência, mesmo que o pedido tenha sido feito de outro jeito.

Também não existe nada que impeça combinações sem sentido, como marcar dois complementos que não podem andar juntos, nem como pedir leite em dobro, já que um `boolean` só aceita sim ou não. E quando o complemento não é apenas "somar preço" — por exemplo, um adicional que muda o tamanho do copo ou aplica desconto sobre o total — a regra não cabe em um `if` isolado e o método vira uma sequência de condições difícil de acompanhar.

### c) Solução usando Decorator

Os papéis ficam assim:

- **`Bebida` (Component):** interface com `getDescricao()` e `custo()`. É o tipo que a tela de pedidos passa a usar, sem saber quantas camadas existem por baixo.
- **`Cafe` (ConcreteComponent):** a bebida base. Volta a ter uma responsabilidade só, devolver `"Café"` e o preço base, e não conhece nenhum complemento.
- **`Complemento` (Decorator abstrato):** implementa `Bebida` e guarda um `protected final Bebida bebida` recebido no construtor. Como ele é uma `Bebida` e também contém uma `Bebida`, é o que permite empilhar as camadas.
- **`Leite`, `Chantilly`, `CaldaDeCaramelo` (ConcreteDecorators):** cada um estende `Complemento`, delega a chamada para o objeto interno e soma a sua parte no resultado, tanto no custo quanto na descrição.

As combinações passam a ser montadas na hora do pedido, envolvendo um objeto no outro:

```java
Bebida pedido = new Chantilly(new Leite(new Cafe()));
```

Como cada camada chama a camada de dentro antes de somar o que é seu, o preço e a descrição são construídos de fora para dentro. Um complemento novo é só mais uma classe que estende `Complemento`, e `Cafe` nunca precisa ser alterado.

## Exercício 4 — Implementação

O código está na pasta `src/cafeteria`:

- `Bebida`: interface (Component) com `getDescricao()` e `custo()`;
- `Cafe`: componente concreto, devolve `"Café"` e `5.0`;
- `Complemento`: decorator abstrato, guarda a `Bebida` decorada;
- `Leite` (+1.5) e `Chantilly` (+2.0): decorators concretos;
- `CaldaDeCaramelo` (+3.0): complemento criado depois, para mostrar a extensão;
- `Main`: monta e imprime as combinações.

Para compilar e executar:

```bash
javac -encoding UTF-8 -d out src/cafeteria/*.java
java -cp out cafeteria.Main
```

Saída em [`saida-execucao.txt`](saida-execucao.txt). Os testes ficam em `test/cafeteria/DecoratorTest.java`:

```bash
javac -encoding UTF-8 -d out src/cafeteria/*.java test/cafeteria/*.java
java -ea -cp out cafeteria.DecoratorTest
```

### Como adicionar um novo complemento

Basta criar uma classe que estenda `Complemento`, receba uma `Bebida` no construtor e delegue ao objeto interno:

```java
public class CaldaDeCaramelo extends Complemento {

    private static final double PRECO = 3.0;

    public CaldaDeCaramelo(Bebida bebida) {
        super(bebida);
    }

    @Override
    public String getDescricao() {
        return bebida.getDescricao() + " com calda de caramelo";
    }

    @Override
    public double custo() {
        return bebida.custo() + PRECO;
    }
}
```

`Cafe`, `Leite`, `Chantilly` e a interface `Bebida` continuam iguais, porque nenhum deles conhece os complementos que existem. A `Main` só precisa de uma linha nova se quiser exibir a combinação nova; o código antigo que já montava os pedidos anteriores não muda.

Como todo complemento aceita qualquer `Bebida`, a calda pode ser aplicada sobre o café puro ou sobre uma bebida que já tem outras camadas, e as combinações continuam sendo montadas no momento do pedido.

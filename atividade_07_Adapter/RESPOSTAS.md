# Atividade 07 — Adapter

## Exercício 1 — Aplicações

### 1. Módulo de previsão do tempo

**Faz sentido usar Adapter.** Os dois fornecedores têm APIs diferentes e não podem ser modificados. Um adaptador para cada SDK permite que o restante da aplicação use uma única interface de consulta.

### 2. Troca da biblioteca de envio de e-mail

**Faz sentido usar Adapter.** Um adaptador pode fazer a biblioteca nova funcionar com a interface que os serviços já conhecem. Assim, a troca fica concentrada em uma classe e não é necessário alterar todos os serviços de envio.

### 3. Criar adaptadores para todas as classes sem necessidade

**Não faz sentido usar Adapter.** Nesse caso não existe uma incompatibilidade real para resolver. Criar várias classes só para uma possível necessidade futura seria overengineering e deixaria o sistema mais difícil de entender.

### 4. Checkout integrado com um SDK de pagamento

**Faz sentido usar Adapter.** O SDK é código de terceiros e provavelmente possui nomes de métodos e tipos diferentes dos usados pelo Checkout. O adaptador diminui o acoplamento e evita que regras do fornecedor fiquem espalhadas pelo sistema.

## Exercício 2 — Analogia

Uma analogia seria uma conversa entre um turista brasileiro e um atendente japonês. Um fala português e o outro fala japonês, então um intérprete fica entre os dois, escuta em um idioma e transmite a mesma mensagem no outro.

Nenhuma das duas pessoas precisa aprender outro idioma para a conversa funcionar. Se outro turista que fala português entrar no lugar do primeiro, o atendente continua trabalhando da mesma forma, pois o intérprete ainda faz a adaptação entre os dois lados.

## Exercício 3 — Anti-pattern

### a) Por que o código é um problema?

O `PainelClima` está fazendo mais coisas do que deveria: escolhe o fornecedor, cria a API e ainda converte a temperatura. Além disso, os módulos ficam ligados diretamente às classes externas e o mesmo cálculo acaba repetido em vários lugares.

Isso dificulta a manutenção. Se uma regra mudar, é preciso lembrar de corrigir todos os locais onde ela foi copiada.

### b) Problemas causados por mudanças ou novos fornecedores

Se um SDK mudar a unidade de Celsius para Fahrenheit, algum módulo pode continuar tratando o valor como Celsius e mostrar uma temperatura errada. Também pode acontecer de corrigirem a conversão em um módulo e esquecerem dos outros.

Para adicionar um novo fornecedor seria necessário criar outro `if/else` em cada lugar que consulta o clima. Com o tempo, o código fica grande, repetido e mais sujeito a erros.

### c) Solução usando Adapter

A aplicação pode criar esta interface para representar o formato que ela espera:

```java
public interface PrevisaoService {
    int obterTemperatura(String cidade);
}
```

Depois, cada fornecedor recebe seu próprio adaptador. O `OpenWeatherAdapter` chama `temperatura()` e converte o `double` para `int`. O `AccuWeatherAdapter` chama `getTemperature()` e faz a conversão de Fahrenheit para Celsius.

O `PainelClima` passa a receber uma `PrevisaoService` no construtor. Dessa forma, ele só chama `obterTemperatura()` e não precisa saber qual SDK está sendo usado ou qual conversão é necessária.

## Exercício 4 — Exemplo real: InputStreamReader

Código consultado: [InputStreamReader.java no repositório do OpenJDK](https://github.com/openjdk/jdk/blob/master/src/java.base/share/classes/java/io/InputStreamReader.java).

### a) Interfaces incompatíveis

As duas interfaces são `InputStream`, que lê bytes, e `Reader`, que lê caracteres. A classe `InputStreamReader` estende `Reader` e recebe um `InputStream` no construtor, junto com o charset que será usado na conversão.

### b) Delegação dos métodos

Os métodos `read()`, `ready()` e `close()` delegam o trabalho para o objeto `StreamDecoder sd`. Como `InputStreamReader` é um `Reader`, o código cliente pode trabalhar somente com os métodos de `Reader` e não precisa acessar o `InputStream` que está dentro dele.

### c) Conversão e tipo de Adapter

A conversão acontece no `StreamDecoder`, que usa um `CharsetDecoder` para transformar os bytes lidos em caracteres. O `InputStreamReader` representa um **Adapter de objeto**, porque usa composição para guardar o objeto adaptado em vez de herdar dele.

Ele funciona com qualquer `InputStream` porque recebe o tipo abstrato no construtor. Por isso a origem dos bytes pode ser um arquivo, um socket, a memória ou qualquer outra classe que estenda `InputStream`.

## Exercício 5 — Implementação

O código está na pasta `src/clima`. As classes têm estes papéis:

- `PrevisaoService`: interface usada pela aplicação;
- `OpenWeatherApi` e `AccuWeatherApi`: fornecedores externos simulados;
- `OpenWeatherAdapter` e `AccuWeatherAdapter`: adaptam os fornecedores;
- `PainelClima`: cliente que depende apenas de `PrevisaoService`;
- `Main`: troca os fornecedores e mostra o resultado.

Para compilar e executar:

```bash
javac -encoding UTF-8 -d out src/clima/*.java
java -cp out clima.Main
```

Para adicionar um terceiro fornecedor, seria necessário criar a classe da API e um novo adaptador que implemente `PrevisaoService`. O `PainelClima` não precisaria ser alterado.

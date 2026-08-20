# Exercício 5 — Dependency Inversion Principle (DIP)

## Situação atual

```java
public class ProcessadorDePagamento {

    public void processarPagamento(double valor, String cartao) {
        PagInseguro gateway = new PagInseguro();          // acoplamento direto
        gateway.pagar(valor, cartao);
    }

    public boolean verificarPagamento(String idTransacao) {
        PagInseguro gateway = new PagInseguro();          // de novo
        return gateway.consultar(idTransacao);
    }

    public void cancelarPagamento(String idTransacao) {
        PagInseguro gateway = new PagInseguro();          // e de novo
        gateway.estornar(idTransacao);
    }
}
```

## 1. Por que isso fere o DIP

O DIP tem duas partes:

> **(a)** Módulos de alto nível não devem depender de módulos de baixo nível. Ambos devem
> depender de abstrações.
> **(b)** Abstrações não devem depender de detalhes. Detalhes devem depender de abstrações.

`ProcessadorDePagamento` é o **módulo de alto nível** — ele carrega a política de negócio
("como a empresa processa um pagamento"). `PagInseguro` é um **detalhe de infraestrutura** —
um fornecedor específico, que pode ser trocado a qualquer momento.

A violação acontece porque:

1. **A seta de dependência aponta para o lado errado.** A regra de negócio depende
   diretamente de uma classe concreta de infraestrutura. Deveria ser o contrário: a
   infraestrutura deveria se adaptar a uma abstração definida pelo domínio.

```
   Hoje:    ProcessadorDePagamento ──────▶ PagInseguro   (alto nível depende do baixo nível)

   Com DIP: ProcessadorDePagamento ──▶ GatewayPagamento ◀── PagInseguroAdapter
                                        (abstração)          (detalhe se adapta)
```

2. **O `new` acopla criação e uso.** A classe não apenas *usa* o gateway; ela também decide
   *qual* gateway existir e *como* construí-lo. São duas responsabilidades (isso também fere
   o SRP), e a escolha fica *hardcoded* dentro do método.

3. **Não há ponto de substituição.** Como o `new` está dentro do corpo dos métodos, não
   existe nenhuma costura por onde injetar outra implementação — nem em produção, nem em teste.

4. **Repetição do acoplamento em três lugares.** Cada método instancia o gateway por conta
   própria, então a dependência está espalhada, e não concentrada em um ponto.

5. **A abstração ausente vaza detalhes.** Sem uma interface de domínio, os nomes, os
   parâmetros e as exceções do `PagInseguro` (`pagar`, `consultar`, `estornar`, tipos de
   retorno, exceções específicas do SDK) contaminam a regra de negócio.

## 2. Consequências de manter a dependência direta

### Manutenção

- **Testes unitários inviáveis.** Não há como testar `processarPagamento` sem executar o
  `PagInseguro` de verdade — o que significa chamada de rede, credenciais, ambiente de
  sandbox e possivelmente cobrança real. Os testes ficam lentos, instáveis (*flaky*) e
  dependentes de um serviço externo estar no ar.
- **Alteração em efeito dominó.** Se o `PagInseguro` mudar a assinatura de `pagar()` ou o
  nome de um método na próxima versão do SDK, o código de negócio precisa ser editado.
- **Depuração mais difícil.** Um erro na regra de negócio se mistura com erros de rede, de
  credencial e de contrato do fornecedor.
- **Compilação acoplada.** O módulo de domínio passa a exigir a biblioteca do fornecedor no
  classpath para sequer compilar.

### Evolução

- **Trocar de fornecedor vira refatoração de risco.** Adotar outro gateway (por preço melhor,
  por um incidente de segurança, ou porque o fornecedor encerrou operações) exige abrir e
  reescrever a classe de negócio — que é exatamente o código que menos deveria mudar.
- **Impossível rodar múltiplos gateways.** Cenários comuns como "cartão vai pelo gateway A,
  PIX pelo gateway B", roteamento por país ou *fallback* automático quando o principal cai
  não têm onde se encaixar.
- **Impossível ligar/desligar por ambiente.** Não dá para usar um gateway *fake* em
  desenvolvimento e o real em produção sem `if (ambiente == ...)` espalhado pelo código.
- **Sem ponto de extensão para comportamentos transversais.** Não existe onde colocar
  *retry*, *circuit breaker*, log de auditoria ou métricas sem poluir a regra de negócio.
- **Violação em cascata do OCP.** Cada novo meio de pagamento força modificação de código
  existente, em vez de acréscimo de código novo.
- **Risco de negócio concentrado.** O nome do fornecedor fica gravado na regra de negócio; a
  empresa fica com *vendor lock-in* técnico, o que enfraquece até o poder de negociação
  comercial.

## 3. Formas de injetar a implementação concreta

O primeiro passo, comum a todas as formas, é **criar a abstração no lado do domínio**:

```java
// GatewayPagamento.java — interface definida pelo domínio, não pelo fornecedor
public interface GatewayPagamento {
    ResultadoPagamento processar(Pagamento pagamento);
    StatusPagamento    verificar(String idTransacao);
    void               cancelar(String idTransacao);
}

// PagInseguroAdapter.java — o detalhe se adapta à abstração
public class PagInseguroAdapter implements GatewayPagamento {

    private final PagInseguro api;

    public PagInseguroAdapter(PagInseguro api) {
        this.api = api;
    }

    @Override
    public ResultadoPagamento processar(Pagamento pagamento) {
        // traduz o vocabulário do fornecedor para o vocabulário do domínio
        return ResultadoPagamento.de(api.pagar(pagamento.getValor(), pagamento.getCartao()));
    }

    @Override
    public StatusPagamento verificar(String idTransacao) {
        return api.consultar(idTransacao) ? StatusPagamento.APROVADO : StatusPagamento.PENDENTE;
    }

    @Override
    public void cancelar(String idTransacao) {
        api.estornar(idTransacao);
    }
}
```

Com a abstração no lugar, existem quatro formas principais de fornecer a implementação:

---

### a) Injeção por construtor — **a preferida**

A dependência é obrigatória e recebida no momento da criação do objeto.

```java
public class ProcessadorDePagamento {

    private final GatewayPagamento gateway;

    public ProcessadorDePagamento(GatewayPagamento gateway) {
        this.gateway = gateway;
    }

    public ResultadoPagamento processarPagamento(Pagamento pagamento) {
        return gateway.processar(pagamento);
    }

    public StatusPagamento verificarPagamento(String idTransacao) {
        return gateway.verificar(idTransacao);
    }

    public void cancelarPagamento(String idTransacao) {
        gateway.cancelar(idTransacao);
    }
}

// Composição na borda da aplicação (Composition Root)
GatewayPagamento gateway = new PagInseguroAdapter(new PagInseguro(chaveApi));
ProcessadorDePagamento processador = new ProcessadorDePagamento(gateway);
```

**Vantagens**: o objeto nasce completo e válido; o campo pode ser `final` (imutável); a
dependência fica explícita na assinatura — é impossível esquecer de fornecê-la; testar é
trivial (`new ProcessadorDePagamento(new GatewayFake())`).

---

### b) Injeção por método *setter* (ou por propriedade)

```java
public class ProcessadorDePagamento {

    private GatewayPagamento gateway = new PagInseguroAdapter(new PagInseguro());  // padrão

    public void setGateway(GatewayPagamento gateway) {
        this.gateway = gateway;
    }
    // ...
}
```

**Quando usar**: dependência genuinamente **opcional** ou que precise ser trocada em tempo de
execução (ex.: alternar de gateway sem reiniciar a aplicação).
**Desvantagens**: o objeto pode existir em estado inconsistente (gateway nulo ou errado); a
dependência deixa de ser óbvia; perde-se a imutabilidade.

---

### c) Injeção por parâmetro de método

A dependência é passada só onde é usada.

```java
public ResultadoPagamento processarPagamento(Pagamento pagamento, GatewayPagamento gateway) {
    return gateway.processar(pagamento);
}
```

**Quando usar**: quando o gateway varia **a cada chamada** (roteamento por bandeira do cartão,
por país, por valor da transação).
**Desvantagens**: polui a assinatura de todos os métodos e empurra a decisão para o chamador,
repetidamente.

---

### d) Container de Injeção de Dependência (DI) / *framework*

O container monta o grafo de objetos automaticamente, a partir de configuração ou anotações.

```java
// Spring: a configuração declara qual implementação usar
@Configuration
public class PagamentoConfig {

    @Bean
    public GatewayPagamento gatewayPagamento(@Value("${pagamento.api-key}") String chave) {
        return new PagInseguroAdapter(new PagInseguro(chave));
    }
}

@Service
public class ProcessadorDePagamento {

    private final GatewayPagamento gateway;

    public ProcessadorDePagamento(GatewayPagamento gateway) {   // injetado pelo container
        this.gateway = gateway;
    }
    // ...
}
```

**Vantagens**: elimina o código repetitivo de montagem; permite escolher a implementação por
ambiente (`@Profile("dev")` com um gateway *fake*, `@Profile("prod")` com o real) e gerenciar
o ciclo de vida dos objetos.
**Desvantagens**: acrescenta uma dependência de framework, e erros de configuração aparecem
só na subida da aplicação.

---

### e) Fábrica / *Service Locator* (variação complementar)

Quando a escolha da implementação depende de dados só conhecidos em tempo de execução:

```java
public interface FabricaGateway {
    GatewayPagamento criar(MeioPagamento meio);
}

public class ProcessadorDePagamento {

    private final FabricaGateway fabrica;   // a fábrica é injetada por construtor

    public ProcessadorDePagamento(FabricaGateway fabrica) {
        this.fabrica = fabrica;
    }

    public ResultadoPagamento processarPagamento(Pagamento pagamento) {
        return fabrica.criar(pagamento.getMeio()).processar(pagamento);
    }
}
```

Repare que a classe continua sem nenhum `new` de gateway concreto: ela depende da abstração
`FabricaGateway`, que também é injetada.

---

## Resumo comparativo

| Forma | Melhor uso | Ponto de atenção |
|-------|-----------|------------------|
| Construtor | Dependência obrigatória (caso padrão) | Muitos parâmetros pode indicar excesso de responsabilidades |
| Setter | Dependência opcional ou trocável em runtime | Objeto pode ficar em estado inválido |
| Parâmetro de método | Dependência varia por chamada | Polui assinaturas |
| Container DI | Aplicações grandes, múltiplos ambientes | Acopla ao framework |
| Fábrica injetada | Escolha depende de dados de runtime | Uma indireção a mais |

## Ganho final

Com a inversão aplicada, `ProcessadorDePagamento` passa a depender apenas de
`GatewayPagamento` — uma abstração que **o próprio domínio define**. Trocar `PagInseguro` por
outro fornecedor vira escrever um novo adaptador e mudar **uma linha** na composição da
aplicação, sem tocar em uma vírgula da regra de negócio. E testar o processador vira passar
um dublê simples:

```java
class GatewayFake implements GatewayPagamento {
    boolean processado = false;
    public ResultadoPagamento processar(Pagamento p) { processado = true; return ResultadoPagamento.aprovado(); }
    public StatusPagamento verificar(String id)      { return StatusPagamento.APROVADO; }
    public void cancelar(String id)                  { }
}

@Test
void deveProcessarPagamento() {
    GatewayFake fake = new GatewayFake();
    new ProcessadorDePagamento(fake).processarPagamento(umPagamento());
    assertTrue(fake.processado);   // sem rede, sem credencial, sem cobrança real
}
```

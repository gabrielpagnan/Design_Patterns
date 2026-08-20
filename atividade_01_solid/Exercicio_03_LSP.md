# Exercício 3 — Liskov Substitution Principle (LSP)

## 1. Quando uma subclasse viola o LSP

O LSP diz que, se `S` é subtipo de `T`, então qualquer objeto de `T` deve poder ser
substituído por um objeto de `S` **sem que o programa que usa `T` perceba a diferença nem
passe a se comportar de forma errada**. Em outras palavras: herdar é assumir um contrato, e
o contrato inclui o *comportamento*, não só a *assinatura* dos métodos.

Uma subclasse viola o LSP quando ela quebra alguma das promessas feitas pela superclasse:

- **Enfraquece a pós-condição** — o método não entrega o efeito que a superclasse garantia.
  Ex.: `sacar(100)` na base garante "o saldo desta conta diminui em 100 e o dinheiro sai";
  a subclasse não faz isso.
- **Fortalece a pré-condição** — a subclasse exige mais do que a base para aceitar a chamada.
  Ex.: a base aceita qualquer valor positivo, a subclasse só aceita valores até R$ 500.
- **Quebra invariantes da superclasse** — regras que valiam para todo objeto do tipo base
  deixam de valer. Ex.: "saldo nunca aumenta em um saque".
- **Lança exceções que a base não previa** — como `UnsupportedOperationException` em um
  método que o contrato dizia ser sempre suportado.
- **Não faz nada (no-op) onde a base prometia um efeito** — o cliente acha que a operação
  ocorreu e segue adiante com uma premissa falsa.
- **Redireciona ou altera o efeito da operação** — executa uma ação *diferente* da
  contratada, ainda que "válida" do ponto de vista de negócio.

O detector prático: se em algum lugar do código eu preciso escrever
`if (conta instanceof ContaSalario) { ... }` para o sistema continuar correto, é sinal de que
a substituição **não** é transparente e o LSP já foi violado.

`ContaSalario` cai justamente nos dois últimos casos: ela mantém as assinaturas
`sacar(Double)` e `transferir(Double, Conta)`, mas o efeito é outro — o valor é desviado para
a conta do empregador em vez de ir para onde o chamador pediu.

## 2. Cenário de uso com comportamento inesperado

### Contexto

O banco tem uma rotina de **pagamento automático de boletos** que trabalha com `Conta`
genérica, porque a rotina deve funcionar para qualquer tipo de conta:

```java
public class ServicoPagamentoBoleto {

    public void pagar(Conta conta, Boleto boleto, Conta contaBeneficiario) {
        double saldoAntes = conta.getSaldo();

        conta.transferir(boleto.getValor(), contaBeneficiario);

        // O contrato de Conta.transferir garante:
        //   1) saiu o valor da conta de origem;
        //   2) o valor chegou na conta de destino.
        // A rotina confia nisso e dá o boleto por quitado.
        boleto.marcarComoPago();
        registrarComprovante(conta, boleto, saldoAntes);
    }
}
```

### O que acontece com `ContaSalario`

```java
Conta conta = repositorio.buscarPorId(idDoCliente);   // devolve uma ContaSalario
servicoPagamento.pagar(conta, boletoDeEnergia, contaDaConcessionaria);
```

Como `ContaSalario.transferir` está sobrescrita para redirecionar o valor à conta vinculada
ao empregador:

1. O dinheiro sai da conta salário, mas **vai para o empregador**, não para a concessionária.
2. A concessionária **não recebe nada**, mas o boleto é marcado como **pago** — o serviço não
   tinha como saber que a transferência foi desviada.
3. O comprovante é gerado com um destinatário que nunca recebeu o valor.
4. O cliente fica inadimplente com a concessionária **e** com menos saldo, e o sistema exibe
   a dívida como quitada.
5. Pior: a conciliação bancária vai fechar (o dinheiro saiu), então o erro passa despercebido
   até a concessionária cortar o serviço.

O mesmo vale para `sacar`: uma rotina de tarifa mensal que faz
`conta.sacar(taxaManutencao)` e depois credita a receita do banco vai debitar o cliente e
mandar a taxa para o empregador — dinheiro "sumindo" do ponto de vista contábil.

Outra variação do problema: uma rotina de saque em caixa eletrônico faria

```java
conta.sacar(valor);
dispenserDeCedulas.entregar(valor);   // entrega dinheiro físico que nunca foi debitado corretamente
```

Aqui o caixa entrega notas reais enquanto o valor foi enviado ao empregador — prejuízo direto
para o banco.

### Por que o cliente não tem como se defender

`ServicoPagamentoBoleto` foi escrito contra `Conta`. Ele não conhece `ContaSalario` e **não
deveria** conhecer. A única forma de se proteger seria adicionar verificações de tipo:

```java
if (conta instanceof ContaSalario) {
    throw new OperacaoNaoPermitidaException("Conta salário não paga boleto");
}
```

O que já é a confissão da violação: o polimorfismo deixou de funcionar, e cada novo subtipo
"especial" vai exigir mais um `if` espalhado por todos os serviços do sistema.

## 3. Por que o problema é de comportamento, e não da simples existência dos métodos

Do ponto de vista do **compilador**, `ContaSalario` está perfeita: ela é uma `Conta`, tem
`sacar(Double)` e `transferir(Double, Conta)` com as assinaturas corretas, compila e pode ser
passada para qualquer método que espere `Conta`. A **compatibilidade sintática** está
garantida.

O que quebrou foi a **compatibilidade semântica**. Herança não é só compartilhar assinaturas:
é assumir o *contrato* que a superclasse publicou. E o contrato de `Conta` inclui promessas
como:

- `sacar(valor)` → "o saldo desta conta diminui em `valor` e o dinheiro fica disponível para
  quem pediu o saque".
- `transferir(valor, destino)` → "o saldo desta conta diminui em `valor` **e o saldo de
  `destino` aumenta em `valor`**".

`ContaSalario` mantém apenas a primeira metade da promessa de `transferir` (o débito) e
substitui a segunda por outra coisa: credita **outra conta**, escolhida por ela, não pelo
chamador. O parâmetro `destino` é silenciosamente ignorado.

Por isso a herança de tipos não é suficiente — o LSP é uma regra sobre **subtipagem
comportamental**:

> Uma subclasse é um subtipo válido quando tudo que era verdade sobre a superclasse continua
> verdade sobre ela.

Como o compilador só verifica assinaturas, ele **não detecta** essa violação. O erro só
aparece em produção, na forma de dinheiro no lugar errado. É esse o perigo: a violação do LSP
transforma um erro de modelagem em um bug silencioso de runtime.

Vale notar que o problema **não é a conta salário existir** — a regra de negócio é legítima.
O problema é modelá-la como subclasse de `Conta` prometendo um comportamento que ela não
cumpre.

### Como corrigir a modelagem

Algumas alternativas, em ordem de preferência:

1. **Separar as capacidades em abstrações menores** (isso conversa diretamente com o ISP):

```java
public interface ContaDepositavel {
    void depositar(Double valor);
    Double getSaldo();
}

public interface ContaSacavel {
    void sacar(Double valor);
    void transferir(Double valor, ContaDepositavel destino);
}

public class ContaCorrente  implements ContaDepositavel, ContaSacavel { /* ... */ }
public class ContaPoupanca  implements ContaDepositavel, ContaSacavel { /* ... */ }
public class ContaSalario   implements ContaDepositavel { /* não promete sacar/transferir */ }
```

   Agora `ServicoPagamentoBoleto` declara que precisa de um `ContaSacavel`, e o compilador
   impede que uma `ContaSalario` chegue lá — o erro migra de runtime para tempo de compilação.

2. **Tornar o repasse ao empregador uma operação própria e explícita**, com nome que revele a
   intenção (`repassarAoEmpregador(Double valor)`), em vez de disfarçá-la de `transferir`.

3. **Composição em vez de herança**: `ContaSalario` tem uma `Conta` interna e expõe só as
   operações que realmente suporta.

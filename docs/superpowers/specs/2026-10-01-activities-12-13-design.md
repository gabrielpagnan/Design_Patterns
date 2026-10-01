# Atividades 12 e 13 — Design de Entrega

## Objetivo

Entregar duas atividades independentes de Padrões de Projeto: uma refatoração do anti-pattern de vendas para **Strategy** e outra do anti-pattern de playlist para **Iterator**. Cada atividade deve responder os exercícios conceituais e permitir compilar, executar e testar o exemplo refatorado localmente.

## Convenções do repositório

- Criar `atividade_12_strategy` e `atividade_13_iterator`, preservando o padrão das atividades 08–10: `src/<pacote>`, `test/<pacote>`, `RESPOSTAS.md` e `saida-execucao.txt`.
- Usar pacotes curtos, respectivamente `strategy` e `iterator`, e execução com `javac`/`java -ea`; não será adicionado um novo layout Maven às entregas, pois ele diverge da organização já adotada no repositório.
- Os projetos Maven originais fornecidos serão baixados e executados apenas como material de análise. A implementação final conservará as regras observáveis do exemplo e não copiará o anti-pattern como código de produção.
- Atualizar a tabela de atividades do `README.md` com as duas novas pastas.

## Fonte analisada

Os arquivos fornecidos foram baixados dos links do enunciado e inspecionados em uma área temporária. No projeto Strategy, `CalculadoraDesconto`, `CalculadoraFrete` e `RelatorioPedido` possuem cada um um `switch` sobre `TipoCliente`. No projeto Iterator, `Playlist.getFaixas()` retorna sua `ArrayList` interna e `Player`, `Recomendador` e `RelatorioPlaylist` percorrem essa lista por índice; `Player.tocarEmbaralhado` ainda aplica `Collections.shuffle` nela.

## Atividade 12 — Strategy

### Arquitetura

- `RegraCliente` será a interface Strategy com `getDescricao()`, `calcularDesconto(Pedido)` e `calcularFrete(Pedido)`.
- `RegraClienteComum`, `RegraClienteVip` e `RegraClienteCorporativo` serão as estratégias concretas que preservam os percentuais, os rótulos e as bases de frete do original. Uma base abstrata privada ao pacote poderá concentrar somente a parcela comum do cálculo de frete (peso e região).
- `RegraPromocional` será uma estratégia adicional que recebe uma regra padrão, aplica um percentual promocional de desconto e delega o frete. Ela demonstra a extensão e a substituição de comportamento em runtime.
- `Pedido` será o Context: manterá valor, peso, região e a referência mutável a `RegraCliente`; `definirRegraCliente(RegraCliente)` permitirá trocar a estratégia durante a execução.
- `RelatorioPedido` obterá a regra do `Pedido` usando o tipo `RegraCliente`; não conterá `switch`, `TipoCliente` nem referências a estratégias concretas. O relatório calcula os valores delegando à abstração.
- `Main` exibirá os três comportamentos originais e trocará temporariamente a regra de um pedido VIP por `RegraPromocional`, voltando em seguida para `RegraClienteVip`.

### Comportamentos que devem permanecer

Para valor 200, peso 2 e região sul, os pedidos comum e VIP devem ter respectivamente desconto/frete/total de `0/31/231` e `20/18/198`. Para o corporativo com região norte, os valores devem ser `40/36/196`. A promoção de 30% sobre o pedido VIP deve produzir desconto 60, frete 18 e total 158, e o retorno à estratégia VIP deve restaurar o total 198.

### Testes

Um teste executável com `assert` verificará as três regras padrão e a substituição promocional seguida de restauração. Ele exercerá as estratégias pelo Context e verificará que o relatório usa a descrição da estratégia, sem instanciar regras concretas internamente.

## Atividade 13 — Iterator

### Arquitetura

- `Iterador<T>` será a interface Iterator com `boolean temProxima()` e `T proxima()`.
- `IteradorPlaylist` será o iterador concreto, guardará a posição atual e avançará sobre a sequência recebida sem expor essa estrutura aos clientes.
- `Playlist` será o Aggregate. Sua lista permanecerá privada e deixará de expor `getFaixas()`; ela fornecerá `criarIterador()`, `criarIteradorEmbaralhado()` e `criarIteradorFavoritas()`.
- As variantes embaralhada e de favoritas operarão sobre listas temporárias privadas ao Aggregate. Portanto, o embaralhamento não altera a ordem de inserção da playlist e clientes não implementam seus próprios laços por índice.
- `Player`, `Recomendador` e `RelatorioPlaylist` usarão apenas `Iterador<Faixa>` para tocar, sugerir e totalizar. A regra de escolha da travessia pertence a `Playlist`.
- `Main` mostrará a ordem original antes e depois da reprodução embaralhada, evidenciando que ela não foi mutada.

### Testes

Um teste executável com `assert` verificará a ordem sequencial, o filtro de favoritas, a independência de dois iteradores e que o iterador embaralhado contém todas as faixas sem alterar a travessia original. O teste não dependerá de uma ordem aleatória específica.

## Documentação de entrega

Cada `RESPOSTAS.md` terá: respostas de aplicação em duas ou três frases; a análise pontual do código original; diagramas Mermaid de classes antes e depois; descrição dos papéis do padrão e das decisões de design; comandos de execução e teste. `saida-execucao.txt` guardará uma execução real da `Main` depois da refatoração.

## Limites e não objetivos

Não haverá alteração das atividades existentes, dependências externas, interface gráfica ou persistência. `TipoCliente` não será mantido como mecanismo de decisão no código Strategy, pois ele recriaria a enumeração fechada que a refatoração precisa eliminar.

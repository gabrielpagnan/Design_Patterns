# Atividade 07 — Adapter: especificação

## Objetivo

Resolver os cinco exercícios da Atividade 07 e implementar, em Java, o exemplo de previsão do tempo usando o padrão Adapter.

## Estrutura

A atividade ficará em `atividade_07_Adapter/`, seguindo o formato das atividades anteriores:

- `RESPOSTAS.md`: respostas dos exercícios 1 a 5;
- `src/clima/`: implementação Java;
- `saida-execucao.txt`: resultado produzido pelo `Main`.

O `README.md` da raiz receberá apenas uma linha na tabela de atividades.

## Implementação

A interface `PrevisaoService` terá o método `int obterTemperatura(String cidade)`. `OpenWeatherAdapter` encapsulará `OpenWeatherApi` e converterá seu `double` para `int`; `AccuWeatherAdapter` encapsulará `AccuWeatherApi` e converterá Fahrenheit para Celsius.

O `PainelClima` receberá uma `PrevisaoService` no construtor e usará somente essa interface. O `Main` criará um painel com cada adaptador para demonstrar que a classe cliente não muda quando o fornecedor é trocado.

## Estilo e escopo

As respostas terão linguagem direta e natural, adequada a uma atividade de graduação. O código será simples, sem bibliotecas externas, frameworks, abstrações adicionais ou recursos que não foram pedidos.

## Verificação

Um teste Java pequeno, executado com assertions, verificará as duas conversões e o comportamento do painel. Depois serão compilados todos os arquivos, o teste será executado e a saída do `Main` será salva em `saida-execucao.txt`.

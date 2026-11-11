# Lavador de Pratos

Simulação em Java do problema produtor-consumidor, feita para a disciplina de Programação Concorrente.

Lavadores (produtores) lavam pratos e os colocam em um escorredor com capacidade limitada. Enxugadores (consumidores) retiram os pratos do escorredor e os enxugam. Tudo roda em threads concorrentes.

## Classes

| Classe | Papel |
|---|---|
| `App` | Cria as threads, inicia a simulação, espera 60 segundos e encerra tudo. |
| `Lavador` | Produtor. Cria um prato, lava e coloca no escorredor. Espera se o escorredor estiver cheio. |
| `Enxugador` | Consumidor. Retira um prato do escorredor e enxuga. Espera se o escorredor estiver vazio. |
| `Escorredor` | Buffer compartilhado (fila circular) com a sincronização das threads. |
| `Prato` | Tem número de série e nível de sujeira sorteado. |
| `NivelSujeira` | `BAIXO`, `MEDIO` ou `ENGORDURADO`, cada um com um tempo de lavagem diferente. |

## Como funciona

- O `Escorredor` usa `synchronized`, `wait()` e `notify()`. Lavadores e enxugadores esperam em objetos de monitor separados, então o `notify()` sempre acorda uma thread do tipo certo.
- A notificação só é enviada quando o estado muda de forma relevante (estava vazio, ficou cheio, etc.).
- O tempo de trabalho é simulado com um laço de contagem (`Thread.onSpinWait()`).
- Nível de sujeira dos pratos: 30% `BAIXO`, 60% `MEDIO` e 10% `ENGORDURADO`. Quanto mais sujo, mais tempo leva para lavar.
- O enxugador leva um tempo aleatório entre 3 e 10 intervalos de contagem.
- No encerramento, os lavadores param primeiro. Depois os enxugadores esvaziam o escorredor e terminam.

## Como executar

A partir da pasta que contém o pacote `lavadordepratos` (a pasta `src`):

```bash
javac lavadordepratos/*.java
java lavadordepratos.App
```

Sem argumentos, a simulação usa 2 lavadores, 3 enxugadores e um escorredor de 5 pratos. Para mudar:

```bash
java lavadordepratos.App <lavadores> <enxugadores> <capacidade>
```

Exemplo: `java lavadordepratos.App 4 2 10`

## Log

Por padrão aparecem as mensagens de nível `INFO` (início, fim e escorredor cheio ou vazio). Para ver cada passo dos trabalhadores, troque `NIVEL_LOG` em `App.java` para `Level.FINE`.

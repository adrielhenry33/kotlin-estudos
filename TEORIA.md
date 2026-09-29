# Kotlin + Mobile — Teoria Completa

> Documento vivo. Atualizado automaticamente sempre que avançamos um tópico no `PROGRESSO.md`. Contém a teoria com exemplos de cada assunto já estudado — o `PROGRESSO.md` é a fonte da verdade do *estado* do aprendizado, este arquivo é a fonte da verdade do *conteúdo*.

Última atualização: 2026-09-29 (`StateInEx1` fechado; início de Compose: teoria do Nível 1)

---

## Roadmap em tempo real

- [x] **1. Tratamento de Erros** (Try/Catch, `Result<T>`) — CONCLUÍDO
- [x] **2. Collections Avançadas** (fold, reduce) — CONCLUÍDO
- [x] **3. Coroutines** (suspend, scopes, dispatchers, launch, async, await, timeout) — CONCLUÍDO
- [ ] **4. Delegação** (`by lazy`, `by delegate`) — EM PROGRESSO
  - [x] Nível 1: Fundações
  - [x] Nível 2: Prático Simples
  - [x] Nível 3.4: Cache + Lazy (`CacheDelegate` com expiração)
  - [ ] Nível 4: Aplicações Reais
- [ ] **5. Flow & StateFlow** — EM PROGRESSO
  - [x] Nível 1: Teoria (cold vs hot, `flow{}`, `emit`, `collect`, `map`/`filter`, `MutableStateFlow`/`StateFlow`)
  - [x] Nível 2: Prático Simples
  - [x] Nível 3+: Avançado (SharedFlow, combine, flatMapLatest, debounce, stateIn)
    - [x] Teoria de `SharedFlow` (replay, buffer, `extraBufferCapacity`, `onBufferOverflow`, `emit` vs `tryEmit`)
    - [x] Teoria de `combine`
    - [x] Exercícios `SharedFlowEx1` a `SharedFlowEx5` (`src/Flow/SharedFlow/`)
    - [x] `FlowEx5.kt` (`debounce` + `flatMapLatest`)
    - [x] `FlowEx6.kt` (`debounce` + `flatMapLatest`, Orchestror validação de e-mail)
    - [x] `FlowEx7.kt` (`combine`, carrinho de compras)
    - [x] Teoria de `stateIn`
    - [x] `StateInEx1.kt` (`src/Flow/StateIn/`) — Parte A e B3 (B4/B5 com o usuário)
    - [ ] `shareIn` / `callbackFlow` — adiado, aprender no caminho
  - [ ] Nível 4: Aplicações Reais
- [ ] **6. Jetpack Compose** — EM PROGRESSO
  - [ ] Nível 1: Fundações (declarativo, `@Composable`, composição/recomposição, `remember`/`mutableStateOf`, state hoisting) — teoria ✅ 2026-09-29, perguntas pendentes — **ATUAL**
  - Objetivo: 5 a 10 projetos básicos de treino, nível crescente, assim que o tópico começar. Lista curada em `PROGRESSO.md`, extraída de `solygambas/kotlin-projects`.
- [ ] **7. Clean Architecture** — não iniciado
- [ ] **8. Room Database** — não iniciado
- [ ] **9. CI/CD com Gradle para Android** — não iniciado (depois da leva de projetos básicos de Compose)
- [ ] **10. Projeto avançado: app de streaming** — não iniciado (depois do CI/CD)

---

## 1. Tratamento de Erros — CONCLUÍDO

### Try/Catch

Kotlin trata `try/catch` como **expressão**, não só como statement — ou seja, pode retornar valor:

```kotlin
val numero: Int = try {
    texto.toInt()
} catch (e: NumberFormatException) {
    -1
}
```

Isso é diferente de Java/TypeScript, onde `try/catch` nunca é uma expressão. Em TS você precisaria de uma variável mutável (`let`) declarada fora do bloco.

Kotlin **não tem checked exceptions** (diferente de Java) — o compilador nunca obriga você a capturar uma exceção. Isso empurra o idioma pra um padrão diferente de tratamento de erro: `Result<T>`.

### `Result<T>`

`Result<T>` é uma classe selada da stdlib que representa **sucesso ou falha** sem lançar exceção — o equivalente conceitual de um `Either<Error, T>` ou de um retorno `{ data, error }` que você já usaria em TypeScript.

```kotlin
fun dividir(a: Int, b: Int): Result<Int> {
    return if (b == 0) {
        Result.failure(ArithmeticException("Divisão por zero"))
    } else {
        Result.success(a / b)
    }
}

val resultado = dividir(10, 0)

resultado
    .onSuccess { valor -> println("Resultado: $valor") }
    .onFailure { erro -> println("Erro: ${erro.message}") }

// ou de forma funcional:
val valorOuPadrao = resultado.getOrElse { -1 }
```

`runCatching { }` é o helper mais comum pra transformar uma chamada que pode lançar exceção em um `Result`:

```kotlin
val resultado = runCatching { texto.toInt() }
```

**Quando usar cada um:** `try/catch` pra fluxo imperativo local e simples. `Result<T>` quando o erro é parte esperada do domínio (ex: validação, parsing) e você quer forçar quem chama a lidar com sucesso/falha explicitamente, sem exceção não capturada estourando a call stack.

---

## 2. Collections Avançadas — CONCLUÍDO

### `fold`

`fold` acumula um valor percorrendo a coleção, começando de um valor inicial explícito:

```kotlin
val precos = listOf(10.0, 20.0, 30.0)
val total = precos.fold(0.0) { acumulado, preco -> acumulado + preco }
// total = 60.0
```

Assinatura genérica: `fun <T, R> Iterable<T>.fold(initial: R, operation: (acc: R, T) -> R): R` — repare que o acumulador (`R`) pode ser de um **tipo diferente** do elemento (`T`). Isso é o que torna `fold` mais poderoso que `reduce`: dá pra transformar uma `List<Produto>` num `Map<String, Double>`, por exemplo, num único fold.

Ponte com TypeScript: `fold` é o `Array.prototype.reduce(fn, initialValue)` do JS — o nome `reduce` do JS na verdade corresponde ao `fold` do Kotlin quando você passa valor inicial.

### `reduce`

`reduce` é como `fold`, mas **usa o primeiro elemento da coleção como valor inicial** — por isso não aceita coleção vazia (lança exceção) e o acumulador é obrigatoriamente do mesmo tipo do elemento:

```kotlin
val numeros = listOf(5, 3, 8, 1)
val maior = numeros.reduce { acc, atual -> if (atual > acc) atual else acc }
// maior = 8
```

**Diferença prática:** use `reduce` quando o "zero" da operação já é o primeiro elemento (ex: achar o maior, concatenar strings). Use `fold` sempre que precisar de um valor inicial customizado ou mudar de tipo no acumulador — e use `fold` como padrão seguro quando a coleção pode estar vazia.

---

## 3. Coroutines — CONCLUÍDO

### `suspend` functions

Uma função `suspend` pode **pausar** sua execução sem bloquear a thread, e ser retomada depois. É o bloco de construção básico de coroutines — só pode ser chamada de dentro de outra `suspend fun` ou de um `CoroutineScope`.

```kotlin
suspend fun buscarUsuario(id: Int): Usuario {
    delay(500) // não bloqueia a thread, só suspende a coroutine
    return Usuario(id, "Nome")
}
```

Ponte com TypeScript: `suspend fun` é conceitualmente próximo de uma `async function`, mas com uma diferença chave — em Kotlin, `suspend` sozinho **não** dispara execução em paralelo/background. Ele só marca que a função pode suspender. Quem decide em que thread/contexto ela roda é o `CoroutineScope` + `Dispatcher` usados pra lançá-la.

### `CoroutineScope`

Define o **ciclo de vida** de um grupo de coroutines — quando o escopo é cancelado, todas as coroutines filhas lançadas nele são canceladas junto. Isso resolve o problema clássico de "esqueci de cancelar essa promise/callback quando a tela fechou".

```kotlin
val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

scope.launch {
    buscarUsuario(1)
}

// ao destruir a tela/ViewModel:
scope.cancel()
```

Em produção Android, você quase nunca cria um `CoroutineScope` manualmente — usa `viewModelScope` (cancelado automaticamente quando o ViewModel morre) ou `lifecycleScope` (ligado ao ciclo de vida da Activity/Fragment).

### Dispatchers

Definem em qual conjunto de threads a coroutine roda:

- `Dispatchers.Main` — thread de UI (Android).
- `Dispatchers.IO` — otimizado pra operações bloqueantes (rede, disco, banco).
- `Dispatchers.Default` — otimizado pra trabalho pesado de CPU (parsing, cálculo).

```kotlin
suspend fun carregarDados(): List<Produto> = withContext(Dispatchers.IO) {
    api.buscarProdutos() // chamada de rede, sai da Main thread
}
```

### `launch` vs `async`/`await`

- **`launch`**: dispara uma coroutine e devolve um `Job`. Não retorna valor. Usado pra "fire and forget" (ex: uma ação que atualiza estado, mas você não precisa do resultado no ponto de chamada).
- **`async`**: dispara uma coroutine e devolve um `Deferred<T>` — que é como uma `Promise<T>` do TypeScript. Você chama `.await()` pra suspender até o resultado ficar pronto.

```kotlin
suspend fun buscarTudo() = coroutineScope {
    val usuarioDeferred = async { buscarUsuario(1) }
    val pedidosDeferred = async { buscarPedidos(1) }

    // as duas chamadas rodam EM PARALELO, não sequencialmente
    val usuario = usuarioDeferred.await()
    val pedidos = pedidosDeferred.await()
}
```

Ponte com TypeScript: `async {}.await()` é exatamente o `Promise.all` quando usado em paralelo como no exemplo acima — a diferença é que em Kotlin você controla explicitamente quando cada `async` é disparado e quando é aguardado.

### Timeout

`withTimeout` cancela a coroutine automaticamente se ela não terminar dentro do prazo, lançando `TimeoutCancellationException`:

```kotlin
try {
    val resultado = withTimeout(3000) {
        buscarUsuario(1)
    }
} catch (e: TimeoutCancellationException) {
    println("Demorou demais")
}
```

`withTimeoutOrNull` faz o mesmo, mas devolve `null` em vez de lançar exceção — geralmente preferível quando o timeout é um caso esperado do fluxo, não um erro excepcional.

---

## 4. Delegação — EM PROGRESSO

### `by lazy`

`lazy` cria uma propriedade cujo valor só é calculado **na primeira vez que é acessado**, e depois fica em cache pro resto da vida do objeto:

```kotlin
class ConfiguracaoApp {
    val configuracaoPesada: Configuracao by lazy {
        println("Calculando configuração...")
        carregarConfiguracaoDoDisco()
    }
}
```

`println` só roda na primeira leitura de `configuracaoPesada` — leituras seguintes retornam o valor já calculado sem reexecutar o bloco. Por padrão, `lazy` é thread-safe (usa `LazyThreadSafetyMode.SYNCHRONIZED`).

### `by` (delegação de propriedade genérica)

Delegação de propriedade é um contrato: qualquer objeto que implemente `getValue`/`setValue` (via operator functions) pode "assumir" o comportamento de leitura/escrita de uma propriedade:

```kotlin
class Preferencia<T>(private var valor: T) {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        println("Lendo ${property.name}")
        return valor
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, novoValor: T) {
        println("Alterando ${property.name} de $valor pra $novoValor")
        valor = novoValor
    }
}

class Usuario {
    var nome: String by Preferencia("sem nome")
}
```

Isso é o mesmo mecanismo por trás de `by lazy`, `by Delegates.observable`, e de delegates customizados como cache. É comparável a um **getter/setter customizado reutilizável** — em TypeScript, o equivalente mais próximo seria um decorator de propriedade (`@observable`, por exemplo, do MobX), mas em Kotlin é um recurso nativo da linguagem, não uma convenção de biblioteca.

### Cache com expiração (`CacheDelegate`)

Padrão implementado no `Exercicio4.kt`: um delegate que guarda um valor calculado, mas o invalida depois de um tempo (TTL), forçando recálculo na próxima leitura. É a combinação de `by lazy` (evitar recálculo desnecessário) com um relógio (saber quando o cache expirou).

**Decisão de design em aberto** (registrada em 2026-09-14, não fechada como certo/errado): se `setValue` deve ser *write-through* (aceitar um valor direto, sobrescrevendo o cache) ou *invalidate-only* (só forçar releitura via um método `carregar()`, nunca aceitar valor externo direto).

**Nível 4 (aplicação real) ainda pendente** — aplicar em GodiTrack (cache de rotas) e Orchestror (validação de contato), seguindo MVVM + Clean Architecture.

---

## 5. Flow & StateFlow — EM PROGRESSO

### Cold vs Hot

- **Cold Flow** (`flow { }`): o código dentro do bloco só roda quando alguém chama `.collect()`, e roda **do zero pra cada coletor**. Se dois coletores diferentes coletarem o mesmo cold flow, cada um dispara sua própria execução independente.
- **Hot Flow** (`StateFlow`, `SharedFlow`): existe e "roda" independente de ter coletor ou não. Todos os coletores compartilham a **mesma** fonte de emissões — é broadcast, não replay individual.

Ponte com TypeScript/RxJS: cold flow é como um `Observable` "unicast" do RxJS (cada subscribe dispara uma nova execução); hot flow é como um `Subject`.

**O que "broadcast" quer dizer, na prática:** numa cold flow, cada `.collect()` reexecuta o bloco `flow { }` do zero — dois coletores geram duas execuções separadas, sem relação entre si:

```kotlin
val numeros = flow {
    println("Começou a produzir")
    emit(1)
}

launch { numeros.collect { println("A recebeu: $it") } } // imprime "Começou a produzir"
launch { numeros.collect { println("B recebeu: $it") } } // imprime "Começou a produzir" DE NOVO
```

Numa hot flow, existe **uma única emissão** compartilhada — os coletores só escutam, nenhum deles causa a emissão acontecer:

```kotlin
val eventos = MutableSharedFlow<Int>()

launch { eventos.collect { println("A recebeu: $it") } }
launch { eventos.collect { println("B recebeu: $it") } }

delay(100)
eventos.emit(1) // as DUAS coroutines acima recebem esse mesmo 1, ao mesmo tempo
```

`StateFlow` sempre carrega um valor atual justamente por ser broadcast: como pode existir um coletor que "sintoniza" depois que a transmissão já começou, precisa haver algo guardado (um replay implícito de 1 valor) pra entregar a ele. `SharedFlow` com `replay = 0` é broadcast sem gravação — quem não estava ouvindo no momento da emissão, perdeu.

### `flow { }`, `emit`, `collect`

```kotlin
fun contarAte(n: Int): Flow<Int> = flow {
    for (i in 1..n) {
        delay(100)
        emit(i)
    }
}

fun main() = runBlocking {
    contarAte(3).collect { valor -> println(valor) }
}
```

`emit` é `suspend` — só pode ser chamado de dentro do builder `flow { }` (ou de outro contexto suspenso). `collect` é o ponto onde a "produção" (emissão) e o "consumo" se conectam.

### `map` / `filter`

Operadores intermediários — transformam o flow sem coletar, e são preguiçosos (só executam quando alguém coleta o flow resultante):

```kotlin
contarAte(10)
    .filter { it % 2 == 0 }
    .map { it * 10 }
    .collect { println(it) }
```

### `MutableStateFlow` / `StateFlow`

`StateFlow` é um hot flow que **sempre tem um valor atual** — não dá pra criar um sem valor inicial, e todo novo coletor recebe imediatamente o valor vigente (é basicamente um "observable de estado", equivalente a um `BehaviorSubject` do RxJS).

```kotlin
class ContadorViewModel {
    private val _contador = MutableStateFlow(0)
    val contador: StateFlow<Int> = _contador.asStateFlow()

    fun incrementar() {
        _contador.value++
    }
}
```

Padrão de mercado: expor a versão mutável como `private`, e a pública como `StateFlow` somente-leitura (via `.asStateFlow()`) — quem está fora do ViewModel nunca deveria conseguir escrever o estado diretamente.

### `SharedFlow`

`SharedFlow` é um hot flow **sem conceito de "valor atual"** — ele é pra eventos, não pra estado. A diferença central pra `StateFlow`:

| | `StateFlow` | `SharedFlow` |
|---|---|---|
| Sempre tem valor atual? | Sim | Não (pode ter 0) |
| Coletor tardio recebe algo? | Sim, o valor vigente | Só se `replay > 0` |
| Uso típico | Estado da UI | Eventos pontuais (navegação, snackbar, cancelamento) |

```kotlin
class EventosViewModel {
    private val _eventos = MutableSharedFlow<String>(replay = 0)
    val eventos: SharedFlow<String> = _eventos.asSharedFlow()

    suspend fun disparar(evento: String) {
        _eventos.emit(evento)
    }
}
```

**`replay`**: quantos dos últimos valores emitidos ficam guardados pra entregar a um coletor que chega depois. `replay = 0` (padrão) significa que só quem estava coletando no momento da emissão recebe o valor.

**`extraBufferCapacity`**: espaço extra de buffer, além do `replay`, pra emissões que **já têm coletor(es) ativo(s)** mas que ainda não deram conta de processar o valor anterior. Ele existe só pra evitar que o `emit()` **suspenda** o produtor esperando um coletor lento — não tem nada a ver com histórico pra coletores futuros.

**Ponto de confusão comum (vale grifar): `replay` e `extraBufferCapacity` resolvem problemas diferentes, mesmo compartilhando o mesmo buffer interno.** Um coletor novo sempre começa a ler o buffer exatamente na posição `(total emitido) - replay` — nunca "mais pra trás" que isso, não importa o tamanho do `extraBufferCapacity`. Ou seja: **`extraBufferCapacity` NÃO estende quanto passado um coletor tardio consegue ver.** Só `replay` faz isso.

Prova prática (rodada nesta sessão): com `replay = 0` e `extraBufferCapacity = 10`, emitindo 3 valores e só depois iniciando um coletor — o coletor **não recebe nenhum dos três**, exatamente como se `extraBufferCapacity` fosse 0:

```kotlin
val flow = MutableSharedFlow<String>(replay = 0, extraBufferCapacity = 10)

flow.emit("aguardando")
flow.emit("motorista a caminho")
flow.emit("em andamento")

delay(50)

launch { flow.collect { println("Tela nova recebeu: $it") } }
delay(100)
// nada é impresso — extraBufferCapacity não ajuda coletor tardio, só replay ajudaria
```

**`onBufferOverflow`**: o que fazer quando o buffer (replay + extra) está cheio e chega uma nova emissão vinda de um produtor mais rápido que os coletores ativos:
- `BufferOverflow.SUSPEND` (padrão): o emissor suspende até haver espaço.
- `BufferOverflow.DROP_OLDEST`: descarta o valor mais antigo do buffer pra abrir espaço pro novo.
- `BufferOverflow.DROP_LATEST`: descarta o valor novo que está tentando entrar, mantendo o buffer como está.

### Exemplo completo comparando as 3 estratégias (`SharedFlowEx4`, 2026-09-22)

Cenário: um sensor de temperatura emite uma leitura a cada 10ms (`emitirLeitura`, via `tryEmit`); o painel que exibe (`collect`) é lento, gasta 100ms processando cada leitura. Buffer: `replay = 0`, `extraBufferCapacity = 2` (capacidade total: 2). Emite-se `1..6` seguidos.

**Regra de raciocínio pra rastrear qualquer um desses testes:** um valor só ocupa espaço no buffer se, no momento em que chega, o coletor **já estiver ocupado** processando outra coisa. Se o coletor estiver livre (parado esperando), a entrega é direta, sem passar pelo buffer.

**Trilha comum aos 3 casos**, antes de divergirem:
```
1 → emitido, mas ainda não existe inscrito nenhum (launch só agendou, não rodou) → PERDIDO
2 → coletor já inscrito e LIVRE (parado esperando) → entregue DIRETO, sem passar pelo buffer
    (a partir daqui o coletor fica ocupado, processando "2" por 100ms)
3 → coletor ocupado → vai pro buffer → buffer = {3}         (1/2)
4 → coletor ainda ocupado → vai pro buffer → buffer = {3,4} (2/2, CHEIO)
```
A partir daqui (`5` e `6` chegando com o buffer já cheio) é que cada estratégia se comporta diferente:

**`SUSPEND`** — `tryEmit` não pode esperar, então recusa na hora quando não cabe:
```
5 → buffer cheio {3,4} → tryEmit RECUSA → "Buffer cheio 5 descartado" (false)
6 → buffer ainda cheio → tryEmit RECUSA → "Buffer cheio 6 descartado" (false)
(coletor termina de processar "2", pega o buffer na ordem: "3", depois "4")
Painel recebeu: 2, 3, 4
```

**`DROP_OLDEST`** — sempre aceita o novo, expulsando o mais antigo do buffer pra abrir vaga:
```
5 → buffer cheio {3,4} → expulsa o mais antigo ("3") → buffer = {4,5} → tryEmit = true
6 → buffer cheio {4,5} → expulsa o mais antigo ("4") → buffer = {5,6} → tryEmit = true
(coletor termina de processar "2", pega o que sobrou no buffer: "5", depois "6")
Painel recebeu: 2, 5, 6
```
Ninguém que já está no buffer é "seguro" — a cada nova chegada, o mais velho de plantão é o próximo a cair. Favorece o dado **mais recente**.

**`DROP_LATEST`** — sempre aceita "com sucesso", mas descarta o próprio valor novo se não couber:
```
5 → buffer cheio {3,4} → descarta o PRÓPRIO "5" → buffer continua {3,4} → tryEmit = true (!)
6 → buffer cheio {3,4} → descarta o PRÓPRIO "6" → buffer continua {3,4} → tryEmit = true (!)
(coletor termina de processar "2", pega "3", depois "4")
Painel recebeu: 2, 3, 4
```
Quem já está no buffer é intocável pra sempre; só quem tenta entrar depois do buffer cheio corre risco. Favorece o dado **mais antigo já em fila**.

**Comparação final:**

| Estratégia | Painel recebeu | O que se perde | `tryEmit` avisa a perda? |
|---|---|---|---|
| `SUSPEND` | 2, 3, 4 | 5, 6 (recusados na hora) | Sim — devolve `false` |
| `DROP_OLDEST` | 2, 5, 6 | 3, 4 (expulsos do buffer pelos mais novos) | Não — sempre `true` |
| `DROP_LATEST` | 2, 3, 4 | 5, 6 (descartados em silêncio) | Não — sempre `true` |

**Pegadinha real, provada nesse exercício: com `DROP_OLDEST` e `DROP_LATEST`, `tryEmit` sempre retorna `true`, mesmo quando o valor foi descartado.** Só `SUSPEND` faz `tryEmit` devolver `false` de verdade quando algo se perde. Isso significa que, com `DROP_OLDEST`/`DROP_LATEST`, **o retorno booleano de `tryEmit` não serve pra saber se o SEU valor específico foi entregue** — do ponto de vista da API, a operação "não falhou" (o buffer sempre dá um jeito de acomodar a emissão, seja expulsando o mais antigo, seja ignorando o novo), só o dado em si que pode não ter sido guardado.

**Conexão com GodiTrack:** `DROP_OLDEST` é a escolha natural pra localização GPS em tempo real — se o app não consegue processar tudo a tempo, você quer a posição **mais recente** do motorista, não uma leitura antiga que já está obsoleta. Já um log de transações (onde perder qualquer evento seria inaceitável) pediria `SUSPEND`, aceitando que o produtor fique mais lento em vez de perder dado.

**`emit()` vs `tryEmit()`**: `emit()` é `suspend` — se o buffer estiver cheio e a estratégia for `SUSPEND`, ela espera até haver espaço. `tryEmit()` **não é suspend**: tenta emitir imediatamente e devolve `Boolean` dizendo se conseguiu (`true`) ou se foi descartado por falta de espaço no buffer (`false`) — essencial quando você precisa emitir de um contexto que não pode ser `suspend` (ex: um callback de hardware, um listener de UI, um `Thread` comum).

```kotlin
class RastreadorGps {
    private val _localizacoes = MutableSharedFlow<Localizacao>(
        replay = 0,
        extraBufferCapacity = 2 // aguenta 2 emissões "adiantadas" sem suspender
    )
    val localizacoes: SharedFlow<Localizacao> = _localizacoes.asSharedFlow()

    // callback do hardware — NÃO é suspend, então emit() nem compilaria aqui
    fun aoReceberDoHardware(localizacao: Localizacao) {
        val conseguiu = _localizacoes.tryEmit(localizacao)
        if (conseguiu) {
            println("Emitido: $localizacao")
        } else {
            println("Descartado (buffer cheio): $localizacao")
        }
    }
}
```

**Correção importante, descoberta e provada nesta sessão (2026-09-21): isso só acontece se já existir um coletor ativo.** Sem nenhum coletor coletando, `tryEmit`/`emit` **sempre têm sucesso**, não importa o `extraBufferCapacity` — não existe "buffer cheio" quando não tem ninguém esperando pra consumir o valor (não faz sentido recusar algo que ninguém vai ver mesmo).

Prova prática (buffer de tamanho 1, zero coletores, 5 tentativas de emissão):

```kotlin
val flow = MutableSharedFlow<Int>(replay = 0, extraBufferCapacity = 1)

repeat(5) { i ->
    println("tryEmit($i) sem coletor = ${flow.tryEmit(i)}")
}
// tryEmit(0) sem coletor = true
// tryEmit(1) sem coletor = true
// tryEmit(2) sem coletor = true
// tryEmit(3) sem coletor = true
// tryEmit(4) sem coletor = true   <- todas true, buffer de 1 "nunca enche"
```

**O cenário que realmente demonstra buffer cheio:** um coletor **ativo e lento** (que ainda não processou o valor anterior) recebendo emissões mais rápido do que consegue consumir. Só nesse caso o buffer (replay + extra) enche de verdade e `tryEmit` começa a devolver `false`:

```kotlin
launch {
    rastreador.localizacoes.collect {
        delay(200) // coletor lento — não dá conta do ritmo do produtor
        println("Processado: $it")
    }
}

delay(50) // garante que o coletor já está rodando
repeat(5) { i ->
    rastreador.aoReceberDoHardware(Localizacao(i.toDouble(), i.toDouble()))
    // a partir da 3ª chamada (replay=0 + extraBufferCapacity=2 = buffer de 2),
    // tryEmit começa a devolver false, porque o coletor lento ainda não abriu espaço
}
```

É esse o cenário que o exercício `SharedFlowEx3` pede pra observar.

**Caso de uso GodiTrack:** `StateFlow` pro **status da corrida** (sempre existe um status atual — "aguardando", "em andamento"), `SharedFlow` (`replay = 0`) pro **evento de corrida cancelada** — uma tela que abre depois do cancelamento não deveria "descobrir" um cancelamento que já passou, mas deveria ver o status atual imediatamente.

**Exemplo completo, verificado em exercício (`SharedFlowEx5`, 2026-09-22):**

```kotlin
class CorridaViewModel(private val scope: CoroutineScope) {
    private val _status = MutableStateFlow("aguardando")
    val status: StateFlow<String> = _status.asStateFlow()

    private val _evento = MutableSharedFlow<String>(replay = 0)
    val evento: SharedFlow<String> = _evento.asSharedFlow()

    fun atualizarStatus(novoStatus: String) { _status.value = novoStatus }

    fun cancelarCorrida() {
        scope.launch { _evento.emit("Corrida cancelada") }
        // sem job.cancel() aqui — emit() termina sozinho, não é um collect infinito
    }
}
```

Resultado observado com 3 coletores em momentos diferentes (status tardio, evento antes do cancelamento, evento depois do cancelamento):

```
Status recebido: em andamento                                  ← tardio, mas StateFlow entrega na hora
Cancelamento recebido (coletor de ANTES): Corrida cancelada     ← já estava ouvindo, recebe
                                                                 ← coletor de DEPOIS: nenhuma linha — SharedFlow não guarda nada pra quem chega atrasado
```

**Erro recorrente encontrado ao implementar isso:** cair de novo no padrão "`cancel()` logo após `launch`, sem nenhuma pausa no meio" — o mesmo bug do `SharedFlowEx4`, só que reaparecendo em 3 lugares diferentes do mesmo arquivo (dentro de `cancelarCorrida()`, e nos dois `launch` da `main`). Reforça a regra: **todo `launch` de um coletor precisa de um `delay` antes de qualquer `cancel()` ou emissão que dependa dele já estar rodando.**

### `combine`

Combina os **valores mais recentes** de dois ou mais flows, recalculando toda vez que **qualquer um** deles emite:

```kotlin
suspend fun <T1, T2, R> combine(
    flow: Flow<T1>,
    flow2: Flow<T2>,
    transform: suspend (T1, T2) -> R
): Flow<R>
```

```kotlin
val email = MutableStateFlow("")
val telefone = MutableStateFlow("")

val formValido: Flow<Boolean> = combine(email, telefone) { email, tel ->
    validarEmail(email) && validarTelefone(tel)
}
```

`combine` só emite depois que **todos** os flows envolvidos já emitiram pelo menos um valor.

**Diferença de `zip`**: `zip` pareia por posição (1º com 1º, 2º com 2º) e espera ambos os lados; `combine` reage a qualquer emissão usando o último valor conhecido do outro lado, sem esperar pareamento.

Timeline:

```
FlowA:    --1--------2------------3-->
FlowB:    ------A---------B---------->
combine:  ------(1,A)-----(2,A)-(2,B)---(3,B)-->
```

Ponte com TypeScript/RxJS: `combine` é o `combineLatest` do RxJS.

**Caso de uso Orchestror:** validação de formulário com campos independentes — habilitar o botão "salvar" só quando email E telefone forem válidos, reagindo a mudança em qualquer um dos dois campos.

**Revisão (2026-09-28) — detalhes que importam na prática:**

- **Forma de extensão:** `flowA.combine(flowB) { a, b -> ... }` é equivalente a `combine(flowA, flowB) { a, b -> ... }`.
- **3 a 5 flows:** existem sobrecargas tipadas até 5 (`combine(f1, f2, f3) { a, b, c -> ... }`). Acima disso, a versão com lista/vararg entrega um `Array<T>`, e você perde a tipagem individual.
- **Com `StateFlow`, emite na hora:** `StateFlow` sempre tem valor, então `combine` de `StateFlow`s já emite a primeira combinação assim que é coletado. Com `flow { }` frio, só emite depois que todos emitiram pelo menos uma vez.
- **Retorna `Flow`, não `StateFlow`:** o resultado de `combine` é um `Flow` frio comum. Pra expor como estado, ou você coleta e joga num `MutableStateFlow` (o que estamos fazendo até agora), ou usa `stateIn(...)`, que é o padrão de mercado e o próximo assunto.
- **Não completa sozinho se as fontes forem hot:** `combine` só termina quando **todas** as fontes terminam. `StateFlow` nunca termina.
- **Conflation:** se duas fontes `StateFlow` mudam ao mesmo tempo, sem nenhuma suspensão no meio, o coletor pode ver só a combinação final e pular as intermediárias. É o mesmo comportamento de conflation do `StateFlow` que já vimos.

**`combine` vs `zip` vs `merge`:**

| Operador | Emite quando | Usa | Caso típico |
|---|---|---|---|
| `combine` | qualquer fonte emite | último valor de cada fonte | estado derivado (filtros + lista, formulário, carrinho) |
| `zip` | as duas fontes emitem o "par" seguinte | valores pareados por posição | juntar requisição N com resposta N |
| `merge` | qualquer fonte emite | só o valor que chegou (mesmo tipo) | juntar eventos de várias origens num stream só |

**Exemplo fora do escopo dos projetos — catálogo de filmes com filtros:**

```kotlin
val filmes = MutableStateFlow(listOf<Filme>())
val genero = MutableStateFlow<Genero?>(null)
val soNaoAssistidos = MutableStateFlow(false)

val filmesVisiveis: Flow<List<Filme>> =
    combine(filmes, genero, soNaoAssistidos) { lista, g, naoAssistidos ->
        lista
            .filter { g == null || it.genero == g }
            .filter { !naoAssistidos || !it.assistido }
    }
```

Mudar o gênero, marcar o toggle ou chegar um filme novo recalcula a lista visível, sempre com o último valor de cada filtro.

**Solução de referência (`FlowEx7.kt`, carrinho, 2026-09-28):**

```kotlin
init {
    scope.launch {
        combine(_itens, _cupom, _tipoEntrega) { itens, cupom, entrega ->
            calcularResumo(itens, cupom, entrega)     // última expressão = valor emitido
        }.collect { resumo ->
            _resumo.value = resumo                     // único lugar que escreve o estado
        }
    }
}

private fun calcularResumo(itens: List<ItemCarrinho>, cupom: String, entrega: TipoEntrega): ResumoCarrinho {
    val subtotal = itens.sumOf { it.precoUnitario * it.quantidade }
    val desconto = if (cupom == "DESCONTO10") subtotal * 0.10 else 0.0
    val frete = if (entrega == TipoEntrega.PADRAO && subtotal >= 200.0) 0.0 else entrega.valorFrete
    return ResumoCarrinho(subtotal, desconto, frete, subtotal - desconto + frete)
}
```

**Erros comuns ao escrever a lambda do `combine`:**

- **`{ a, b -> { ... } }`**: é o hábito do arrow function do JS/TS. Em Kotlin, as chaves de dentro criam **outra lambda**, então o `combine` emite uma função, não o seu resultado. O corpo já começa logo depois do `->`.
- **Escrever estado dentro do bloco** (`_resumo.value.subtotal = ...`): não compila, porque as propriedades de data class com `val` não podem ser reatribuídas. E mesmo que compilasse, estaria errado: o bloco do `combine` deve ser uma **transformação pura**, que recebe valores e devolve um objeto novo. Quem escreve o estado é o `collect`.
- **`map` usado como loop**: `map` serve pra transformar uma lista em outra lista. Pra somar, use `sumOf { ... }`, e pra só iterar, `forEach`.
- **Isolar a regra de negócio numa função pura** (`calcularResumo`) deixa o `combine` com uma linha só e a regra testável sem coroutine nenhuma.
- **Encerrar coletores de `StateFlow` na `main`**: `coroutineContext.cancelChildren()` cancela todos os filhos do `runBlocking` de uma vez (o collect do ViewModel e o da main). No Android, quem faz isso é o `viewModelScope`, que é cancelado no `onCleared()`.

### `debounce` e `flatMapLatest`

- **`debounce(tempoMs)`**: só deixa passar um valor se nenhum outro valor chegar dentro da janela de tempo especificada — usado pra evitar disparar uma busca a cada tecla digitada. Recebe `Duration` (ex: `300.milliseconds`).
- **`flatMapLatest`**: pra cada novo valor emitido, dispara um novo flow interno (ex: uma chamada de rede) e **cancela** o flow interno anterior se ele ainda não tiver terminado — evita que uma busca antiga "atropele" o resultado de uma busca mais recente.

**Opt-in necessário:** os dois ainda pedem anotação explícita — `debounce` exige `@OptIn(FlowPreview::class)`, `flatMapLatest` exige `@OptIn(ExperimentalCoroutinesApi::class)` (dá pra combinar os dois na mesma anotação: `@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)`). Isso é o mecanismo de opt-in do Kotlin: a biblioteca avisa que a API ainda pode mudar de forma incompatível numa versão futura, e você precisa reconhecer esse risco explicitamente pra usar.

**Pipeline completo verificado (`FlowEx5.kt`, 2026-09-22):**

```kotlin
termos
    .map { termo -> termo.trim().lowercase() }           // normaliza antes de tudo
    .debounce(300.milliseconds)                          // só passa após 300ms de silêncio
    .flatMapLatest { termo -> buscarNoServidor(termo) }   // busca, cancela a anterior se preciso
    .collect { resultado -> _busca.value = resultado }    // atualiza o estado exposto
```

Resultado observado com a sequência `"n"` → 50ms → `"no"` → 50ms → `"note"` → pausa de 600ms → `"nota"` → 600ms:

```
Resultado : []                                              ← valor inicial do StateFlow
Resultado : [Notebook, Notebook Gamer]                       ← só depois de "note" (300ms sem novo termo)
Resultado : [Nota Fiscal Impressora]                          ← só depois de "nota"
```

`"n"` e `"no"` nunca chegam a virar busca — são engolidos pelo `debounce` porque o próximo termo chega antes dos 300ms passarem.

Exercício de reforço (`FlowEx6.kt`, concluído em 2026-09-28): mesmo padrão, tema Orchestror — validação de e-mail em tempo real durante cadastro, verificando no "servidor" (simulado) se o e-mail já está cadastrado. Feito sem ajuda. Saída observada:

```
Resultado Flow.StatusEmail$Digitando@52d455b8                    ← valor inicial
Resultado JaCadastrado(email=adriel@goditransportes.com.br)      ← ~800ms (100 + 300 debounce + 400 servidor)
Resultado Disponivel(email=novo@empresa.com)                      ← ~1400ms (700 + 300 + 400)
```

**`object` vs `data object` em sealed class:** repare no `Digitando@52d455b8` — um `object` comum usa o `toString()` padrão do Java (nome da classe + hash). Desde o Kotlin 1.9, o idiomático é `data object Digitando : StatusEmail()`, que gera `toString()` = `"Digitando"` (além de `equals`/`hashCode` consistentes). Padrão de mercado pra estados sem dados dentro de `sealed class`/`sealed interface` de UI state.


### `stateIn` (teoria dada em 2026-09-28)

**Definição:** operador que transforma um `Flow` **frio** num `StateFlow` **quente**, compartilhado entre todos os coletores, com um valor atual sempre disponível. É o jeito de mercado de expor estado **derivado** num ViewModel (resultado de `combine`, `map`, consulta ao banco etc.).

```kotlin
fun <T> Flow<T>.stateIn(
    scope: CoroutineScope,      // onde a coleta do upstream vai rodar (no Android: viewModelScope)
    started: SharingStarted,    // QUANDO começar e parar de coletar o upstream
    initialValue: T             // valor do StateFlow antes do upstream emitir
): StateFlow<T>
```

Não precisa de `@OptIn`: é API estável.

**Antes e depois (carrinho do `FlowEx7`):**

```kotlin
// Antes: 4 peças manuais
private val _resumo = MutableStateFlow(ResumoCarrinho())
val resumo: StateFlow<ResumoCarrinho> = _resumo.asStateFlow()
init {
    scope.launch {
        combine(_itens, _cupom, _tipoEntrega) { i, c, e -> calcularResumo(i, c, e) }
            .collect { _resumo.value = it }
    }
}

// Depois: uma declaração
val resumo: StateFlow<ResumoCarrinho> =
    combine(_itens, _cupom, _tipoEntrega) { i, c, e -> calcularResumo(i, c, e) }
        .stateIn(scope, SharingStarted.WhileSubscribed(5_000), ResumoCarrinho())
```

As **fontes** (`_itens`, `_cupom`, `_tipoEntrega`) continuam `MutableStateFlow`, porque a View precisa alterar. Só o estado **derivado** vira `stateIn`, e ele é somente-leitura por natureza (não tem `.value =`).

**Por que "compartilhado" importa — frio vs quente:**

```kotlin
val cotacao = flow {
    println("abrindo conexão com a API")   // efeito caro
    emit(buscarCotacao())
}

// Sem stateIn: cada coletor roda o flow do zero → 2 conexões
launch { cotacao.collect { ... } }
launch { cotacao.collect { ... } }

// Com stateIn: 1 conexão, os 2 coletores recebem o mesmo valor
val cotacaoState = cotacao.stateIn(scope, SharingStarted.Lazily, null)
launch { cotacaoState.collect { ... } }
launch { cotacaoState.collect { ... } }
```

**As 3 estratégias de `SharingStarted`:**

| Estratégia | Começa a coletar o upstream | Para | Uso |
|---|---|---|---|
| `Eagerly` | imediatamente, mesmo sem coletor | nunca (só quando o scope é cancelado) | dado que precisa estar pronto antes da tela abrir |
| `Lazily` | no 1º coletor | nunca | começar sob demanda, mas manter pra sempre |
| `WhileSubscribed(ms)` | no 1º coletor | `ms` depois que o **último** coletor sai | **padrão no Android** |

```
coletores:   0 ──── 1 ──── 2 ──── 1 ──── 0 ········(5s)········ para upstream
upstream:    parado  ▶ roda ─────────────────────────────────── ■ parado
                                               ↑ se alguém voltar antes dos 5s,
                                                 o upstream nem chega a parar
```

**Por que `WhileSubscribed(5_000)` é a recomendação oficial do Android:**
- **Rotação de tela:** a Activity é destruída e recriada, e o coletor sai e volta em menos de 1s. Com os 5s de folga, o upstream **não reinicia**, então não refaz consulta nem requisição.
- **App em segundo plano:** a UI para de coletar (com `collectAsStateWithLifecycle`). Depois de 5s, o upstream para, o que economiza GPS, banco, rede e bateria. Quando o usuário volta, o `StateFlow` ainda tem o **último valor** (a tela não pisca vazia) e o upstream volta a rodar.
- `Eagerly`/`Lazily` nunca param. Isso serve pra dado leve, mas desperdiça recurso com fonte cara (localização, socket).

**Pegadinhas:**

1. **Declare como `val`, uma única vez.** `stateIn` dentro de uma função ou de um `get()` cria um `StateFlow` **novo** (e uma coleta nova) a cada chamada:
   ```kotlin
   fun resumo() = combine(...).stateIn(...)          // ERRADO: novo StateFlow a cada chamada
   val resumo = combine(...).stateIn(...)            // CERTO: um só, compartilhado
   ```
2. **O escopo mantém uma coroutine viva.** Mesmo com `WhileSubscribed`, o `stateIn` lança no `scope` uma coroutine que fica esperando coletores. Num `runBlocking`, o programa não termina sozinho: cancele com `coroutineContext.cancelChildren()` (no Android, o `viewModelScope` resolve isso no `onCleared()`).
3. **`initialValue` aparece primeiro.** Antes do upstream emitir, quem coleta recebe o valor inicial. Escolha um valor que faça sentido na tela: lista vazia, `null`, um estado `Carregando`...
4. **Conflation e `equals`:** como qualquer `StateFlow`, valores iguais ao atual (por `equals`) não são reemitidos.

**Variante `suspend` (sem valor inicial):** `val state = flow.stateIn(scope)` suspende até o upstream emitir o primeiro valor e usa esse valor como inicial. Ela usa `Eagerly` por baixo, e é útil fora da UI quando você não tem um valor inicial razoável.

**Irmão: `shareIn`** (detalhado depois): `flow.shareIn(scope, started, replay = 0)` devolve um `SharedFlow` em vez de `StateFlow`. Ele não tem valor inicial, não tem `.value` e não faz conflation por `equals`. Serve pra **eventos** ou quando não existe um "estado atual" que faça sentido.

**Ponte com RxJS:** `stateIn` ≈ `BehaviorSubject` / `shareReplay({ bufferSize: 1, refCount: true })`. `WhileSubscribed` ≈ `refCount` (com um atraso antes de desconectar). `shareIn` ≈ `share()` / `shareReplay(n)`.

---

## 6. Jetpack Compose — EM PROGRESSO

### Nível 1 — Fundações (teoria dada em 2026-09-29)

#### 1. O que é o Compose: UI declarativa

**Definição:** Jetpack Compose é o toolkit moderno de UI do Android. Você descreve **como a tela deve ser para um estado**, e o Compose se encarrega de atualizar a tela quando esse estado muda.

- **Imperativo (jeito antigo, View/XML):** você cria a tela uma vez e depois **muda ela na mão** (`textView.text = "..."`, `button.isEnabled = false`). Esquecer uma atualização gera bug de tela dessincronizada.
- **Declarativo (Compose):** você **não muda a tela**. Você muda o **estado**, e a função que descreve a tela roda de novo com o valor novo.

> 📝 Caderno
> UI = f(estado)
> Imperativo: "mude o texto para X"
> Declarativo: "a tela é assim quando o estado é X"
> Não mexo na tela, mexo no estado

**Ponte com React:** é o mesmo modelo mental do React. A diferença é que não existe JSX nem virtual DOM: são funções Kotlin normais, e um plugin do compilador rastreia quais estados cada função leu.

#### 2. Função `@Composable`

**Definição:** uma função marcada com `@Composable` descreve um pedaço da UI. Ela não devolve uma View: ela **emite** a UI para dentro da árvore do Compose.

```kotlin
@Composable
fun Saudacao(nome: String) {
    Text("Olá, $nome!")
}
```

Regras e convenções:
- Nome em **PascalCase** (`Saudacao`, não `saudacao`), como um componente React.
- A que emite UI devolve `Unit`, ou seja, não tem `return` de valor.
- Os **parâmetros são as "props"**: dados entram por parâmetro.
- Ela **só pode ser chamada de outra `@Composable`**, assim como uma `suspend` só pode ser chamada de outra `suspend` ou de uma coroutine. Não é coincidência: nos dois casos o compilador adiciona um parâmetro escondido (a `suspend` recebe `Continuation`, a `@Composable` recebe o `Composer`).
- Deve ser **rápida e sem efeito colateral**: nada de chamar API, gravar em banco ou lançar coroutine direto no corpo. Ela pode rodar muitas vezes (ver recomposição).

> 📝 Caderno
> @Composable = função que desenha um pedaço da tela
> PascalCase · devolve Unit · parâmetros = props
> Só chamada por outra @Composable (igual suspend)
> Corpo sem efeito colateral: pode rodar N vezes

**Peças mínimas usadas nos exemplos** (a fundo depois, no Nível 2):
- `Text("...")`: mostra texto.
- `Button(onClick = { ... }) { Text("...") }`: botão. A última lambda é o **conteúdo** do botão, o "children" do React. Esse padrão se chama **slot**.
- `Column { ... }`: empilha os filhos **na vertical**, como um `flex-direction: column`.

#### 3. Composição e árvore de UI

Quando a tela abre, o Compose executa as funções `@Composable` e monta uma **árvore** com o que elas emitiram. Esse processo se chama **composição**.

```
TelaClima()
 └─ Column
     ├─ Text("São Paulo")
     └─ Button
         └─ Text("Atualizar")
```

> 📝 Caderno
> Composição = rodar as @Composable e montar a árvore da UI
> Composição inicial: 1ª vez que a tela aparece

#### 4. Recomposição

**Definição:** quando um **estado lido** por uma função `@Composable` muda, o Compose **roda essa função de novo** para atualizar a árvore. É o "re-render" do React.

Ciclo:

```
estado muda ──► Compose marca quem LEU esse estado
            ──► roda de novo só essas funções (recomposição)
            ──► pula as funções cujos parâmetros não mudaram
            ──► tela atualizada
```

Consequências práticas:
- A recomposição é **granular**: só re-executa quem leu o estado que mudou, não a tela inteira.
- Uma função pode rodar **muitas vezes** (em animação, até a cada frame). Por isso o corpo não pode ter efeito colateral: uma chamada de API ali seria disparada várias vezes.
- Variável comum dentro da função é **recriada do zero** a cada recomposição. Guardar valor entre recomposições exige `remember` (próximo item).

> 📝 Caderno
> Recomposição = rodar de novo a @Composable quando um estado que ela LEU muda
> Granular: só quem leu · pula quem não mudou
> Pode rodar muitas vezes → sem efeito colateral
> Variável local morre a cada recomposição

#### 5. Estado: `mutableStateOf` e `remember`

São duas peças com funções diferentes:

| Peça | O que faz | Ponte |
|---|---|---|
| `mutableStateOf(v)` | Cria um valor **observável**: quando muda, dispara recomposição de quem o leu | Parecido com `MutableStateFlow`, mas feito para o Compose |
| `remember { ... }` | **Guarda** um valor entre recomposições (roda o bloco só na 1ª vez) | Parecido com o `by lazy` (calcula uma vez e guarda) |
| `remember { mutableStateOf(v) }` | Estado observável que **sobrevive** às recomposições | `useState(v)` do React |

```kotlin
@Composable
fun Contador() {
    var cliques by remember { mutableStateOf(0) }

    Button(onClick = { cliques++ }) {
        Text("Cliquei $cliques vezes")
    }
}
```

Leitura linha a linha:
- `mutableStateOf(0)`: estado observável que começa em 0.
- `remember { ... }`: na 1ª composição cria o estado, e nas recomposições devolve **o mesmo** objeto.
- `by`: é **delegação de propriedade** (tópico 4!). Permite escrever `cliques` e `cliques++` em vez de `cliques.value`. Precisa dos imports `androidx.compose.runtime.getValue` e `setValue`, que são exatamente o `getValue`/`setValue` de um delegate.
- O clique muda o estado → o `Text` que leu `cliques` recompõe → o número muda na tela.

Erros clássicos:

```kotlin
var cliques by mutableStateOf(0)            // ❌ sem remember: volta a 0 a cada recomposição
var cliques = 0                             // ❌ não é observável: muda, mas a tela não sabe
var lista by remember { mutableStateOf(mutableListOf<String>()) }
lista.add("x")                              // ❌ mutou por dentro: o estado não percebe
lista = lista + "x"                         // ✅ valor novo → recompõe (igual imutabilidade no React)
```

Genéricos por trás (mesmo desenho de `StateFlow`/`MutableStateFlow`):

```kotlin
fun <T> mutableStateOf(value: T): MutableState<T>
interface State<out T> { val value: T }                  // só leitura
interface MutableState<T> : State<T> { override var value: T }  // leitura + escrita
inline fun <T> remember(calculation: () -> T): T
```

**`remember` vs `rememberSaveable`:**
- `remember` sobrevive à **recomposição**, mas se perde ao **girar a tela** (a Activity é recriada) ou quando o composable sai da tela.
- `rememberSaveable` sobrevive também a girar a tela e à morte do processo, porque salva num `Bundle`. Serve para tipos simples (texto digitado, número, booleano).

> 📝 Caderno
> mutableStateOf = valor observável (muda → recompõe)
> remember = guarda entre recomposições
> var x by remember { mutableStateOf(0) } ≈ useState(0)
> Sem remember → reseta · sem mutableStateOf → tela não sabe
> Lista: criar nova (lista + item), nunca .add()
> rememberSaveable = sobrevive a girar a tela

#### 6. State hoisting (elevar o estado) e fluxo unidirecional

**Definição:** tirar o estado de dentro de um composable e passá-lo **por parâmetro**, junto com uma função de evento. O composable vira **stateless**: ele só mostra o que recebe e avisa quando algo acontece.

Padrão de assinatura: `valor: T` + `onValorChange: (T) -> Unit`.

```kotlin
// stateless: não guarda nada, só mostra e avisa
@Composable
fun CampoCidade(cidade: String, onCidadeChange: (String) -> Unit) {
    TextField(value = cidade, onValueChange = onCidadeChange)
}

// stateful: é dono do estado
@Composable
fun TelaClima() {
    var cidade by remember { mutableStateOf("") }

    Column {
        CampoCidade(cidade = cidade, onCidadeChange = { cidade = it })
        Text("Buscando: $cidade")
    }
}
```

Fluxo unidirecional (UDF):

```
        estado desce ▼
TelaClima ───────────────► CampoCidade
          ◄───────────────
        ▲ evento sobe (onCidadeChange)
```

Por que fazer assim:
- **Uma fonte da verdade:** o `Text` e o campo mostram o mesmo `cidade`.
- **Reuso:** `CampoCidade` serve em qualquer tela.
- **Teste e preview:** um stateless é só função com parâmetro.
- **Casa com o ViewModel:** mais para frente, o dono do estado deixa de ser o `remember` e passa a ser o `StateFlow` do ViewModel. É o mesmo desenho do `ClimaViewModel` do `StateInEx1`: `trocarCidade()` é o evento que sobe, `uiState` é o estado que desce.

> 📝 Caderno
> Hoisting = estado sobe pro pai; filho recebe (valor, onChange)
> Estado desce ▼ · evento sobe ▲ (UDF)
> Stateless: só mostra e avisa · Stateful: dono do estado
> = "lifting state up" do React

#### 7. Resumo: ponte React ↔ Compose

| React | Compose |
|---|---|
| Componente | Função `@Composable` |
| Props | Parâmetros |
| `children` | Lambda de conteúdo (slot) |
| Re-render | Recomposição |
| `useState(0)` | `var x by remember { mutableStateOf(0) }` |
| Lifting state up | State hoisting |
| Imutabilidade (`[...lista, item]`) | `lista + item` |
| `useEffect` | `LaunchedEffect` / `DisposableEffect` (**ainda não visto, Nível 2**) |

#### Onde fica cada estado (visão geral, detalhes depois)

```
remember/rememberSaveable → estado de UI local (campo aberto, aba selecionada, texto digitado)
ViewModel + StateFlow     → estado da tela/negócio (dados, carregando, erro)
```

A ponte ViewModel → Compose (`viewModelScope`, `collectAsStateWithLifecycle()`) ainda **não** foi vista. Está na lista de pendências do `PROGRESSO.md`.

#### Exercício Nível 1: perguntas para responder sem consultar

1. Explique com suas palavras a diferença entre UI imperativa e declarativa, e escreva a "fórmula" do Compose.
2. Por que uma função `@Composable` não pode chamar uma API direto no corpo?
3. Em que é parecido o fato de `@Composable` só poder ser chamada por outra `@Composable` com a regra do `suspend`?
4. O que acontece com `var x by mutableStateOf(0)` sem `remember`? E com `var x = 0` com um botão fazendo `x++`?
5. Um `remember { mutableStateOf("") }` guarda o texto digitado. O usuário gira o celular. O que acontece e como resolver?
6. Uma lista em estado recebe `.add(item)` e a tela não atualiza. Por quê? Como corrigir?
7. Transforme mentalmente um composable `CampoBusca` que tem `remember` dentro numa versão stateless: qual é a assinatura?
8. Desenhe (no caderno) o fluxo estado/evento entre uma `TelaPlayer` (dona de `tocando: Boolean`) e um `BotaoPlay`.


**Objetivo combinado em 2026-09-21:** ao chegar em Compose e começar a fazer projetos, criar entre 5 e 10 projetos básicos pra treinar Kotlin/Compose na prática, com nível crescente a cada um — treino solto, separado dos projetos reais de Nível 4 (GodiTrack/Orchestror).

**Lista curada (2026-09-21), extraída do repo [`solygambas/kotlin-projects`](https://github.com/solygambas/kotlin-projects)** (25 projetos didáticos de um curso de Android Kotlin; a maioria no original usa View system/XML/LiveData — a ideia é reproduzir cada um adaptado pra **Compose + StateFlow/Coroutines**, que já é o padrão desta trilha, não copiar a stack antiga). Ordem crescente de dificuldade:

| # | Projeto original | O que treina | Por que entra na lista |
|---|---|---|---|
| 1 | Temperature Converter | Compose básico, sem estado complexo | Aquecimento — já é Compose no original |
| 2 | Guessing Game | Compose + ViewModel + estado observável | Trocar `LiveData` (original) por `StateFlow` (o que vocês já dominam) |
| 3 | Todo List | CRUD em memória + lista | `LazyColumn` no lugar do RecyclerView original |
| 4 | Stopwatch | Cronômetro, ciclo de vida | Compose + Coroutines (`LaunchedEffect`/`delay` em loop) — ponto forte de vocês |
| 5 | Tasks | MVVM + Room + lista | Entrada natural assim que Room (item 8) começar |
| 6 | Mars Photos | Consumo de API REST com Retrofit | Primeira rede de verdade, junto com Compose |
| 7 | DevBytes | Room + Retrofit + Coroutines + cache offline | Capstone antes de fechar Clean Architecture (item 7) — repository/single-source-of-truth |
| 8 | Wander | Google Maps + localização | Conecta direto com o domínio do GodiTrack (rotas, rastreamento) |
| 9 | To-Do Notes | Testes automatizados (Room + Coroutines) | Ponte direta pro item 9 (CI/CD) — CI sem teste automatizado não faz muito sentido |

## 7. Clean Architecture — não iniciado

_Teoria será adicionada quando o tópico começar._

## 8. Room Database — não iniciado

_Teoria será adicionada quando o tópico começar._

## 9. CI/CD com Gradle para Android — não iniciado

_Teoria será adicionada quando o tópico começar._

## 10. Projeto avançado: app de streaming — não iniciado

_Teoria será adicionada quando o tópico começar._

**Objetivo combinado em 2026-09-21:** depois do CI/CD, projeto de fechamento mais avançado. Referência conceitual: o app open-source **CloudStream** (Kotlin, arquitetura de plugins carregados dinamicamente, Media3/ExoPlayer pra reprodução de vídeo, OkHttp/Jsoup pros provedores). A ideia é reproduzir a **arquitetura** — catálogo consumido de uma API legal, player com Media3, cache/favoritos com Room, paginação (Paging 3), Clean Architecture completa — e não as fontes de conteúdo pirateado do projeto original.

**Objetivo combinado em 2026-09-21:** depois da leva de projetos básicos de Compose, configurar CI/CD pra Android usando o `gradlew` (ex: GitHub Actions rodando `./gradlew test`, `assembleDebug`, lint a cada push/PR) — aprender do zero, nunca configurou CI pra Android antes.

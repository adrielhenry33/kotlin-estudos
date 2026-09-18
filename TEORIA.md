# Kotlin + Mobile — Teoria Completa

> Documento vivo. Atualizado automaticamente sempre que avançamos um tópico no `PROGRESSO.md`. Contém a teoria com exemplos de cada assunto já estudado — o `PROGRESSO.md` é a fonte da verdade do *estado* do aprendizado, este arquivo é a fonte da verdade do *conteúdo*.

Última atualização: 2026-09-18 (esclarecimento sobre broadcast em hot flows)

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
  - [ ] Nível 3+: Avançado (SharedFlow, combine, flatMapLatest, debounce) — **ATUAL**
    - [x] Teoria de `SharedFlow` (replay, buffer, `extraBufferCapacity`, `onBufferOverflow`, `emit` vs `tryEmit`)
    - [x] Teoria de `combine`
    - [ ] Exercícios `SharedFlowEx1` a `SharedFlowEx5` (`src/Flow/SharedFlow/`) — em resolução
    - [ ] `FlowEx5.kt` (`debounce` + `flatMapLatest`) — pendente
    - [ ] Exercício de `combine` — pendente
  - [ ] Nível 4: Aplicações Reais
- [ ] **6. Jetpack Compose** — não iniciado
- [ ] **7. Clean Architecture** — não iniciado
- [ ] **8. Room Database** — não iniciado

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

**`extraBufferCapacity`**: espaço extra de buffer além do `replay`, pra emissões que ainda não foram coletadas não travarem o emissor.

**`onBufferOverflow`**: o que fazer quando o buffer (replay + extra) está cheio e chega uma nova emissão:
- `BufferOverflow.SUSPEND` (padrão): o emissor suspende até haver espaço.
- `BufferOverflow.DROP_OLDEST`: descarta o valor mais antigo do buffer pra abrir espaço pro novo.
- `BufferOverflow.DROP_LATEST`: descarta o valor novo que está tentando entrar, mantendo o buffer como está.

**`emit()` vs `tryEmit()`**: `emit()` é `suspend` — se o buffer estiver cheio e a estratégia for `SUSPEND`, ela espera. `tryEmit()` **não é suspend**, tenta emitir imediatamente e devolve `Boolean` dizendo se conseguiu — essencial quando você precisa emitir de um contexto não-suspenso (ex: um callback de hardware, um listener de UI).

**Caso de uso GodiTrack:** `StateFlow` pro **status da corrida** (sempre existe um status atual — "aguardando", "em andamento"), `SharedFlow` (`replay = 0`) pro **evento de corrida cancelada** — uma tela que abre depois do cancelamento não deveria "descobrir" um cancelamento que já passou, mas deveria ver o status atual imediatamente.

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

### `debounce` e `flatMapLatest` (introduzidos, exercício em aberto)

- **`debounce(tempoMs)`**: só deixa passar um valor se nenhum outro valor chegar dentro da janela de tempo especificada — usado pra evitar disparar uma busca a cada tecla digitada.
- **`flatMapLatest`**: pra cada novo valor emitido, dispara um novo flow interno (ex: uma chamada de rede) e **cancela** o flow interno anterior se ele ainda não tiver terminado — evita que uma busca antiga "atropele" o resultado de uma busca mais recente.

Cenário prático em construção: `BuscaAsyncViewModel` (`FlowEx5.kt`) — termo digitado → `debounce(300ms)` → `flatMapLatest` chamando uma busca simulada de rede.

---

## 6. Jetpack Compose — não iniciado

_Teoria será adicionada quando o tópico começar._

## 7. Clean Architecture — não iniciado

_Teoria será adicionada quando o tópico começar._

## 8. Room Database — não iniciado

_Teoria será adicionada quando o tópico começar._

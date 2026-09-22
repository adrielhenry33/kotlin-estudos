# Kotlin + Mobile — Teoria Completa

> Documento vivo. Atualizado automaticamente sempre que avançamos um tópico no `PROGRESSO.md`. Contém a teoria com exemplos de cada assunto já estudado — o `PROGRESSO.md` é a fonte da verdade do *estado* do aprendizado, este arquivo é a fonte da verdade do *conteúdo*.

Última atualização: 2026-09-22 (exemplo completo do caso GodiTrack StateFlow+SharedFlow, `SharedFlowEx5` verificado e concluído)

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

### `debounce` e `flatMapLatest` (introduzidos, exercício em aberto)

- **`debounce(tempoMs)`**: só deixa passar um valor se nenhum outro valor chegar dentro da janela de tempo especificada — usado pra evitar disparar uma busca a cada tecla digitada.
- **`flatMapLatest`**: pra cada novo valor emitido, dispara um novo flow interno (ex: uma chamada de rede) e **cancela** o flow interno anterior se ele ainda não tiver terminado — evita que uma busca antiga "atropele" o resultado de uma busca mais recente.

Cenário prático em construção: `BuscaAsyncViewModel` (`FlowEx5.kt`) — termo digitado → `debounce(300ms)` → `flatMapLatest` chamando uma busca simulada de rede.

---

## 6. Jetpack Compose — não iniciado

_Teoria será adicionada quando o tópico começar._

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

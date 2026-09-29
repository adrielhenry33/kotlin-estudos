package Flow.StateIn

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.ExperimentalCoroutinesApi


// Exercício StateIn 1: stateIn na prática — app de clima
//
// Duas partes:
//   Parte A — provar, observando o console, QUANDO o upstream liga e desliga em cada
//             estratégia de SharingStarted (Eagerly, Lazily, WhileSubscribed)
//   Parte B — escrever um ViewModel de clima expondo estado derivado com stateIn
//             (sem o trio launch + collect + _state.value = ...)

// ============================================================================
// PARTE A — as 3 estratégias
// ============================================================================

// Fonte pronta (não precisa mexer): simula um sensor que lê a temperatura a cada 200ms.
// Imprime quando começa a ser coletado e quando para — é isso que você vai observar.
fun sensorTemperatura(): Flow<Double> = flow {
    println("    ▶ upstream INICIOU")
    try {
        var leitura = 20.0
        while (true) {
            emit(leitura)
            leitura += 0.5
            delay(200)
        }
    } finally {
        println("    ■ upstream PAROU")
    }
}

// TODO A1: escreva uma função suspend testarEstrategia(nome: String, started: SharingStarted)
//          que, num escopo próprio, transforme sensorTemperatura() em StateFlow com stateIn
//          (usando a estratégia recebida, valor inicial 0.0) e execute este roteiro,
//          imprimindo uma linha antes de cada etapa pra dar pra acompanhar no console:
//
//            1. espera 500ms SEM nenhum coletor
//            2. inicia um coletor que imprime cada valor recebido
//            3. espera 500ms
//            4. cancela esse coletor
//            5. espera 1000ms sem nenhum coletor
//            6. encerra tudo que ainda estiver rodando, antes de retornar

suspend fun testarEstrategia(nome: String, started: SharingStarted){
        coroutineScope {
            println("\n========== $nome ==========")
           val temperatura: StateFlow<Double> =  sensorTemperatura().stateIn(scope = this, started = started, 0.0);
            println("[$nome] 1. esperando 500ms SEM coletor")
            delay(500.milliseconds);
            println("[$nome] 2. iniciando coletor")
            val job =  launch {
                    temperatura.collect { value ->
                        println("        coletor recebeu: $value")
                    }
            }
            println("[$nome] 3. esperando 500ms COM coletor")
            delay(500.milliseconds);
            println("[$nome] 4. cancelando coletor")
            job.cancel();
            println("[$nome] 5. esperando 1000ms SEM coletor")
            delay(1000.milliseconds);
            println("[$nome] 6. encerrando tudo (cancelChildren)")
            this.coroutineContext.cancelChildren();
        }
}
//

//
// TODO A3: depois de rodar, responda aqui nos comentários, com base no que você VIU no console:
//          a) Em qual(is) estratégia(s) o upstream iniciou antes de existir coletor?
//          b) Em qual(is) o upstream parou quando o coletor foi cancelado (etapa 4/5)?
//          c) Na WhileSubscribed(300): quanto tempo depois do cancelamento o upstream parou?
//          d) Nas que não pararam na etapa 5: o que fez o upstream parar no fim?
//
//          Respostas:
//          a) Só na Eagerly — o "▶ INICIOU" aparece já na etapa 1, sem coletor nenhum.
//             Na Lazily e na WhileSubscribed ele só aparece depois da etapa 2 (1º coletor).
//          b) Só na WhileSubscribed — o "■ PAROU" aparece durante a etapa 5.
//             Na Eagerly e na Lazily o upstream seguiu rodando mesmo sem ninguém coletando.
//          c) Depois do timeout passado (≈ 500ms no meu código, que usou WhileSubscribed(500)),
//             contado a partir do cancelamento do último coletor — por isso cai DENTRO da
//             etapa 5 (1000ms), e não depois dela.
//          d) O cancelChildren() da etapa 6: ele cancela a coroutine de compartilhamento que o
//             stateIn abriu no scope, e junto com ela a coleta do upstream (o finally roda).
//             Ou seja: Eagerly/Lazily nunca param sozinhos — vivem enquanto o scope viver.

// ============================================================================
// PARTE B — ViewModel de clima com stateIn
// ============================================================================
//
// Cenário: a tela mostra a temperatura da cidade escolhida, na unidade escolhida.
// O usuário pode trocar a cidade e a unidade a qualquer momento, de forma independente.
//
// Regras:
// - trocar a cidade dispara uma nova busca no "servidor" (buscarTemperatura, já pronta)
//   e, se chegar outra cidade antes da busca terminar, a busca antiga é cancelada
// - trocar a unidade NÃO faz nova busca — só converte o último valor recebido
// - Fahrenheit = Celsius * 9 / 5 + 32
// - antes do primeiro resultado, a tela mostra Carregando

enum class Unidade(val simbolo: String) {
    CELSIUS("°C"),
    FAHRENHEIT("°F")
}

data class Leitura(val cidade: String, val celsius: Double)

sealed interface ClimaUiState {
    data object Carregando : ClimaUiState
    data class Sucesso(val cidade: String, val temperatura: Double, val unidade: Unidade) : ClimaUiState
}

@OptIn(ExperimentalCoroutinesApi::class)
class ClimaViewModel(private val scope: CoroutineScope) {

    private val temperaturasPorCidade = mapOf(
        "São Paulo" to 22.0,
        "Lisboa" to 18.0,
        "Tóquio" to 27.0
    )

    // "Servidor" pronto (não precisa mexer): demora 300ms e devolve a leitura da cidade.
    private fun buscarTemperatura(cidade: String): Flow<Leitura> = flow {
        delay(300)
        emit(Leitura(cidade, temperaturasPorCidade[cidade] ?: 0.0))
    }

    // TODO B1: guarde cidade e unidade como fontes internas, privadas e mutáveis.
    //          Valores iniciais: "São Paulo" e CELSIUS

    private val _cidade = MutableStateFlow<String>("São Paulo");
    private val _unidade = MutableStateFlow<Unidade>(Unidade.CELSIUS);



    // TODO B2: crie funções públicas trocarCidade(cidade) e trocarUnidade(unidade)

    fun trocarCidade(cidade: String){
        if (cidade.isEmpty())return;
        _cidade.value = cidade;
    }

    fun trocarUnidade(unidade: Unidade){
        _unidade.value = unidade;
    }

    // TODO B3: exponha publicamente uiState: StateFlow<ClimaUiState>, derivado das fontes,
    //          construído com stateIn e seguindo as regras acima.
    //          Estratégia: a recomendada pra Android. Valor inicial: Carregando.
    //          Restrição: sem MutableStateFlow pro uiState e sem launch dentro do ViewModel.

    private val leitura: Flow<Leitura> = _cidade.flatMapLatest { cidade ->
        buscarTemperatura(cidade)
    }

    val uiState: StateFlow<ClimaUiState> = combine(leitura, _unidade) { leitura, unidade ->
        val temperatura = when (unidade) {
            Unidade.CELSIUS -> leitura.celsius
            Unidade.FAHRENHEIT -> leitura.celsius * 9 / 5 + 32
        }
        ClimaUiState.Sucesso(leitura.cidade, temperatura, unidade)
    }.stateIn(
        scope = scope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ClimaUiState.Carregando
    )

}


// TODO A2: na main, chame testarEstrategia 3 vezes, uma pra cada estratégia:
//          SharingStarted.Eagerly, SharingStarted.Lazily, SharingStarted.WhileSubscribed(300)
fun main() = runBlocking {
    // TODO A2 aqui

    testarEstrategia("Adriel", SharingStarted.Eagerly);
    testarEstrategia("Adrirel", SharingStarted.Lazily);
    testarEstrategia("Adriel", SharingStarted.WhileSubscribed(500));

    println("\n===== PARTE B =====")

    // TODO B4: crie o ClimaViewModel, observe o uiState (em paralelo) imprimindo cada
    //          estado, e simule o usuário com um delay entre cada ação suficiente pra busca
    //          terminar. Saída esperada, nesta ordem:
    //            Carregando
    //            Sucesso(São Paulo, 22.0 °C)
    //          trocarUnidade(FAHRENHEIT)   → Sucesso(São Paulo, 71.6 °F)   (sem nova busca)
    //          trocarCidade("Lisboa")      → Sucesso(Lisboa, 64.4 °F)
    //          trocarUnidade(CELSIUS)      → Sucesso(Lisboa, 18.0 °C)
    //          trocarCidade("Tóquio") e, 100ms depois, trocarCidade("São Paulo")
    //                                      → só Sucesso(São Paulo, 22.0 °C) — Tóquio nunca aparece
    //
    // TODO B5: encerre tudo que precisa ser encerrado antes do programa terminar

    // DESAFIO EXTRA (opcional): depois do roteiro, cancele o coletor, espere 6 segundos e
    // colete de novo. Qual é o PRIMEIRO valor que o novo coletor recebe? Carregando ou o
    // último Sucesso? A busca roda de novo? Explique por quê, com base na estratégia usada.
}

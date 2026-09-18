package Flow.SharedFlow

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

// SharedFlow — Exercício 5: caso de uso real GodiTrack — evento vs estado
//
// Cenário: um CorridaViewModel precisa expor duas coisas de natureza diferente:
//
// 1. O STATUS atual da corrida ("aguardando", "em andamento", "concluída") —
//    isso é ESTADO: sempre existe um valor atual, e uma tela que abrir depois
//    precisa ver o status vigente imediatamente.
//
// 2. O EVENTO "corrida cancelada" — isso é um EVENTO PONTUAL: só deve ser
//    tratado por quem estava coletando NO MOMENTO em que aconteceu. Uma tela
//    que abrir depois do cancelamento não deve "descobrir" um cancelamento
//    antigo.

class CorridaViewModel(private val scope: CoroutineScope) {

    // TODO 1: escolha o tipo certo (Flow "quente" com estado atual) pro status
    //         da corrida, privado/mutável, começando em "aguardando"
    // TODO 2: exponha o status publicamente, somente-leitura

    // TODO 3: escolha o tipo certo (Flow "quente" sem estado, replay = 0) pro
    //         evento de cancelamento, privado/mutável
    // TODO 4: exponha o evento publicamente, somente-leitura

    fun atualizarStatus(novoStatus: String) {
        // TODO 5: atualize o estado
    }

    fun cancelarCorrida() {
        scope.launch {
            // TODO 6: emita o evento de cancelamento (pense: emit ou tryEmit?)
        }
    }
}

fun main() = runBlocking {
    val viewModel = CorridaViewModel(this)

    viewModel.atualizarStatus("motorista a caminho")
    viewModel.atualizarStatus("em andamento")

    delay(50)

    // TODO 7: inicie um coletor TARDIO do status (depois das atualizações acima)
    //         e confirme que ele já recebe "em andamento" imediatamente

    // TODO 8: inicie um coletor do evento de cancelamento ANTES de cancelar,
    //         imprimindo "Cancelamento recebido!" quando ele chegar

    delay(50)
    viewModel.cancelarCorrida()

    delay(50)

    // TODO 9: inicie um SEGUNDO coletor do evento de cancelamento, mas só DEPOIS
    //         que cancelarCorrida() já rodou. Confirme que ele NÃO recebe nada
    //         (diferente do que aconteceria com o status, que tem replay do valor atual)

    delay(100)
}

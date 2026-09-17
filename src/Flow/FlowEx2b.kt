package Flow

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

// Exercício 2b: StateFlow — treino de novo (mesma ideia do Exercício 2, cenário diferente)
//
// Cenário: acompanhamento de status de uma entrega (GodiTrack). O motorista vai
// atualizando o status conforme avança; a "tela" (main) observa esse status e
// reage a cada mudança.

class StatusEntregaViewModel {
    // TODO 1: guarde o status atual como estado interno, privado e mutável, começando em "Aguardando coleta"
    // TODO 2: exponha esse estado publicamente, de forma somente-leitura

    private val _state = MutableStateFlow<String>("");
    val estado : StateFlow<String> = _state;
    fun atualizarStatus(novoStatus: String) {
        _state.value = novoStatus;
    }
}

fun main() = runBlocking {
    val viewModel = StatusEntregaViewModel()

    // TODO 4: lance uma coroutine em paralelo (launch) que observe o estado exposto
    //         e imprima "Status da entrega: $valor" a cada mudança

    val job = launch {
        viewModel.estado.collect {
            valor ->
            println("Status da entrega $valor");
        }
    }

    delay(100)
    viewModel.atualizarStatus("Coletado")

    delay(100)
    viewModel.atualizarStatus("Em trânsito")

    delay(100)
    viewModel.atualizarStatus("Entregue")

    delay(100)
    // TODO 5: cancele a coroutine lançada no TODO 4
    job.cancel();
}

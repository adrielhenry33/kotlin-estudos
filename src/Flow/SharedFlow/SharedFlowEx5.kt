package Flow.SharedFlow

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.time.Duration.Companion.milliseconds

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

    // Estado: StateFlow, sempre tem valor atual, coletor tardio vê o vigente na hora.
    private val _status = MutableStateFlow("aguardando")
    val status: StateFlow<String> = _status.asStateFlow()

    // Evento: SharedFlow com replay = 0, só quem já está coletando recebe.
    private val _evento = MutableSharedFlow<String>(replay = 0)
    val evento: SharedFlow<String> = _evento.asSharedFlow()

    fun atualizarStatus(novoStatus: String) {
        _status.value = novoStatus
    }

    fun cancelarCorrida() {
        // emit(), não tryEmit(): estamos num contexto suspenso (dentro de um launch),
        // não é um callback de hardware — não tem motivo pra recusar em vez de esperar.
        // Também NÃO cancelamos esse job: emit() é uma chamada única que termina
        // sozinha assim que entrega o valor, diferente de um collect (que é infinito).
        scope.launch {
            _evento.emit("Corrida cancelada")
        }
    }
}

fun main() = runBlocking {
    val viewModel = CorridaViewModel(this)

    viewModel.atualizarStatus("motorista a caminho")
    viewModel.atualizarStatus("em andamento")

    delay(50.milliseconds)

    // TODO 7: coletor TARDIO do status — inicia bem depois das atualizações,
    // mas por ser StateFlow, recebe "em andamento" (o valor vigente) imediatamente.
    val jobStatus = launch {
        viewModel.status.collect { status ->
            println("Status recebido: $status")
        }
    }

    // TODO 8: coletor do evento, iniciado ANTES do cancelamento acontecer.
    val jobEventoAntes = launch {
        viewModel.evento.collect { evento ->
            println("Cancelamento recebido (coletor de ANTES): $evento")
        }
    }

    delay(50.milliseconds) // garante que os dois coletores acima já estão inscritos

    viewModel.cancelarCorrida()

    delay(50.milliseconds) // dá tempo do evento ser entregue ao coletor de antes

    // TODO 9: coletor do evento, iniciado DEPOIS que cancelarCorrida() já rodou.
    // Diferente do status (que tem "replay" do valor atual), esse NÃO deve receber nada.
    val jobEventoDepois = launch {
        viewModel.evento.collect { evento ->
            println("Cancelamento recebido (coletor de DEPOIS): $evento")
        }
    }

    delay(100.milliseconds) // tempo suficiente pra confirmar que esse coletor fica em silêncio

    jobStatus.cancel()
    jobEventoAntes.cancel()
    jobEventoDepois.cancel()
}

package Flow.SharedFlow

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

// SharedFlow — Exercício 2: replay pra coletor tardio
//
// Cenário: histórico de status de uma corrida. Os status "aguardando",
// "motorista a caminho" e "em andamento" já foram emitidos ANTES de uma
// tela nova abrir. Quando essa tela abrir e começar a coletar, ela precisa
// enxergar os últimos status que já aconteceram, não só os que vierem depois.

class HistoricoStatus {

    // TODO 1: declare a fonte privada e mutável com replay = 2

    // TODO 2: exponha publicamente como somente-leitura

    private val _mState = MutableSharedFlow<String>(2);
    val mState : SharedFlow<String> = _mState.asSharedFlow();

    suspend fun atualizarStatus(status: String) {
        // TODO 3: emita o status na fonte
        _mState.emit(status);
    }
}

fun main() = runBlocking {
    val historico = HistoricoStatus()

    // TODO 4: emita, em sequência, "aguardando", "motorista a caminho" e "em andamento"
    //         (sem nenhum coletor rodando ainda)
    historico.atualizarStatus("aguardando");
    historico.atualizarStatus("motorista a caminho");
    historico.atualizarStatus("em andamento");

    delay(50)

    // TODO 5: só agora inicie um coletor ("Tela nova") e imprima cada status recebido
    //         como "Tela nova recebeu: <status>"

    val coletor= launch { historico.mState.collect { value -> println("Tela nova recebeu: $value") } }

    delay(100)
    coletor.cancel();

    // Desafio extra (opcional): repita o exercício com replay = 0 e observe a diferença
    // no que a "Tela nova" consegue ver.
}

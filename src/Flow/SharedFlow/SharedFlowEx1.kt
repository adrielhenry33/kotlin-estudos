package Flow.SharedFlow

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

// SharedFlow — Exercício 1: broadcast básico pra múltiplos coletores
//
// Cenário: uma central de notificações do GodiTrack. Duas telas diferentes
// (Tela A e Tela B) estão abertas ao mesmo tempo e ambas precisam receber
// a MESMA notificação assim que ela for enviada — nenhuma das duas pode
// "perder" um evento que a outra recebeu.

class CentralNotificacoes {

    // TODO 1: declare a fonte privada e mutável (sem replay, configuração padrão)

    // TODO 2: exponha publicamente como somente-leitura

    private val _mState = MutableSharedFlow<String>(0);
    val mState : SharedFlow<String> = _mState.asSharedFlow()

    suspend fun notificar(mensagem: String) {
        // TODO 3: emita a mensagem na fonte

        _mState.emit(mensagem);
    }
}

fun main() = runBlocking {
    val central = CentralNotificacoes()

    // TODO 4: inicie DOIS coletores em paralelo (Tela A e Tela B), cada um
    //         imprimindo "Tela A recebeu: <msg>" / "Tela B recebeu: <msg>"

    val jobA =  launch {
        central.mState.collect { value -> println("Tela A recebeu: $value") };

    }
    val jobB =  launch {
        central.mState.collect { value -> println("Tela b recebeu: $value") };
    }

    delay(100) // garante que os dois coletores já estão rodando antes de notificar

    central.notificar("Corrida #123 aceita")
    central.notificar("Corrida #123 chegou ao destino")

    delay(100) // tempo pros coletores processarem antes do programa terminar
    jobA.cancel();
    jobB.cancel();
}

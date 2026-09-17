package Flow

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

// Exercício 4: verificação — Flow + StateFlow sem ajuda
//
// Cenário: sensor de temperatura de uma câmara fria (GodiTrack, carga refrigerada).
// Leituras chegam como Flow<Int> (graus Celsius). Leituras inválidas (sensor com
// defeito) chegam como valores fora da faixa -30..10 e devem ser ignoradas.
// O ViewModel guarda a ÚLTIMA leitura válida como estado observável.

class MonitorTemperaturaViewModel(private val scope: CoroutineScope) {

    // TODO 1: guarde a última leitura válida como estado interno, privado e mutável, começando em null (Int?)
    // TODO 2: exponha esse estado publicamente, de forma somente-leitura


    private val _state = MutableStateFlow<Int?>(null);
    val state : StateFlow<Int?> = _state;



    fun monitorar(leituras: Flow<Int>) {
        // TODO 3: dentro de uma coroutine no scope recebido, colete as leituras
        // TODO 4: descarte leituras fora da faixa -30..10 (sensor com defeito)
        // TODO 5: atualize o estado exposto com a leitura válida

        scope.launch {
            leituras.collect { valor ->
                if(valor !in -30..10 ) return@collect;
                _state.value = valor;
            }
        }
    }
}

fun main() = runBlocking {
    val viewModel = MonitorTemperaturaViewModel(this)

    // TODO 6: observe o estado exposto (em paralelo) e imprima "Temperatura atual: $valor"
    //         a cada mudança
    // TODO 7: lembre de cancelar essa observação antes do programa terminar

    val job = launch {
        viewModel.state.collect {
            println("Temperatura atual:${it}");
        }
    }

    val leituras = flow {
        emit(-5)
        delay(150)
        emit(999) // leitura inválida, sensor com defeito
        delay(150)
        emit(-8)
        delay(150)
        emit(-40) // fora da faixa
        delay(150)
        emit(2)
    }

    viewModel.monitorar(leituras)
    delay(1000)
    job.cancel();
}

package Flow

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

// Exercício 2: StateFlow — estado observável
//
// Objetivo: expor um estado mutável de forma segura, no mesmo padrão
// que um ViewModel usaria pra alimentar uma tela Compose.

class ContadorCliquesViewModel {
    // TODO 1: guarde a contagem de cliques como um estado interno, privado e mutável
    // TODO 2: exponha esse estado publicamente, de forma somente-leitura pra quem observa de fora

    private val _estado = MutableStateFlow(0);
    val estado : StateFlow<Int> = _estado;

    fun clicar() {
        _estado.value= _estado.value + 1;
    }

    fun resetar() {
        _estado.value = 0;
    }
}

fun main() = runBlocking {
    val viewModel = ContadorCliquesViewModel()

    // TODO 5: observe o estado exposto publicamente e imprima "Cliques: $valor" a cada mudança

    val job = launch {
        viewModel.estado.collect { valor ->
            println("Cliques: $valor")
        }
    }

    delay(100)
    viewModel.clicar()

    delay(100)
    viewModel.clicar()

    delay(100)
    viewModel.clicar()

    delay(100)
    viewModel.resetar()

    delay(100)
    job.cancel()
}

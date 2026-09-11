package Flow

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

// Exercício 2: StateFlow — estado observável
//
// Objetivo: expor um estado mutável de forma segura, no mesmo padrão
// que um ViewModel usaria pra alimentar uma tela Compose.

class ContadorCliquesViewModel {
    // TODO 1: crie um MutableStateFlow<Int> privado chamado _cliques, iniciando em 0
    // TODO 2: exponha um StateFlow<Int> público chamado cliques, apontando pro _cliques
    //         (padrão: val cliques: StateFlow<Int> = _cliques)

    fun clicar() {
        // TODO 3: incremente o valor de _cliques (_cliques.value = _cliques.value + 1)
    }

    fun resetar() {
        // TODO 4: zere o valor de _cliques
    }
}

fun main() = runBlocking {
    val viewModel = ContadorCliquesViewModel()

    // TODO 5: dispare uma coroutine (launch) que faz .collect em viewModel.cliques
    //         e imprime "Cliques: $valor" a cada mudança

    delay(100)
    viewModel.clicar()
    delay(100)
    viewModel.clicar()
    delay(100)
    viewModel.clicar()
    delay(100)
    viewModel.resetar()
    delay(100)
}

package Flow

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

// Exercício 5: Nível 3 (avançado) — debounce + flatMapLatest
//
// Cenário: evoluindo o BuscaViewModel do Exercício 3. Agora a "busca" simula uma
// chamada de rede (delay de 500ms pra devolver resultado). Se o usuário digitar
// rápido, não queremos disparar uma busca por letra, e buscas antigas que ainda
// não terminaram devem ser abandonadas quando um termo mais novo chegar.

data class ProdutoBusca(val nome: String, val preco: Double)

class BuscaAsyncViewModel(private val scope: CoroutineScope) {

    private val produtos = listOf(
        ProdutoBusca("Notebook", 3500.0),
        ProdutoBusca("Mouse", 50.0),
        ProdutoBusca("Notebook Gamer", 7200.0),
        ProdutoBusca("Teclado", 120.0),
        ProdutoBusca("Nota Fiscal Impressora", 300.0)
    )

    // TODO 1: guarde o resultado da busca como estado interno, privado e mutável, começando vazio
    // TODO 2: exponha esse estado publicamente, de forma somente-leitura

    // Simula uma chamada de rede: demora 500ms e devolve os produtos que combinam com o termo.
    private fun buscarNoServidor(termo: String): Flow<List<ProdutoBusca>> = flow {
        delay(500)
        emit(produtos.filter { it.nome.lowercase().contains(termo) })
    }

    fun buscar(termos: Flow<String>) {
        scope.launch {
            termos
                // TODO 3: normalize cada termo (trim + lowercase)
                // TODO 4: aplique debounce (300ms) pra não buscar a cada tecla digitada
                // TODO 5: use flatMapLatest pra chamar buscarNoServidor(termo), cancelando
                //         buscas antigas que ainda não terminaram quando um termo novo chegar
                .collect { resultado ->
                    // TODO 6: atualize o estado exposto com o resultado
                }
        }
    }
}

fun main() = runBlocking {
    val viewModel = BuscaAsyncViewModel(this)

    // TODO 7: observe o estado exposto (em paralelo) e imprima "Resultado: ${nomes}"
    //         a cada mudança
    // TODO 8: lembre de cancelar essa observação antes do programa terminar

    val termosDigitados = flow {
        emit("n")
        delay(50)
        emit("no")
        delay(50)
        emit("note")   // usuário parou de digitar por um tempo depois desse
        delay(600)
        emit("nota")
        delay(600)
    }

    viewModel.buscar(termosDigitados)
    delay(2000)
}

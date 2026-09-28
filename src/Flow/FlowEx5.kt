package Flow

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map

import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.time.Duration.Companion.milliseconds

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

    private val _busca = MutableStateFlow<List<ProdutoBusca>>(mutableListOf());
    val busca : StateFlow<List<ProdutoBusca>> = _busca.asStateFlow();

    // Simula uma chamada de rede: demora 500ms e devolve os produtos que combinam com o termo.
    private fun buscarNoServidor(termo: String): Flow<List<ProdutoBusca>> = flow {
        delay(500)
        emit(produtos.filter { it.nome.lowercase().contains(termo) })
    }

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    fun buscar(termos: Flow<String>) {
        scope.launch {
            // TODO 3: normalize cada termo (trim + lowercase)
            // TODO 4: aplique debounce (300ms) pra não buscar a cada tecla digitada
            // TODO 5: use flatMapLatest pra chamar buscarNoServidor(termo), cancelando
            //         buscas antigas que ainda não terminaram quando um termo novo chegar
            // TODO 6: atualize o estado exposto com o resultado

            termos
                .map { termo -> termo.trim().lowercase() }
                .debounce(300.milliseconds)
                .flatMapLatest { termo -> buscarNoServidor(termo) }
                .collect { resultado -> _busca.value = resultado }

        }
    }
}

fun main() = runBlocking {
    val viewModel = BuscaAsyncViewModel(this)

    // TODO 7: observe o estado exposto (em paralelo) e imprima "Resultado: ${nomes}"
    //         a cada mudança
    val job = launch{ viewModel.busca.collect { value -> println("Resultado : $value") }; }
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
    job.cancel();
}

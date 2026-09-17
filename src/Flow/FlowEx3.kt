package Flow

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

// Exercício 3: juntando Flow (pipeline) + StateFlow (estado da UI)
//
// Cenário: uma busca por produtos. Uma "fonte" de termos digitados chega como Flow,
// é processada (map/filter) e o RESULTADO final fica guardado num StateFlow —
// exatamente como um ViewModel faria pra alimentar uma tela Compose com collectAsState().

data class Produto(val nome: String, val preco: Double)

class BuscaViewModel(private val scope: CoroutineScope) {

    private val produtos = listOf(
        Produto("Notebook", 3500.0),
        Produto("Mouse", 50.0),
        Produto("Notebook Gamer", 7200.0),
        Produto("Teclado", 120.0),
        Produto("Nota Fiscal Impressora", 300.0)
    )

    // TODO 1: guarde o resultado da busca (lista de produtos) como estado interno, privado e mutável, começando vazio
    // TODO 2: exponha esse estado publicamente, de forma somente-leitura

    fun buscar(termo: Flow<String>) {
        scope.launch {
            termo
                // TODO 3: normalize cada termo digitado antes de usar
                // TODO 4: descarte termos curtos demais pra valer a pena buscar
                .collect { termoFiltrado ->
                    // TODO 5: encontre os produtos cujo nome combina com o termo
                    // TODO 6: atualize o estado exposto com essa lista
                }
        }
    }
}

fun main() = runBlocking {
    val viewModel = BuscaViewModel(this)

    // TODO 7: observe o estado exposto e imprima os nomes encontrados a cada atualização

    val termosDigitados = flow {
        emit("n")
        delay(200)
        emit("no")
        delay(200)
        emit("note")
        delay(200)
        emit("nota")
    }

    viewModel.buscar(termosDigitados)
    delay(1000)
}

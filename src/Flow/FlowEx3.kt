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

    // TODO 1: crie um MutableStateFlow<List<Produto>> privado chamado _resultado, iniciando com listOf()
    // TODO 2: exponha um StateFlow<List<Produto>> público chamado resultado

    fun buscar(termo: Flow<String>) {
        scope.launch {
            termo
                // TODO 3: use .map para transformar cada termo em minúsculas (lowercase())
                // TODO 4: use .filter para ignorar termos com menos de 2 caracteres
                .collect { termoFiltrado ->
                    // TODO 5: filtre `produtos` cujo nome (em minúsculas) contenha `termoFiltrado`
                    // TODO 6: atualize _resultado.value com essa lista filtrada
                }
        }
    }
}

fun main() = runBlocking {
    val viewModel = BuscaViewModel(this)

    // TODO 7: dispare uma coroutine (launch) que faz .collect em viewModel.resultado
    //         e imprime os nomes encontrados a cada atualização

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

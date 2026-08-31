package Coroutines

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlin.time.Duration.Companion.seconds

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job;
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch


suspend fun buscarResumoRota(): Result<String> {
    delay(3.seconds)
    return Result.success("Rota completa: 150km")
}

suspend fun buscarHistoricoPosicoes(): Result<List<Posicao>> {
    delay(2.seconds)
    return Result.success(
        listOf(
            Posicao(-23.5505, -46.6333, System.currentTimeMillis()),
            Posicao(-23.5510, -46.6340, System.currentTimeMillis())
        )
    )
}

suspend fun buscarDadosCombustivel(): Result<Double> {
    delay(1.5.seconds) // 1.5 segundos
    return Result.success(45.5) // litros
}

fun launchResult() {
    val scope = CoroutineScope(Dispatchers.IO + Job());


    scope.launch {

        try {
            val tarefa1 = async { buscarDadosCombustivel() };
            val tarefa2 = async { buscarHistoricoPosicoes() };
            val tarefa3 = async { buscarResumoRota() };

            val results = awaitAll(tarefa1, tarefa2, tarefa3);

            var combustivel = results[0] as Result<Double>;
            var historico = results[1] as Result<List<Posicao>>;
            var resumo = results[2] as Result<String>;


            resumo.onSuccess { println("Resumo: $it") }
                .onFailure { println("Erro resumo: ${it.message}") }

            historico.onSuccess { println("Histórico: $it") }
                .onFailure { println("Erro histórico: ${it.message}") }
            combustivel.onSuccess { println("Resumo Combustivel: ¬$it") }
                .onFailure { println("Erro no combustivel ${it.message}") };


        } catch (e: Exception) {
            println("Erro geral: ${e.message}")


        }


    }

    scope.cancel();

}

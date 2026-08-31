package Coroutines

import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlin.time.Duration.Companion.seconds



suspend fun enviarPosicaoComTimeout(posicao: Posicao): Result<String> {
    delay(2.seconds)
    return Result.success("Posição $posicao enviada")
}

suspend fun obterPosicaoGPSComTimeout(): Posicao {
    delay(1.seconds)
    return Posicao(
        lat = -23.5505,
        lng = -46.6333,
        timestamp = System.currentTimeMillis()
    )
}

suspend fun iniciarRastreamento() {
    var estaEmRota = true;

    val result = withTimeoutOrNull(timeout = 10.seconds) {
        while (estaEmRota) {
            val posicao = obterPosicaoGPSComTimeout();
            val result = enviarPosicaoComTimeout(posicao);

            result.onSuccess { println(it) }.onFailure { println("Erro: ${it.message}") }

            delay(2.seconds);
        }
    }
    if (result == null){
        println("Timeout Excedido!");
        estaEmRota = false;
    }
}

//runBlocking cria um scope e bloqueia a thread ate tudo terminar
fun main() = runBlocking{
    iniciarRastreamento();
}
package Coroutines

import kotlinx.coroutines.withTimeoutOrNull;
import kotlinx.coroutines.delay;
import kotlin.time.Duration.Companion.seconds;
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking;
import kotlinx.coroutines.coroutineScope

data class PosicaoFinal(val lat: Double, val lng: Double, val timestamp: Long)
data class RotaFinal(val id: String, val posicoes: List<PosicaoFinal>)

suspend fun enviarPosicaoRastreamentoFinal(posicao: PosicaoFinal): Result<String> {
    delay(1.seconds)
    return Result.success("Posição enviada")
}

suspend fun obterPosicaoGPSFinal(): PosicaoFinal {
    delay(500)
    return PosicaoFinal(-23.5505, -46.6333, System.currentTimeMillis())
}

// Operações finais
suspend fun salvarRotaNoBancoFinal(rota: RotaFinal): Result<String> {
    delay(4.seconds)
    return Result.success("Rota ${rota.id} salva")
}

suspend fun gerarRelatorioCombustivel(rota: RotaFinal): Result<Double> {
    delay(3.seconds)
    return Result.success(45.5) // litros gastos
}

suspend fun atualizarEstatisticasMotorista(motoristaid: String): Result<String> {
    delay(2.seconds)
    return Result.success("Estatísticas atualizadas")
}

suspend fun iniciarPipelineRastreamento() {
    var isEmRota = true;
    val listaPosicoes = mutableListOf<PosicaoFinal>();
    val tempoInicio = System.currentTimeMillis();

    val result = withTimeoutOrNull(timeout = 15.seconds) {
        while (isEmRota ) {
            val posicao = obterPosicaoGPSFinal();
            val resultEnvio = enviarPosicaoRastreamentoFinal(posicao);
            listaPosicoes.add(posicao);
            resultEnvio.onSuccess { println(it) }.onFailure { println("Erro ${it.message}") }

            val tempoAtual = System.currentTimeMillis() - tempoInicio;

            if(tempoAtual >= 8000){
                isEmRota = false;
            }
        }
    }

    isEmRota = false
    val rota = RotaFinal("1", listaPosicoes);

    if (result == null) {
        println("Timeout Excedido!");
    } else {
        println("Rastreamento finalizado");
    }

    finalizarPipelineRastreamento(rota);
}

suspend fun finalizarPipelineRastreamento(rota: RotaFinal) {


    coroutineScope {  // ← Cria um scope dentro da suspend function
        val tarefa1 = async { salvarRotaNoBancoFinal(rota) };
        val tarefa2 = async { atualizarEstatisticasMotorista(rota.id) };
        val tarefa3 = async { gerarRelatorioCombustivel(rota) };

        val results = awaitAll(tarefa1, tarefa2, tarefa3);

        val save = results[0] as Result<String>;
        val estatisticas = results[1] as Result<String>;
        val consumoCombustivel = results[2] as Result<Double>;

        save.onSuccess { println("Banco: $it") }.onFailure { println("Erro ao salvar no banco: ${it.message}") };
        estatisticas.onSuccess { println("Estatísticas: $it") }
            .onFailure { println("Erro ao gerar as estatísticas: ${it.message}") }
        consumoCombustivel.onSuccess { println("Consumo: $it") }
            .onFailure { println("Erro ao calcular o consumo: ${it.message}") }
    }
}

fun main() = runBlocking {
    iniciarPipelineRastreamento();
}
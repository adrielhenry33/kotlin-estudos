package Coroutines

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlin.time.Duration.Companion.seconds

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job;
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

data class Posicao(val lat: Double, val lng: Double, val timestamp: Long)

class RastreamentoService (){
    private  val scope = CoroutineScope(Dispatchers.IO + Job());
    var estaEmRota = true;

    fun iniciarRastreamento() {
        scope.launch {
            while (estaEmRota){
                val posicao = obterPosicaoGPS();
                enviarPosicaoParaAPI(posicao);
                delay(5.seconds);
            }
        }
    }

    fun parar(){

            estaEmRota = false;
            scope.cancel();

    }
}


// Simula envio pra API (suspende por 2 segundos)
suspend fun enviarPosicaoParaAPI(posicao: Posicao): Result<String> {
    delay(2.seconds)
    return Result.success("Posição ${posicao.lat}, ${posicao.lng} enviada")
}

// Simula obter a posição atual do GPS (suspende por 1 segundo)
suspend fun obterPosicaoGPS(): Posicao {
    delay(1.seconds)
    return Posicao(
        lat = -23.5505,
        lng = -46.6333,
        timestamp = System.currentTimeMillis()
    )
}


fun main() = runBlocking {
    val rastreamento = RastreamentoService();
    rastreamento.iniciarRastreamento();
    Thread.sleep(300000);
    rastreamento.parar();
}

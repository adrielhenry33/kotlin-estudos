package Flow.SharedFlow

import Coroutines.launchResult
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds


// SharedFlow — Exercício 4: comparando as 3 estratégias de onBufferOverflow
//
// Cenário: um sensor de temperatura do motor emite uma leitura a cada 10ms,
// mas o coletor (o painel que exibe a leitura) é lento pra processar (delay
// de 100ms por leitura). O buffer vai encher — a pergunta é: o que acontece
// com as leituras que não cabem, em cada estratégia?

class SensorTemperatura(overflow: BufferOverflow) {
    private  val _mState = MutableSharedFlow<Int>(replay = 0, extraBufferCapacity = 2, onBufferOverflow = overflow);
    val mState  = _mState.asSharedFlow();


    // TODO 1: declare a fonte privada e mutável, replay = 0, extraBufferCapacity = 2,
    //         usando o parâmetro `overflow` recebido no construtor

    // TODO 2: exponha publicamente como somente-leitura

    suspend fun emitirLeitura(valor: Int) {
        // TODO 3: emita o valor (pode usar emit ou tryEmit — pense em qual faz
        //         mais sentido pra esse cenário e por quê)

        val response =  _mState.tryEmit(valor)
        if(response) println("Emitido $valor");
        else println("Buffer cheio $valor descartado");
    }
}

suspend fun testarEstrategia(nome: String, overflow: BufferOverflow) {
    println("--- Testando $nome ---")
    val sensor = SensorTemperatura(overflow)

    // TODO 4: inicie um coletor lento (delay de 100ms a cada valor recebido),
    //         imprimindo "Painel recebeu: <valor>"

     coroutineScope {
         val job = launch {
             sensor.mState.collect {
                     value ->
                 println("Painel recebeu: $value");
                 delay(100.milliseconds);
             }

         }

         sensor.emitirLeitura(1);
         delay(10.milliseconds);
         sensor.emitirLeitura(2);
         delay(10.milliseconds);
         sensor.emitirLeitura(3);
         delay(10.milliseconds);
         sensor.emitirLeitura(4);
         delay(10.milliseconds);
         sensor.emitirLeitura(5);
         delay(10.milliseconds);
         sensor.emitirLeitura(6);

         delay(700.milliseconds);
         job.cancel();
     }


    // TODO 5: emita rapidamente os valores 1..6, com um delay pequeno (10ms) entre eles


// tempo pro coletor processar o que sobrou no buffer
}

fun main() = runBlocking {
    testarEstrategia("SUSPEND", BufferOverflow.SUSPEND)
    testarEstrategia("DROP_OLDEST", BufferOverflow.DROP_OLDEST)
    testarEstrategia("DROP_LATEST", BufferOverflow.DROP_LATEST)

    // Depois de rodar: anote (num comentário aqui) quais valores cada estratégia
    // entregou ao painel, e por que a diferença faz sentido.
}

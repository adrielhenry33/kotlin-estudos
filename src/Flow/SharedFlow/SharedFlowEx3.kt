package Flow.SharedFlow

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

// SharedFlow — Exercício 3: emit() vs tryEmit() + extraBufferCapacity
//
// Cenário: um sensor de GPS manda atualizações de localização de um motorista.
// A função que recebe essas atualizações do hardware NÃO é suspend (é um
// callback comum) — então ela não pode usar emit() diretamente. Além disso,
// como pode chegar mais de uma localização antes de alguém coletar, a fonte
// precisa de um buffer extra.

data class Localizacao(val lat: Double, val lng: Double)

class RastreadorGps {

    // TODO 1: declare a fonte privada e mutável, sem replay, com extraBufferCapacity = 2

    // TODO 2: exponha publicamente como somente-leitura

    private val _mState = MutableSharedFlow<Localizacao>(replay = 0, extraBufferCapacity = 2);
    val mState : SharedFlow<Localizacao> =  _mState.asSharedFlow();

    // Simula o callback do hardware de GPS (não é suspend de propósito).
    fun aoReceberLocalizacaoDoHardware(localizacao: Localizacao) {
        // TODO 3: tente emitir a localização SEM suspender, e imprima se deu certo
        //         ("Emitido: ..." ou "Descartado (buffer cheio): ...")

       val emissao =  _mState.tryEmit(localizacao);

        if (emissao) println("Emitido: $localizacao");
        else println("Descartado (bufferCheio): $localizacao/. /");
    }
}

fun main() = runBlocking {
    val rastreador = RastreadorGps()

    // CORREÇÃO DO ENUNCIADO (2026-09-21): testamos e confirmamos que, SEM nenhum
    // coletor ativo, tryEmit() sempre retorna true, não importa o extraBufferCapacity —
    // não existe "buffer cheio" quando não tem ninguém esperando pra consumir.
    // Pra ver tryEmit() recusando de verdade, o buffer precisa estar cheio ENQUANTO
    // existe um coletor ativo que ainda não deu conta de processar os valores anteriores.

    // TODO 4: inicie um coletor da fonte pública que seja LENTO (ex: delay(200) dentro
    //         do collect, antes ou depois do println), imprimindo cada localização recebida

    // TODO 5: com esse coletor lento já rodando, chame aoReceberLocalizacaoDoHardware(...)
    //         5 vezes SEGUIDAS e RÁPIDO (sem delay entre elas), com localizações diferentes
    //         entre si — observe a partir de qual chamada o buffer (extraBufferCapacity = 2)
    //         enche e tryEmit começa a devolver false

    val job = launch {
        rastreador.mState.collect {
            value -> println("Localizacao emitida : $value");
            delay(200);
        }
    }

    delay(50)

    rastreador.aoReceberLocalizacaoDoHardware(Localizacao(-23.6435, 44.3213));
    rastreador.aoReceberLocalizacaoDoHardware(Localizacao(-28.5134, 44.3141));
    rastreador.aoReceberLocalizacaoDoHardware(Localizacao(-23.7575, 44.64234));
    rastreador.aoReceberLocalizacaoDoHardware(Localizacao(-23.5752, 44.86346));
    rastreador.aoReceberLocalizacaoDoHardware(Localizacao(-23.3887, 44.3587));

    delay(2000)

    job.cancel();
}

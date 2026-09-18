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

    // Simula o callback do hardware de GPS (não é suspend de propósito).
    fun aoReceberLocalizacaoDoHardware(localizacao: Localizacao) {
        // TODO 3: tente emitir a localização SEM suspender, e imprima se deu certo
        //         ("Emitido: ..." ou "Descartado (buffer cheio): ...")
    }
}

fun main() = runBlocking {
    val rastreador = RastreadorGps()

    // TODO 4: chame aoReceberLocalizacaoDoHardware(...) 5 vezes seguidas, ainda
    //         SEM nenhum coletor rodando, com localizações diferentes

    // TODO 5: agora inicie um coletor da fonte pública, imprimindo cada localização
    //         recebida, e chame o "hardware" mais 2 vezes

    delay(100)
}

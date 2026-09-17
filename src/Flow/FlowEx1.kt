package Flow

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.runBlocking

// Exercício 1: Fundamentos de Flow (map, filter, collect)
//
// Objetivo: entender que cada valor atravessa o pipeline inteiro
// (map -> filter -> collect) antes do próximo ser emitido.

fun precos(): Flow<Int> = flow {
    val valores = listOf(10, 25, 5, 40, 15, 8)
    for (v in valores) {
        // TODO 1: use delay(300) antes de emitir, simulando uma origem assíncrona (ex: sensor, API)
        // TODO 2: emit(v)

        delay(300);
        emit(v);
    }
}

suspend fun main() {
    // TODO 3: a partir de precos(), monte um pipeline que:
    //   - aplica um desconto de 10% (multiplica por 0.9) -> use map
    //   - descarta valores abaixo de 10 -> use filter
    //   - imprime cada valor final -> use collect
    //
    // Dica de assinatura:
    // precos()
    //     .map { ??? }
    //     .filter { ??? }
    //     .collect { println(it) }


    precos()
        .map { it * 0.9 }
        .onEach { println("Valor com desconto: $it") }
        .filter { it > 10 }
        .onEach { println("Passou no filtro (> 10): $it") }
        .collect { valor -> println("Valor final: $valor") }

    // TODO 4 (opcional, pra fixar o "elemento por elemento"):
    //   adicione um println dentro do map e outro dentro do filter mostrando
    //   o valor que está passando. Rode e observe a ORDEM dos prints —
    //   note que cada valor atravessa map -> filter -> collect antes do próximo ser emitido.
}

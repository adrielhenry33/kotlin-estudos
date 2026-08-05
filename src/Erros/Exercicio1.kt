package Erros


/**
 * EXERCÍCIO 10 — Tratamento de Erros com Try/Catch
 *
 * Cenário: você está processando uma lista de números que vêm de uma API.
 * Alguns são válidos, outros causam erro ao processar (divisão por zero, overflow, etc).
 *
 * Você precisa capturar os erros sem quebrar a aplicação.
 *
 * Camada: Data/Domain — tratamento robusto de falhas.
 */

fun dividir(dividendo: Int, divisor: Int): Int {

    if(divisor == 0) throw Exception("Divisor invalido! nao existe divisao por 0");
    return dividendo/divisor;
}

fun processarNumeros(numeros: List<Pair<Int, Int>>): List<Int> {

    val lista = mutableListOf<Int>();

    numeros.forEach { numero ->
        try {
            val result = dividir(numero.first, numero.second);
            lista.add(result);
        }catch (e: Exception){
            println("Erro ao processar ${numero.first} e  ${numero.second}");
        }
    }
    return  lista;
}

fun main() {
    val dados = listOf(
        10 to 2,    // 5
        20 to 4,    // 5
        30 to 0,    // ❌ erro
        40 to 5,    // 8
        50 to 10    // 5
    )

    val resultados = processarNumeros(dados)
    println("Resultados: $resultados")  // [5, 5, 8, 5]
}
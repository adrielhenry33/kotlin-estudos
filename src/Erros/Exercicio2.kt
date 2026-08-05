package Erros

/**
 * EXERCÍCIO 11 — Padrão Result (Alternativa Modern a Try/Catch)
 *
 * Cenário: ao invés de lançar exceções, você retorna um objeto que
 * representa sucesso ou falha. Mais funcional, menos ruidoso.
 *
 * Camada: Data/Domain — representar falhas como dados.
 */

sealed class Resultado<T> {
    data class Sucesso<T>(val valor: T) : Resultado<T>()
    data class Erro<T>(val mensagem: String, val excecao: Exception) : Resultado<T>()
}

fun dividirSeguro(dividendo: Int, divisor: Int): Resultado<Int> {


    if(divisor == 0) return Resultado.Erro<Int>("Erro ao fazer a divisao: ", Exception("Divisor é 0 nao existe divisao por 0"));
    return Resultado.Sucesso<Int>(valor = dividendo/divisor);
}

fun processarNumerosSeguro(numeros: List<Pair<Int, Int>>): List<Resultado<Int>> {
  val lista = mutableListOf<Resultado<Int>>();

        numeros.forEach { item  ->
            lista.add(dividirSeguro(item.first, item.second));
        }

    return lista;
}

fun main() {
    val dados = listOf(
        10 to 2,
        20 to 0,    // erro
        30 to 3
    )

    val resultados = processarNumerosSeguro(dados)

    resultados.forEach { resultado ->
        when (resultado) {
            is Resultado.Sucesso -> println("✅ ${resultado.valor}")
            is Resultado.Erro -> println("❌ ${resultado.mensagem}")
        }
    }

    /*
    Saída esperada:
    ✅ 5
    ❌ Divisão por zero não permitida
    ✅ 10
    */
}
package Genericos

/**
 * EXERCÍCIO 4 — Particionamento de Respostas de API
 *
 * Cenário: você disparou um batch de chamadas (ex: buscar detalhes de vários pedidos)
 * e cada uma virou um Resultado<T>. Antes de seguir, precisa separar sucessos de
 * falhas pra decidir o que mostrar na UI e o que logar.
 *
 * Camada: fronteira Data/Domain — geralmente tratado dentro do UseCase que
 * orquestra o batch.
 */

sealed interface Resultado<out T> {
    data class Sucesso<T>(val dado: T) : Resultado<T>
    data class Erro(val excecao: Throwable) : Resultado<Nothing>
}

fun <T> List<Resultado<T>>.particionar(): Pair<List<T>, List<Throwable>> {
    TODO("Implemente usando when + smart cast")
}

fun main() {
    val resultados = listOf(
        Resultado.Sucesso("Pedido 1"),
        Resultado.Sucesso("Pedido 2"),
        Resultado.Erro(Exception("Falha na API")),
        Resultado.Sucesso("Pedido 3"),
        Resultado.Erro(Exception("Timeout"))
    )

    val (sucessos, erros) = resultados.particionar()

    println("Sucessos: $sucessos")
    println("Erros: $erros")

    /*
    Saída esperada:
    Sucessos: [Pedido 1, Pedido 2, Pedido 3]
    Erros: [Exception: Falha na API, Exception: Timeout]
    */
}
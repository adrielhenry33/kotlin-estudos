package Genericos

data class Pagina<T>(
    val itens: List<T>,
    val paginaAtual: Int,
    val totalPaginas: Int,
    val temProximaPagina: Boolean,
)

fun <T> List<T>.paginar(tamanhoPagina: Int, pagina: Int): Pagina<T> {
    val totalPaginas = (this.size + tamanhoPagina - 1) / tamanhoPagina

    val indexInicio = (pagina - 1) * tamanhoPagina
    val indexFim = minOf(indexInicio + tamanhoPagina, this.size)
    println(indexFim)

    val itensDaPagina = this.subList(indexInicio, indexFim)

    val temProxima = pagina < totalPaginas

    return Pagina(
        itens = itensDaPagina,
        paginaAtual = pagina,
        totalPaginas = totalPaginas,
        temProximaPagina = temProxima
    )
}

fun main() {
    val produtos = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)

    val resultado = produtos.paginar(tamanhoPagina = 2, pagina = 2)

    println("Itens: ${resultado.itens}")  // [4, 5, 6]
    println("Página: ${resultado.paginaAtual}")  // 2
    println("Total de páginas: ${resultado.totalPaginas}")  // 4
    println("Tem próxima? ${resultado.temProximaPagina}")  // true
}
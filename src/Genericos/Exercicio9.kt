package Genericos

/**
 * EXERCÍCIO 9 — Pipeline de Transformação (Composição de Funções)
 *
 * Cenário: você tem uma lista de dados brutos e precisa:
 * 1. Filtrar (remover inválidos)
 * 2. Mapear (converter de um tipo pra outro)
 * 3. Agrupar (organizar por categoria)
 *
 * Tudo isso em uma pipeline genérica e reutilizável.
 *
 * Camada: Domain/Data — processamento de streams de dados.
 *
 * Exemplo: lista de números brutos.
 * Filtrar pares, multiplicar por 2, agrupar por intervalo.
 */

data class Numero(val valor: Int)
data class NumeroProcessado(val valor: Int, val categoria: String)

fun <T, R> List<T>.pipeline(
    filtro: (T) -> Boolean,
    transformacao: (T) -> R,
    agrupamentoPor: (R) -> String
): Map<String, List<R>> {
   /* TODO("""
        Implemente a pipeline:
        1. Filtre os itens (use o filtro fornecido)
        2. Mapeie para R (use a transformação fornecida)
        3. Agrupe por categoria (use agrupamentoPor como chave)

        Retorne um Map<String, List<R>> onde:
        - String = categoria (resultado de agrupamentoPor)
        - List<R> = itens transformados daquela categoria
    """)*/
    var mapa = mutableMapOf<String, MutableList<R>>();

    this.forEach { item ->
        if (filtro(item)) {
            val transVar = transformacao(item);
            val categoria = agrupamentoPor(transVar);
            if(!mapa.containsKey(categoria)) {
                mapa[categoria] = mutableListOf()
            }
            mapa[categoria]?.add(transVar);
        }
    }
    return mapa;
}

fun main() {
    val numeros = listOf(
        Numero(2), Numero(3), Numero(4), Numero(5),
        Numero(6), Numero(7), Numero(8), Numero(9)
    )

    val resultado = numeros.pipeline(
        filtro = { it.valor % 2 == 0 },  // filtra pares
        transformacao = { NumeroProcessado(it.valor * 2, "") },  // multiplica por 2
        agrupamentoPor = {
            when {
                it.valor < 10 -> "Pequeno"
                it.valor < 20 -> "Médio"
                else -> "Grande"
            }
        }
    )

    resultado.forEach { (categoria, itens) ->
        println("$categoria: $itens")
    }

    /*
    Saída esperada:
    Pequeno: [NumeroProcessado(4, ...), NumeroProcessado(8, ...), NumeroProcessado(12, ...)]
    Médio: [NumeroProcessado(16, ...)]
    */
}
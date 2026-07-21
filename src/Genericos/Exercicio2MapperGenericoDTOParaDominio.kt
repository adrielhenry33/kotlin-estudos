package Genericos

/**
 * EXERCÍCIO 2 — Mapper Genérico DTO → Domínio
 *
 * Cenário: sua API devolve DTOs cheios de detalhe de transporte (campo nullable,
 * centavos em Int) que não deveriam vazar pra UI. Em vez de um mapper hardcoded
 * por tipo, você quer um contrato genérico injetável via DI em qualquer Repository.
 *
 * Camada: Data (o mapeamento acontece dentro do RepositoryImpl;
 * o Mapper é injetado como dependência).
 */

interface Mapper<Entrada, Saida> {
    fun mapear(entrada: Entrada): Saida
}

fun <Entrada, Saida> List<Entrada>.mapearCom(mapper: Mapper<Entrada, Saida>): List<Saida> {
  return this.map { item -> mapper.mapear(item) };
}

data class ProdutoDto(val id: String, val precoCentavos: Int, val nomeRaw: String?)
data class Produto(val id: String, val preco: Double, val nome: String)

class ProdutoMapper : Mapper<ProdutoDto, Produto> {
    override fun mapear(entrada: ProdutoDto): Produto {
        return Produto(
            id = entrada.id,
            preco = entrada.precoCentavos / 100.0,
            nome = entrada.nomeRaw ?: "Sem nome"
        )
    }
}

/**
 * REQUISITOS:
 * 1. nomeRaw == null deve cair num fallback sensato (ex: "Sem nome"),
 *    nunca propagar null pra UI.
 * 2. Pensa em por que aqui é interface e não abstract class — você já viu
 *    essa diferença, então já sabe a resposta; só confirma com a implementação.
 */

fun main() {
    // Teste sua implementação aqui
    val dtos = listOf(
        ProdutoDto("1", 1500, "Arroz"),
        ProdutoDto("2", 2000, null),
        ProdutoDto("3", 3500, "Feijão")
    )

    val mapper = ProdutoMapper()
    val produtos = dtos.mapearCom(mapper)

    produtos.forEach { println(it) }
}
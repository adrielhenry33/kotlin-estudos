package Genericos
/**
 * EXERCÍCIO 3 — Extrato Agregado por Categoria
 *
 * Cenário: tela de extrato de um app financeiro: uma lista de transações
 * precisa ser agrupada por categoria (ou mês, ou qualquer critério) e resumida
 * em total + quantidade.
 *
 * Camada: Domain — regra de negócio de agregação, usada por um UseCase de extrato.
 */

data class Transacao(val id: String, val categoria: String, val valor: Double)

/**
 * ResumoPorChave = o resultado agregado
 *
 * Exemplo: se você agrupa por categoria "Alimentação",
 * o resumo será:
 *   - chave = "Alimentação"
 *   - total = 150.0 (soma de todas as transações dessa categoria)
 *   - quantidade = 3 (quantas transações tem nessa categoria)
 */
data class ResumoPorChave<K>(
    val chave: K,
    val total: Double,
    val quantidade: Int
)

/**
 * Função genérica que agrupa dados por uma chave e resume
 *
 * chaveDe: função que extrai a chave de cada item
 * valorDe: função que extrai o valor numérico de cada item
 */
fun <T, K> List<T>.agruparEResumir(
    chaveDe: (T) -> K,
    valorDe: (T) -> Double
): List<ResumoPorChave<K>> {
    val mapaTransacao = mutableMapOf<K, MutableList<T>>  ();

    this.forEach { it ->
        if(!mapaTransacao.containsKey(chaveDe(it))) /* ou if(chaveDe(it) !in mapaTransacao) mesma coisa */{
            mapaTransacao[chaveDe(it)] = mutableListOf(it);
        } else{
            mapaTransacao[chaveDe(it)]?.add(it);
        }
    }

   val lista =  mapaTransacao.map { (chave, transacao) ->
       val total = transacao.sumOf{valorDe(it)}
       val quantidade = transacao.size;

       ResumoPorChave(chave, total, quantidade);
   };

    return lista;
}

fun main() {
    val transacoes = listOf(
        Transacao("1", "Alimentação", 50.0),
        Transacao("2", "Alimentação", 35.0),
        Transacao("3", "Transporte", 20.0),
        Transacao("4", "Alimentação", 45.0),
        Transacao("5", "Transporte", 15.0),
        Transacao("6", "Lazer", 100.0)
    )

    val resumo = transacoes.agruparEResumir(
        chaveDe = { it.categoria },
        valorDe = { it.valor }
    )

    resumo.forEach { println("${it.chave}: Total = R$ ${it.total}, Quantidade = ${it.quantidade}") }


}
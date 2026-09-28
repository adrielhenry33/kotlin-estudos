package Flow

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

// Exercício 7: combine — carrinho de compras de um e-commerce
//
// Cenário: a tela do carrinho mostra um resumo (subtotal, desconto, frete, total) que
// depende de 3 coisas que mudam de forma independente:
//   - os itens do carrinho (o usuário adiciona/remove produtos)
//   - o cupom digitado
//   - o tipo de entrega escolhido
// Qualquer mudança em qualquer um dos 3 precisa recalcular o resumo.
//
// Regras:
// - subtotal = soma de (precoUnitario * quantidade) de todos os itens
// - cupom "DESCONTO10" → desconto de 10% sobre o subtotal. Qualquer outro código (ou vazio) → sem desconto
// - frete: PADRAO = 15.0, EXPRESSA = 30.0, RETIRADA = 0.0
// - frete grátis automático se a entrega for PADRAO e o subtotal (antes do desconto) for >= 200.0
// - total = subtotal - desconto + frete
//
// Obs: Double é usado aqui por simplicidade. Em produção, dinheiro usa BigDecimal
// ou valores inteiros em centavos (Long), pra evitar erro de arredondamento.

data class ItemCarrinho(val nome: String, val precoUnitario: Double, val quantidade: Int)

enum class TipoEntrega(val valorFrete: Double) {
    PADRAO(15.0),
    EXPRESSA(30.0),
    RETIRADA(0.0)
}

data class ResumoCarrinho(
    val subtotal: Double = 0.0,
    val desconto: Double = 0.0,
    val frete: Double = 0.0,
    val total: Double = 0.0
)

class CarrinhoViewModel(private val scope: CoroutineScope) {

    // Fontes: cada uma muda de forma independente, pelas funções públicas abaixo
    private val _itens = MutableStateFlow<List<ItemCarrinho>>(emptyList())
    private val _cupom = MutableStateFlow("")
    private val _tipoEntrega = MutableStateFlow(TipoEntrega.PADRAO)

    // Estado derivado: só é escrito pelo combine, a View só lê
    private val _resumo = MutableStateFlow(ResumoCarrinho())
    val resumo: StateFlow<ResumoCarrinho> = _resumo.asStateFlow()

    fun adicionarItem(item: ItemCarrinho) {
        if (item.nome.isBlank() || item.precoUnitario <= 0.0 || item.quantidade <= 0) return
        _itens.update { itensAtuais -> itensAtuais + item }
    }

    fun removerItem(nome: String) {
        _itens.update { itensAtuais -> itensAtuais.filter { it.nome != nome } }
    }

    fun aplicarCupom(codigo: String) {
        _cupom.value = codigo.trim().uppercase()
    }

    fun escolherEntrega(tipo: TipoEntrega) {
        _tipoEntrega.value = tipo
    }

    init {
        scope.launch {
            combine(_itens, _cupom, _tipoEntrega) { itens, cupom, entrega ->
                calcularResumo(itens, cupom, entrega)
            }.collect { resumo ->
                _resumo.value = resumo
            }
        }
    }

    // Regra de negócio isolada numa função pura: recebe valores, devolve um resumo novo.
    // Não lê nem escreve nenhum StateFlow — fácil de testar sozinha.
    private fun calcularResumo(
        itens: List<ItemCarrinho>,
        cupom: String,
        entrega: TipoEntrega
    ): ResumoCarrinho {
        val subtotal = itens.sumOf { it.precoUnitario * it.quantidade }
        val desconto = if (cupom == "DESCONTO10") subtotal * 0.10 else 0.0
        val freteGratis = entrega == TipoEntrega.PADRAO && subtotal >= 200.0
        val frete = if (freteGratis) 0.0 else entrega.valorFrete

        return ResumoCarrinho(
            subtotal = subtotal,
            desconto = desconto,
            frete = frete,
            total = subtotal - desconto + frete
        )
    }
}

fun main() = runBlocking {
    val viewModel = CarrinhoViewModel(this)

    launch {
        viewModel.resumo.collect { r ->
            println("Subtotal ${r.subtotal} | Desconto ${r.desconto} | Frete ${r.frete} | Total ${r.total}")
        }
    }

    delay(100)
    println("\n> adicionar Fone de Ouvido")
    viewModel.adicionarItem(ItemCarrinho("Fone de Ouvido", 150.0, 1))
    delay(100)
    println("\n> adicionar Cabo USB x2")
    viewModel.adicionarItem(ItemCarrinho("Cabo USB", 25.0, 2))
    delay(100)
    println("\n> aplicar cupom DESCONTO10")
    viewModel.aplicarCupom("DESCONTO10")
    delay(100)
    println("\n> entrega EXPRESSA")
    viewModel.escolherEntrega(TipoEntrega.EXPRESSA)
    delay(100)
    println("\n> remover Cabo USB")
    viewModel.removerItem("Cabo USB")
    delay(100)

    // Os dois collect (o do ViewModel e o daqui) nunca terminam sozinhos, porque
    // StateFlow nunca completa. Cancelar os filhos do runBlocking encerra os dois —
    // é o equivalente ao viewModelScope sendo cancelado no onCleared() do Android.
    coroutineContext.cancelChildren()
}

package Funcoes

/**
 * RESUMO DE FUNÇÕES - KOTLIN
 * * 1. LAMBDAS: Funções anônimas.
 * Sintaxe: { param: Tipo -> corpo }
 * Ex: val soma = { a: Int, b: Int -> a + b }
 * * 2. EXTENSION FUNCTIONS: Adiciona funcionalidade a tipos existentes.
 * Sintaxe: fun Tipo.nomeFuncao(): Retorno { ... }
 * Ex: fun String.limpar() = this.trim()
 * * 3. INFIX FUNCTIONS: Permite sintaxe "natural" (sem ponto ou parênteses).
 * Requisito: Precisa ser membro ou extensão de classe e ter apenas 1 parâmetro.
 * Sintaxe: infix fun Tipo.nome(param: Tipo): Retorno { ... }
 * Ex: 5 sum 2
 * * 4. HIGH-ORDER FUNCTIONS: Recebem ou retornam outras funções.
 * Sintaxe: fun nome(..., operacao: (Tipo, Tipo) -> Retorno): Retorno { ... }
 * Ex: fun calcular(a: Int, b: Int, op: (Int, Int) -> Int) = op(a, b)
 */

/**
 * 1. Desafio: Lambdas
 * Exercício A: Crie uma lista de números inteiros de 1 a 10. Use uma lambda
 * com a função .filter para retornar apenas os números pares.
 * Exercício B: Crie uma lambda chamada processar que recebe uma
 * String e um Int. Ela deve retornar uma nova String que é a repetição
 * da string original N vezes (Dica: pesquise a função repeat() do Kotlin).
 * */

fun main(){
    val lista = mutableListOf<Int>(1,2,4,5,6,7,8,9,0);
    val array = arrayOf<Int>(1,2,4);

    val listOfPair = {listaNumeros: List<Int>, -> {
        listaNumeros.filter { it  %2 ==0 }
    }}

    val processar = {palavra: String, nVezes: Int,  -> {
        palavra.repeat(nVezes);
    } }

    println("Lista de numeros pares ${listOfPair(lista).invoke()}")
    println("Palavra repetida ${processar("Adriel", 3).invoke()}")

}
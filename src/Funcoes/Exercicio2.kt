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
2. Desafio: Extension Functions
Exercício A: Crie uma Extension Function para a classe Int
 chamada isEven() que retorna
true se o número for par e false se for ímpar.

Exercício B: Crie uma Extension Function para a
 classe String chamada removeSpaces() que retorna
a string sem nenhum espaço em branco (Dica: replace(" ", "")).
*
**/


fun Int.isEven(): Boolean {
 return this %2 ==0;
}
fun String.removeSpaces(): String{
    return this.replace(" ", "");
}

fun main(){
    val numero = 23;
    val word = "adfad fadfadf adf3r23 ";
    println("O numero $numero e par : ${numero.isEven()}" );
    println("A palavar $word sem espacos ${word.removeSpaces()}")
}

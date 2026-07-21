package Funcoes

fun main (){
    var sum = 2+2;
    val funSum = {a:Int, b: Int  ->
        a + b;
    }
    val funMinus = { a: Float, b: Float -> a -b }

    val resultSum = funSum(2, 5);
    val resultMinus = funMinus(10.4f, 10.5f );

    println("Resultado soma: $resultSum ");
    println("Resultado subtracao: $resultMinus")

    //funcao anonima que retorna outra funcão anonima
    val funSum1 = {a: Int, b: Int ->{
        a + b + 100;
    } }

    println("Funcao anonima que chama outra funcao anonima $funSum1");

    //Para executar uma funcao dentro de outra funcao
    val funSum2 = { a: Int, b: Int ->{
        sum = 200;
        a + b;
    }}
    println("Resultado da chamada da funcao dentro da funcao com o metodo invoke ${funSum2(2,2).invoke()}");
    println("Alterando o valor de sum para:  $sum")


}
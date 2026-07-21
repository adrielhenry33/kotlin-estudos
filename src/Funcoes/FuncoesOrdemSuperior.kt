package Funcoes

import jdk.dynalink.NamedOperation

//Passar uma funcao como parametro para outra funcao

fun sum(a: Int, b: Int): Int{
    return  a+ b;
};
fun subtract (a: Int, b:Int): Int = a - b
fun multiply (a: Int, b: Int): Int = a * b;


//Funcao que recebe funcao
fun mathOperation(a: Int, b: Int, operation: (Int, Int) -> Int): Int = operation(a, b);

//Funcao que passada como parametro que não retorna nada
fun changeValue(a: Int, b: Int, change:(Int, Int)-> Unit): Unit = change(a,b );

data class ValoresMutaveis( var a : Int, var b: String);

fun alterarDoisValores(obj: ValoresMutaveis){
    obj.a = 10;
    obj.b = "Adriel Henry";
}

fun main(){
    val n1 = 1;
    val n2 = 2;

    var resutlMathOperation = mathOperation(n1, n2, operation = :: sum);
    println(resutlMathOperation);

    resutlMathOperation = mathOperation(n1, n2, operation = :: subtract );
    println(resutlMathOperation);

    resutlMathOperation = mathOperation(10, 20, operation = :: multiply);
    println(resutlMathOperation);


    val container = ValoresMutaveis(10, "Pamela");
    alterarDoisValores(container);
    println(container.a);
    println(container.b);

    val divide = {a: Int, b: Int -> a /b}

    resutlMathOperation = mathOperation(10, 20, operation =  divide);


}
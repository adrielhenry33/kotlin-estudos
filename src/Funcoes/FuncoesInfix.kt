package Funcoes

//so ira funcionar se estiver definido dentro de uma classe
// ou seja se for um metodo de um classe ou uma extensao de uma classe ou tipo
infix fun Int.sum(num: Int): Int = this +  num;

//
class XY(val  x: Int, val y: Int){
    infix fun sum(xy: XY): XY{
        return XY(x = this.x + xy.x, y = this.y + xy.y)
    }
}

fun main(){
    println(5 sum 2)
    //instancia da classe e depois chamada da funcao
    val xy = (XY(x =1 , y = 5) sum XY(x = 2 , y = 1 ));
    println(xy.x)
    println(xy.y);
}
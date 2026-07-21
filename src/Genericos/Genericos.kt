package Genericos

import kotlin.coroutines.Continuation


fun <T> printItem(item: T){
    println(item)
}

//essas duas classes são iguais ambas recebem um generico como parametro, o tipo item é um generico também

fun <item> printItem2(item:item){
    println(item)
}

data class Product(val name: String, val qtd: Int);

class Container <T> (val items: List<T>) : ItemsPrinter<T>{
    fun showItens(){
        println("""
            Items do container:
            ${items.joinToString ( )}
        """.trimIndent())
    }

    override fun printItem(item: T) {
        println("item $item");
    }
}

interface ItemsPrinter<T>{
    fun printItem(item:T);
}

fun <T> List<T>.secondOrNull(): T?{
    return if(this.size >=2) this[1] else null;
}

fun <T: Comparable<T>> finMax(a: T, b: T): T{
    return if(a>b) a else b
}


fun main(){

    printItem("loading");
    printItem(2);
    printItem(false);


    val container = Container<Product>(listOf(
        Product("Alberto", 2),
        Product("caixa", 3),
    ));

    val container2 = Container<Product>(listOf(
        Product("macça", 2),
        Product("banco", 3),
    ))

    container.showItens();
    container2.printItem(container2.items.first());

    val productList = listOf(Product("Alberto", 2),
        Product("caixa", 3),);

    println(productList.secondOrNull())

    println(finMax(a = "hello", b = "kotlin"));
    println(finMax(a = 10, b = 20))

}
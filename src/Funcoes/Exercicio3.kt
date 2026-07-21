package Funcoes


fun executarVariaveis(nVezes: Int, acao:(Int)-> Unit){
    for (i in 0 until nVezes) {
        acao(i);
    }
}

fun filtrarLista(lista: List<Int>, acao: (Int)-> Boolean): List<Int>{
    var listAux = mutableListOf<Int>();

    for(i in lista) {
        if(acao(i)) listAux.add(i);
    }

    return listAux;

}

fun soma (a: Int, b: Int): Int = a + b;

fun main(){
    executarVariaveis(5){indice -> println("Executando a ação numero $indice")};
    val lista = listOf<Int>(1,2,4,5,6,)

    val pares =  filtrarLista(lista){it % 2 == 0}
    val impares = filtrarLista(lista){it % 2 == 1};

    println("Pares $pares");
    println("Impares $impares");
}
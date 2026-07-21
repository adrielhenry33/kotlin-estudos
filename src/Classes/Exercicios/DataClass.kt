package Classes.Exercicios

data class Product(val id: Int, val name:String, val price: Double, val isAvailble: Boolean);
data class HomeUiState(val listCarros: List<String>, val isLoading: Boolean, val errorMessage: String);
data class User (val name : String =  "Adriel"){}
class UserRegular(val name: String = "Adriel"){}


fun main(): Unit {
    //Exercicio 1.a
    val Produto = Product(1, name = "Leite", price = 2.4, isAvailble = true);
    val Produto2 = Product(1, name = "Leite", price = 2.4, isAvailble = false);

    if(Produto == Produto2){
        println("Produtos Iguais");
    }
    println("Produtos Diferentes");

    //Exercicio 1.b
    val state = HomeUiState(listOf("Fiesta", "Ferrari", "Gol"), isLoading = true, errorMessage = "Nenhum erro detectado");
    val changeState =  state.copy(isLoading = false, errorMessage = "Erro detectado");

    println(state.toString());
    println(changeState.toString())

    //Exercicio 1.c
    // A data class a partir do construtor ja gera metodos embutidos como o toString(), a classe rugular por sua vez
    // não possui isso é necessario implementar isso manualmente por isso no print ela aparece o nome do pacote o nome
    // da classe e o endereço em que ela esta;
    println(", ${User()}, ${UserRegular()}");

}



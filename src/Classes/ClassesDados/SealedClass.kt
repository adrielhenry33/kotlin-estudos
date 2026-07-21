package Classes.ClassesDados


//3.a
sealed class Result {
   data class Success(val data: String) : Result()
    data class Error(val message: String): Result()

}


fun receiveResult( result: Result){
    when (result){
        is Result.Success -> println("Successfully received ${result.data}");
        is Result.Error -> println("Error ${result.message}");
    }
}

fun Result.tratar(){
    when(this){
        is Result.Success -> println("Sucesso: ${this.data}")
        is Result.Error -> println("Erro: ${this.message}")
    }
}

//3.b
sealed interface  AppScreen {
    object Home: AppScreen { override fun toString() =  "Home" }
    object Settings: AppScreen{ override fun toString() =  "Settings" };
    data class Profile(val userId: Int): AppScreen{
        override fun toString(): String {
            return  "Profile (ID: $userId)"
        }
    }

}

sealed interface  NetworkState {
    object Loading: NetworkState{  override fun toString() = "Loading"}
    object Error: NetworkState{ override fun toString( )= "Erro"}
    object Success: NetworkState{override fun toString( )= "Success"}
}


fun handleState (state: NetworkState){
    when(state){
        is NetworkState.Success -> println("Sucesso");
        is NetworkState.Error -> println("Erro");
        is NetworkState.Loading -> println("Loading");
    }
}



fun main(): Unit {
     receiveResult(Result.Error("Teste"));
    receiveResult(Result.Success("successo"));

    val meuResultado = Result.Success("Dados carregados!")
    meuResultado.tratar()

    val telas = listOf(
        AppScreen.Home,
        AppScreen.Settings,
        AppScreen.Profile(userId = 26)
    )

    telas.forEach { println(it) }
}
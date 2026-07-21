package Classes.ClassesSingleton

// O Singleto é um padrão de projeto que é utilizado para gerear instancias unicas de um varivael, classe ou outra coisa
// Ele gera uma instância global para todo codigo sendo impossivel instanciar aquela classe ou variavel novamente
// e garantindo a mesma implementaação para todos que a chamarem. Não é permitido usar construtores para singletons
object DatabaseUtil {
    val url: String = "jdbc:mysql://localhost:8080/mydb";
    val user: String = "admin";
    val password : String = "admin";

    fun connect () {
        println("Conectando ao database $url | Usuário : $user | Senha: $password" )
    }
}

//tambem é possivel criar data objects
data object  DatabaseHelper {

}

fun main (){

    val db = DatabaseUtil;
    db.connect();
    println(db);
    println(DatabaseHelper)
}
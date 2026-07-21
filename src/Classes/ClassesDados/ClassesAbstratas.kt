package Classes.ClassesDados

// 2.a
abstract class BaseViewModel () {
    abstract var isLoggingEnabled : Boolean;
    abstract  fun logAction(message: String);

}

class LoginViewModel() : BaseViewModel(){
    override var isLoggingEnabled: Boolean = false
        set(value)  {field = !value}
    override fun logAction(message: String) {
        println("Mensagem de logo $message");
    }
}

//2.b
abstract class BaseRepository(){
    abstract fun getTimeStamp(): Long; // retorna o tempo atual Long

}

class CarRepository(): BaseRepository(){
    override fun getTimeStamp(): Long {
        return System.currentTimeMillis();
    }
}

fun main () {

    val carro = CarRepository();
    println(carro.getTimeStamp());
}
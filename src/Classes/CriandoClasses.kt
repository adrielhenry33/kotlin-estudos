package Classes

import kotlin.random.Random

class Person (val  name: String = "Desconhecido", val lastName: String = "Desconhecido", val age: Int = 0) {
    val fullName: String = "$name $lastName";

    private val password :String = name + Random.nextInt(until = 100);

    private fun usePassword(){
        println(password);
    }

    fun work(){
        usePassword()
        println("${name} ${lastName}, ${age} esta Trabalhando");
    }
}

fun main( ){
    val jose: Person = Person("Jose",  lastName = "Barbosa", age = 26);
    var maria: Person = Person(name = "Maria", lastName = "Jesus ", age = 25);

    jose.work()
    maria.work();
}
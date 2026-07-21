package Classes.ClassesAbertas

open  class Animal (val nome: String, ){
    open val age: Int = 0;
    open fun sound(){
        println("$nome : som")
    }
}


class Dog(override  val age: Int) : Animal ( nome =  "Cachorro" ){
    override fun sound (){
        println("$nome: Au")
    }
}

fun main (){
    val dog = Dog(age = 5);

    dog.sound();
    println(dog.age)
}
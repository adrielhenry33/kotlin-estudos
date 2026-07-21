package Classes.Get_Set

class Person ( val name: String) {
    var age: Int = 0
        set(value){
            if(value >=0) field = value;
            else println("Idade não pode ser negativa");
        }
    var height = 0.0
        set(value){
            if(height >=0) field = value;
            else println("Idade não pode ser negativa");
        }
        get() = Math.ceil(field);
}

fun main (){
    var adriel : Person = Person("Adriel");
    adriel.age = 26;
    adriel.height = 193.0;

    println("Idade ${adriel.age}");
    println("heigh ${adriel.height}");


}
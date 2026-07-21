package Classes.ClassesDados

class UserClass(val name: String, val age: Int);

data class UserDataClass(val name:String, val age: Int);

fun main (){
    val userClass: UserClass = UserClass("John", 10)
    val userDataClass: UserDataClass = UserDataClass("Adriel", 10)


    //sempre que printamos uma data class vermos o resultado da declaracao dela
    println(userClass.toString());
    //sao classes mais vinculadas a receber dados
    println(userDataClass.toString()); // -> UserDataClass("Adriel", 10)
    //equals

    val jose  =  UserDataClass(name = "Jose", age =  40);
    val joao = UserClass("Joan", 10);
    val jose2  =  UserDataClass(name = "Jose", age =  40);

    val userClass2 : UserClass = UserClass("John", 10);
    // data classes que poussuem os atributos iguais perante o kotlin
    // nao e verificado a posicao de memoria

    /*O grande diferencial da data class é que o compilador Kotlin gera
    automaticamente implementações específicas para vários métodos, incluindo o equals(), hashCode() e toString(),
     baseando-se apenas nos atributos definidos no construtor.
    O que o equals() gerado faz: Ele realiza a igualdade estrutural. Ele pergunta:
     "Os valores dos campos desta classe são iguais aos valores dos campos daquela classe?"
    No seu exemplo: Quando o Kotlin compara jose e jose2, ele não olha para a memória.
    Ele olha para o name ("Jose") e o age (40). Como ambos são iguais, ele retorna true.*/

    println(jose == jose2);
    println(userClass == userClass2)

    val maria  = jose.copy();
    println(maria == jose); // true

    // desestruracao de classe existe somente para data Class, classes comuns náo funciona
    val (name, age) = jose;


}
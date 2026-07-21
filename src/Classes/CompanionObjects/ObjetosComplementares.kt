package Classes.CompanionObjects

class Person  (){
    companion object{
        const val  KEY = "12345678";
        private val KEY_2 = "123535";
        fun getKey (): String  { return KEY_2;}

    }



    fun getKey (): String  { return KEY_2;}
}


fun main () {
    //nao e necessario instanciar a classe para acessar um companion
     val key = Person.KEY;
     // porem nao e posivell acessar da seguinte forma
    val jose = Person()
    //jose.KEY - errado
    val getKey  = Person.getKey();

}
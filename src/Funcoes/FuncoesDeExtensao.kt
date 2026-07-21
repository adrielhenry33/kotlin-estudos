package Funcoes

//São aquelas que permitem voce adicionar novas funcionalidade
// a classes existentes sem prcisar modificar o codigo original

fun String.isPalindrome(): Boolean{
    return this == this.reversed();
}

fun Double.format (decimalDigits: Int): String {
    return "%.${decimalDigits}f".format(this);
};

fun main(){
    println("radar".isPalindrome());
    println("Banana".isPalindrome());

    println(3.14159.format(2));
}
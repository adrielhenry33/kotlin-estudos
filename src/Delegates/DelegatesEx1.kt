package Delegates

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class ValidadorEmail: ReadWriteProperty<Any, String>{
    private  var valor:String = "";

    override fun getValue(thisRef: Any, property: KProperty<*>): String {
        return valor;
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: String) {

        try {
            if(value.matches(Regex("^[^@]+@[^@]+\$"))) valor  = value;

        }catch (e: IllegalArgumentException){
            println("❌ Erro: ${e.message}")

        }

    }
}

class Usuario {
    var email: String by ValidadorEmail()
    var emailSecundario: String by ValidadorEmail()
}

fun main() {
    val usuario = Usuario()

    usuario.email = "adriel@godi.com"
     println("Email salvo: ${usuario.email}")

     usuario.email = "adriel-sem-arroba"

     usuario.emailSecundario = "reserva@godi.com"
     println("Email secundário: ${usuario.emailSecundario}")
}
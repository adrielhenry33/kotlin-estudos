package Delegates

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty
import java.time.LocalTime;
import java.time.format.DateTimeFormatter

class LoggerDelegate<T>(private val valorPadrao: T) : ReadWriteProperty<Any, T> {
    private var valor = valorPadrao;

    override fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
        val hora = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))
        println("[$hora] ${property.name}  foi modificado de $valor para $value");
        valor = value;
    }

    override fun getValue(thisRef: Any, property: KProperty<*>): T {
        println("Lendo valor $valor");
        return valor;
    }
}




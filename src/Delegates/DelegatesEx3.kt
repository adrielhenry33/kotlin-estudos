package Delegates


import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty
import java.time.LocalTime;
import java.time.format.DateTimeFormatter

class TransformadorDelegate<T>(
    private val valorPadrao: T,
    private val transformar: (T) -> T  // ← Função que transforma
) : ReadWriteProperty<Any, T> {

    private var valor: T = valorPadrao

    override fun getValue(thisRef: Any, property: KProperty<*>): T {
        println("Lendo o valor ${property.name} = $valor");
        return  valor;
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
        // TODO 2: Aplicar a transformação: val transformado = transformar(value)
        // TODO 3: Printi: "🔄 ${property.name}: '$value' → '$transformado'"
        // TODO 4: Armazenar o valor TRANSFORMADO (não o original)

        val transformado = transformar(value);

        valor = transformado

        println("O ${property.name} foi transformado de $value => $valor");
    }
}
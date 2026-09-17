package Delegates
// Arquivo: CacheDelegate.kt

import java.time.LocalTime
import java.time.temporal.ChronoUnit
import kotlin.properties.ReadWriteProperty;
import kotlin.reflect.KProperty

class CacheDelegate<T>(
    private val carregar: () -> T,  // ← Função que carrega o valor
    private val tempoExpiracaoSegundos: Long = 300  // 5 minutos
) : ReadWriteProperty<Any, T> {

    private var valor: T? = null
    private var ultimoCarregamento: LocalTime? = null

    override fun getValue(thisRef: Any, property: KProperty<*>): T {

        if (valor != null && !expirou()) return valor!!;

        valor = carregar();
        ultimoCarregamento = LocalTime.now();
        println("O valor $valor foi recarregado as $ultimoCarregamento");
        return valor!!;

    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: T) {

        valor = null;
        ultimoCarregamento = null
        println("Cache de ${property.name} invalidado ");

    }

    private fun expirou(): Boolean {
        return ChronoUnit.SECONDS.between(ultimoCarregamento, LocalTime.now())> tempoExpiracaoSegundos;
    }
}
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
        // TODO 1: Verificar se valor está cacheado E não expirou
        //         if (valor != null && !expirou()) return valor

        // TODO 2: Se expirou ou é null, recarregar:
        //         println("📥 Recarregando ${property.name}...")
        //         valor = carregar()
        //         ultimoCarregamento = LocalTime.now()
        //         return valor!!
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
        // TODO 3: Invalidar cache (limpar)
        //         valor = null
        //         ultimoCarregamento = null
        //         println("🗑️ Cache de ${property.name} invalidado")
    }

    private fun expirou(): Boolean {
        // TODO 4: Verificar se passou tempoExpiracaoSegundos desde ultimoCarregamento
        //         Dica: ChronoUnit.SECONDS.between(ultimoCarregamento, LocalTime.now()) > tempoExpiracaoSegundos
    }
}
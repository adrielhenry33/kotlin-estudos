package Genericos

/**
 * EXERCÍCIO 6 — Cache com TTL (Time To Live)
 *
 * Cenário: você busca dados da API e quer guardar em cache local.
 * Mas o cache expira depois de X segundos — dados antigos não podem ser usados.
 *
 * Camada: Data — normalmente um UseCase que decide "uso cache antigo ou faço chamada nova?"
 *
 * Exemplo real: buscar lista de usuários. Se o cache tem menos de 5 minutos, usa.
 * Se expirou, chama a API de novo.
 */

data class CacheItem<T>(
    val dado: T,
    val timestampMs: Long  // quando foi armazenado
)

fun <T> CacheItem<T>.expirou(ttlMs: Long): Boolean {
    val currentTime = System.currentTimeMillis();
    val diference = currentTime - this.timestampMs;//tempo em que a variavel esta armazenada em cache
    println("tempo do dado em cache ${diference}");

    return diference > ttlMs;
}

fun main() {
    val agora = System.currentTimeMillis()
    val umSegundoAtras = agora - 1000

    val itemFresco = CacheItem("Usuário 1", agora)
    val itemVelho = CacheItem("Usuário 2", umSegundoAtras)

    println(agora);
    println(itemFresco.expirou(5000))  // false (ainda válido)
    println(itemVelho.expirou(500))    // true (expirou)
}
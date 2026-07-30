package Genericos

import jdk.internal.org.jline.utils.Log

/**
 * EXERCÍCIO 8 — Merge Offline-First
 *
 * Cenário: app offline-first com dados do Room (cache local) e dados da API (rede).
 * Precisa mesclar as duas listas por um ID comum, escolhendo qual versão "ganha".
 *
 * Camada: Data — normalmente dentro do RepositoryImpl.
 *
 * Exemplo: sincronizar tarefas. Local tem [Task(1, "Estudar"), Task(2, "Treinar")].
 * API retorna [Task(1, "Estudar Kotlin"), Task(3, "Correr")].
 * Resultado: [Task(1, "Estudar Kotlin"), Task(2, "Treinar"), Task(3, "Correr")]
 * (usa dados da API quando existem, mantém dados locais que não vieram na API)
 */

data class Tarefa(val id: String, val titulo: String, val atualizado: Long)

fun <T, K> mesclarPorChave(
    local: List<T>,
    remoto: List<T>,
    chaveDe: (T) -> K,
    dataAtualizacaoDe: (T) -> Long
): List<T> {
    //Tarefa -> Tarefa.id  -> {1 = Tarefa(id=1 ...}
    val mapaLocal = local.associateBy { chaveDe(it) }
    val mapaRemoto = remoto.associateBy { chaveDe(it) };
    val listaMesclado = mutableListOf<T>();

    mapaLocal.forEach {(chave , valor )->
        if(mapaRemoto.containsKey(chave)){
            val tarefaRemota= mapaRemoto[chave]!!;
            val dataLocal = dataAtualizacaoDe(valor)
            val dataRemoto =dataAtualizacaoDe(tarefaRemota)
            if(dataLocal < dataRemoto){
                listaMesclado.add(tarefaRemota);

            }else{
                listaMesclado.add(tarefaRemota);
            }
        }
        else{
            listaMesclado.add(valor);
        }
    }

    mapaRemoto.forEach {(chave , valor )->
        if(!mapaLocal.containsKey(chave)){
            listaMesclado.add(valor)
        }
    }
    return listaMesclado;
}

fun main() {
    val local = listOf(
        Tarefa("1", "Estudar", 1000),
        Tarefa("2", "Treinar", 2000)
    )

    val remoto = listOf(
        Tarefa("1", "Estudar Kotlin", 3000),  // mais recente
        Tarefa("3", "Correr", 1500)
    )

    val resultado = mesclarPorChave(
        local, remoto,
        chaveDe = { it.id },
        dataAtualizacaoDe = { it.atualizado }
    )

    resultado.forEach { println(it) }

    /*
    Saída esperada:
    Tarefa(id=1, titulo=Estudar Kotlin, atualizado=3000)  // versão remota é mais recente
    Tarefa(id=2, titulo=Treinar, atualizado=2000)         // só existe localmente
    Tarefa(id=3, titulo=Correr, atualizado=1500)          // só existe remotamente
    */
}
package Genericos

/**
 * EXERCÍCIO 7 — Validação em Batch
 *
 * Cenário: você tem um formulário com múltiplos campos.
 * Precisa validar todos de uma vez e retornar quais falharam + o erro específico.
 *
 * Camada: Domain — lógica de validação pura.
 *
 * Exemplo: registrar usuário. Validar email, senha, nome.
 * Se algum falhar, mostrar qual e por quê.
 */

interface Validador<T> {
    fun validar(item: T): Boolean
    fun mensagem(): String  // mensagem de erro se falhar
}

data class CampoValidacao<T>(
    val nome: String,
    val valor: T,
    val validador: Validador<T>
)

data class ErroValidacao(
    val campo: String,
    val mensagem: String
)

fun <T> List<CampoValidacao<T>>.validarTodos(): List<ErroValidacao> {


    return this.mapNotNull { campo ->
        if(!campo.validador.validar(campo.valor)){
            ErroValidacao(
                campo = campo.nome,
                mensagem = campo.validador.mensagem()
            )
        }else{
            null
        }
    }

}

fun main() {
    val emailValidador = object : Validador<String> {
        override fun validar(item: String) = item.contains("@")
        override fun mensagem() = "Email inválido"
    }

    val senhaValidador = object : Validador<String> {
        override fun validar(item: String) = item.length >= 6
        override fun mensagem() = "Senha deve ter no mínimo 6 caracteres"
    }

    val campos = listOf(
        CampoValidacao("email", "usuario", emailValidador),
        CampoValidacao("senha", "12345", senhaValidador),
        CampoValidacao("email2", "user@email.com", emailValidador)
    )

    val erros = campos.validarTodos()
    erros.forEach { println("${it.campo}: ${it.mensagem}") }

    println("=== Erros encontrados ===")
    if (erros.isEmpty()) {
        println("Nenhum erro! Todos os campos estão válidos.")
    } else {
        erros.forEach { println("${it.campo}: ${it.mensagem}") }
    }
}
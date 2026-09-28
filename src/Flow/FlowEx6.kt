package Flow

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.time.Duration.Companion.milliseconds

// Exercício 6: debounce + flatMapLatest — Orchestror, validação de e-mail em tempo real
//
// Cenário: um formulário de cadastro de contato verifica, enquanto o usuário digita
// o e-mail, se ele já está cadastrado — uma chamada assíncrona ao "servidor" (delay
// de 400ms). Não queremos verificar a cada tecla digitada, e se o usuário continuar
// digitando antes da verificação anterior terminar, a verificação antiga deve ser
// cancelada (o resultado dela já não importa mais).

sealed class StatusEmail {
    object Digitando : StatusEmail()
    data class Disponivel(val email: String) : StatusEmail()
    data class JaCadastrado(val email: String) : StatusEmail()
}

class CadastroContatoViewModel(private val scope: CoroutineScope) {

    private val emailsCadastrados = listOf(
        "adriel@goditransportes.com.br",
        "financeiro@goditransportes.com.br",
        "suporte@orchestror.com.br"
    )



    // TODO 1: guarde o status da verificação como estado interno, privado e mutável,
    //         começando em StatusEmail.Digitando
    // TODO 2: exponha esse estado publicamente, de forma somente-leitura

    private val _state = MutableStateFlow<StatusEmail>(StatusEmail.Digitando);
    val state : StateFlow<StatusEmail> = _state.asStateFlow();

    // Simula uma chamada de rede: demora 400ms e devolve se o e-mail já existe.
    private fun verificarNoServidor(email: String): Flow<StatusEmail> = flow {
        delay(400)
        if (email in emailsCadastrados) {
            emit(StatusEmail.JaCadastrado(email))
        } else {
            emit(StatusEmail.Disponivel(email))
        }
    }

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    fun observarDigitacao(emailsDigitados: Flow<String>) {
        scope.launch {
            // TODO 3: normalize cada e-mail (trim + lowercase)
            // TODO 4: aplique debounce (300ms) pra não verificar a cada tecla digitada
            // TODO 5: use flatMapLatest pra chamar verificarNoServidor(email), cancelando
            //         verificações antigas que ainda não terminaram quando um e-mail novo chegar
            // TODO 6: atualize o estado exposto com o resultado
            emailsDigitados.map {
                email -> email.trim().lowercase();
            }.debounce(300.milliseconds).flatMapLatest { value -> verificarNoServidor(value) }.collect {
                result -> _state.value = result
            };
        }
    }
}

fun main() = runBlocking {
    val viewModel = CadastroContatoViewModel(this)

    // TODO 7: observe o estado exposto (em paralelo, com launch) e imprima o status
    //         a cada mudança
    // TODO 8: lembre de cancelar essa observação antes do programa terminar

    val job = launch {
        viewModel.state.collect {
            value -> println("Resultado $value");
        }
    }

    val emailsDigitados = flow {
        emit("a")
        delay(50)
        emit("ad")
        delay(50)
        emit("adriel@goditransportes.com.br")   // usuário parou de digitar por um tempo depois desse
        delay(600)
        emit("novo@empresa.com")
        delay(600)
    }

    viewModel.observarDigitacao(emailsDigitados)
    delay(2000)
    job.cancel();
}

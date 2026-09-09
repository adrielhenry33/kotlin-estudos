package Delegates

class Rota{
    var status: String by LoggerDelegate("Pendente");
    var distancia: Int by LoggerDelegate(0);
}

fun main(){
    val rota = Rota();


    rota.status = "ATIVA"

     println(rota.status)
    // Esperado: printi "📖 Lendo: status = ATIVA" depois retornar "ATIVA"

     rota.status = "CONCLUIDA";
    // Esperado: "✍️ Escrevendo: status = CONCLUIDA (era ATIVA)"

    rota.distancia = 150
    // Esperado: Log com Int, não String
}
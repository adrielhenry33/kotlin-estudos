package Delegates

class Motorista {
    var nome: String by TransformadorDelegate("") { it.uppercase() }
    var email: String by TransformadorDelegate("") { it.lowercase() }
}

fun main() {
    val motorista = Motorista()

    motorista.nome = "joão silva"
    // Output: "🔄 nome: 'joão silva' → 'JOÃO SILVA'"

    println(motorista.nome)
    // Output: "Lendo o valor nome = JOÃO SILVA"
    //         "JOÃO SILVA"
}
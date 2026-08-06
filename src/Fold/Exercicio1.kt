package Fold

data class Transaction(val id: String, val amount: Double, val type: String)


fun main () {
    val listaTransaction = mutableListOf<Transaction>(
        Transaction("1", 20.5, "Uber"),
    Transaction("2",  12.3, "Uber"),
    Transaction("3", 31.1, "Uber"),
    Transaction("4", 22.2, "Uber"),

        );
    val amount =  listaTransaction.fold(0.0){acumulador, amount -> acumulador  + amount.amount};
    println(amount);


}
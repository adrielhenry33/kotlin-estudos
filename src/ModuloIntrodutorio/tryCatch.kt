package ModuloIntrodutorio

fun main(){
    var x = 1;
    try {
        x = 2;

    }catch (e: Exception){
        x =3 ;
    }finally {
        //roda ao final do try ou do catch
        x = 4;
    }
    println(x)

}
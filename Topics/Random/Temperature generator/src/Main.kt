import kotlin.random.Random

fun generateTemperature(seed: Int): String{
    var rez = ""
    val rangen = Random(seed)
    for (i in 1..7){
        rez += rangen.nextInt(20,31).toString() + " "
    }
    return rez.trim()
}
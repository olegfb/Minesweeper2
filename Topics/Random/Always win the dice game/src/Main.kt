import kotlin.random.Random

fun createDiceGameRandomizer(n: Int): Int {
    var rez = n
    do {
        var randomgen = Random(rez)
        var i1 = 0
        var i2 = 0
        for (i in 1..6){
            i1+= randomgen.nextInt(1,7)
        }
        for (i in 1..6) {
            i2+= randomgen.nextInt(1,7)
        }
        if (i1<i2) return rez
        rez++
    } while (true)
}
package om.trbr.s5differences

import android.os.Build
import java.io.File
import java.util.concurrent.TimeUnit

/*private fun appendHex(sb: StringBuffer, b: Byte) {
    sb.append(hex.toCharArray()[b shr 4 and 0x0f]).append(hex.toCharArray()[b and 0x0f])
}*/

/*private fun appendHex(sb: StringBuffer, b: Byte) {
    sb.append(hex.toCharArray()[b.toInt() shr 4 and 0x0f]).append(hex.toCharArray()[b.toInt() and 0x0f])
}*/

infix fun Byte.shl(that: Int): Int = this.toInt().shl(that)
infix fun Int.shl(that: Byte): Int = this.shl(that.toInt()) // Not necessary in this case because no there's (Int shl Byte)
infix fun Byte.shl(that: Byte): Int = this.toInt().shl(that.toInt()) // Not necessary in this case because no there's (Byte shl Byte)

infix fun Byte.and(that: Int): Int = this.toInt().and(that)
infix fun Int.and(that: Byte): Int = this.and(that.toInt()) // Not necessary in this case because no there's (Int and Byte)
infix fun Byte.and(that: Byte): Int = this.toInt().and(that.toInt()) // Not necessary in this case because no there's (Byte and Byte)


//о форматах вывода https://stonesoupprogramming.com/2017/11/17/kotlin-string-formatting/
infix fun Double.f(fmt: String) = "%$fmt".format(this)
infix fun Double.f(fmt: Float) = "%${if (fmt < 1) fmt + 1 else fmt}f".format(this)
//test
//val pi =3.14159265358979323
//println("""pi = ${pi f ".2f"}""")
//println("pi = ${pi f .2f}")

infix fun String.f(fmt: String) = "%$fmt".format(this)
infix fun String.f(fmt: Int) = "%${if (fmt < 1) fmt + 1 else fmt}s".format(this)
//val val = "test stroka"
//println("""pi = ${val f "20s"}""")
//println("pi = ${val f 20}")

infix fun Int.f(fmt: String) = "%$fmt".format(this)
infix fun Int.f(fmt: Int) = "%${if (fmt < 1) fmt + 1 else fmt}s".format(this)

//infix fun List<Any>.init(that : Any) = {mutableListOf(that) }
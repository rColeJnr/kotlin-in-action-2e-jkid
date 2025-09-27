package exercise

import kotlin.test.Test
import kia.jkid.deserialization.deserialize
import kia.jkid.serialization.serialize
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertFailsWith

data class BookStore(val bookPrice: Map<String, Double>)

class MapTest {
    private val bookStore = BookStore(mapOf("Catch-22" to 10.92, "The Lord of the Rings" to 11.49))
    private val json = """{"bookPrice": {"Catch-22": 10.92, "The Lord of the Rings": 11.49}}"""

    @Test fun testSerialization() {
        println(serialize(bookStore))
        assertEquals(json, serialize(bookStore))
    }

    @Test fun testDeserialization() {
        assertEquals(bookStore, deserialize(json))
    }

    @Test fun buildString() {


       val one = with("1") {
           println("ghgh$this")
            toInt()
        }
        assertEquals(1, one)
    }

    @Test fun infixCopy() {
        val str = "Could, Should, Does - Miracle" // Jim Rohn
        assertEquals("$str$str", str copy CopiadoraString())
    }

    @Test fun infixQuote() {
        val str = "Could, Should, Don't - Mistake collaso"
        assertEquals("$str - Jim Rohn", str quote CopiadoraString())
    }

    @Test fun infixIntCopy() {
        val int = 1
        assertEquals(11, int copy CopiadoraInt())
    }

    @Test fun infixIntQuote() {
        val int = 1
        assertFails { int quote CopiadoraInt() }
    }

    @Test fun `extent primitive type`() {
        val str = """
            |Isn't this good stuff?, i am telling you, this stuff
            |changed my life,
            |turned me every way, but lose.
            |It's so good i should have paid to get in.
        """.trimMargin()

        assertEquals("$str - quoting Jim Rohn", str.JimRohn)
    }

    @Test fun testReceiver() {
        val greeter = Greeter()
        greeter.missMom("Mother")
        greeter {
            missMom("Mother")
            printSomething("Ricardo") {
                isNullOrBlank()
                this.equals("Ricardo")
                JimRohn
            }
        }

       greeter {
            assertEquals( "I miss my Lumina",  missMom("Lumina"))
        }



    }

}

infix fun <T> T.quote(copiadora: Copiadora<T>) = copiadora.quoteJim(this)

infix fun <T> T.copy(copiadora: Copiadora<T>) = copiadora.copia(this)

interface Copiadora<T> {
    fun copia(value: T): T
    fun quoteJim(value: T): T
}

fun CopiadoraString(): Copiadora<String> {
    return object: Copiadora<String> {
        override fun copia(value: String): String {
            return "$value$value"
        }

        override fun quoteJim(value: String): String {
            return "$value - Jim Rohn"
        }
    }
}

fun CopiadoraInt(): Copiadora<Int> {
    return object: Copiadora<Int> {
        override fun copia(value: Int): Int {
            return "$value$value".toInt()
        }

        override fun quoteJim(value: Int): Int {
            throw AssertionError("Can't quote jim on Int type")
        }
    }
}

val String.JimRohn: String
    get() = "$this - quoting Jim Rohn"

class Greeter() {

    fun missMom(name: String): String {
        return "I miss my $name"
    }

    fun printSomething(value: String, body: String.() -> Unit) {
       print("String Operations on $this")
        body(value)
    }
    // operator invoke makes so that you can call a class, like a function, and executes the code in the invoke fun
    operator fun invoke(body: Greeter.() -> Unit) {
        body()
    }

}

inline fun <T> T.apply(block: T.() -> Unit): T {
    block()
    return this
} // returns the receiver

inline fun <T, R> with(receiver: T, block: T.() -> R): R =
    receiver.block() // returns the result of calling the lambda
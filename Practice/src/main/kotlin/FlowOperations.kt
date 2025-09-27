
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.launch
import kotlin.random.Random
import kotlin.random.nextInt
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

interface Flow<out T> {
    suspend fun collect(collector: FlowCollector<T>): T
}

interface FlowCollector<in T> {
    suspend fun emit(value: T)
}

class RadioStation {
    private val _messageFlow = MutableSharedFlow<Int>(replay = 2)
    val messageFlow = _messageFlow.asSharedFlow()

    fun beginBroadcasting(scope: CoroutineScope) {
        scope.launch {
            while (true) {
                delay(1.seconds)
                val number = Random.nextInt(0..10)
                println("$this, Emitting $number")
                _messageFlow.emit(number)
            }
        }
    }

    suspend fun getRandomNumber(): Int {
        delay(1.seconds)
        return Random.nextInt(2..25)
    }
}

class ViewCounter {
    private val _counter = MutableStateFlow(0)
    val counter = _counter.asStateFlow()

    fun increment() {
        _counter.update { it + 1 }  // over counter.value++
    }
}

fun getAllUserIds(): Flow<Int> {
    return flow {
        repeat(3) {
            delay(200.milliseconds)
            println("Emitting!")
            emit(it)
        }
    }
}

suspend fun getProfileFromNetwork(id: Int): String {
    delay(2.seconds)
    return "Profile[$id]"
}
//Out of the box, when working with a cold flow such as the one shown
//previously, the producer of values suspends its work until the collector has
//finished processing the previous element

fun <T> Flow<T>.customOperator(): Flow<T> =
    flow {
        collect {
            emit(it)
        }
    }
fun <T> Flow<T>.customBlockOperator(block: Flow<T>.(T) -> Unit): Flow<T> =
    flow{
        collect {
            block(it)
            emit(it)

        }
    }

// Flows are a coroutine-based abstraction that makes it possible to work with values that appear
// over time

While all flows in Kotlin expose a consistent set of APIs for working with
values that appear over time, in Kotlin, you distinguish between two categories
of flows: cold flows and hot flows. In brief:
Cold flows represent asynchronous data streams that only start
emitting items when their items are being consumed by an individual
collector.
Hot flows, on the other hand, produce items independently of
whether the items are actually being consumed, operating in a
broadcast fashion.


When deciding between regular cold flows and channel flows, only pick
channel flows if you need to launch new coroutines from inside your flow.
Otherwise, choosing regular cold flows is the way to go

Did you know that i didn't know what shareIn does??? So embarrassing mate. Bit Jarring 

* _Eagerly_ starts the collection of the flow immediately.
* _Lazily_ starts the collection only when the first subscriber appears.
* _WhileSubscribed_ starts the collection only when the first
subscriber appears, and then cancels the collection of the flow when
the last subscriber disappears.

How amazed would you be to learn that i also didn't know what stateIn does??? Bit Jarring

the _stateIn_ function doesn’t
provide any starting strategies. It always starts the flow in the given coroutine
scope and keeps providing its latest value to its subscribers via the value
property until the coroutine scope is cancelled

Cold flow Hot flow
Inert by default (triggered by the
collector)
Active by default
Has a collector Has multiple subscribers
Collector gets all emissions Subscribers get emissions
from the start of
subscription
Potentially completes Doesn’t complete
Emissions happen from a single
coroutine (unless channelFlow is
used).
Emissions can happen from
arbitrary coroutines.

the _take_ function is another way to cancel the
collection of a flow in a controlled manner.

runBlocking {
    flowOf(1)
        .onEach { log("A") }
        .flowOn(Dispatchers.Default)
        .onEach { log("B") }
        .flowOn(Dispatchers.IO)
        .onEach { log("C") }
        .collect()
    }
}
// 36 [DefaultDispatcher-worker-3 @coroutine#3] A
// 44 [DefaultDispatcher-worker-1 @coroutine#2] B
// 44 [main @coroutine#1] C

It’s important to note that the flowOn operator only affects the dispatcher of
the upstream flow—that is, the flow (and any intermediate operators) that come
before the invocation of flowOn . The downstream flow remains untouched,
which is why this operator is also referred to as being context preserving.
Switching the dispatcher to Dispatchers.Default only affects "A" , the
switch to Dispatchers.IO only affects "B" , and "C" is not affected by
the preceding invocations of flowOn at all.


// when one of its child
coroutines fails, no sibling coroutines are terminated, and the uncaught
exception isn’t propagated further. Instead, both the parent coroutine and the
sibling coroutines keep working

fun main(): Unit = runBlocking {
    supervisorScope {
        launch {
            try {
                while (true) {
                    println("Heartbeat!")
                    delay(500.milliseconds)
                }
            } catch (e: Exception) {
                println("Heartbeat terminated: $e")
                throw e
                }
            }
            launch {
                delay(1.seconds)
                throw UnsupportedOperationException("Ow!")
            }
        }
    }
}
Heartbeat!
Heartbeat!
Exception in thread "main" java.lang.UnsupportedOperationE
xception: Ow!
...
Heartbeat!
Heartbeat!
the SupervisorJob invokes the
CoroutineExceptionHandler for child coroutines that were started using the
launch builder

val exceptionHandler = CoroutineExceptionHandler { context
, exception ->
println("[ERROR] $exception")
}

fun main() = runBlocking {
    exceptionalFlow
    .catch { cause ->
        println("\nHandled: $cause")
        emit(-1)
    }
    .collect {
        print("$it ")
    }
}
// 0 1 2 3 4
// Handled: UnhappyFlowException
// -1

It’s important to reiterate that the catch operator only operates on its
upstream,
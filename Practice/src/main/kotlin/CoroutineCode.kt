package org.rcolejnr

import kotlinx.coroutines.*
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

/*
    This class can start and manage coroutines alongside its own life cycle
    It takes a coroutine dispatcher as a constructor
argument and uses the CoroutineScope function to create a new coroutine
scope associated with the class. The start function launches a coroutine that
keeps running and another coroutine that simply does a one-off task. The
stop function cancels the scope associated with the class and, with it, the
previously started coroutines
 */

class ComponentWithScope(dispatcher: CoroutineDispatcher = Dispatchers.Default) {
    private val scope = CoroutineScope(dispatcher + SupervisorJob())

    fun start() {
        println("$this Starting")
        scope.launch {
            while (true) {
                delay(500.milliseconds)
                println("Component working")
            }
        }
        scope.launch {
            println("Doing a task...")
            delay(500.milliseconds)
            println("Task done")
        }
    }

    fun stop() {
        println("Stopping")
        scope.cancel()
    }

}

/**
 * coroutineScope vs CoroutineScope
 * @coroutineScope - used for the concurrent decomposition of work. launches coroutines and wait for them to complete. it's a suspend function
 * @CoroutineScope - creates a scope that associates coroutines with the life cycle of a class. creates the scope, but doens't wait for operations. returns a reference to that coroutine scope.
 */

// Coroutine COntexts and structured concurrency
fun contextHierarchy() {
    runBlocking(Dispatchers.Default) {
        println(coroutineContext)
        launch {
            {
                println(coroutineContext)
                launch(Dispatchers.IO + CoroutineName("mine")) {
                    println(coroutineContext)
                }
            }()
        }
    }
}

suspend fun doWork() {
    delay(500.milliseconds)
    throw UnsupportedOperationException("I wanna see you work out for me")
}

suspend fun sumsum(): Int {
    delay(4.seconds)
    return 4
}

// Cancelation
suspend fun cancelando() {
    val sumResult = withTimeoutOrNull(1_000){ sumsum() }
    println(sumResult)

    withTimeoutOrNull(2.seconds) {
        while (true) {
            try {
                doWork()
            } catch (e: UnsupportedOperationException) {
                println("Hey, we got a good thing: ${e.message}")
//                throw e
            } catch (e: CancellationException) {
                println("Girl, won't you drop that thing down to the floor, ${e.message}")
                throw e
            }
        }
    }
}

suspend fun doCpuHeavyWork(): Int {
    var counter = 0
    val startTime = System.currentTimeMillis()
    while (System.currentTimeMillis() < startTime + 500) {
        counter++
        yield()
    }
    return counter
}
//When you cancel a coroutine, all of its child coroutines are also cancelled.

/**
 * You use isActive to check if a coroutine is still active. if(!isActive) return@launch
 * you use ensureActive(), if the coroutine is no longer active, this function throws a CancellationException.
 * yield() - it's a suspending function that introduces a point in your code where a
 * cancellationException can occur, and also allows the dispatcher to switch to working on a
 * different coroutine
 */
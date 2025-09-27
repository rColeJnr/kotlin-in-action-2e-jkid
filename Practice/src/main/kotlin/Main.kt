package org.rcolejnr

import RadioStation
import customBlockOperator
import customOperator
import getAllUserIds
import getProfileFromNetwork
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import midLevelHandler
import topLevelHandler

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
fun main() {
    val name = "Kotlin"
    //TIP Press <shortcut actionId="ShowIntentionActions"/> with your caret at the highlighted text
    // to see how IntelliJ IDEA suggests fixing it.
    println("Hello, " + name + "!")

    GlobalScope.launch(topLevelHandler) {
        launch(midLevelHandler) {
            try {
                throw UnsupportedOperationException("Zara")
            } catch(e: UnsupportedOperationException) {
                throw UnsupportedOperationException("Larson")
                println("I catched, but it was rethrowed: ${e.message}")
            }
        }
    }

//    contextHierarchy()

   // runBlocking {
//        cancelando()

//        launch {
//            repeat(3) {
//                println("cpu heavy work 1")
//                doCpuHeavyWork()
//            }
//        }
//        launch {
//            repeat(3) {
//                println("cpu heavy work 2")
//                doCpuHeavyWork()
//            }
//        }
        //cpu heavy work 1
        //cpu heavy work 1
        //cpu heavy work 1
        //cpu heavy work 2
        //cpu heavy work 2
        //cpu heavy work 2
        // without suspension point in doCpuHeavyWork() implementation the first coroutine
        // runs to completion before the second even begins to run
        // Why? Because any suspension points in the body of your coroutine, there's never an
        // opportunity for the underlying coroutines machinery to pause execution of 1 and
        // start exec of 2
        // WITH YIELD()
        //cpu heavy work 1
        //cpu heavy work 2
        //cpu heavy work 1
        //cpu heavy work 2
        //cpu heavy work 1
        //cpu heavy work 2
   // }

//    The
//root coroutine suspends when you call await , until a value is available.
//    runBlocking {
//        println("Starting async computation")
//        val firstDeffered: Deferred<Int> = async { slowlyAddNumbers(4, 4) }
//        val secondDeffered: Deferred<Int> = async { slowlyAddNumbers(8, 4) }
//
//        println("waiting for deferred values")
//        println("deffered1: ${firstDeffered.await()}")
//        println("deffered1 completed")
//        println("deffered2: ${secondDeffered.await()}")
//    }
//    runBlocking {
//        var x = 0
//        repeat(10_000) {
//            launch(Dispatchers.Default) {
//                x++
//            }
//        }
//        delay(1.seconds)
//        println(x)
//    }
//    running()
//}
//
//fun running() {
//    runBlocking {
//        val mutex = Mutex()
//        var x = 0
//        repeat(10_000) {
//            launch(Dispatchers.Default) {
//                mutex.withLock {
//                    x++
//                }
//            }
//        }
//        delay(1.seconds)
//        println(x)
//    }
// 10000

    val radio = RadioStation()
    val randomNumber = flow {
        repeat(5){
            emit(radio.getRandomNumber())
        }
    }
//    runBlocking {
//        randomNumber
//            .take(5)
//            .onCompletion { cause ->
//                if (cause != null) {
//                    println("Went wrong: $cause")
//                } else {
//                    println("Completed")
//                }
//            }
//            .collect {
//                println(it)
//            }
//        launch{
//            radio.getRandomNumber()
//            println(randomNumber.collect {
//                println(it)
//            })
//        }
//        launch{
//            radio.beginBroadcasting(this)
//            delay(3.seconds)
//            radio.messageFlow.collect {
//                println("Collecitong: $it")
//            }
//        }
//
//            /*
//            flow
//            .onEmpty {
//                println("nothing")
//            }
//            .onStart {
//                println("Starting")
//            }
//            .onEach {
//                println("Bit Jarring")
//            }
//            .collect()
//            */
//    }

val ids = getAllUserIds()
    runBlocking {
        ids
            .buffer(onBufferOverflow = BufferOverflow.SUSPEND)
            .customOperator()
            .customBlockOperator {
                buffer(3)
                println(it)
            }
            .map { getProfileFromNetwork(it) }
            .collect { println("Got it $it") }
    }
}

suspend fun slowlyAddNumbers(a: Int, b: Int): Int {
    println("slowing adding $a to $b")
    delay(4_000 / a.toLong())
    println("computing: $a to $b")
    return a + b
}



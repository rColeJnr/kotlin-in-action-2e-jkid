import kotlinx.coroutines.CoroutineExceptionHandler

val topLevelHandler = CoroutineExceptionHandler { _, e ->
    println("[TOP] ${e.message}")
    throw e
}
val midLevelHandler = CoroutineExceptionHandler { _, e ->
    println("[MID] ${e.message}")
}


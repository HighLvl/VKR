import kotlinx.coroutines.*
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn


fun main() {

    runBlocking {
        flow {
            (1..100).forEach {
                emit(it)
                println("$it emitted")
                delay(100)
            }
        }.flowOn(Dispatchers.Default).collect {
            delay(10000)
            println("$it consumed")
        }
    }
//
//    runBlocking {
//        launch {
//            delay(1000)
//            launch {
//                flow.emit(1)
//                println("1 emitted")
//            }
//            launch {
//                flow.emit(2)
//                println("2 emitted")
//            }
//            launch {
//                flow.emit(3)
//                println("3 emitted")
//            }
//            launch {
//                flow.emit(4)
//                println("4 emitted")
//            }
//            println("wtf")
//        }
//        launch {
//            flow.collect {
//                println("FirstCoroutine $it")// false, true, false
//            }
//        }
//
//        launch {
//            flow.collect {
//                println("SecondCoroutine $it")// false, true, false
//                delay(10000)
//            }
//        }
//
//
//        launch {
//            flow.collect {
//                // false, true, false
//                delay(200000)
//                    println("ThirdCoroutine $it")
//                }
//
//        }
//    }
}
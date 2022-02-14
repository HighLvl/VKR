package api

class Client {
//    class HelloWorldClient(
//        private val channel: ManagedChannel
//    ) : Closeable {
//        private val stub = ModelAPIGrpcKt.ModelAPICoroutineStub(channel)
//
//        suspend fun greet(name: String) {
//            val request =
//            val response = stub.requestSnapshot(Empty.getDefaultInstance())
//            val snapshot = Snapshot.getRootAsSnapshot(response.body.asReadOnlyByteBuffer())
//            println("Received: ${}")
//            val againResponse = stub.sayHelloAgain(request)
//            println("Received: ${againResponse.message}")
//        }
//
//        override fun close() {
//            channel.shutdown().awaitTermination(5, TimeUnit.SECONDS)
//        }
//    }
}
package app.api

import APIGrpcKt
import Model
import app.api.base.AgentModelApi
import app.api.dto.Requests
import app.api.dto.Responses
import com.google.protobuf.kotlin.toByteString
import io.grpc.ManagedChannelBuilder
import io.ktor.utils.io.core.*

class ApiImpl : AgentModelApi {
    private var apiClient: ApiClient? = null

    override suspend fun connect(ip: String, port: Int) {
        apiClient = ApiClient(ip, port)
    }

    override fun disconnect() {
        apiClient?.close()
        apiClient = null
    }

    override suspend fun handleRequests(requests: Requests): Responses {
        return apiClient!!.getSnapshot(requests)
    }

    private class ApiClient(
        ip: String, port: Int
    ) : Closeable {
        private val channel =
            ManagedChannelBuilder.forAddress(ip, port)
                .usePlaintext()
                .build()
        private val stub = APIGrpcKt.APICoroutineStub(channel)

        suspend fun getSnapshot(requests: Requests): Responses {
            val bytes = requests.mapToBytes()
            val protoInputData = Model.Requests.newBuilder().setData(bytes.toByteString()).build()
            return stub.handleRequests(protoInputData).data.toByteArray().mapToSnapshot()
        }

        override fun close() {
            kotlin.runCatching {
                channel.shutdownNow()
            }
        }
    }
}


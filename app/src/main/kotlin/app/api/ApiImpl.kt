package app.api

import APIGrpcKt
import Model
import app.api.base.AgentModelApi
import app.api.dto.InputData
import app.api.dto.Snapshot
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

    override suspend fun getSnapshot(inputData: InputData): Snapshot {
        return apiClient!!.getSnapshot(inputData)
    }

    private class ApiClient(
        ip: String, port: Int
    ) : Closeable {
        private val channel =
            ManagedChannelBuilder.forAddress(ip, port)
                .usePlaintext()
                .build()
        private val stub = APIGrpcKt.APICoroutineStub(channel)

        suspend fun getSnapshot(inputData: InputData): Snapshot {
            val bytes = inputData.mapToBytes()
            val protoInputData = Model.InputData.newBuilder().setData(bytes.toByteString()).build()
            return stub.getSnapshot(protoInputData).data.toByteArray().mapToSnapshot()
        }

        override fun close() {
            channel.shutdownNow()
        }
    }
}


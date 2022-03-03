package app.api

import Model.APIGrpc
import Model.Empty
import com.google.flatbuffers.FlatBufferBuilder
import core.api.AgentModelApiClient
import core.api.dto.Behaviour
import core.api.dto.GlobalArgs
import core.api.dto.Snapshot
import core.api.dto.State
import io.grpc.ManagedChannelBuilder
import kotlinx.coroutines.flow.Flow
import java.io.Closeable
import java.util.concurrent.TimeUnit

class ApiClientImpl : AgentModelApiClient {
    private var apiClient: ApiClient? = null

    override suspend fun connect(ip: String, port: Int): State {
        apiClient = ApiClient(ip, port)
        return apiClient!!.getState()
    }

    override suspend fun disconnect() {
        try {
            apiClient!!.close()
        } finally {
            apiClient = null
        }
    }

    override suspend fun run(globalArgs: GlobalArgs) {
        apiClient!!.run(globalArgs)
    }

    override suspend fun runAndSubscribeOnUpdate(globalArgs: GlobalArgs): Flow<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun callBehaviourFunctions(behaviour: Behaviour) {
        apiClient!!.callBehaviourFunctions(behaviour)
    }

    override suspend fun requestSnapshot(): Snapshot {
        return apiClient!!.requestSnapshot()
    }

    override suspend fun pause() {
        apiClient!!.pause()
    }

    override suspend fun resume() {
        apiClient!!.resume()
    }

    override suspend fun stop() {
        apiClient!!.stop()
    }

    private class ApiClient(
        ip: String, port: Int
    ) : Closeable {
        private val channel = ManagedChannelBuilder.forAddress(ip, port).usePlaintext().build()
        private val stub = APIGrpc.newBlockingStub(channel)
        private val empty by lazy {
            val builder = FlatBufferBuilder()
            Empty.startEmpty(builder)
            val emptyOffset = Empty.endEmpty(builder)
            builder.finish(emptyOffset)
            Empty.getRootAsEmpty(builder.dataBuffer())
        }

        fun getState(): State {
            val modelState = stub.getState(empty)
            return modelState.mapToStateTable()
        }

        fun run(globalArgs: GlobalArgs) {
            val globalArgsTable = globalArgs.mapToGlobalArgsTable()
            stub.run(globalArgsTable)
        }

        fun callBehaviourFunctions(behaviour: Behaviour) {
            val behaviourTable = behaviour.mapToBehaviourTable()
            stub.callBehaviourFunctions(behaviourTable)
        }

        fun requestSnapshot(): Snapshot {
            val snapshotTable = stub.requestSnapshot(empty)
            return snapshotTable.mapToSnapshot()
        }

        fun pause() {
            stub.pause(empty)
        }

        fun resume() {
            stub.resume(empty)
        }

        fun stop() {
            stub.stop(empty)
        }

        override fun close() {
            channel.shutdown()
        }
    }
}


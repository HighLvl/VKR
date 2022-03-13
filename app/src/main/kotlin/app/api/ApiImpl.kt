package app.api

import Model.APIGrpc
import Model.Empty
import app.api.base.AgentModelApi
import app.api.dto.Behaviour
import app.api.dto.GlobalArgs
import app.api.dto.Snapshot
import app.api.dto.State
import com.google.flatbuffers.FlatBufferBuilder
import io.grpc.ManagedChannelBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.io.Closeable

class ApiImpl : AgentModelApi {
    private var apiClient: ApiClient? = null

    override suspend fun connect(ip: String, port: Int): State = withContext(Dispatchers.IO) {
        apiClient = ApiClient(ip, port)
        apiClient!!.getState()
    }

    override fun disconnect() {
        apiClient?.close()
        apiClient = null
    }

    override suspend fun run(globalArgs: GlobalArgs) = withContext(Dispatchers.IO) {
        apiClient!!.run(globalArgs)
    }

    override suspend fun runAndSubscribeOnUpdate(globalArgs: GlobalArgs): Flow<Unit> = withContext(Dispatchers.IO) {
        TODO("Not yet implemented")
    }

    override suspend fun callBehaviourFunctions(behaviour: Behaviour) = withContext(Dispatchers.IO) {
        apiClient!!.callBehaviourFunctions(behaviour)
    }

    override suspend fun requestSnapshot(): Snapshot = withContext(Dispatchers.IO) {
        apiClient!!.requestSnapshot()
    }

    override suspend fun pause() = withContext(Dispatchers.IO) {
        apiClient!!.pause()
    }

    override suspend fun resume() = withContext(Dispatchers.IO) {
        apiClient!!.resume()
    }

    override suspend fun stop() = withContext(Dispatchers.IO) {
        apiClient!!.stop()
    }

    private class ApiClient(
        ip: String, port: Int
    ) : Closeable {
        private val channel =
            ManagedChannelBuilder.forAddress(ip, port)
                .usePlaintext()
                .build()
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
            channel.shutdownNow()
        }
    }
}


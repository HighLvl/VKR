package app.api

import APIGrpcKt
import Model
import app.api.base.AgentModelApi
import app.api.dto.Requests
import app.api.dto.Responses
import com.google.protobuf.kotlin.toByteString
import io.grpc.*
import io.grpc.Metadata.ASCII_STRING_MARSHALLER
import java.io.Closeable
import java.util.concurrent.Executor


class ApiImpl(private val ip: String, private val port: Int, private val token: String = "") : AgentModelApi {
    private var apiClient: ApiClient? = null

    override suspend fun connect() {
        if (apiClient != null) {
            throw IllegalStateException("Connection is not closed")
        }
        apiClient = when {
            token.isBlank() -> UnsecuredApiClient(ip, port)
            else -> SecuredApiClient(ip, port, token)
        }
    }

    override fun disconnect() {
        apiClient?.close()
        apiClient = null
    }

    override suspend fun handleRequests(requests: Requests): Responses {
        return apiClient!!.getSnapshot(requests)
    }

    private class SecuredApiClient(ip: String, port: Int, token: String) : ApiClient() {
        override val channel: ManagedChannel = createChannel(ip, port)
        override val stub: APIGrpcKt.APICoroutineStub =
            APIGrpcKt.APICoroutineStub(channel).withCallCredentials(BearerToken(token))


        private companion object {
            fun createChannel(ip: String, port: Int): ManagedChannel {
                return Grpc.newChannelBuilderForAddress(ip, port, TlsChannelCredentials.create())
                    .maxInboundMessageSize(Int.MAX_VALUE)
                    .build()
            }

            private class BearerToken(private val token: String) : CallCredentials() {
                override fun applyRequestMetadata(
                    requestInfo: RequestInfo,
                    appExecutor: Executor,
                    applier: MetadataApplier
                ) {
                    appExecutor.execute {
                        try {
                            val headers = Metadata()
                            headers.put(
                                AUTHORIZATION_METADATA_KEY,
                                java.lang.String.format("%s %s", BEARER_TYPE, token)
                            )
                            applier.apply(headers)
                        } catch (e: Throwable) {
                            applier.fail(Status.UNAUTHENTICATED.withCause(e))
                        }
                    }
                }

                override fun thisUsesUnstableApi() {}

                private companion object {
                    const val BEARER_TYPE = "Bearer"
                    val AUTHORIZATION_METADATA_KEY: Metadata.Key<String> =
                        Metadata.Key.of("Authorization", ASCII_STRING_MARSHALLER)
                }
            }
        }
    }

    private class UnsecuredApiClient(
        ip: String, port: Int
    ) : ApiClient() {
        override val channel: ManagedChannel = createChannel(ip, port)
        override val stub: APIGrpcKt.APICoroutineStub = APIGrpcKt.APICoroutineStub(channel)

        private companion object {
            fun createChannel(ip: String, port: Int): ManagedChannel {
                return ManagedChannelBuilder.forAddress(ip, port)
                    .usePlaintext()
                    .maxInboundMessageSize(Int.MAX_VALUE)
                    .build()
            }
        }
    }

    private abstract class ApiClient : Closeable {
        protected abstract val stub: APIGrpcKt.APICoroutineStub
        protected abstract val channel: ManagedChannel

        suspend fun getSnapshot(requests: Requests, metadata: Metadata = Metadata()): Responses {
            val bytes = requests.mapToBytes()
            val protoInputData = Model.Requests.newBuilder().setData(bytes.toByteString()).build()
            return stub.handleRequests(protoInputData, metadata).data.toByteArray().mapToSnapshot()
        }

        override fun close() {
            kotlin.runCatching {
                channel.shutdownNow()
            }
        }
    }
}


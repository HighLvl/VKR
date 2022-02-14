package api
import com.google.protobuf.Empty
import io.grpc.CallOptions
import io.grpc.CallOptions.DEFAULT
import io.grpc.Channel
import io.grpc.Metadata
import io.grpc.MethodDescriptor
import io.grpc.ServerServiceDefinition
import io.grpc.ServerServiceDefinition.builder
import io.grpc.ServiceDescriptor
import io.grpc.Status
import io.grpc.Status.UNIMPLEMENTED
import io.grpc.StatusException
import io.grpc.kotlin.AbstractCoroutineServerImpl
import io.grpc.kotlin.AbstractCoroutineStub
import io.grpc.kotlin.ClientCalls.serverStreamingRpc
import io.grpc.kotlin.ClientCalls.unaryRpc
import io.grpc.kotlin.ServerCalls.serverStreamingServerMethodDefinition
import io.grpc.kotlin.ServerCalls.unaryServerMethodDefinition
import io.grpc.kotlin.StubFor
import kotlin.String
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic
import kotlinx.coroutines.flow.Flow

/**
 * Holder for Kotlin coroutine-based client and server APIs for ModelAPI.
 */
object ModelAPIGrpcKt {
  const val SERVICE_NAME: String = ModelAPIGrpc.SERVICE_NAME

  @JvmStatic
  val serviceDescriptor: ServiceDescriptor
    get() = ModelAPIGrpc.getServiceDescriptor()

  val runMethod: MethodDescriptor<ModelAPIOuterClass.Bytes, ModelAPIOuterClass.Bytes>
    @JvmStatic
    get() = ModelAPIGrpc.getRunMethod()

  val runAndSubscribeOnUpdateMethod: MethodDescriptor<ModelAPIOuterClass.Bytes,
      ModelAPIOuterClass.Event>
    @JvmStatic
    get() = ModelAPIGrpc.getRunAndSubscribeOnUpdateMethod()

  val callBehaviourFunctionsMethod: MethodDescriptor<ModelAPIOuterClass.Bytes, Empty>
    @JvmStatic
    get() = ModelAPIGrpc.getCallBehaviourFunctionsMethod()

  val requestSnapshotMethod: MethodDescriptor<Empty, ModelAPIOuterClass.Bytes>
    @JvmStatic
    get() = ModelAPIGrpc.getRequestSnapshotMethod()

  val pauseMethod: MethodDescriptor<Empty, Empty>
    @JvmStatic
    get() = ModelAPIGrpc.getPauseMethod()

  val continueMethod: MethodDescriptor<Empty, Empty>
    @JvmStatic
    get() = ModelAPIGrpc.getContinueMethod()

  val stopMethod: MethodDescriptor<Empty, Empty>
    @JvmStatic
    get() = ModelAPIGrpc.getStopMethod()

  /**
   * A stub for issuing RPCs to a(n) ModelAPI service as suspending coroutines.
   */
  @StubFor(ModelAPIGrpc::class)
  class ModelAPICoroutineStub @JvmOverloads constructor(
    channel: Channel,
    callOptions: CallOptions = DEFAULT
  ) : AbstractCoroutineStub<ModelAPICoroutineStub>(channel, callOptions) {
    override fun build(channel: Channel, callOptions: CallOptions): ModelAPICoroutineStub =
        ModelAPICoroutineStub(channel, callOptions)

    /**
     * Executes this RPC and returns the response message, suspending until the RPC completes
     * with [`Status.OK`][Status].  If the RPC completes with another status, a corresponding
     * [StatusException] is thrown.  If this coroutine is cancelled, the RPC is also cancelled
     * with the corresponding exception as a cause.
     *
     * @param request The request message to send to the server.
     *
     * @param headers Metadata to attach to the request.  Most users will not need this.
     *
     * @return The single response from the server.
     */
    suspend fun run(request: ModelAPIOuterClass.Bytes, headers: Metadata = Metadata()):
        ModelAPIOuterClass.Bytes = unaryRpc(
      channel,
      ModelAPIGrpc.getRunMethod(),
      request,
      callOptions,
      headers
    )
    /**
     * Returns a [Flow] that, when collected, executes this RPC and emits responses from the
     * server as they arrive.  That flow finishes normally if the server closes its response with
     * [`Status.OK`][Status], and fails by throwing a [StatusException] otherwise.  If
     * collecting the flow downstream fails exceptionally (including via cancellation), the RPC
     * is cancelled with that exception as a cause.
     *
     * @param request The request message to send to the server.
     *
     * @param headers Metadata to attach to the request.  Most users will not need this.
     *
     * @return A flow that, when collected, emits the responses from the server.
     */
    fun runAndSubscribeOnUpdate(request: ModelAPIOuterClass.Bytes, headers: Metadata = Metadata()):
        Flow<ModelAPIOuterClass.Event> = serverStreamingRpc(
      channel,
      ModelAPIGrpc.getRunAndSubscribeOnUpdateMethod(),
      request,
      callOptions,
      headers
    )
    /**
     * Executes this RPC and returns the response message, suspending until the RPC completes
     * with [`Status.OK`][Status].  If the RPC completes with another status, a corresponding
     * [StatusException] is thrown.  If this coroutine is cancelled, the RPC is also cancelled
     * with the corresponding exception as a cause.
     *
     * @param request The request message to send to the server.
     *
     * @param headers Metadata to attach to the request.  Most users will not need this.
     *
     * @return The single response from the server.
     */
    suspend fun callBehaviourFunctions(request: ModelAPIOuterClass.Bytes, headers: Metadata =
        Metadata()): Empty = unaryRpc(
      channel,
      ModelAPIGrpc.getCallBehaviourFunctionsMethod(),
      request,
      callOptions,
      headers
    )
    /**
     * Executes this RPC and returns the response message, suspending until the RPC completes
     * with [`Status.OK`][Status].  If the RPC completes with another status, a corresponding
     * [StatusException] is thrown.  If this coroutine is cancelled, the RPC is also cancelled
     * with the corresponding exception as a cause.
     *
     * @param request The request message to send to the server.
     *
     * @param headers Metadata to attach to the request.  Most users will not need this.
     *
     * @return The single response from the server.
     */
    suspend fun requestSnapshot(request: Empty, headers: Metadata = Metadata()):
        ModelAPIOuterClass.Bytes = unaryRpc(
      channel,
      ModelAPIGrpc.getRequestSnapshotMethod(),
      request,
      callOptions,
      headers
    )
    /**
     * Executes this RPC and returns the response message, suspending until the RPC completes
     * with [`Status.OK`][Status].  If the RPC completes with another status, a corresponding
     * [StatusException] is thrown.  If this coroutine is cancelled, the RPC is also cancelled
     * with the corresponding exception as a cause.
     *
     * @param request The request message to send to the server.
     *
     * @param headers Metadata to attach to the request.  Most users will not need this.
     *
     * @return The single response from the server.
     */
    suspend fun pause(request: Empty, headers: Metadata = Metadata()): Empty = unaryRpc(
      channel,
      ModelAPIGrpc.getPauseMethod(),
      request,
      callOptions,
      headers
    )
    /**
     * Executes this RPC and returns the response message, suspending until the RPC completes
     * with [`Status.OK`][Status].  If the RPC completes with another status, a corresponding
     * [StatusException] is thrown.  If this coroutine is cancelled, the RPC is also cancelled
     * with the corresponding exception as a cause.
     *
     * @param request The request message to send to the server.
     *
     * @param headers Metadata to attach to the request.  Most users will not need this.
     *
     * @return The single response from the server.
     */
    suspend fun `continue`(request: Empty, headers: Metadata = Metadata()): Empty = unaryRpc(
      channel,
      ModelAPIGrpc.getContinueMethod(),
      request,
      callOptions,
      headers
    )
    /**
     * Executes this RPC and returns the response message, suspending until the RPC completes
     * with [`Status.OK`][Status].  If the RPC completes with another status, a corresponding
     * [StatusException] is thrown.  If this coroutine is cancelled, the RPC is also cancelled
     * with the corresponding exception as a cause.
     *
     * @param request The request message to send to the server.
     *
     * @param headers Metadata to attach to the request.  Most users will not need this.
     *
     * @return The single response from the server.
     */
    suspend fun stop(request: Empty, headers: Metadata = Metadata()): Empty = unaryRpc(
      channel,
      ModelAPIGrpc.getStopMethod(),
      request,
      callOptions,
      headers
    )}

  /**
   * Skeletal implementation of the ModelAPI service based on Kotlin coroutines.
   */
  abstract class ModelAPICoroutineImplBase(
    coroutineContext: CoroutineContext = EmptyCoroutineContext
  ) : AbstractCoroutineServerImpl(coroutineContext) {
    /**
     * Returns the response to an RPC for ModelAPI.Run.
     *
     * If this method fails with a [StatusException], the RPC will fail with the corresponding
     * [Status].  If this method fails with a [java.util.concurrent.CancellationException], the RPC
     * will fail
     * with status `Status.CANCELLED`.  If this method fails for any other reason, the RPC will
     * fail with `Status.UNKNOWN` with the exception as a cause.
     *
     * @param request The request from the client.
     */
    open suspend fun run(request: ModelAPIOuterClass.Bytes): ModelAPIOuterClass.Bytes = throw
        StatusException(UNIMPLEMENTED.withDescription("Method ModelAPI.Run is unimplemented"))

    /**
     * Returns a [Flow] of responses to an RPC for ModelAPI.RunAndSubscribeOnUpdate.
     *
     * If creating or collecting the returned flow fails with a [StatusException], the RPC
     * will fail with the corresponding [Status].  If it fails with a
     * [java.util.concurrent.CancellationException], the RPC will fail with status
     * `Status.CANCELLED`.  If creating
     * or collecting the returned flow fails for any other reason, the RPC will fail with
     * `Status.UNKNOWN` with the exception as a cause.
     *
     * @param request The request from the client.
     */
    open fun runAndSubscribeOnUpdate(request: ModelAPIOuterClass.Bytes):
        Flow<ModelAPIOuterClass.Event> = throw
        StatusException(UNIMPLEMENTED.withDescription("Method ModelAPI.RunAndSubscribeOnUpdate is unimplemented"))

    /**
     * Returns the response to an RPC for ModelAPI.CallBehaviourFunctions.
     *
     * If this method fails with a [StatusException], the RPC will fail with the corresponding
     * [Status].  If this method fails with a [java.util.concurrent.CancellationException], the RPC
     * will fail
     * with status `Status.CANCELLED`.  If this method fails for any other reason, the RPC will
     * fail with `Status.UNKNOWN` with the exception as a cause.
     *
     * @param request The request from the client.
     */
    open suspend fun callBehaviourFunctions(request: ModelAPIOuterClass.Bytes): Empty = throw
        StatusException(UNIMPLEMENTED.withDescription("Method ModelAPI.CallBehaviourFunctions is unimplemented"))

    /**
     * Returns the response to an RPC for ModelAPI.RequestSnapshot.
     *
     * If this method fails with a [StatusException], the RPC will fail with the corresponding
     * [Status].  If this method fails with a [java.util.concurrent.CancellationException], the RPC
     * will fail
     * with status `Status.CANCELLED`.  If this method fails for any other reason, the RPC will
     * fail with `Status.UNKNOWN` with the exception as a cause.
     *
     * @param request The request from the client.
     */
    open suspend fun requestSnapshot(request: Empty): ModelAPIOuterClass.Bytes = throw
        StatusException(UNIMPLEMENTED.withDescription("Method ModelAPI.RequestSnapshot is unimplemented"))

    /**
     * Returns the response to an RPC for ModelAPI.Pause.
     *
     * If this method fails with a [StatusException], the RPC will fail with the corresponding
     * [Status].  If this method fails with a [java.util.concurrent.CancellationException], the RPC
     * will fail
     * with status `Status.CANCELLED`.  If this method fails for any other reason, the RPC will
     * fail with `Status.UNKNOWN` with the exception as a cause.
     *
     * @param request The request from the client.
     */
    open suspend fun pause(request: Empty): Empty = throw
        StatusException(UNIMPLEMENTED.withDescription("Method ModelAPI.Pause is unimplemented"))

    /**
     * Returns the response to an RPC for ModelAPI.Continue.
     *
     * If this method fails with a [StatusException], the RPC will fail with the corresponding
     * [Status].  If this method fails with a [java.util.concurrent.CancellationException], the RPC
     * will fail
     * with status `Status.CANCELLED`.  If this method fails for any other reason, the RPC will
     * fail with `Status.UNKNOWN` with the exception as a cause.
     *
     * @param request The request from the client.
     */
    open suspend fun `continue`(request: Empty): Empty = throw
        StatusException(UNIMPLEMENTED.withDescription("Method ModelAPI.Continue is unimplemented"))

    /**
     * Returns the response to an RPC for ModelAPI.Stop.
     *
     * If this method fails with a [StatusException], the RPC will fail with the corresponding
     * [Status].  If this method fails with a [java.util.concurrent.CancellationException], the RPC
     * will fail
     * with status `Status.CANCELLED`.  If this method fails for any other reason, the RPC will
     * fail with `Status.UNKNOWN` with the exception as a cause.
     *
     * @param request The request from the client.
     */
    open suspend fun stop(request: Empty): Empty = throw
        StatusException(UNIMPLEMENTED.withDescription("Method ModelAPI.Stop is unimplemented"))

    final override fun bindService(): ServerServiceDefinition =
        builder(ModelAPIGrpc.getServiceDescriptor())
      .addMethod(unaryServerMethodDefinition(
      context = this.context,
      descriptor = ModelAPIGrpc.getRunMethod(),
      implementation = ::run
    ))
      .addMethod(serverStreamingServerMethodDefinition(
      context = this.context,
      descriptor = ModelAPIGrpc.getRunAndSubscribeOnUpdateMethod(),
      implementation = ::runAndSubscribeOnUpdate
    ))
      .addMethod(unaryServerMethodDefinition(
      context = this.context,
      descriptor = ModelAPIGrpc.getCallBehaviourFunctionsMethod(),
      implementation = ::callBehaviourFunctions
    ))
      .addMethod(unaryServerMethodDefinition(
      context = this.context,
      descriptor = ModelAPIGrpc.getRequestSnapshotMethod(),
      implementation = ::requestSnapshot
    ))
      .addMethod(unaryServerMethodDefinition(
      context = this.context,
      descriptor = ModelAPIGrpc.getPauseMethod(),
      implementation = ::pause
    ))
      .addMethod(unaryServerMethodDefinition(
      context = this.context,
      descriptor = ModelAPIGrpc.getContinueMethod(),
      implementation = ::`continue`
    ))
      .addMethod(unaryServerMethodDefinition(
      context = this.context,
      descriptor = ModelAPIGrpc.getStopMethod(),
      implementation = ::stop
    )).build()
  }
}

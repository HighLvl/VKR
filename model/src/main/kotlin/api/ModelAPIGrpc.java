package api;
import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.43.0)",
    comments = "Source: ModelAPI.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class ModelAPIGrpc {

  private ModelAPIGrpc() {}

  public static final String SERVICE_NAME = "ModelAPI";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<ModelAPIOuterClass.Bytes,
      ModelAPIOuterClass.Bytes> getRunMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "Run",
      requestType = ModelAPIOuterClass.Bytes.class,
      responseType = ModelAPIOuterClass.Bytes.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<ModelAPIOuterClass.Bytes,
      ModelAPIOuterClass.Bytes> getRunMethod() {
    io.grpc.MethodDescriptor<ModelAPIOuterClass.Bytes, ModelAPIOuterClass.Bytes> getRunMethod;
    if ((getRunMethod = ModelAPIGrpc.getRunMethod) == null) {
      synchronized (ModelAPIGrpc.class) {
        if ((getRunMethod = ModelAPIGrpc.getRunMethod) == null) {
          ModelAPIGrpc.getRunMethod = getRunMethod =
              io.grpc.MethodDescriptor.<ModelAPIOuterClass.Bytes, ModelAPIOuterClass.Bytes>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "Run"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  ModelAPIOuterClass.Bytes.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  ModelAPIOuterClass.Bytes.getDefaultInstance()))
              .setSchemaDescriptor(new ModelAPIMethodDescriptorSupplier("Run"))
              .build();
        }
      }
    }
    return getRunMethod;
  }

  private static volatile io.grpc.MethodDescriptor<ModelAPIOuterClass.Bytes,
      ModelAPIOuterClass.Event> getRunAndSubscribeOnUpdateMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "RunAndSubscribeOnUpdate",
      requestType = ModelAPIOuterClass.Bytes.class,
      responseType = ModelAPIOuterClass.Event.class,
      methodType = io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
  public static io.grpc.MethodDescriptor<ModelAPIOuterClass.Bytes,
      ModelAPIOuterClass.Event> getRunAndSubscribeOnUpdateMethod() {
    io.grpc.MethodDescriptor<ModelAPIOuterClass.Bytes, ModelAPIOuterClass.Event> getRunAndSubscribeOnUpdateMethod;
    if ((getRunAndSubscribeOnUpdateMethod = ModelAPIGrpc.getRunAndSubscribeOnUpdateMethod) == null) {
      synchronized (ModelAPIGrpc.class) {
        if ((getRunAndSubscribeOnUpdateMethod = ModelAPIGrpc.getRunAndSubscribeOnUpdateMethod) == null) {
          ModelAPIGrpc.getRunAndSubscribeOnUpdateMethod = getRunAndSubscribeOnUpdateMethod =
              io.grpc.MethodDescriptor.<ModelAPIOuterClass.Bytes, ModelAPIOuterClass.Event>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "RunAndSubscribeOnUpdate"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  ModelAPIOuterClass.Bytes.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  ModelAPIOuterClass.Event.getDefaultInstance()))
              .setSchemaDescriptor(new ModelAPIMethodDescriptorSupplier("RunAndSubscribeOnUpdate"))
              .build();
        }
      }
    }
    return getRunAndSubscribeOnUpdateMethod;
  }

  private static volatile io.grpc.MethodDescriptor<ModelAPIOuterClass.Bytes,
      com.google.protobuf.Empty> getCallBehaviourFunctionsMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "CallBehaviourFunctions",
      requestType = ModelAPIOuterClass.Bytes.class,
      responseType = com.google.protobuf.Empty.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<ModelAPIOuterClass.Bytes,
      com.google.protobuf.Empty> getCallBehaviourFunctionsMethod() {
    io.grpc.MethodDescriptor<ModelAPIOuterClass.Bytes, com.google.protobuf.Empty> getCallBehaviourFunctionsMethod;
    if ((getCallBehaviourFunctionsMethod = ModelAPIGrpc.getCallBehaviourFunctionsMethod) == null) {
      synchronized (ModelAPIGrpc.class) {
        if ((getCallBehaviourFunctionsMethod = ModelAPIGrpc.getCallBehaviourFunctionsMethod) == null) {
          ModelAPIGrpc.getCallBehaviourFunctionsMethod = getCallBehaviourFunctionsMethod =
              io.grpc.MethodDescriptor.<ModelAPIOuterClass.Bytes, com.google.protobuf.Empty>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "CallBehaviourFunctions"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  ModelAPIOuterClass.Bytes.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.google.protobuf.Empty.getDefaultInstance()))
              .setSchemaDescriptor(new ModelAPIMethodDescriptorSupplier("CallBehaviourFunctions"))
              .build();
        }
      }
    }
    return getCallBehaviourFunctionsMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.google.protobuf.Empty,
      ModelAPIOuterClass.Bytes> getRequestSnapshotMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "RequestSnapshot",
      requestType = com.google.protobuf.Empty.class,
      responseType = ModelAPIOuterClass.Bytes.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.google.protobuf.Empty,
      ModelAPIOuterClass.Bytes> getRequestSnapshotMethod() {
    io.grpc.MethodDescriptor<com.google.protobuf.Empty, ModelAPIOuterClass.Bytes> getRequestSnapshotMethod;
    if ((getRequestSnapshotMethod = ModelAPIGrpc.getRequestSnapshotMethod) == null) {
      synchronized (ModelAPIGrpc.class) {
        if ((getRequestSnapshotMethod = ModelAPIGrpc.getRequestSnapshotMethod) == null) {
          ModelAPIGrpc.getRequestSnapshotMethod = getRequestSnapshotMethod =
              io.grpc.MethodDescriptor.<com.google.protobuf.Empty, ModelAPIOuterClass.Bytes>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "RequestSnapshot"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.google.protobuf.Empty.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  ModelAPIOuterClass.Bytes.getDefaultInstance()))
              .setSchemaDescriptor(new ModelAPIMethodDescriptorSupplier("RequestSnapshot"))
              .build();
        }
      }
    }
    return getRequestSnapshotMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.google.protobuf.Empty,
      com.google.protobuf.Empty> getPauseMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "Pause",
      requestType = com.google.protobuf.Empty.class,
      responseType = com.google.protobuf.Empty.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.google.protobuf.Empty,
      com.google.protobuf.Empty> getPauseMethod() {
    io.grpc.MethodDescriptor<com.google.protobuf.Empty, com.google.protobuf.Empty> getPauseMethod;
    if ((getPauseMethod = ModelAPIGrpc.getPauseMethod) == null) {
      synchronized (ModelAPIGrpc.class) {
        if ((getPauseMethod = ModelAPIGrpc.getPauseMethod) == null) {
          ModelAPIGrpc.getPauseMethod = getPauseMethod =
              io.grpc.MethodDescriptor.<com.google.protobuf.Empty, com.google.protobuf.Empty>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "Pause"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.google.protobuf.Empty.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.google.protobuf.Empty.getDefaultInstance()))
              .setSchemaDescriptor(new ModelAPIMethodDescriptorSupplier("Pause"))
              .build();
        }
      }
    }
    return getPauseMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.google.protobuf.Empty,
      com.google.protobuf.Empty> getContinueMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "Continue",
      requestType = com.google.protobuf.Empty.class,
      responseType = com.google.protobuf.Empty.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.google.protobuf.Empty,
      com.google.protobuf.Empty> getContinueMethod() {
    io.grpc.MethodDescriptor<com.google.protobuf.Empty, com.google.protobuf.Empty> getContinueMethod;
    if ((getContinueMethod = ModelAPIGrpc.getContinueMethod) == null) {
      synchronized (ModelAPIGrpc.class) {
        if ((getContinueMethod = ModelAPIGrpc.getContinueMethod) == null) {
          ModelAPIGrpc.getContinueMethod = getContinueMethod =
              io.grpc.MethodDescriptor.<com.google.protobuf.Empty, com.google.protobuf.Empty>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "Continue"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.google.protobuf.Empty.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.google.protobuf.Empty.getDefaultInstance()))
              .setSchemaDescriptor(new ModelAPIMethodDescriptorSupplier("Continue"))
              .build();
        }
      }
    }
    return getContinueMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.google.protobuf.Empty,
      com.google.protobuf.Empty> getStopMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "Stop",
      requestType = com.google.protobuf.Empty.class,
      responseType = com.google.protobuf.Empty.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.google.protobuf.Empty,
      com.google.protobuf.Empty> getStopMethod() {
    io.grpc.MethodDescriptor<com.google.protobuf.Empty, com.google.protobuf.Empty> getStopMethod;
    if ((getStopMethod = ModelAPIGrpc.getStopMethod) == null) {
      synchronized (ModelAPIGrpc.class) {
        if ((getStopMethod = ModelAPIGrpc.getStopMethod) == null) {
          ModelAPIGrpc.getStopMethod = getStopMethod =
              io.grpc.MethodDescriptor.<com.google.protobuf.Empty, com.google.protobuf.Empty>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "Stop"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.google.protobuf.Empty.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.google.protobuf.Empty.getDefaultInstance()))
              .setSchemaDescriptor(new ModelAPIMethodDescriptorSupplier("Stop"))
              .build();
        }
      }
    }
    return getStopMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static ModelAPIStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<ModelAPIStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<ModelAPIStub>() {
        @java.lang.Override
        public ModelAPIStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new ModelAPIStub(channel, callOptions);
        }
      };
    return ModelAPIStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static ModelAPIBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<ModelAPIBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<ModelAPIBlockingStub>() {
        @java.lang.Override
        public ModelAPIBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new ModelAPIBlockingStub(channel, callOptions);
        }
      };
    return ModelAPIBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static ModelAPIFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<ModelAPIFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<ModelAPIFutureStub>() {
        @java.lang.Override
        public ModelAPIFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new ModelAPIFutureStub(channel, callOptions);
        }
      };
    return ModelAPIFutureStub.newStub(factory, channel);
  }

  /**
   */
  public static abstract class ModelAPIImplBase implements io.grpc.BindableService {

    /**
     * <pre>
     *arg GlobalParams returns Snapshot
     * </pre>
     */
    public void run(ModelAPIOuterClass.Bytes request,
        io.grpc.stub.StreamObserver<ModelAPIOuterClass.Bytes> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getRunMethod(), responseObserver);
    }

    /**
     * <pre>
     *arg GlobalParams returns onUpdate event stream
     * </pre>
     */
    public void runAndSubscribeOnUpdate(ModelAPIOuterClass.Bytes request,
        io.grpc.stub.StreamObserver<ModelAPIOuterClass.Event> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getRunAndSubscribeOnUpdateMethod(), responseObserver);
    }

    /**
     * <pre>
     *arg Behaviour
     * </pre>
     */
    public void callBehaviourFunctions(ModelAPIOuterClass.Bytes request,
        io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getCallBehaviourFunctionsMethod(), responseObserver);
    }

    /**
     * <pre>
     *returns Snapshot
     * </pre>
     */
    public void requestSnapshot(com.google.protobuf.Empty request,
        io.grpc.stub.StreamObserver<ModelAPIOuterClass.Bytes> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getRequestSnapshotMethod(), responseObserver);
    }

    /**
     */
    public void pause(com.google.protobuf.Empty request,
        io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getPauseMethod(), responseObserver);
    }

    /**
     */
    public void continue_(com.google.protobuf.Empty request,
        io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getContinueMethod(), responseObserver);
    }

    /**
     */
    public void stop(com.google.protobuf.Empty request,
        io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getStopMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getRunMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                ModelAPIOuterClass.Bytes,
                ModelAPIOuterClass.Bytes>(
                  this, METHODID_RUN)))
          .addMethod(
            getRunAndSubscribeOnUpdateMethod(),
            io.grpc.stub.ServerCalls.asyncServerStreamingCall(
              new MethodHandlers<
                ModelAPIOuterClass.Bytes,
                ModelAPIOuterClass.Event>(
                  this, METHODID_RUN_AND_SUBSCRIBE_ON_UPDATE)))
          .addMethod(
            getCallBehaviourFunctionsMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                ModelAPIOuterClass.Bytes,
                com.google.protobuf.Empty>(
                  this, METHODID_CALL_BEHAVIOUR_FUNCTIONS)))
          .addMethod(
            getRequestSnapshotMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                com.google.protobuf.Empty,
                ModelAPIOuterClass.Bytes>(
                  this, METHODID_REQUEST_SNAPSHOT)))
          .addMethod(
            getPauseMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                com.google.protobuf.Empty,
                com.google.protobuf.Empty>(
                  this, METHODID_PAUSE)))
          .addMethod(
            getContinueMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                com.google.protobuf.Empty,
                com.google.protobuf.Empty>(
                  this, METHODID_CONTINUE)))
          .addMethod(
            getStopMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                com.google.protobuf.Empty,
                com.google.protobuf.Empty>(
                  this, METHODID_STOP)))
          .build();
    }
  }

  /**
   */
  public static final class ModelAPIStub extends io.grpc.stub.AbstractAsyncStub<ModelAPIStub> {
    private ModelAPIStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ModelAPIStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new ModelAPIStub(channel, callOptions);
    }

    /**
     * <pre>
     *arg GlobalParams returns Snapshot
     * </pre>
     */
    public void run(ModelAPIOuterClass.Bytes request,
        io.grpc.stub.StreamObserver<ModelAPIOuterClass.Bytes> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getRunMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     *arg GlobalParams returns onUpdate event stream
     * </pre>
     */
    public void runAndSubscribeOnUpdate(ModelAPIOuterClass.Bytes request,
        io.grpc.stub.StreamObserver<ModelAPIOuterClass.Event> responseObserver) {
      io.grpc.stub.ClientCalls.asyncServerStreamingCall(
          getChannel().newCall(getRunAndSubscribeOnUpdateMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     *arg Behaviour
     * </pre>
     */
    public void callBehaviourFunctions(ModelAPIOuterClass.Bytes request,
        io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getCallBehaviourFunctionsMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     *returns Snapshot
     * </pre>
     */
    public void requestSnapshot(com.google.protobuf.Empty request,
        io.grpc.stub.StreamObserver<ModelAPIOuterClass.Bytes> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getRequestSnapshotMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void pause(com.google.protobuf.Empty request,
        io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getPauseMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void continue_(com.google.protobuf.Empty request,
        io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getContinueMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void stop(com.google.protobuf.Empty request,
        io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getStopMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class ModelAPIBlockingStub extends io.grpc.stub.AbstractBlockingStub<ModelAPIBlockingStub> {
    private ModelAPIBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ModelAPIBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new ModelAPIBlockingStub(channel, callOptions);
    }

    /**
     * <pre>
     *arg GlobalParams returns Snapshot
     * </pre>
     */
    public ModelAPIOuterClass.Bytes run(ModelAPIOuterClass.Bytes request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getRunMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     *arg GlobalParams returns onUpdate event stream
     * </pre>
     */
    public java.util.Iterator<ModelAPIOuterClass.Event> runAndSubscribeOnUpdate(
        ModelAPIOuterClass.Bytes request) {
      return io.grpc.stub.ClientCalls.blockingServerStreamingCall(
          getChannel(), getRunAndSubscribeOnUpdateMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     *arg Behaviour
     * </pre>
     */
    public com.google.protobuf.Empty callBehaviourFunctions(ModelAPIOuterClass.Bytes request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getCallBehaviourFunctionsMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     *returns Snapshot
     * </pre>
     */
    public ModelAPIOuterClass.Bytes requestSnapshot(com.google.protobuf.Empty request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getRequestSnapshotMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.google.protobuf.Empty pause(com.google.protobuf.Empty request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getPauseMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.google.protobuf.Empty continue_(com.google.protobuf.Empty request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getContinueMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.google.protobuf.Empty stop(com.google.protobuf.Empty request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getStopMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class ModelAPIFutureStub extends io.grpc.stub.AbstractFutureStub<ModelAPIFutureStub> {
    private ModelAPIFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ModelAPIFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new ModelAPIFutureStub(channel, callOptions);
    }

    /**
     * <pre>
     *arg GlobalParams returns Snapshot
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<ModelAPIOuterClass.Bytes> run(
        ModelAPIOuterClass.Bytes request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getRunMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     *arg Behaviour
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.google.protobuf.Empty> callBehaviourFunctions(
        ModelAPIOuterClass.Bytes request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getCallBehaviourFunctionsMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     *returns Snapshot
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<ModelAPIOuterClass.Bytes> requestSnapshot(
        com.google.protobuf.Empty request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getRequestSnapshotMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.google.protobuf.Empty> pause(
        com.google.protobuf.Empty request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getPauseMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.google.protobuf.Empty> continue_(
        com.google.protobuf.Empty request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getContinueMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.google.protobuf.Empty> stop(
        com.google.protobuf.Empty request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getStopMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_RUN = 0;
  private static final int METHODID_RUN_AND_SUBSCRIBE_ON_UPDATE = 1;
  private static final int METHODID_CALL_BEHAVIOUR_FUNCTIONS = 2;
  private static final int METHODID_REQUEST_SNAPSHOT = 3;
  private static final int METHODID_PAUSE = 4;
  private static final int METHODID_CONTINUE = 5;
  private static final int METHODID_STOP = 6;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final ModelAPIImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(ModelAPIImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_RUN:
          serviceImpl.run((ModelAPIOuterClass.Bytes) request,
              (io.grpc.stub.StreamObserver<ModelAPIOuterClass.Bytes>) responseObserver);
          break;
        case METHODID_RUN_AND_SUBSCRIBE_ON_UPDATE:
          serviceImpl.runAndSubscribeOnUpdate((ModelAPIOuterClass.Bytes) request,
              (io.grpc.stub.StreamObserver<ModelAPIOuterClass.Event>) responseObserver);
          break;
        case METHODID_CALL_BEHAVIOUR_FUNCTIONS:
          serviceImpl.callBehaviourFunctions((ModelAPIOuterClass.Bytes) request,
              (io.grpc.stub.StreamObserver<com.google.protobuf.Empty>) responseObserver);
          break;
        case METHODID_REQUEST_SNAPSHOT:
          serviceImpl.requestSnapshot((com.google.protobuf.Empty) request,
              (io.grpc.stub.StreamObserver<ModelAPIOuterClass.Bytes>) responseObserver);
          break;
        case METHODID_PAUSE:
          serviceImpl.pause((com.google.protobuf.Empty) request,
              (io.grpc.stub.StreamObserver<com.google.protobuf.Empty>) responseObserver);
          break;
        case METHODID_CONTINUE:
          serviceImpl.continue_((com.google.protobuf.Empty) request,
              (io.grpc.stub.StreamObserver<com.google.protobuf.Empty>) responseObserver);
          break;
        case METHODID_STOP:
          serviceImpl.stop((com.google.protobuf.Empty) request,
              (io.grpc.stub.StreamObserver<com.google.protobuf.Empty>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  private static abstract class ModelAPIBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    ModelAPIBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return ModelAPIOuterClass.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("ModelAPI");
    }
  }

  private static final class ModelAPIFileDescriptorSupplier
      extends ModelAPIBaseDescriptorSupplier {
    ModelAPIFileDescriptorSupplier() {}
  }

  private static final class ModelAPIMethodDescriptorSupplier
      extends ModelAPIBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    ModelAPIMethodDescriptorSupplier(String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (ModelAPIGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new ModelAPIFileDescriptorSupplier())
              .addMethod(getRunMethod())
              .addMethod(getRunAndSubscribeOnUpdateMethod())
              .addMethod(getCallBehaviourFunctionsMethod())
              .addMethod(getRequestSnapshotMethod())
              .addMethod(getPauseMethod())
              .addMethod(getContinueMethod())
              .addMethod(getStopMethod())
              .build();
        }
      }
    }
    return result;
  }
}

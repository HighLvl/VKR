// automatically generated by the FlatBuffers compiler, do not modify

package Model;

import java.nio.*;
import java.lang.*;
import java.util.*;
import com.google.flatbuffers.*;

@SuppressWarnings("unused")
public final class GlobalArgs extends Table {
  public static void ValidateVersion() { Constants.FLATBUFFERS_2_0_0(); }
  public static GlobalArgs getRootAsGlobalArgs(ByteBuffer _bb) { return getRootAsGlobalArgs(_bb, new GlobalArgs()); }
  public static GlobalArgs getRootAsGlobalArgs(ByteBuffer _bb, GlobalArgs obj) { _bb.order(ByteOrder.LITTLE_ENDIAN); return (obj.__assign(_bb.getInt(_bb.position()) + _bb.position(), _bb)); }
  public void __init(int _i, ByteBuffer _bb) { __reset(_i, _bb); }
  public GlobalArgs __assign(int _i, ByteBuffer _bb) { __init(_i, _bb); return this; }

  public String args() { int o = __offset(4); return o != 0 ? __string(o + bb_pos) : null; }
  public ByteBuffer argsAsByteBuffer() { return __vector_as_bytebuffer(4, 1); }
  public ByteBuffer argsInByteBuffer(ByteBuffer _bb) { return __vector_in_bytebuffer(_bb, 4, 1); }

  public static int createGlobalArgs(FlatBufferBuilder builder,
      int argsOffset) {
    builder.startTable(1);
    GlobalArgs.addArgs(builder, argsOffset);
    return GlobalArgs.endGlobalArgs(builder);
  }

  public static void startGlobalArgs(FlatBufferBuilder builder) { builder.startTable(1); }
  public static void addArgs(FlatBufferBuilder builder, int argsOffset) { builder.addOffset(0, argsOffset, 0); }
  public static int endGlobalArgs(FlatBufferBuilder builder) {
    int o = builder.endTable();
    return o;
  }

  public static final class Vector extends BaseVector {
    public Vector __assign(int _vector, int _element_size, ByteBuffer _bb) { __reset(_vector, _element_size, _bb); return this; }

    public GlobalArgs get(int j) { return get(new GlobalArgs(), j); }
    public GlobalArgs get(GlobalArgs obj, int j) {  return obj.__assign(__indirect(__element(j), bb), bb); }
  }
}

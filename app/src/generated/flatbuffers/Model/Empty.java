// automatically generated by the FlatBuffers compiler, do not modify

package Model;

import java.nio.*;
import java.lang.*;
import java.util.*;
import com.google.flatbuffers.*;

@SuppressWarnings("unused")
public final class Empty extends Table {
  public static void ValidateVersion() { Constants.FLATBUFFERS_2_0_0(); }
  public static Empty getRootAsEmpty(ByteBuffer _bb) { return getRootAsEmpty(_bb, new Empty()); }
  public static Empty getRootAsEmpty(ByteBuffer _bb, Empty obj) { _bb.order(ByteOrder.LITTLE_ENDIAN); return (obj.__assign(_bb.getInt(_bb.position()) + _bb.position(), _bb)); }
  public void __init(int _i, ByteBuffer _bb) { __reset(_i, _bb); }
  public Empty __assign(int _i, ByteBuffer _bb) { __init(_i, _bb); return this; }


  public static void startEmpty(FlatBufferBuilder builder) { builder.startTable(0); }
  public static int endEmpty(FlatBufferBuilder builder) {
    int o = builder.endTable();
    return o;
  }

  public static final class Vector extends BaseVector {
    public Vector __assign(int _vector, int _element_size, ByteBuffer _bb) { __reset(_vector, _element_size, _bb); return this; }

    public Empty get(int j) { return get(new Empty(), j); }
    public Empty get(Empty obj, int j) {  return obj.__assign(__indirect(__element(j), bb), bb); }
  }
}

// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: OnlineResponseProtocol.proto

package ly.pp.justpiano3.protobuf.vo;

public interface OnlineRoomPositionCoupleVOOrBuilder extends
    // @@protoc_insertion_point(interface_extends:ly.pp.justpiano3.protobuf.vo.OnlineRoomPositionCoupleVO)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <pre>
   * 发送搭档请求
   * </pre>
   *
   * <code>bool invite = 1;</code>
   * @return The invite.
   */
  boolean getInvite();

  /**
   * <pre>
   * 搭档位置楼号
   * </pre>
   *
   * <code>int32 couple_position = 2;</code>
   * @return The couplePosition.
   */
  int getCouplePosition();

  /**
   * <pre>
   * 搭档名称
   * </pre>
   *
   * <code>string couple_name = 3;</code>
   * @return The coupleName.
   */
  java.lang.String getCoupleName();
  /**
   * <pre>
   * 搭档名称
   * </pre>
   *
   * <code>string couple_name = 3;</code>
   * @return The bytes for coupleName.
   */
  com.google.protobuf.ByteString
      getCoupleNameBytes();

  /**
   * <pre>
   * 搭档类型
   * </pre>
   *
   * <code>uint32 couple_type = 4;</code>
   * @return The coupleType.
   */
  int getCoupleType();
}

// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: OnlineRequestProtocol.proto

package protobuf.dto;

public interface OnlineEnterRoomDTOOrBuilder extends
    // @@protoc_insertion_point(interface_extends:protobuf.dto.OnlineEnterRoomDTO)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <pre>
   * 房间id
   * </pre>
   *
   * <code>uint32 room_id = 1;</code>
   * @return The roomId.
   */
  int getRoomId();

  /**
   * <pre>
   * 房间密码
   * </pre>
   *
   * <code>string password = 2;</code>
   * @return The password.
   */
  java.lang.String getPassword();
  /**
   * <pre>
   * 房间密码
   * </pre>
   *
   * <code>string password = 2;</code>
   * @return The bytes for password.
   */
  com.google.protobuf.ByteString
      getPasswordBytes();
}

// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: OnlineRequestProtocol.proto

package protobuf.dto;

public interface OnlineSetUserInfoDTOOrBuilder extends
    // @@protoc_insertion_point(interface_extends:protobuf.dto.OnlineSetUserInfoDTO)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <pre>
   * 消息类型
   * </pre>
   *
   * <code>uint32 type = 1;</code>
   * @return The type.
   */
  int getType();

  /**
   * <pre>
   * 相关用户名
   * </pre>
   *
   * <code>string name = 2;</code>
   * @return The name.
   */
  java.lang.String getName();
  /**
   * <pre>
   * 相关用户名
   * </pre>
   *
   * <code>string name = 2;</code>
   * @return The bytes for name.
   */
  com.google.protobuf.ByteString
      getNameBytes();

  /**
   * <pre>
   * 是否拒绝好友请求
   * </pre>
   *
   * <code>optional bool reject = 3;</code>
   * @return Whether the reject field is set.
   */
  boolean hasReject();
  /**
   * <pre>
   * 是否拒绝好友请求
   * </pre>
   *
   * <code>optional bool reject = 3;</code>
   * @return The reject.
   */
  boolean getReject();

  /**
   * <pre>
   * 祝语
   * </pre>
   *
   * <code>optional string declaration = 4;</code>
   * @return Whether the declaration field is set.
   */
  boolean hasDeclaration();
  /**
   * <pre>
   * 祝语
   * </pre>
   *
   * <code>optional string declaration = 4;</code>
   * @return The declaration.
   */
  java.lang.String getDeclaration();
  /**
   * <pre>
   * 祝语
   * </pre>
   *
   * <code>optional string declaration = 4;</code>
   * @return The bytes for declaration.
   */
  com.google.protobuf.ByteString
      getDeclarationBytes();
}

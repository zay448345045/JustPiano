// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: OnlineResponseProtocol.proto

package ly.pp.justpiano3.protobuf.vo;

public interface OnlineFriendUserVOOrBuilder extends
    // @@protoc_insertion_point(interface_extends:ly.pp.justpiano3.protobuf.vo.OnlineFriendUserVO)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <pre>
   * 好友用户名称
   * </pre>
   *
   * <code>string name = 1;</code>
   * @return The name.
   */
  java.lang.String getName();
  /**
   * <pre>
   * 好友用户名称
   * </pre>
   *
   * <code>string name = 1;</code>
   * @return The bytes for name.
   */
  com.google.protobuf.ByteString
      getNameBytes();

  /**
   * <pre>
   * 好友用户性别
   * </pre>
   *
   * <code>string gender = 2;</code>
   * @return The gender.
   */
  java.lang.String getGender();
  /**
   * <pre>
   * 好友用户性别
   * </pre>
   *
   * <code>string gender = 2;</code>
   * @return The bytes for gender.
   */
  com.google.protobuf.ByteString
      getGenderBytes();

  /**
   * <pre>
   * 好友用户是否在线
   * </pre>
   *
   * <code>bool online = 3;</code>
   * @return The online.
   */
  boolean getOnline();

  /**
   * <pre>
   * 好友用户等级
   * </pre>
   *
   * <code>uint32 lv = 4;</code>
   * @return The lv.
   */
  int getLv();
}

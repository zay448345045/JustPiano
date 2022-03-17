// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: OnlineResponseProtocol.proto

package ly.pp.justpiano3.protobuf.vo;

public interface OnlineFamilyListVOOrBuilder extends
    // @@protoc_insertion_point(interface_extends:ly.pp.justpiano3.protobuf.vo.OnlineFamilyListVO)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <pre>
   * 家族信息
   * </pre>
   *
   * <code>repeated .ly.pp.justpiano3.protobuf.vo.OnlineFamilyInfoVO family_info = 1;</code>
   */
  java.util.List<ly.pp.justpiano3.protobuf.vo.OnlineFamilyInfoVO> 
      getFamilyInfoList();
  /**
   * <pre>
   * 家族信息
   * </pre>
   *
   * <code>repeated .ly.pp.justpiano3.protobuf.vo.OnlineFamilyInfoVO family_info = 1;</code>
   */
  ly.pp.justpiano3.protobuf.vo.OnlineFamilyInfoVO getFamilyInfo(int index);
  /**
   * <pre>
   * 家族信息
   * </pre>
   *
   * <code>repeated .ly.pp.justpiano3.protobuf.vo.OnlineFamilyInfoVO family_info = 1;</code>
   */
  int getFamilyInfoCount();
  /**
   * <pre>
   * 家族信息
   * </pre>
   *
   * <code>repeated .ly.pp.justpiano3.protobuf.vo.OnlineFamilyInfoVO family_info = 1;</code>
   */
  java.util.List<? extends ly.pp.justpiano3.protobuf.vo.OnlineFamilyInfoVOOrBuilder> 
      getFamilyInfoOrBuilderList();
  /**
   * <pre>
   * 家族信息
   * </pre>
   *
   * <code>repeated .ly.pp.justpiano3.protobuf.vo.OnlineFamilyInfoVO family_info = 1;</code>
   */
  ly.pp.justpiano3.protobuf.vo.OnlineFamilyInfoVOOrBuilder getFamilyInfoOrBuilder(
      int index);

  /**
   * <pre>
   * 当前用户所在家族名称
   * </pre>
   *
   * <code>string name = 2;</code>
   * @return The name.
   */
  java.lang.String getName();
  /**
   * <pre>
   * 当前用户所在家族名称
   * </pre>
   *
   * <code>string name = 2;</code>
   * @return The bytes for name.
   */
  com.google.protobuf.ByteString
      getNameBytes();

  /**
   * <pre>
   * 当前用户所在家族排名
   * </pre>
   *
   * <code>uint32 position = 3;</code>
   * @return The position.
   */
  int getPosition();

  /**
   * <pre>
   * 当前用户所在家族贡献
   * </pre>
   *
   * <code>uint32 contribution = 4;</code>
   * @return The contribution.
   */
  int getContribution();

  /**
   * <pre>
   * 当前用户所在家族最大容量
   * </pre>
   *
   * <code>uint32 capacity = 5;</code>
   * @return The capacity.
   */
  int getCapacity();

  /**
   * <pre>
   * 当前用户所在家族当前成员总数
   * </pre>
   *
   * <code>uint32 size = 6;</code>
   * @return The size.
   */
  int getSize();

  /**
   * <pre>
   * 当前用户所在家族id
   * </pre>
   *
   * <code>uint32 family_id = 7;</code>
   * @return The familyId.
   */
  int getFamilyId();

  /**
   * <pre>
   * 当前用户所在家族族徽图片
   * </pre>
   *
   * <code>string picture = 8;</code>
   * @return The picture.
   */
  java.lang.String getPicture();
  /**
   * <pre>
   * 当前用户所在家族族徽图片
   * </pre>
   *
   * <code>string picture = 8;</code>
   * @return The bytes for picture.
   */
  com.google.protobuf.ByteString
      getPictureBytes();
}

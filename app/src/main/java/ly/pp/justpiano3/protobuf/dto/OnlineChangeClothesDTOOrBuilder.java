// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: OnlineRequestProtocol.proto

package ly.pp.justpiano3.protobuf.dto;

public interface OnlineChangeClothesDTOOrBuilder extends
    // @@protoc_insertion_point(interface_extends:ly.pp.justpiano3.protobuf.dto.OnlineChangeClothesDTO)
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
   * 购买服装分类编号
   * </pre>
   *
   * <code>optional uint32 buy_clothes_type = 2;</code>
   * @return Whether the buyClothesType field is set.
   */
  boolean hasBuyClothesType();
  /**
   * <pre>
   * 购买服装分类编号
   * </pre>
   *
   * <code>optional uint32 buy_clothes_type = 2;</code>
   * @return The buyClothesType.
   */
  int getBuyClothesType();

  /**
   * <pre>
   * 购买服装编号
   * </pre>
   *
   * <code>optional uint32 buy_clothes_id = 3;</code>
   * @return Whether the buyClothesId field is set.
   */
  boolean hasBuyClothesId();
  /**
   * <pre>
   * 购买服装编号
   * </pre>
   *
   * <code>optional uint32 buy_clothes_id = 3;</code>
   * @return The buyClothesId.
   */
  int getBuyClothesId();

  /**
   * <pre>
   * 头发
   * </pre>
   *
   * <code>uint32 hair = 4;</code>
   * @return The hair.
   */
  int getHair();

  /**
   * <pre>
   * 眼睛
   * </pre>
   *
   * <code>uint32 eye = 5;</code>
   * @return The eye.
   */
  int getEye();

  /**
   * <pre>
   * 上衣
   * </pre>
   *
   * <code>uint32 jacket = 6;</code>
   * @return The jacket.
   */
  int getJacket();

  /**
   * <pre>
   * 裤子
   * </pre>
   *
   * <code>uint32 trousers = 7;</code>
   * @return The trousers.
   */
  int getTrousers();

  /**
   * <pre>
   * 鞋子
   * </pre>
   *
   * <code>uint32 shoes = 8;</code>
   * @return The shoes.
   */
  int getShoes();
}

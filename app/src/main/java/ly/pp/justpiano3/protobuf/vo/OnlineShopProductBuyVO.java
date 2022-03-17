// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: OnlineResponseProtocol.proto

package ly.pp.justpiano3.protobuf.vo;

/**
 * <pre>
 * 商店购买商品
 * </pre>
 *
 * Protobuf type {@code ly.pp.justpiano3.protobuf.vo.OnlineShopProductBuyVO}
 */
public final class OnlineShopProductBuyVO extends
    com.google.protobuf.GeneratedMessageV3 implements
    // @@protoc_insertion_point(message_implements:ly.pp.justpiano3.protobuf.vo.OnlineShopProductBuyVO)
    OnlineShopProductBuyVOOrBuilder {
private static final long serialVersionUID = 0L;
  // Use OnlineShopProductBuyVO.newBuilder() to construct.
  private OnlineShopProductBuyVO(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
    super(builder);
  }
  private OnlineShopProductBuyVO() {
    message_ = "";
  }

  @java.lang.Override
  @SuppressWarnings({"unused"})
  protected java.lang.Object newInstance(
      UnusedPrivateParameter unused) {
    return new OnlineShopProductBuyVO();
  }

  @java.lang.Override
  public final com.google.protobuf.UnknownFieldSet
  getUnknownFields() {
    return this.unknownFields;
  }
  private OnlineShopProductBuyVO(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    this();
    if (extensionRegistry == null) {
      throw new java.lang.NullPointerException();
    }
    com.google.protobuf.UnknownFieldSet.Builder unknownFields =
        com.google.protobuf.UnknownFieldSet.newBuilder();
    try {
      boolean done = false;
      while (!done) {
        int tag = input.readTag();
        switch (tag) {
          case 0:
            done = true;
            break;
          case 8: {

            gold_ = input.readUInt32();
            break;
          }
          case 18: {
            java.lang.String s = input.readStringRequireUtf8();

            message_ = s;
            break;
          }
          default: {
            if (!parseUnknownField(
                input, unknownFields, extensionRegistry, tag)) {
              done = true;
            }
            break;
          }
        }
      }
    } catch (com.google.protobuf.InvalidProtocolBufferException e) {
      throw e.setUnfinishedMessage(this);
    } catch (java.io.IOException e) {
      throw new com.google.protobuf.InvalidProtocolBufferException(
          e).setUnfinishedMessage(this);
    } finally {
      this.unknownFields = unknownFields.build();
      makeExtensionsImmutable();
    }
  }
  public static final com.google.protobuf.Descriptors.Descriptor
      getDescriptor() {
    return ly.pp.justpiano3.protobuf.vo.OnlineResponse.internal_static_ly_pp_justpiano3_protobuf_vo_OnlineShopProductBuyVO_descriptor;
  }

  @java.lang.Override
  protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internalGetFieldAccessorTable() {
    return ly.pp.justpiano3.protobuf.vo.OnlineResponse.internal_static_ly_pp_justpiano3_protobuf_vo_OnlineShopProductBuyVO_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            ly.pp.justpiano3.protobuf.vo.OnlineShopProductBuyVO.class, ly.pp.justpiano3.protobuf.vo.OnlineShopProductBuyVO.Builder.class);
  }

  public static final int GOLD_FIELD_NUMBER = 1;
  private int gold_;
  /**
   * <pre>
   * 剩余音符数量
   * </pre>
   *
   * <code>uint32 gold = 1;</code>
   * @return The gold.
   */
  @java.lang.Override
  public int getGold() {
    return gold_;
  }

  public static final int MESSAGE_FIELD_NUMBER = 2;
  private volatile java.lang.Object message_;
  /**
   * <pre>
   * 返回对话框消息
   * </pre>
   *
   * <code>string message = 2;</code>
   * @return The message.
   */
  @java.lang.Override
  public java.lang.String getMessage() {
    java.lang.Object ref = message_;
    if (ref instanceof java.lang.String) {
      return (java.lang.String) ref;
    } else {
      com.google.protobuf.ByteString bs = 
          (com.google.protobuf.ByteString) ref;
      java.lang.String s = bs.toStringUtf8();
      message_ = s;
      return s;
    }
  }
  /**
   * <pre>
   * 返回对话框消息
   * </pre>
   *
   * <code>string message = 2;</code>
   * @return The bytes for message.
   */
  @java.lang.Override
  public com.google.protobuf.ByteString
      getMessageBytes() {
    java.lang.Object ref = message_;
    if (ref instanceof java.lang.String) {
      com.google.protobuf.ByteString b = 
          com.google.protobuf.ByteString.copyFromUtf8(
              (java.lang.String) ref);
      message_ = b;
      return b;
    } else {
      return (com.google.protobuf.ByteString) ref;
    }
  }

  private byte memoizedIsInitialized = -1;
  @java.lang.Override
  public final boolean isInitialized() {
    byte isInitialized = memoizedIsInitialized;
    if (isInitialized == 1) return true;
    if (isInitialized == 0) return false;

    memoizedIsInitialized = 1;
    return true;
  }

  @java.lang.Override
  public void writeTo(com.google.protobuf.CodedOutputStream output)
                      throws java.io.IOException {
    if (gold_ != 0) {
      output.writeUInt32(1, gold_);
    }
    if (!com.google.protobuf.GeneratedMessageV3.isStringEmpty(message_)) {
      com.google.protobuf.GeneratedMessageV3.writeString(output, 2, message_);
    }
    unknownFields.writeTo(output);
  }

  @java.lang.Override
  public int getSerializedSize() {
    int size = memoizedSize;
    if (size != -1) return size;

    size = 0;
    if (gold_ != 0) {
      size += com.google.protobuf.CodedOutputStream
        .computeUInt32Size(1, gold_);
    }
    if (!com.google.protobuf.GeneratedMessageV3.isStringEmpty(message_)) {
      size += com.google.protobuf.GeneratedMessageV3.computeStringSize(2, message_);
    }
    size += unknownFields.getSerializedSize();
    memoizedSize = size;
    return size;
  }

  @java.lang.Override
  public boolean equals(final java.lang.Object obj) {
    if (obj == this) {
     return true;
    }
    if (!(obj instanceof ly.pp.justpiano3.protobuf.vo.OnlineShopProductBuyVO)) {
      return super.equals(obj);
    }
    ly.pp.justpiano3.protobuf.vo.OnlineShopProductBuyVO other = (ly.pp.justpiano3.protobuf.vo.OnlineShopProductBuyVO) obj;

    if (getGold()
        != other.getGold()) return false;
    if (!getMessage()
        .equals(other.getMessage())) return false;
    if (!unknownFields.equals(other.unknownFields)) return false;
    return true;
  }

  @java.lang.Override
  public int hashCode() {
    if (memoizedHashCode != 0) {
      return memoizedHashCode;
    }
    int hash = 41;
    hash = (19 * hash) + getDescriptor().hashCode();
    hash = (37 * hash) + GOLD_FIELD_NUMBER;
    hash = (53 * hash) + getGold();
    hash = (37 * hash) + MESSAGE_FIELD_NUMBER;
    hash = (53 * hash) + getMessage().hashCode();
    hash = (29 * hash) + unknownFields.hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static ly.pp.justpiano3.protobuf.vo.OnlineShopProductBuyVO parseFrom(
      java.nio.ByteBuffer data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static ly.pp.justpiano3.protobuf.vo.OnlineShopProductBuyVO parseFrom(
      java.nio.ByteBuffer data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static ly.pp.justpiano3.protobuf.vo.OnlineShopProductBuyVO parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static ly.pp.justpiano3.protobuf.vo.OnlineShopProductBuyVO parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static ly.pp.justpiano3.protobuf.vo.OnlineShopProductBuyVO parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static ly.pp.justpiano3.protobuf.vo.OnlineShopProductBuyVO parseFrom(
      byte[] data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static ly.pp.justpiano3.protobuf.vo.OnlineShopProductBuyVO parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static ly.pp.justpiano3.protobuf.vo.OnlineShopProductBuyVO parseFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }
  public static ly.pp.justpiano3.protobuf.vo.OnlineShopProductBuyVO parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input);
  }
  public static ly.pp.justpiano3.protobuf.vo.OnlineShopProductBuyVO parseDelimitedFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
  }
  public static ly.pp.justpiano3.protobuf.vo.OnlineShopProductBuyVO parseFrom(
      com.google.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static ly.pp.justpiano3.protobuf.vo.OnlineShopProductBuyVO parseFrom(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }

  @java.lang.Override
  public Builder newBuilderForType() { return newBuilder(); }
  public static Builder newBuilder() {
    return DEFAULT_INSTANCE.toBuilder();
  }
  public static Builder newBuilder(ly.pp.justpiano3.protobuf.vo.OnlineShopProductBuyVO prototype) {
    return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
  }
  @java.lang.Override
  public Builder toBuilder() {
    return this == DEFAULT_INSTANCE
        ? new Builder() : new Builder().mergeFrom(this);
  }

  @java.lang.Override
  protected Builder newBuilderForType(
      com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
    Builder builder = new Builder(parent);
    return builder;
  }
  /**
   * <pre>
   * 商店购买商品
   * </pre>
   *
   * Protobuf type {@code ly.pp.justpiano3.protobuf.vo.OnlineShopProductBuyVO}
   */
  public static final class Builder extends
      com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
      // @@protoc_insertion_point(builder_implements:ly.pp.justpiano3.protobuf.vo.OnlineShopProductBuyVO)
      ly.pp.justpiano3.protobuf.vo.OnlineShopProductBuyVOOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return ly.pp.justpiano3.protobuf.vo.OnlineResponse.internal_static_ly_pp_justpiano3_protobuf_vo_OnlineShopProductBuyVO_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return ly.pp.justpiano3.protobuf.vo.OnlineResponse.internal_static_ly_pp_justpiano3_protobuf_vo_OnlineShopProductBuyVO_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              ly.pp.justpiano3.protobuf.vo.OnlineShopProductBuyVO.class, ly.pp.justpiano3.protobuf.vo.OnlineShopProductBuyVO.Builder.class);
    }

    // Construct using ly.pp.justpiano3.protobuf.vo.OnlineShopProductBuyVO.newBuilder()
    private Builder() {
      maybeForceBuilderInitialization();
    }

    private Builder(
        com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
      super(parent);
      maybeForceBuilderInitialization();
    }
    private void maybeForceBuilderInitialization() {
      if (com.google.protobuf.GeneratedMessageV3
              .alwaysUseFieldBuilders) {
      }
    }
    @java.lang.Override
    public Builder clear() {
      super.clear();
      gold_ = 0;

      message_ = "";

      return this;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.Descriptor
        getDescriptorForType() {
      return ly.pp.justpiano3.protobuf.vo.OnlineResponse.internal_static_ly_pp_justpiano3_protobuf_vo_OnlineShopProductBuyVO_descriptor;
    }

    @java.lang.Override
    public ly.pp.justpiano3.protobuf.vo.OnlineShopProductBuyVO getDefaultInstanceForType() {
      return ly.pp.justpiano3.protobuf.vo.OnlineShopProductBuyVO.getDefaultInstance();
    }

    @java.lang.Override
    public ly.pp.justpiano3.protobuf.vo.OnlineShopProductBuyVO build() {
      ly.pp.justpiano3.protobuf.vo.OnlineShopProductBuyVO result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    @java.lang.Override
    public ly.pp.justpiano3.protobuf.vo.OnlineShopProductBuyVO buildPartial() {
      ly.pp.justpiano3.protobuf.vo.OnlineShopProductBuyVO result = new ly.pp.justpiano3.protobuf.vo.OnlineShopProductBuyVO(this);
      result.gold_ = gold_;
      result.message_ = message_;
      onBuilt();
      return result;
    }

    @java.lang.Override
    public Builder clone() {
      return super.clone();
    }
    @java.lang.Override
    public Builder setField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        java.lang.Object value) {
      return super.setField(field, value);
    }
    @java.lang.Override
    public Builder clearField(
        com.google.protobuf.Descriptors.FieldDescriptor field) {
      return super.clearField(field);
    }
    @java.lang.Override
    public Builder clearOneof(
        com.google.protobuf.Descriptors.OneofDescriptor oneof) {
      return super.clearOneof(oneof);
    }
    @java.lang.Override
    public Builder setRepeatedField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        int index, java.lang.Object value) {
      return super.setRepeatedField(field, index, value);
    }
    @java.lang.Override
    public Builder addRepeatedField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        java.lang.Object value) {
      return super.addRepeatedField(field, value);
    }
    @java.lang.Override
    public Builder mergeFrom(com.google.protobuf.Message other) {
      if (other instanceof ly.pp.justpiano3.protobuf.vo.OnlineShopProductBuyVO) {
        return mergeFrom((ly.pp.justpiano3.protobuf.vo.OnlineShopProductBuyVO)other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(ly.pp.justpiano3.protobuf.vo.OnlineShopProductBuyVO other) {
      if (other == ly.pp.justpiano3.protobuf.vo.OnlineShopProductBuyVO.getDefaultInstance()) return this;
      if (other.getGold() != 0) {
        setGold(other.getGold());
      }
      if (!other.getMessage().isEmpty()) {
        message_ = other.message_;
        onChanged();
      }
      this.mergeUnknownFields(other.unknownFields);
      onChanged();
      return this;
    }

    @java.lang.Override
    public final boolean isInitialized() {
      return true;
    }

    @java.lang.Override
    public Builder mergeFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      ly.pp.justpiano3.protobuf.vo.OnlineShopProductBuyVO parsedMessage = null;
      try {
        parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        parsedMessage = (ly.pp.justpiano3.protobuf.vo.OnlineShopProductBuyVO) e.getUnfinishedMessage();
        throw e.unwrapIOException();
      } finally {
        if (parsedMessage != null) {
          mergeFrom(parsedMessage);
        }
      }
      return this;
    }

    private int gold_ ;
    /**
     * <pre>
     * 剩余音符数量
     * </pre>
     *
     * <code>uint32 gold = 1;</code>
     * @return The gold.
     */
    @java.lang.Override
    public int getGold() {
      return gold_;
    }
    /**
     * <pre>
     * 剩余音符数量
     * </pre>
     *
     * <code>uint32 gold = 1;</code>
     * @param value The gold to set.
     * @return This builder for chaining.
     */
    public Builder setGold(int value) {
      
      gold_ = value;
      onChanged();
      return this;
    }
    /**
     * <pre>
     * 剩余音符数量
     * </pre>
     *
     * <code>uint32 gold = 1;</code>
     * @return This builder for chaining.
     */
    public Builder clearGold() {
      
      gold_ = 0;
      onChanged();
      return this;
    }

    private java.lang.Object message_ = "";
    /**
     * <pre>
     * 返回对话框消息
     * </pre>
     *
     * <code>string message = 2;</code>
     * @return The message.
     */
    public java.lang.String getMessage() {
      java.lang.Object ref = message_;
      if (!(ref instanceof java.lang.String)) {
        com.google.protobuf.ByteString bs =
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        message_ = s;
        return s;
      } else {
        return (java.lang.String) ref;
      }
    }
    /**
     * <pre>
     * 返回对话框消息
     * </pre>
     *
     * <code>string message = 2;</code>
     * @return The bytes for message.
     */
    public com.google.protobuf.ByteString
        getMessageBytes() {
      java.lang.Object ref = message_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        message_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }
    /**
     * <pre>
     * 返回对话框消息
     * </pre>
     *
     * <code>string message = 2;</code>
     * @param value The message to set.
     * @return This builder for chaining.
     */
    public Builder setMessage(
        java.lang.String value) {
      if (value == null) {
    throw new NullPointerException();
  }
  
      message_ = value;
      onChanged();
      return this;
    }
    /**
     * <pre>
     * 返回对话框消息
     * </pre>
     *
     * <code>string message = 2;</code>
     * @return This builder for chaining.
     */
    public Builder clearMessage() {
      
      message_ = getDefaultInstance().getMessage();
      onChanged();
      return this;
    }
    /**
     * <pre>
     * 返回对话框消息
     * </pre>
     *
     * <code>string message = 2;</code>
     * @param value The bytes for message to set.
     * @return This builder for chaining.
     */
    public Builder setMessageBytes(
        com.google.protobuf.ByteString value) {
      if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
      
      message_ = value;
      onChanged();
      return this;
    }
    @java.lang.Override
    public final Builder setUnknownFields(
        final com.google.protobuf.UnknownFieldSet unknownFields) {
      return super.setUnknownFields(unknownFields);
    }

    @java.lang.Override
    public final Builder mergeUnknownFields(
        final com.google.protobuf.UnknownFieldSet unknownFields) {
      return super.mergeUnknownFields(unknownFields);
    }


    // @@protoc_insertion_point(builder_scope:ly.pp.justpiano3.protobuf.vo.OnlineShopProductBuyVO)
  }

  // @@protoc_insertion_point(class_scope:ly.pp.justpiano3.protobuf.vo.OnlineShopProductBuyVO)
  private static final ly.pp.justpiano3.protobuf.vo.OnlineShopProductBuyVO DEFAULT_INSTANCE;
  static {
    DEFAULT_INSTANCE = new ly.pp.justpiano3.protobuf.vo.OnlineShopProductBuyVO();
  }

  public static ly.pp.justpiano3.protobuf.vo.OnlineShopProductBuyVO getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final com.google.protobuf.Parser<OnlineShopProductBuyVO>
      PARSER = new com.google.protobuf.AbstractParser<OnlineShopProductBuyVO>() {
    @java.lang.Override
    public OnlineShopProductBuyVO parsePartialFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return new OnlineShopProductBuyVO(input, extensionRegistry);
    }
  };

  public static com.google.protobuf.Parser<OnlineShopProductBuyVO> parser() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.protobuf.Parser<OnlineShopProductBuyVO> getParserForType() {
    return PARSER;
  }

  @java.lang.Override
  public ly.pp.justpiano3.protobuf.vo.OnlineShopProductBuyVO getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

}


// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: OnlineResponseProtocol.proto

package protobuf.vo;

/**
 * <pre>
 * 搭档信息
 * </pre>
 *
 * Protobuf type {@code protobuf.vo.OnlineRoomPositionCoupleVO}
 */
public final class OnlineRoomPositionCoupleVO extends
    com.google.protobuf.GeneratedMessageV3 implements
    // @@protoc_insertion_point(message_implements:protobuf.vo.OnlineRoomPositionCoupleVO)
    OnlineRoomPositionCoupleVOOrBuilder {
private static final long serialVersionUID = 0L;
  // Use OnlineRoomPositionCoupleVO.newBuilder() to construct.
  private OnlineRoomPositionCoupleVO(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
    super(builder);
  }
  private OnlineRoomPositionCoupleVO() {
    coupleName_ = "";
  }

  @java.lang.Override
  @SuppressWarnings({"unused"})
  protected java.lang.Object newInstance(
      UnusedPrivateParameter unused) {
    return new OnlineRoomPositionCoupleVO();
  }

  @java.lang.Override
  public final com.google.protobuf.UnknownFieldSet
  getUnknownFields() {
    return this.unknownFields;
  }
  private OnlineRoomPositionCoupleVO(
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

            invite_ = input.readBool();
            break;
          }
          case 16: {

            couplePosition_ = input.readInt32();
            break;
          }
          case 26: {
            java.lang.String s = input.readStringRequireUtf8();

            coupleName_ = s;
            break;
          }
          case 32: {

            coupleType_ = input.readUInt32();
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
    return protobuf.vo.OnlineResponse.internal_static_protobuf_vo_OnlineRoomPositionCoupleVO_descriptor;
  }

  @java.lang.Override
  protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internalGetFieldAccessorTable() {
    return protobuf.vo.OnlineResponse.internal_static_protobuf_vo_OnlineRoomPositionCoupleVO_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            protobuf.vo.OnlineRoomPositionCoupleVO.class, protobuf.vo.OnlineRoomPositionCoupleVO.Builder.class);
  }

  public static final int INVITE_FIELD_NUMBER = 1;
  private boolean invite_;
  /**
   * <pre>
   * 发送搭档请求
   * </pre>
   *
   * <code>bool invite = 1;</code>
   * @return The invite.
   */
  @java.lang.Override
  public boolean getInvite() {
    return invite_;
  }

  public static final int COUPLE_POSITION_FIELD_NUMBER = 2;
  private int couplePosition_;
  /**
   * <pre>
   * 搭档位置楼号
   * </pre>
   *
   * <code>int32 couple_position = 2;</code>
   * @return The couplePosition.
   */
  @java.lang.Override
  public int getCouplePosition() {
    return couplePosition_;
  }

  public static final int COUPLE_NAME_FIELD_NUMBER = 3;
  private volatile java.lang.Object coupleName_;
  /**
   * <pre>
   * 搭档名称
   * </pre>
   *
   * <code>string couple_name = 3;</code>
   * @return The coupleName.
   */
  @java.lang.Override
  public java.lang.String getCoupleName() {
    java.lang.Object ref = coupleName_;
    if (ref instanceof java.lang.String) {
      return (java.lang.String) ref;
    } else {
      com.google.protobuf.ByteString bs = 
          (com.google.protobuf.ByteString) ref;
      java.lang.String s = bs.toStringUtf8();
      coupleName_ = s;
      return s;
    }
  }
  /**
   * <pre>
   * 搭档名称
   * </pre>
   *
   * <code>string couple_name = 3;</code>
   * @return The bytes for coupleName.
   */
  @java.lang.Override
  public com.google.protobuf.ByteString
      getCoupleNameBytes() {
    java.lang.Object ref = coupleName_;
    if (ref instanceof java.lang.String) {
      com.google.protobuf.ByteString b = 
          com.google.protobuf.ByteString.copyFromUtf8(
              (java.lang.String) ref);
      coupleName_ = b;
      return b;
    } else {
      return (com.google.protobuf.ByteString) ref;
    }
  }

  public static final int COUPLE_TYPE_FIELD_NUMBER = 4;
  private int coupleType_;
  /**
   * <pre>
   * 搭档类型
   * </pre>
   *
   * <code>uint32 couple_type = 4;</code>
   * @return The coupleType.
   */
  @java.lang.Override
  public int getCoupleType() {
    return coupleType_;
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
    if (invite_ != false) {
      output.writeBool(1, invite_);
    }
    if (couplePosition_ != 0) {
      output.writeInt32(2, couplePosition_);
    }
    if (!com.google.protobuf.GeneratedMessageV3.isStringEmpty(coupleName_)) {
      com.google.protobuf.GeneratedMessageV3.writeString(output, 3, coupleName_);
    }
    if (coupleType_ != 0) {
      output.writeUInt32(4, coupleType_);
    }
    unknownFields.writeTo(output);
  }

  @java.lang.Override
  public int getSerializedSize() {
    int size = memoizedSize;
    if (size != -1) return size;

    size = 0;
    if (invite_ != false) {
      size += com.google.protobuf.CodedOutputStream
        .computeBoolSize(1, invite_);
    }
    if (couplePosition_ != 0) {
      size += com.google.protobuf.CodedOutputStream
        .computeInt32Size(2, couplePosition_);
    }
    if (!com.google.protobuf.GeneratedMessageV3.isStringEmpty(coupleName_)) {
      size += com.google.protobuf.GeneratedMessageV3.computeStringSize(3, coupleName_);
    }
    if (coupleType_ != 0) {
      size += com.google.protobuf.CodedOutputStream
        .computeUInt32Size(4, coupleType_);
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
    if (!(obj instanceof protobuf.vo.OnlineRoomPositionCoupleVO)) {
      return super.equals(obj);
    }
    protobuf.vo.OnlineRoomPositionCoupleVO other = (protobuf.vo.OnlineRoomPositionCoupleVO) obj;

    if (getInvite()
        != other.getInvite()) return false;
    if (getCouplePosition()
        != other.getCouplePosition()) return false;
    if (!getCoupleName()
        .equals(other.getCoupleName())) return false;
    if (getCoupleType()
        != other.getCoupleType()) return false;
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
    hash = (37 * hash) + INVITE_FIELD_NUMBER;
    hash = (53 * hash) + com.google.protobuf.Internal.hashBoolean(
        getInvite());
    hash = (37 * hash) + COUPLE_POSITION_FIELD_NUMBER;
    hash = (53 * hash) + getCouplePosition();
    hash = (37 * hash) + COUPLE_NAME_FIELD_NUMBER;
    hash = (53 * hash) + getCoupleName().hashCode();
    hash = (37 * hash) + COUPLE_TYPE_FIELD_NUMBER;
    hash = (53 * hash) + getCoupleType();
    hash = (29 * hash) + unknownFields.hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static protobuf.vo.OnlineRoomPositionCoupleVO parseFrom(
      java.nio.ByteBuffer data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static protobuf.vo.OnlineRoomPositionCoupleVO parseFrom(
      java.nio.ByteBuffer data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static protobuf.vo.OnlineRoomPositionCoupleVO parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static protobuf.vo.OnlineRoomPositionCoupleVO parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static protobuf.vo.OnlineRoomPositionCoupleVO parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static protobuf.vo.OnlineRoomPositionCoupleVO parseFrom(
      byte[] data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static protobuf.vo.OnlineRoomPositionCoupleVO parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static protobuf.vo.OnlineRoomPositionCoupleVO parseFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }
  public static protobuf.vo.OnlineRoomPositionCoupleVO parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input);
  }
  public static protobuf.vo.OnlineRoomPositionCoupleVO parseDelimitedFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
  }
  public static protobuf.vo.OnlineRoomPositionCoupleVO parseFrom(
      com.google.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static protobuf.vo.OnlineRoomPositionCoupleVO parseFrom(
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
  public static Builder newBuilder(protobuf.vo.OnlineRoomPositionCoupleVO prototype) {
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
   * 搭档信息
   * </pre>
   *
   * Protobuf type {@code protobuf.vo.OnlineRoomPositionCoupleVO}
   */
  public static final class Builder extends
      com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
      // @@protoc_insertion_point(builder_implements:protobuf.vo.OnlineRoomPositionCoupleVO)
      protobuf.vo.OnlineRoomPositionCoupleVOOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return protobuf.vo.OnlineResponse.internal_static_protobuf_vo_OnlineRoomPositionCoupleVO_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return protobuf.vo.OnlineResponse.internal_static_protobuf_vo_OnlineRoomPositionCoupleVO_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              protobuf.vo.OnlineRoomPositionCoupleVO.class, protobuf.vo.OnlineRoomPositionCoupleVO.Builder.class);
    }

    // Construct using protobuf.vo.OnlineRoomPositionCoupleVO.newBuilder()
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
      invite_ = false;

      couplePosition_ = 0;

      coupleName_ = "";

      coupleType_ = 0;

      return this;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.Descriptor
        getDescriptorForType() {
      return protobuf.vo.OnlineResponse.internal_static_protobuf_vo_OnlineRoomPositionCoupleVO_descriptor;
    }

    @java.lang.Override
    public protobuf.vo.OnlineRoomPositionCoupleVO getDefaultInstanceForType() {
      return protobuf.vo.OnlineRoomPositionCoupleVO.getDefaultInstance();
    }

    @java.lang.Override
    public protobuf.vo.OnlineRoomPositionCoupleVO build() {
      protobuf.vo.OnlineRoomPositionCoupleVO result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    @java.lang.Override
    public protobuf.vo.OnlineRoomPositionCoupleVO buildPartial() {
      protobuf.vo.OnlineRoomPositionCoupleVO result = new protobuf.vo.OnlineRoomPositionCoupleVO(this);
      result.invite_ = invite_;
      result.couplePosition_ = couplePosition_;
      result.coupleName_ = coupleName_;
      result.coupleType_ = coupleType_;
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
      if (other instanceof protobuf.vo.OnlineRoomPositionCoupleVO) {
        return mergeFrom((protobuf.vo.OnlineRoomPositionCoupleVO)other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(protobuf.vo.OnlineRoomPositionCoupleVO other) {
      if (other == protobuf.vo.OnlineRoomPositionCoupleVO.getDefaultInstance()) return this;
      if (other.getInvite() != false) {
        setInvite(other.getInvite());
      }
      if (other.getCouplePosition() != 0) {
        setCouplePosition(other.getCouplePosition());
      }
      if (!other.getCoupleName().isEmpty()) {
        coupleName_ = other.coupleName_;
        onChanged();
      }
      if (other.getCoupleType() != 0) {
        setCoupleType(other.getCoupleType());
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
      protobuf.vo.OnlineRoomPositionCoupleVO parsedMessage = null;
      try {
        parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        parsedMessage = (protobuf.vo.OnlineRoomPositionCoupleVO) e.getUnfinishedMessage();
        throw e.unwrapIOException();
      } finally {
        if (parsedMessage != null) {
          mergeFrom(parsedMessage);
        }
      }
      return this;
    }

    private boolean invite_ ;
    /**
     * <pre>
     * 发送搭档请求
     * </pre>
     *
     * <code>bool invite = 1;</code>
     * @return The invite.
     */
    @java.lang.Override
    public boolean getInvite() {
      return invite_;
    }
    /**
     * <pre>
     * 发送搭档请求
     * </pre>
     *
     * <code>bool invite = 1;</code>
     * @param value The invite to set.
     * @return This builder for chaining.
     */
    public Builder setInvite(boolean value) {
      
      invite_ = value;
      onChanged();
      return this;
    }
    /**
     * <pre>
     * 发送搭档请求
     * </pre>
     *
     * <code>bool invite = 1;</code>
     * @return This builder for chaining.
     */
    public Builder clearInvite() {
      
      invite_ = false;
      onChanged();
      return this;
    }

    private int couplePosition_ ;
    /**
     * <pre>
     * 搭档位置楼号
     * </pre>
     *
     * <code>int32 couple_position = 2;</code>
     * @return The couplePosition.
     */
    @java.lang.Override
    public int getCouplePosition() {
      return couplePosition_;
    }
    /**
     * <pre>
     * 搭档位置楼号
     * </pre>
     *
     * <code>int32 couple_position = 2;</code>
     * @param value The couplePosition to set.
     * @return This builder for chaining.
     */
    public Builder setCouplePosition(int value) {
      
      couplePosition_ = value;
      onChanged();
      return this;
    }
    /**
     * <pre>
     * 搭档位置楼号
     * </pre>
     *
     * <code>int32 couple_position = 2;</code>
     * @return This builder for chaining.
     */
    public Builder clearCouplePosition() {
      
      couplePosition_ = 0;
      onChanged();
      return this;
    }

    private java.lang.Object coupleName_ = "";
    /**
     * <pre>
     * 搭档名称
     * </pre>
     *
     * <code>string couple_name = 3;</code>
     * @return The coupleName.
     */
    public java.lang.String getCoupleName() {
      java.lang.Object ref = coupleName_;
      if (!(ref instanceof java.lang.String)) {
        com.google.protobuf.ByteString bs =
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        coupleName_ = s;
        return s;
      } else {
        return (java.lang.String) ref;
      }
    }
    /**
     * <pre>
     * 搭档名称
     * </pre>
     *
     * <code>string couple_name = 3;</code>
     * @return The bytes for coupleName.
     */
    public com.google.protobuf.ByteString
        getCoupleNameBytes() {
      java.lang.Object ref = coupleName_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        coupleName_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }
    /**
     * <pre>
     * 搭档名称
     * </pre>
     *
     * <code>string couple_name = 3;</code>
     * @param value The coupleName to set.
     * @return This builder for chaining.
     */
    public Builder setCoupleName(
        java.lang.String value) {
      if (value == null) {
    throw new NullPointerException();
  }
  
      coupleName_ = value;
      onChanged();
      return this;
    }
    /**
     * <pre>
     * 搭档名称
     * </pre>
     *
     * <code>string couple_name = 3;</code>
     * @return This builder for chaining.
     */
    public Builder clearCoupleName() {
      
      coupleName_ = getDefaultInstance().getCoupleName();
      onChanged();
      return this;
    }
    /**
     * <pre>
     * 搭档名称
     * </pre>
     *
     * <code>string couple_name = 3;</code>
     * @param value The bytes for coupleName to set.
     * @return This builder for chaining.
     */
    public Builder setCoupleNameBytes(
        com.google.protobuf.ByteString value) {
      if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
      
      coupleName_ = value;
      onChanged();
      return this;
    }

    private int coupleType_ ;
    /**
     * <pre>
     * 搭档类型
     * </pre>
     *
     * <code>uint32 couple_type = 4;</code>
     * @return The coupleType.
     */
    @java.lang.Override
    public int getCoupleType() {
      return coupleType_;
    }
    /**
     * <pre>
     * 搭档类型
     * </pre>
     *
     * <code>uint32 couple_type = 4;</code>
     * @param value The coupleType to set.
     * @return This builder for chaining.
     */
    public Builder setCoupleType(int value) {
      
      coupleType_ = value;
      onChanged();
      return this;
    }
    /**
     * <pre>
     * 搭档类型
     * </pre>
     *
     * <code>uint32 couple_type = 4;</code>
     * @return This builder for chaining.
     */
    public Builder clearCoupleType() {
      
      coupleType_ = 0;
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


    // @@protoc_insertion_point(builder_scope:protobuf.vo.OnlineRoomPositionCoupleVO)
  }

  // @@protoc_insertion_point(class_scope:protobuf.vo.OnlineRoomPositionCoupleVO)
  private static final protobuf.vo.OnlineRoomPositionCoupleVO DEFAULT_INSTANCE;
  static {
    DEFAULT_INSTANCE = new protobuf.vo.OnlineRoomPositionCoupleVO();
  }

  public static protobuf.vo.OnlineRoomPositionCoupleVO getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final com.google.protobuf.Parser<OnlineRoomPositionCoupleVO>
      PARSER = new com.google.protobuf.AbstractParser<OnlineRoomPositionCoupleVO>() {
    @java.lang.Override
    public OnlineRoomPositionCoupleVO parsePartialFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return new OnlineRoomPositionCoupleVO(input, extensionRegistry);
    }
  };

  public static com.google.protobuf.Parser<OnlineRoomPositionCoupleVO> parser() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.protobuf.Parser<OnlineRoomPositionCoupleVO> getParserForType() {
    return PARSER;
  }

  @java.lang.Override
  public protobuf.vo.OnlineRoomPositionCoupleVO getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

}


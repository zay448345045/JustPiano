// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: OnlineResponseProtocol.proto

package protobuf.vo;

/**
 * <pre>
 * 每日奖励
 * </pre>
 *
 * Protobuf type {@code protobuf.vo.OnlineDailyVO}
 */
public final class OnlineDailyVO extends
    com.google.protobuf.GeneratedMessageV3 implements
    // @@protoc_insertion_point(message_implements:protobuf.vo.OnlineDailyVO)
    OnlineDailyVOOrBuilder {
private static final long serialVersionUID = 0L;
  // Use OnlineDailyVO.newBuilder() to construct.
  private OnlineDailyVO(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
    super(builder);
  }
  private OnlineDailyVO() {
  }

  @java.lang.Override
  @SuppressWarnings({"unused"})
  protected java.lang.Object newInstance(
      UnusedPrivateParameter unused) {
    return new OnlineDailyVO();
  }

  @java.lang.Override
  public final com.google.protobuf.UnknownFieldSet
  getUnknownFields() {
    return this.unknownFields;
  }
  private OnlineDailyVO(
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

            type_ = input.readUInt32();
            break;
          }
          case 18: {
            protobuf.vo.OnlineDailyTimeListVO.Builder subBuilder = null;
            if (dailyCase_ == 2) {
              subBuilder = ((protobuf.vo.OnlineDailyTimeListVO) daily_).toBuilder();
            }
            daily_ =
                input.readMessage(protobuf.vo.OnlineDailyTimeListVO.parser(), extensionRegistry);
            if (subBuilder != null) {
              subBuilder.mergeFrom((protobuf.vo.OnlineDailyTimeListVO) daily_);
              daily_ = subBuilder.buildPartial();
            }
            dailyCase_ = 2;
            break;
          }
          case 26: {
            protobuf.vo.OnlineDailyPrizeGetVO.Builder subBuilder = null;
            if (dailyCase_ == 3) {
              subBuilder = ((protobuf.vo.OnlineDailyPrizeGetVO) daily_).toBuilder();
            }
            daily_ =
                input.readMessage(protobuf.vo.OnlineDailyPrizeGetVO.parser(), extensionRegistry);
            if (subBuilder != null) {
              subBuilder.mergeFrom((protobuf.vo.OnlineDailyPrizeGetVO) daily_);
              daily_ = subBuilder.buildPartial();
            }
            dailyCase_ = 3;
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
    return protobuf.vo.OnlineResponse.internal_static_protobuf_vo_OnlineDailyVO_descriptor;
  }

  @java.lang.Override
  protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internalGetFieldAccessorTable() {
    return protobuf.vo.OnlineResponse.internal_static_protobuf_vo_OnlineDailyVO_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            protobuf.vo.OnlineDailyVO.class, protobuf.vo.OnlineDailyVO.Builder.class);
  }

  private int dailyCase_ = 0;
  private java.lang.Object daily_;
  public enum DailyCase
      implements com.google.protobuf.Internal.EnumLite,
          com.google.protobuf.AbstractMessage.InternalOneOfEnum {
    DAILY_TIME_LIST(2),
    DAILY_PRIZE_GET(3),
    DAILY_NOT_SET(0);
    private final int value;
    private DailyCase(int value) {
      this.value = value;
    }
    /**
     * @param value The number of the enum to look for.
     * @return The enum associated with the given number.
     * @deprecated Use {@link #forNumber(int)} instead.
     */
    @java.lang.Deprecated
    public static DailyCase valueOf(int value) {
      return forNumber(value);
    }

    public static DailyCase forNumber(int value) {
      switch (value) {
        case 2: return DAILY_TIME_LIST;
        case 3: return DAILY_PRIZE_GET;
        case 0: return DAILY_NOT_SET;
        default: return null;
      }
    }
    public int getNumber() {
      return this.value;
    }
  };

  public DailyCase
  getDailyCase() {
    return DailyCase.forNumber(
        dailyCase_);
  }

  public static final int TYPE_FIELD_NUMBER = 1;
  private int type_;
  /**
   * <pre>
   * 消息类型
   * </pre>
   *
   * <code>uint32 type = 1;</code>
   * @return The type.
   */
  @java.lang.Override
  public int getType() {
    return type_;
  }

  public static final int DAILY_TIME_LIST_FIELD_NUMBER = 2;
  /**
   * <pre>
   * 昨日在线时长列表
   * </pre>
   *
   * <code>.protobuf.vo.OnlineDailyTimeListVO daily_time_list = 2;</code>
   * @return Whether the dailyTimeList field is set.
   */
  @java.lang.Override
  public boolean hasDailyTimeList() {
    return dailyCase_ == 2;
  }
  /**
   * <pre>
   * 昨日在线时长列表
   * </pre>
   *
   * <code>.protobuf.vo.OnlineDailyTimeListVO daily_time_list = 2;</code>
   * @return The dailyTimeList.
   */
  @java.lang.Override
  public protobuf.vo.OnlineDailyTimeListVO getDailyTimeList() {
    if (dailyCase_ == 2) {
       return (protobuf.vo.OnlineDailyTimeListVO) daily_;
    }
    return protobuf.vo.OnlineDailyTimeListVO.getDefaultInstance();
  }
  /**
   * <pre>
   * 昨日在线时长列表
   * </pre>
   *
   * <code>.protobuf.vo.OnlineDailyTimeListVO daily_time_list = 2;</code>
   */
  @java.lang.Override
  public protobuf.vo.OnlineDailyTimeListVOOrBuilder getDailyTimeListOrBuilder() {
    if (dailyCase_ == 2) {
       return (protobuf.vo.OnlineDailyTimeListVO) daily_;
    }
    return protobuf.vo.OnlineDailyTimeListVO.getDefaultInstance();
  }

  public static final int DAILY_PRIZE_GET_FIELD_NUMBER = 3;
  /**
   * <pre>
   * 领取每日奖励
   * </pre>
   *
   * <code>.protobuf.vo.OnlineDailyPrizeGetVO daily_prize_get = 3;</code>
   * @return Whether the dailyPrizeGet field is set.
   */
  @java.lang.Override
  public boolean hasDailyPrizeGet() {
    return dailyCase_ == 3;
  }
  /**
   * <pre>
   * 领取每日奖励
   * </pre>
   *
   * <code>.protobuf.vo.OnlineDailyPrizeGetVO daily_prize_get = 3;</code>
   * @return The dailyPrizeGet.
   */
  @java.lang.Override
  public protobuf.vo.OnlineDailyPrizeGetVO getDailyPrizeGet() {
    if (dailyCase_ == 3) {
       return (protobuf.vo.OnlineDailyPrizeGetVO) daily_;
    }
    return protobuf.vo.OnlineDailyPrizeGetVO.getDefaultInstance();
  }
  /**
   * <pre>
   * 领取每日奖励
   * </pre>
   *
   * <code>.protobuf.vo.OnlineDailyPrizeGetVO daily_prize_get = 3;</code>
   */
  @java.lang.Override
  public protobuf.vo.OnlineDailyPrizeGetVOOrBuilder getDailyPrizeGetOrBuilder() {
    if (dailyCase_ == 3) {
       return (protobuf.vo.OnlineDailyPrizeGetVO) daily_;
    }
    return protobuf.vo.OnlineDailyPrizeGetVO.getDefaultInstance();
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
    if (type_ != 0) {
      output.writeUInt32(1, type_);
    }
    if (dailyCase_ == 2) {
      output.writeMessage(2, (protobuf.vo.OnlineDailyTimeListVO) daily_);
    }
    if (dailyCase_ == 3) {
      output.writeMessage(3, (protobuf.vo.OnlineDailyPrizeGetVO) daily_);
    }
    unknownFields.writeTo(output);
  }

  @java.lang.Override
  public int getSerializedSize() {
    int size = memoizedSize;
    if (size != -1) return size;

    size = 0;
    if (type_ != 0) {
      size += com.google.protobuf.CodedOutputStream
        .computeUInt32Size(1, type_);
    }
    if (dailyCase_ == 2) {
      size += com.google.protobuf.CodedOutputStream
        .computeMessageSize(2, (protobuf.vo.OnlineDailyTimeListVO) daily_);
    }
    if (dailyCase_ == 3) {
      size += com.google.protobuf.CodedOutputStream
        .computeMessageSize(3, (protobuf.vo.OnlineDailyPrizeGetVO) daily_);
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
    if (!(obj instanceof protobuf.vo.OnlineDailyVO)) {
      return super.equals(obj);
    }
    protobuf.vo.OnlineDailyVO other = (protobuf.vo.OnlineDailyVO) obj;

    if (getType()
        != other.getType()) return false;
    if (!getDailyCase().equals(other.getDailyCase())) return false;
    switch (dailyCase_) {
      case 2:
        if (!getDailyTimeList()
            .equals(other.getDailyTimeList())) return false;
        break;
      case 3:
        if (!getDailyPrizeGet()
            .equals(other.getDailyPrizeGet())) return false;
        break;
      case 0:
      default:
    }
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
    hash = (37 * hash) + TYPE_FIELD_NUMBER;
    hash = (53 * hash) + getType();
    switch (dailyCase_) {
      case 2:
        hash = (37 * hash) + DAILY_TIME_LIST_FIELD_NUMBER;
        hash = (53 * hash) + getDailyTimeList().hashCode();
        break;
      case 3:
        hash = (37 * hash) + DAILY_PRIZE_GET_FIELD_NUMBER;
        hash = (53 * hash) + getDailyPrizeGet().hashCode();
        break;
      case 0:
      default:
    }
    hash = (29 * hash) + unknownFields.hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static protobuf.vo.OnlineDailyVO parseFrom(
      java.nio.ByteBuffer data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static protobuf.vo.OnlineDailyVO parseFrom(
      java.nio.ByteBuffer data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static protobuf.vo.OnlineDailyVO parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static protobuf.vo.OnlineDailyVO parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static protobuf.vo.OnlineDailyVO parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static protobuf.vo.OnlineDailyVO parseFrom(
      byte[] data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static protobuf.vo.OnlineDailyVO parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static protobuf.vo.OnlineDailyVO parseFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }
  public static protobuf.vo.OnlineDailyVO parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input);
  }
  public static protobuf.vo.OnlineDailyVO parseDelimitedFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
  }
  public static protobuf.vo.OnlineDailyVO parseFrom(
      com.google.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static protobuf.vo.OnlineDailyVO parseFrom(
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
  public static Builder newBuilder(protobuf.vo.OnlineDailyVO prototype) {
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
   * 每日奖励
   * </pre>
   *
   * Protobuf type {@code protobuf.vo.OnlineDailyVO}
   */
  public static final class Builder extends
      com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
      // @@protoc_insertion_point(builder_implements:protobuf.vo.OnlineDailyVO)
      protobuf.vo.OnlineDailyVOOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return protobuf.vo.OnlineResponse.internal_static_protobuf_vo_OnlineDailyVO_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return protobuf.vo.OnlineResponse.internal_static_protobuf_vo_OnlineDailyVO_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              protobuf.vo.OnlineDailyVO.class, protobuf.vo.OnlineDailyVO.Builder.class);
    }

    // Construct using protobuf.vo.OnlineDailyVO.newBuilder()
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
      type_ = 0;

      dailyCase_ = 0;
      daily_ = null;
      return this;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.Descriptor
        getDescriptorForType() {
      return protobuf.vo.OnlineResponse.internal_static_protobuf_vo_OnlineDailyVO_descriptor;
    }

    @java.lang.Override
    public protobuf.vo.OnlineDailyVO getDefaultInstanceForType() {
      return protobuf.vo.OnlineDailyVO.getDefaultInstance();
    }

    @java.lang.Override
    public protobuf.vo.OnlineDailyVO build() {
      protobuf.vo.OnlineDailyVO result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    @java.lang.Override
    public protobuf.vo.OnlineDailyVO buildPartial() {
      protobuf.vo.OnlineDailyVO result = new protobuf.vo.OnlineDailyVO(this);
      result.type_ = type_;
      if (dailyCase_ == 2) {
        if (dailyTimeListBuilder_ == null) {
          result.daily_ = daily_;
        } else {
          result.daily_ = dailyTimeListBuilder_.build();
        }
      }
      if (dailyCase_ == 3) {
        if (dailyPrizeGetBuilder_ == null) {
          result.daily_ = daily_;
        } else {
          result.daily_ = dailyPrizeGetBuilder_.build();
        }
      }
      result.dailyCase_ = dailyCase_;
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
      if (other instanceof protobuf.vo.OnlineDailyVO) {
        return mergeFrom((protobuf.vo.OnlineDailyVO)other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(protobuf.vo.OnlineDailyVO other) {
      if (other == protobuf.vo.OnlineDailyVO.getDefaultInstance()) return this;
      if (other.getType() != 0) {
        setType(other.getType());
      }
      switch (other.getDailyCase()) {
        case DAILY_TIME_LIST: {
          mergeDailyTimeList(other.getDailyTimeList());
          break;
        }
        case DAILY_PRIZE_GET: {
          mergeDailyPrizeGet(other.getDailyPrizeGet());
          break;
        }
        case DAILY_NOT_SET: {
          break;
        }
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
      protobuf.vo.OnlineDailyVO parsedMessage = null;
      try {
        parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        parsedMessage = (protobuf.vo.OnlineDailyVO) e.getUnfinishedMessage();
        throw e.unwrapIOException();
      } finally {
        if (parsedMessage != null) {
          mergeFrom(parsedMessage);
        }
      }
      return this;
    }
    private int dailyCase_ = 0;
    private java.lang.Object daily_;
    public DailyCase
        getDailyCase() {
      return DailyCase.forNumber(
          dailyCase_);
    }

    public Builder clearDaily() {
      dailyCase_ = 0;
      daily_ = null;
      onChanged();
      return this;
    }


    private int type_ ;
    /**
     * <pre>
     * 消息类型
     * </pre>
     *
     * <code>uint32 type = 1;</code>
     * @return The type.
     */
    @java.lang.Override
    public int getType() {
      return type_;
    }
    /**
     * <pre>
     * 消息类型
     * </pre>
     *
     * <code>uint32 type = 1;</code>
     * @param value The type to set.
     * @return This builder for chaining.
     */
    public Builder setType(int value) {
      
      type_ = value;
      onChanged();
      return this;
    }
    /**
     * <pre>
     * 消息类型
     * </pre>
     *
     * <code>uint32 type = 1;</code>
     * @return This builder for chaining.
     */
    public Builder clearType() {
      
      type_ = 0;
      onChanged();
      return this;
    }

    private com.google.protobuf.SingleFieldBuilderV3<
        protobuf.vo.OnlineDailyTimeListVO, protobuf.vo.OnlineDailyTimeListVO.Builder, protobuf.vo.OnlineDailyTimeListVOOrBuilder> dailyTimeListBuilder_;
    /**
     * <pre>
     * 昨日在线时长列表
     * </pre>
     *
     * <code>.protobuf.vo.OnlineDailyTimeListVO daily_time_list = 2;</code>
     * @return Whether the dailyTimeList field is set.
     */
    @java.lang.Override
    public boolean hasDailyTimeList() {
      return dailyCase_ == 2;
    }
    /**
     * <pre>
     * 昨日在线时长列表
     * </pre>
     *
     * <code>.protobuf.vo.OnlineDailyTimeListVO daily_time_list = 2;</code>
     * @return The dailyTimeList.
     */
    @java.lang.Override
    public protobuf.vo.OnlineDailyTimeListVO getDailyTimeList() {
      if (dailyTimeListBuilder_ == null) {
        if (dailyCase_ == 2) {
          return (protobuf.vo.OnlineDailyTimeListVO) daily_;
        }
        return protobuf.vo.OnlineDailyTimeListVO.getDefaultInstance();
      } else {
        if (dailyCase_ == 2) {
          return dailyTimeListBuilder_.getMessage();
        }
        return protobuf.vo.OnlineDailyTimeListVO.getDefaultInstance();
      }
    }
    /**
     * <pre>
     * 昨日在线时长列表
     * </pre>
     *
     * <code>.protobuf.vo.OnlineDailyTimeListVO daily_time_list = 2;</code>
     */
    public Builder setDailyTimeList(protobuf.vo.OnlineDailyTimeListVO value) {
      if (dailyTimeListBuilder_ == null) {
        if (value == null) {
          throw new NullPointerException();
        }
        daily_ = value;
        onChanged();
      } else {
        dailyTimeListBuilder_.setMessage(value);
      }
      dailyCase_ = 2;
      return this;
    }
    /**
     * <pre>
     * 昨日在线时长列表
     * </pre>
     *
     * <code>.protobuf.vo.OnlineDailyTimeListVO daily_time_list = 2;</code>
     */
    public Builder setDailyTimeList(
        protobuf.vo.OnlineDailyTimeListVO.Builder builderForValue) {
      if (dailyTimeListBuilder_ == null) {
        daily_ = builderForValue.build();
        onChanged();
      } else {
        dailyTimeListBuilder_.setMessage(builderForValue.build());
      }
      dailyCase_ = 2;
      return this;
    }
    /**
     * <pre>
     * 昨日在线时长列表
     * </pre>
     *
     * <code>.protobuf.vo.OnlineDailyTimeListVO daily_time_list = 2;</code>
     */
    public Builder mergeDailyTimeList(protobuf.vo.OnlineDailyTimeListVO value) {
      if (dailyTimeListBuilder_ == null) {
        if (dailyCase_ == 2 &&
            daily_ != protobuf.vo.OnlineDailyTimeListVO.getDefaultInstance()) {
          daily_ = protobuf.vo.OnlineDailyTimeListVO.newBuilder((protobuf.vo.OnlineDailyTimeListVO) daily_)
              .mergeFrom(value).buildPartial();
        } else {
          daily_ = value;
        }
        onChanged();
      } else {
        if (dailyCase_ == 2) {
          dailyTimeListBuilder_.mergeFrom(value);
        }
        dailyTimeListBuilder_.setMessage(value);
      }
      dailyCase_ = 2;
      return this;
    }
    /**
     * <pre>
     * 昨日在线时长列表
     * </pre>
     *
     * <code>.protobuf.vo.OnlineDailyTimeListVO daily_time_list = 2;</code>
     */
    public Builder clearDailyTimeList() {
      if (dailyTimeListBuilder_ == null) {
        if (dailyCase_ == 2) {
          dailyCase_ = 0;
          daily_ = null;
          onChanged();
        }
      } else {
        if (dailyCase_ == 2) {
          dailyCase_ = 0;
          daily_ = null;
        }
        dailyTimeListBuilder_.clear();
      }
      return this;
    }
    /**
     * <pre>
     * 昨日在线时长列表
     * </pre>
     *
     * <code>.protobuf.vo.OnlineDailyTimeListVO daily_time_list = 2;</code>
     */
    public protobuf.vo.OnlineDailyTimeListVO.Builder getDailyTimeListBuilder() {
      return getDailyTimeListFieldBuilder().getBuilder();
    }
    /**
     * <pre>
     * 昨日在线时长列表
     * </pre>
     *
     * <code>.protobuf.vo.OnlineDailyTimeListVO daily_time_list = 2;</code>
     */
    @java.lang.Override
    public protobuf.vo.OnlineDailyTimeListVOOrBuilder getDailyTimeListOrBuilder() {
      if ((dailyCase_ == 2) && (dailyTimeListBuilder_ != null)) {
        return dailyTimeListBuilder_.getMessageOrBuilder();
      } else {
        if (dailyCase_ == 2) {
          return (protobuf.vo.OnlineDailyTimeListVO) daily_;
        }
        return protobuf.vo.OnlineDailyTimeListVO.getDefaultInstance();
      }
    }
    /**
     * <pre>
     * 昨日在线时长列表
     * </pre>
     *
     * <code>.protobuf.vo.OnlineDailyTimeListVO daily_time_list = 2;</code>
     */
    private com.google.protobuf.SingleFieldBuilderV3<
        protobuf.vo.OnlineDailyTimeListVO, protobuf.vo.OnlineDailyTimeListVO.Builder, protobuf.vo.OnlineDailyTimeListVOOrBuilder> 
        getDailyTimeListFieldBuilder() {
      if (dailyTimeListBuilder_ == null) {
        if (!(dailyCase_ == 2)) {
          daily_ = protobuf.vo.OnlineDailyTimeListVO.getDefaultInstance();
        }
        dailyTimeListBuilder_ = new com.google.protobuf.SingleFieldBuilderV3<
            protobuf.vo.OnlineDailyTimeListVO, protobuf.vo.OnlineDailyTimeListVO.Builder, protobuf.vo.OnlineDailyTimeListVOOrBuilder>(
                (protobuf.vo.OnlineDailyTimeListVO) daily_,
                getParentForChildren(),
                isClean());
        daily_ = null;
      }
      dailyCase_ = 2;
      onChanged();;
      return dailyTimeListBuilder_;
    }

    private com.google.protobuf.SingleFieldBuilderV3<
        protobuf.vo.OnlineDailyPrizeGetVO, protobuf.vo.OnlineDailyPrizeGetVO.Builder, protobuf.vo.OnlineDailyPrizeGetVOOrBuilder> dailyPrizeGetBuilder_;
    /**
     * <pre>
     * 领取每日奖励
     * </pre>
     *
     * <code>.protobuf.vo.OnlineDailyPrizeGetVO daily_prize_get = 3;</code>
     * @return Whether the dailyPrizeGet field is set.
     */
    @java.lang.Override
    public boolean hasDailyPrizeGet() {
      return dailyCase_ == 3;
    }
    /**
     * <pre>
     * 领取每日奖励
     * </pre>
     *
     * <code>.protobuf.vo.OnlineDailyPrizeGetVO daily_prize_get = 3;</code>
     * @return The dailyPrizeGet.
     */
    @java.lang.Override
    public protobuf.vo.OnlineDailyPrizeGetVO getDailyPrizeGet() {
      if (dailyPrizeGetBuilder_ == null) {
        if (dailyCase_ == 3) {
          return (protobuf.vo.OnlineDailyPrizeGetVO) daily_;
        }
        return protobuf.vo.OnlineDailyPrizeGetVO.getDefaultInstance();
      } else {
        if (dailyCase_ == 3) {
          return dailyPrizeGetBuilder_.getMessage();
        }
        return protobuf.vo.OnlineDailyPrizeGetVO.getDefaultInstance();
      }
    }
    /**
     * <pre>
     * 领取每日奖励
     * </pre>
     *
     * <code>.protobuf.vo.OnlineDailyPrizeGetVO daily_prize_get = 3;</code>
     */
    public Builder setDailyPrizeGet(protobuf.vo.OnlineDailyPrizeGetVO value) {
      if (dailyPrizeGetBuilder_ == null) {
        if (value == null) {
          throw new NullPointerException();
        }
        daily_ = value;
        onChanged();
      } else {
        dailyPrizeGetBuilder_.setMessage(value);
      }
      dailyCase_ = 3;
      return this;
    }
    /**
     * <pre>
     * 领取每日奖励
     * </pre>
     *
     * <code>.protobuf.vo.OnlineDailyPrizeGetVO daily_prize_get = 3;</code>
     */
    public Builder setDailyPrizeGet(
        protobuf.vo.OnlineDailyPrizeGetVO.Builder builderForValue) {
      if (dailyPrizeGetBuilder_ == null) {
        daily_ = builderForValue.build();
        onChanged();
      } else {
        dailyPrizeGetBuilder_.setMessage(builderForValue.build());
      }
      dailyCase_ = 3;
      return this;
    }
    /**
     * <pre>
     * 领取每日奖励
     * </pre>
     *
     * <code>.protobuf.vo.OnlineDailyPrizeGetVO daily_prize_get = 3;</code>
     */
    public Builder mergeDailyPrizeGet(protobuf.vo.OnlineDailyPrizeGetVO value) {
      if (dailyPrizeGetBuilder_ == null) {
        if (dailyCase_ == 3 &&
            daily_ != protobuf.vo.OnlineDailyPrizeGetVO.getDefaultInstance()) {
          daily_ = protobuf.vo.OnlineDailyPrizeGetVO.newBuilder((protobuf.vo.OnlineDailyPrizeGetVO) daily_)
              .mergeFrom(value).buildPartial();
        } else {
          daily_ = value;
        }
        onChanged();
      } else {
        if (dailyCase_ == 3) {
          dailyPrizeGetBuilder_.mergeFrom(value);
        }
        dailyPrizeGetBuilder_.setMessage(value);
      }
      dailyCase_ = 3;
      return this;
    }
    /**
     * <pre>
     * 领取每日奖励
     * </pre>
     *
     * <code>.protobuf.vo.OnlineDailyPrizeGetVO daily_prize_get = 3;</code>
     */
    public Builder clearDailyPrizeGet() {
      if (dailyPrizeGetBuilder_ == null) {
        if (dailyCase_ == 3) {
          dailyCase_ = 0;
          daily_ = null;
          onChanged();
        }
      } else {
        if (dailyCase_ == 3) {
          dailyCase_ = 0;
          daily_ = null;
        }
        dailyPrizeGetBuilder_.clear();
      }
      return this;
    }
    /**
     * <pre>
     * 领取每日奖励
     * </pre>
     *
     * <code>.protobuf.vo.OnlineDailyPrizeGetVO daily_prize_get = 3;</code>
     */
    public protobuf.vo.OnlineDailyPrizeGetVO.Builder getDailyPrizeGetBuilder() {
      return getDailyPrizeGetFieldBuilder().getBuilder();
    }
    /**
     * <pre>
     * 领取每日奖励
     * </pre>
     *
     * <code>.protobuf.vo.OnlineDailyPrizeGetVO daily_prize_get = 3;</code>
     */
    @java.lang.Override
    public protobuf.vo.OnlineDailyPrizeGetVOOrBuilder getDailyPrizeGetOrBuilder() {
      if ((dailyCase_ == 3) && (dailyPrizeGetBuilder_ != null)) {
        return dailyPrizeGetBuilder_.getMessageOrBuilder();
      } else {
        if (dailyCase_ == 3) {
          return (protobuf.vo.OnlineDailyPrizeGetVO) daily_;
        }
        return protobuf.vo.OnlineDailyPrizeGetVO.getDefaultInstance();
      }
    }
    /**
     * <pre>
     * 领取每日奖励
     * </pre>
     *
     * <code>.protobuf.vo.OnlineDailyPrizeGetVO daily_prize_get = 3;</code>
     */
    private com.google.protobuf.SingleFieldBuilderV3<
        protobuf.vo.OnlineDailyPrizeGetVO, protobuf.vo.OnlineDailyPrizeGetVO.Builder, protobuf.vo.OnlineDailyPrizeGetVOOrBuilder> 
        getDailyPrizeGetFieldBuilder() {
      if (dailyPrizeGetBuilder_ == null) {
        if (!(dailyCase_ == 3)) {
          daily_ = protobuf.vo.OnlineDailyPrizeGetVO.getDefaultInstance();
        }
        dailyPrizeGetBuilder_ = new com.google.protobuf.SingleFieldBuilderV3<
            protobuf.vo.OnlineDailyPrizeGetVO, protobuf.vo.OnlineDailyPrizeGetVO.Builder, protobuf.vo.OnlineDailyPrizeGetVOOrBuilder>(
                (protobuf.vo.OnlineDailyPrizeGetVO) daily_,
                getParentForChildren(),
                isClean());
        daily_ = null;
      }
      dailyCase_ = 3;
      onChanged();;
      return dailyPrizeGetBuilder_;
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


    // @@protoc_insertion_point(builder_scope:protobuf.vo.OnlineDailyVO)
  }

  // @@protoc_insertion_point(class_scope:protobuf.vo.OnlineDailyVO)
  private static final protobuf.vo.OnlineDailyVO DEFAULT_INSTANCE;
  static {
    DEFAULT_INSTANCE = new protobuf.vo.OnlineDailyVO();
  }

  public static protobuf.vo.OnlineDailyVO getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final com.google.protobuf.Parser<OnlineDailyVO>
      PARSER = new com.google.protobuf.AbstractParser<OnlineDailyVO>() {
    @java.lang.Override
    public OnlineDailyVO parsePartialFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return new OnlineDailyVO(input, extensionRegistry);
    }
  };

  public static com.google.protobuf.Parser<OnlineDailyVO> parser() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.protobuf.Parser<OnlineDailyVO> getParserForType() {
    return PARSER;
  }

  @java.lang.Override
  public protobuf.vo.OnlineDailyVO getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

}


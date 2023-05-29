// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: OnlineResponseProtocol.proto

package protobuf.vo;

/**
 * <pre>
 * 弹奏界面左上角成绩条
 * </pre>
 *
 * Protobuf type {@code protobuf.vo.OnlineMiniGradeVO}
 */
public final class OnlineMiniGradeVO extends
    com.google.protobuf.GeneratedMessageV3 implements
    // @@protoc_insertion_point(message_implements:protobuf.vo.OnlineMiniGradeVO)
    OnlineMiniGradeVOOrBuilder {
private static final long serialVersionUID = 0L;
  // Use OnlineMiniGradeVO.newBuilder() to construct.
  private OnlineMiniGradeVO(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
    super(builder);
  }
  private OnlineMiniGradeVO() {
  }

  @java.lang.Override
  @SuppressWarnings({"unused"})
  protected java.lang.Object newInstance(
      UnusedPrivateParameter unused) {
    return new OnlineMiniGradeVO();
  }

  @java.lang.Override
  public final com.google.protobuf.UnknownFieldSet
  getUnknownFields() {
    return this.unknownFields;
  }
  private OnlineMiniGradeVO(
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

            combo_ = input.readUInt32();
            break;
          }
          case 16: {

            score_ = input.readUInt32();
            break;
          }
          case 24: {

            roomPosition_ = input.readUInt32();
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
    return protobuf.vo.OnlineResponse.internal_static_protobuf_vo_OnlineMiniGradeVO_descriptor;
  }

  @java.lang.Override
  protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internalGetFieldAccessorTable() {
    return protobuf.vo.OnlineResponse.internal_static_protobuf_vo_OnlineMiniGradeVO_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            protobuf.vo.OnlineMiniGradeVO.class, protobuf.vo.OnlineMiniGradeVO.Builder.class);
  }

  public static final int COMBO_FIELD_NUMBER = 1;
  private int combo_;
  /**
   * <pre>
   * 连击数量
   * </pre>
   *
   * <code>uint32 combo = 1;</code>
   * @return The combo.
   */
  @java.lang.Override
  public int getCombo() {
    return combo_;
  }

  public static final int SCORE_FIELD_NUMBER = 2;
  private int score_;
  /**
   * <pre>
   * 用户当前弹奏分数
   * </pre>
   *
   * <code>uint32 score = 2;</code>
   * @return The score.
   */
  @java.lang.Override
  public int getScore() {
    return score_;
  }

  public static final int ROOM_POSITION_FIELD_NUMBER = 3;
  private int roomPosition_;
  /**
   * <pre>
   * 用户所在房间位置
   * </pre>
   *
   * <code>uint32 room_position = 3;</code>
   * @return The roomPosition.
   */
  @java.lang.Override
  public int getRoomPosition() {
    return roomPosition_;
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
    if (combo_ != 0) {
      output.writeUInt32(1, combo_);
    }
    if (score_ != 0) {
      output.writeUInt32(2, score_);
    }
    if (roomPosition_ != 0) {
      output.writeUInt32(3, roomPosition_);
    }
    unknownFields.writeTo(output);
  }

  @java.lang.Override
  public int getSerializedSize() {
    int size = memoizedSize;
    if (size != -1) return size;

    size = 0;
    if (combo_ != 0) {
      size += com.google.protobuf.CodedOutputStream
        .computeUInt32Size(1, combo_);
    }
    if (score_ != 0) {
      size += com.google.protobuf.CodedOutputStream
        .computeUInt32Size(2, score_);
    }
    if (roomPosition_ != 0) {
      size += com.google.protobuf.CodedOutputStream
        .computeUInt32Size(3, roomPosition_);
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
    if (!(obj instanceof protobuf.vo.OnlineMiniGradeVO)) {
      return super.equals(obj);
    }
    protobuf.vo.OnlineMiniGradeVO other = (protobuf.vo.OnlineMiniGradeVO) obj;

    if (getCombo()
        != other.getCombo()) return false;
    if (getScore()
        != other.getScore()) return false;
    if (getRoomPosition()
        != other.getRoomPosition()) return false;
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
    hash = (37 * hash) + COMBO_FIELD_NUMBER;
    hash = (53 * hash) + getCombo();
    hash = (37 * hash) + SCORE_FIELD_NUMBER;
    hash = (53 * hash) + getScore();
    hash = (37 * hash) + ROOM_POSITION_FIELD_NUMBER;
    hash = (53 * hash) + getRoomPosition();
    hash = (29 * hash) + unknownFields.hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static protobuf.vo.OnlineMiniGradeVO parseFrom(
      java.nio.ByteBuffer data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static protobuf.vo.OnlineMiniGradeVO parseFrom(
      java.nio.ByteBuffer data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static protobuf.vo.OnlineMiniGradeVO parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static protobuf.vo.OnlineMiniGradeVO parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static protobuf.vo.OnlineMiniGradeVO parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static protobuf.vo.OnlineMiniGradeVO parseFrom(
      byte[] data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static protobuf.vo.OnlineMiniGradeVO parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static protobuf.vo.OnlineMiniGradeVO parseFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }
  public static protobuf.vo.OnlineMiniGradeVO parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input);
  }
  public static protobuf.vo.OnlineMiniGradeVO parseDelimitedFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
  }
  public static protobuf.vo.OnlineMiniGradeVO parseFrom(
      com.google.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static protobuf.vo.OnlineMiniGradeVO parseFrom(
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
  public static Builder newBuilder(protobuf.vo.OnlineMiniGradeVO prototype) {
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
   * 弹奏界面左上角成绩条
   * </pre>
   *
   * Protobuf type {@code protobuf.vo.OnlineMiniGradeVO}
   */
  public static final class Builder extends
      com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
      // @@protoc_insertion_point(builder_implements:protobuf.vo.OnlineMiniGradeVO)
      protobuf.vo.OnlineMiniGradeVOOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return protobuf.vo.OnlineResponse.internal_static_protobuf_vo_OnlineMiniGradeVO_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return protobuf.vo.OnlineResponse.internal_static_protobuf_vo_OnlineMiniGradeVO_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              protobuf.vo.OnlineMiniGradeVO.class, protobuf.vo.OnlineMiniGradeVO.Builder.class);
    }

    // Construct using protobuf.vo.OnlineMiniGradeVO.newBuilder()
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
      combo_ = 0;

      score_ = 0;

      roomPosition_ = 0;

      return this;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.Descriptor
        getDescriptorForType() {
      return protobuf.vo.OnlineResponse.internal_static_protobuf_vo_OnlineMiniGradeVO_descriptor;
    }

    @java.lang.Override
    public protobuf.vo.OnlineMiniGradeVO getDefaultInstanceForType() {
      return protobuf.vo.OnlineMiniGradeVO.getDefaultInstance();
    }

    @java.lang.Override
    public protobuf.vo.OnlineMiniGradeVO build() {
      protobuf.vo.OnlineMiniGradeVO result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    @java.lang.Override
    public protobuf.vo.OnlineMiniGradeVO buildPartial() {
      protobuf.vo.OnlineMiniGradeVO result = new protobuf.vo.OnlineMiniGradeVO(this);
      result.combo_ = combo_;
      result.score_ = score_;
      result.roomPosition_ = roomPosition_;
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
      if (other instanceof protobuf.vo.OnlineMiniGradeVO) {
        return mergeFrom((protobuf.vo.OnlineMiniGradeVO)other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(protobuf.vo.OnlineMiniGradeVO other) {
      if (other == protobuf.vo.OnlineMiniGradeVO.getDefaultInstance()) return this;
      if (other.getCombo() != 0) {
        setCombo(other.getCombo());
      }
      if (other.getScore() != 0) {
        setScore(other.getScore());
      }
      if (other.getRoomPosition() != 0) {
        setRoomPosition(other.getRoomPosition());
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
      protobuf.vo.OnlineMiniGradeVO parsedMessage = null;
      try {
        parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        parsedMessage = (protobuf.vo.OnlineMiniGradeVO) e.getUnfinishedMessage();
        throw e.unwrapIOException();
      } finally {
        if (parsedMessage != null) {
          mergeFrom(parsedMessage);
        }
      }
      return this;
    }

    private int combo_ ;
    /**
     * <pre>
     * 连击数量
     * </pre>
     *
     * <code>uint32 combo = 1;</code>
     * @return The combo.
     */
    @java.lang.Override
    public int getCombo() {
      return combo_;
    }
    /**
     * <pre>
     * 连击数量
     * </pre>
     *
     * <code>uint32 combo = 1;</code>
     * @param value The combo to set.
     * @return This builder for chaining.
     */
    public Builder setCombo(int value) {
      
      combo_ = value;
      onChanged();
      return this;
    }
    /**
     * <pre>
     * 连击数量
     * </pre>
     *
     * <code>uint32 combo = 1;</code>
     * @return This builder for chaining.
     */
    public Builder clearCombo() {
      
      combo_ = 0;
      onChanged();
      return this;
    }

    private int score_ ;
    /**
     * <pre>
     * 用户当前弹奏分数
     * </pre>
     *
     * <code>uint32 score = 2;</code>
     * @return The score.
     */
    @java.lang.Override
    public int getScore() {
      return score_;
    }
    /**
     * <pre>
     * 用户当前弹奏分数
     * </pre>
     *
     * <code>uint32 score = 2;</code>
     * @param value The score to set.
     * @return This builder for chaining.
     */
    public Builder setScore(int value) {
      
      score_ = value;
      onChanged();
      return this;
    }
    /**
     * <pre>
     * 用户当前弹奏分数
     * </pre>
     *
     * <code>uint32 score = 2;</code>
     * @return This builder for chaining.
     */
    public Builder clearScore() {
      
      score_ = 0;
      onChanged();
      return this;
    }

    private int roomPosition_ ;
    /**
     * <pre>
     * 用户所在房间位置
     * </pre>
     *
     * <code>uint32 room_position = 3;</code>
     * @return The roomPosition.
     */
    @java.lang.Override
    public int getRoomPosition() {
      return roomPosition_;
    }
    /**
     * <pre>
     * 用户所在房间位置
     * </pre>
     *
     * <code>uint32 room_position = 3;</code>
     * @param value The roomPosition to set.
     * @return This builder for chaining.
     */
    public Builder setRoomPosition(int value) {
      
      roomPosition_ = value;
      onChanged();
      return this;
    }
    /**
     * <pre>
     * 用户所在房间位置
     * </pre>
     *
     * <code>uint32 room_position = 3;</code>
     * @return This builder for chaining.
     */
    public Builder clearRoomPosition() {
      
      roomPosition_ = 0;
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


    // @@protoc_insertion_point(builder_scope:protobuf.vo.OnlineMiniGradeVO)
  }

  // @@protoc_insertion_point(class_scope:protobuf.vo.OnlineMiniGradeVO)
  private static final protobuf.vo.OnlineMiniGradeVO DEFAULT_INSTANCE;
  static {
    DEFAULT_INSTANCE = new protobuf.vo.OnlineMiniGradeVO();
  }

  public static protobuf.vo.OnlineMiniGradeVO getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final com.google.protobuf.Parser<OnlineMiniGradeVO>
      PARSER = new com.google.protobuf.AbstractParser<OnlineMiniGradeVO>() {
    @java.lang.Override
    public OnlineMiniGradeVO parsePartialFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return new OnlineMiniGradeVO(input, extensionRegistry);
    }
  };

  public static com.google.protobuf.Parser<OnlineMiniGradeVO> parser() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.protobuf.Parser<OnlineMiniGradeVO> getParserForType() {
    return PARSER;
  }

  @java.lang.Override
  public protobuf.vo.OnlineMiniGradeVO getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

}


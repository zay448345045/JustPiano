// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: OnlineResponseProtocol.proto

package ly.pp.justpiano3.protobuf.vo;

/**
 * <pre>
 * 房间
 * </pre>
 *
 * Protobuf type {@code ly.pp.justpiano3.protobuf.vo.OnlineRoomVO}
 */
public final class OnlineRoomVO extends
    com.google.protobuf.GeneratedMessageV3 implements
    // @@protoc_insertion_point(message_implements:ly.pp.justpiano3.protobuf.vo.OnlineRoomVO)
    OnlineRoomVOOrBuilder {
private static final long serialVersionUID = 0L;
  // Use OnlineRoomVO.newBuilder() to construct.
  private OnlineRoomVO(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
    super(builder);
  }
  private OnlineRoomVO() {
    roomName_ = "";
  }

  @java.lang.Override
  @SuppressWarnings({"unused"})
  protected java.lang.Object newInstance(
      UnusedPrivateParameter unused) {
    return new OnlineRoomVO();
  }

  @java.lang.Override
  public final com.google.protobuf.UnknownFieldSet
  getUnknownFields() {
    return this.unknownFields;
  }
  private OnlineRoomVO(
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

            roomId_ = input.readUInt32();
            break;
          }
          case 18: {
            java.lang.String s = input.readStringRequireUtf8();

            roomName_ = s;
            break;
          }
          case 24: {

            color_ = input.readUInt32();
            break;
          }
          case 32: {

            roomMode_ = input.readUInt32();
            break;
          }
          case 40: {

            isPlaying_ = input.readBool();
            break;
          }
          case 48: {

            isEncrypt_ = input.readBool();
            break;
          }
          case 56: {

            femaleNum_ = input.readUInt32();
            break;
          }
          case 64: {

            maleNum_ = input.readUInt32();
            break;
          }
          case 72: {

            closeNum_ = input.readUInt32();
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
    return ly.pp.justpiano3.protobuf.vo.OnlineResponse.internal_static_ly_pp_justpiano3_protobuf_vo_OnlineRoomVO_descriptor;
  }

  @java.lang.Override
  protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internalGetFieldAccessorTable() {
    return ly.pp.justpiano3.protobuf.vo.OnlineResponse.internal_static_ly_pp_justpiano3_protobuf_vo_OnlineRoomVO_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            ly.pp.justpiano3.protobuf.vo.OnlineRoomVO.class, ly.pp.justpiano3.protobuf.vo.OnlineRoomVO.Builder.class);
  }

  public static final int ROOM_ID_FIELD_NUMBER = 1;
  private int roomId_;
  /**
   * <pre>
   * 房间id
   * </pre>
   *
   * <code>uint32 room_id = 1;</code>
   * @return The roomId.
   */
  @java.lang.Override
  public int getRoomId() {
    return roomId_;
  }

  public static final int ROOM_NAME_FIELD_NUMBER = 2;
  private volatile java.lang.Object roomName_;
  /**
   * <pre>
   * 房间名称
   * </pre>
   *
   * <code>string room_name = 2;</code>
   * @return The roomName.
   */
  @java.lang.Override
  public java.lang.String getRoomName() {
    java.lang.Object ref = roomName_;
    if (ref instanceof java.lang.String) {
      return (java.lang.String) ref;
    } else {
      com.google.protobuf.ByteString bs = 
          (com.google.protobuf.ByteString) ref;
      java.lang.String s = bs.toStringUtf8();
      roomName_ = s;
      return s;
    }
  }
  /**
   * <pre>
   * 房间名称
   * </pre>
   *
   * <code>string room_name = 2;</code>
   * @return The bytes for roomName.
   */
  @java.lang.Override
  public com.google.protobuf.ByteString
      getRoomNameBytes() {
    java.lang.Object ref = roomName_;
    if (ref instanceof java.lang.String) {
      com.google.protobuf.ByteString b = 
          com.google.protobuf.ByteString.copyFromUtf8(
              (java.lang.String) ref);
      roomName_ = b;
      return b;
    } else {
      return (com.google.protobuf.ByteString) ref;
    }
  }

  public static final int COLOR_FIELD_NUMBER = 3;
  private int color_;
  /**
   * <pre>
   * 房名颜色
   * </pre>
   *
   * <code>uint32 color = 3;</code>
   * @return The color.
   */
  @java.lang.Override
  public int getColor() {
    return color_;
  }

  public static final int ROOM_MODE_FIELD_NUMBER = 4;
  private int roomMode_;
  /**
   * <pre>
   * 房间模式
   * </pre>
   *
   * <code>uint32 room_mode = 4;</code>
   * @return The roomMode.
   */
  @java.lang.Override
  public int getRoomMode() {
    return roomMode_;
  }

  public static final int IS_PLAYING_FIELD_NUMBER = 5;
  private boolean isPlaying_;
  /**
   * <pre>
   * 房间是否处于弹奏中状态
   * </pre>
   *
   * <code>bool is_playing = 5;</code>
   * @return The isPlaying.
   */
  @java.lang.Override
  public boolean getIsPlaying() {
    return isPlaying_;
  }

  public static final int IS_ENCRYPT_FIELD_NUMBER = 6;
  private boolean isEncrypt_;
  /**
   * <pre>
   * 房间是否有密码
   * </pre>
   *
   * <code>bool is_encrypt = 6;</code>
   * @return The isEncrypt.
   */
  @java.lang.Override
  public boolean getIsEncrypt() {
    return isEncrypt_;
  }

  public static final int FEMALE_NUM_FIELD_NUMBER = 7;
  private int femaleNum_;
  /**
   * <pre>
   * 房间女生人数
   * </pre>
   *
   * <code>uint32 female_num = 7;</code>
   * @return The femaleNum.
   */
  @java.lang.Override
  public int getFemaleNum() {
    return femaleNum_;
  }

  public static final int MALE_NUM_FIELD_NUMBER = 8;
  private int maleNum_;
  /**
   * <pre>
   * 房间男生人数
   * </pre>
   *
   * <code>uint32 male_num = 8;</code>
   * @return The maleNum.
   */
  @java.lang.Override
  public int getMaleNum() {
    return maleNum_;
  }

  public static final int CLOSE_NUM_FIELD_NUMBER = 9;
  private int closeNum_;
  /**
   * <pre>
   * 房间关闭空位人数
   * </pre>
   *
   * <code>uint32 close_num = 9;</code>
   * @return The closeNum.
   */
  @java.lang.Override
  public int getCloseNum() {
    return closeNum_;
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
    if (roomId_ != 0) {
      output.writeUInt32(1, roomId_);
    }
    if (!com.google.protobuf.GeneratedMessageV3.isStringEmpty(roomName_)) {
      com.google.protobuf.GeneratedMessageV3.writeString(output, 2, roomName_);
    }
    if (color_ != 0) {
      output.writeUInt32(3, color_);
    }
    if (roomMode_ != 0) {
      output.writeUInt32(4, roomMode_);
    }
    if (isPlaying_ != false) {
      output.writeBool(5, isPlaying_);
    }
    if (isEncrypt_ != false) {
      output.writeBool(6, isEncrypt_);
    }
    if (femaleNum_ != 0) {
      output.writeUInt32(7, femaleNum_);
    }
    if (maleNum_ != 0) {
      output.writeUInt32(8, maleNum_);
    }
    if (closeNum_ != 0) {
      output.writeUInt32(9, closeNum_);
    }
    unknownFields.writeTo(output);
  }

  @java.lang.Override
  public int getSerializedSize() {
    int size = memoizedSize;
    if (size != -1) return size;

    size = 0;
    if (roomId_ != 0) {
      size += com.google.protobuf.CodedOutputStream
        .computeUInt32Size(1, roomId_);
    }
    if (!com.google.protobuf.GeneratedMessageV3.isStringEmpty(roomName_)) {
      size += com.google.protobuf.GeneratedMessageV3.computeStringSize(2, roomName_);
    }
    if (color_ != 0) {
      size += com.google.protobuf.CodedOutputStream
        .computeUInt32Size(3, color_);
    }
    if (roomMode_ != 0) {
      size += com.google.protobuf.CodedOutputStream
        .computeUInt32Size(4, roomMode_);
    }
    if (isPlaying_ != false) {
      size += com.google.protobuf.CodedOutputStream
        .computeBoolSize(5, isPlaying_);
    }
    if (isEncrypt_ != false) {
      size += com.google.protobuf.CodedOutputStream
        .computeBoolSize(6, isEncrypt_);
    }
    if (femaleNum_ != 0) {
      size += com.google.protobuf.CodedOutputStream
        .computeUInt32Size(7, femaleNum_);
    }
    if (maleNum_ != 0) {
      size += com.google.protobuf.CodedOutputStream
        .computeUInt32Size(8, maleNum_);
    }
    if (closeNum_ != 0) {
      size += com.google.protobuf.CodedOutputStream
        .computeUInt32Size(9, closeNum_);
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
    if (!(obj instanceof ly.pp.justpiano3.protobuf.vo.OnlineRoomVO)) {
      return super.equals(obj);
    }
    ly.pp.justpiano3.protobuf.vo.OnlineRoomVO other = (ly.pp.justpiano3.protobuf.vo.OnlineRoomVO) obj;

    if (getRoomId()
        != other.getRoomId()) return false;
    if (!getRoomName()
        .equals(other.getRoomName())) return false;
    if (getColor()
        != other.getColor()) return false;
    if (getRoomMode()
        != other.getRoomMode()) return false;
    if (getIsPlaying()
        != other.getIsPlaying()) return false;
    if (getIsEncrypt()
        != other.getIsEncrypt()) return false;
    if (getFemaleNum()
        != other.getFemaleNum()) return false;
    if (getMaleNum()
        != other.getMaleNum()) return false;
    if (getCloseNum()
        != other.getCloseNum()) return false;
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
    hash = (37 * hash) + ROOM_ID_FIELD_NUMBER;
    hash = (53 * hash) + getRoomId();
    hash = (37 * hash) + ROOM_NAME_FIELD_NUMBER;
    hash = (53 * hash) + getRoomName().hashCode();
    hash = (37 * hash) + COLOR_FIELD_NUMBER;
    hash = (53 * hash) + getColor();
    hash = (37 * hash) + ROOM_MODE_FIELD_NUMBER;
    hash = (53 * hash) + getRoomMode();
    hash = (37 * hash) + IS_PLAYING_FIELD_NUMBER;
    hash = (53 * hash) + com.google.protobuf.Internal.hashBoolean(
        getIsPlaying());
    hash = (37 * hash) + IS_ENCRYPT_FIELD_NUMBER;
    hash = (53 * hash) + com.google.protobuf.Internal.hashBoolean(
        getIsEncrypt());
    hash = (37 * hash) + FEMALE_NUM_FIELD_NUMBER;
    hash = (53 * hash) + getFemaleNum();
    hash = (37 * hash) + MALE_NUM_FIELD_NUMBER;
    hash = (53 * hash) + getMaleNum();
    hash = (37 * hash) + CLOSE_NUM_FIELD_NUMBER;
    hash = (53 * hash) + getCloseNum();
    hash = (29 * hash) + unknownFields.hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static ly.pp.justpiano3.protobuf.vo.OnlineRoomVO parseFrom(
      java.nio.ByteBuffer data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static ly.pp.justpiano3.protobuf.vo.OnlineRoomVO parseFrom(
      java.nio.ByteBuffer data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static ly.pp.justpiano3.protobuf.vo.OnlineRoomVO parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static ly.pp.justpiano3.protobuf.vo.OnlineRoomVO parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static ly.pp.justpiano3.protobuf.vo.OnlineRoomVO parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static ly.pp.justpiano3.protobuf.vo.OnlineRoomVO parseFrom(
      byte[] data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static ly.pp.justpiano3.protobuf.vo.OnlineRoomVO parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static ly.pp.justpiano3.protobuf.vo.OnlineRoomVO parseFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }
  public static ly.pp.justpiano3.protobuf.vo.OnlineRoomVO parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input);
  }
  public static ly.pp.justpiano3.protobuf.vo.OnlineRoomVO parseDelimitedFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
  }
  public static ly.pp.justpiano3.protobuf.vo.OnlineRoomVO parseFrom(
      com.google.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static ly.pp.justpiano3.protobuf.vo.OnlineRoomVO parseFrom(
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
  public static Builder newBuilder(ly.pp.justpiano3.protobuf.vo.OnlineRoomVO prototype) {
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
   * 房间
   * </pre>
   *
   * Protobuf type {@code ly.pp.justpiano3.protobuf.vo.OnlineRoomVO}
   */
  public static final class Builder extends
      com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
      // @@protoc_insertion_point(builder_implements:ly.pp.justpiano3.protobuf.vo.OnlineRoomVO)
      ly.pp.justpiano3.protobuf.vo.OnlineRoomVOOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return ly.pp.justpiano3.protobuf.vo.OnlineResponse.internal_static_ly_pp_justpiano3_protobuf_vo_OnlineRoomVO_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return ly.pp.justpiano3.protobuf.vo.OnlineResponse.internal_static_ly_pp_justpiano3_protobuf_vo_OnlineRoomVO_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              ly.pp.justpiano3.protobuf.vo.OnlineRoomVO.class, ly.pp.justpiano3.protobuf.vo.OnlineRoomVO.Builder.class);
    }

    // Construct using ly.pp.justpiano3.protobuf.vo.OnlineRoomVO.newBuilder()
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
      roomId_ = 0;

      roomName_ = "";

      color_ = 0;

      roomMode_ = 0;

      isPlaying_ = false;

      isEncrypt_ = false;

      femaleNum_ = 0;

      maleNum_ = 0;

      closeNum_ = 0;

      return this;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.Descriptor
        getDescriptorForType() {
      return ly.pp.justpiano3.protobuf.vo.OnlineResponse.internal_static_ly_pp_justpiano3_protobuf_vo_OnlineRoomVO_descriptor;
    }

    @java.lang.Override
    public ly.pp.justpiano3.protobuf.vo.OnlineRoomVO getDefaultInstanceForType() {
      return ly.pp.justpiano3.protobuf.vo.OnlineRoomVO.getDefaultInstance();
    }

    @java.lang.Override
    public ly.pp.justpiano3.protobuf.vo.OnlineRoomVO build() {
      ly.pp.justpiano3.protobuf.vo.OnlineRoomVO result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    @java.lang.Override
    public ly.pp.justpiano3.protobuf.vo.OnlineRoomVO buildPartial() {
      ly.pp.justpiano3.protobuf.vo.OnlineRoomVO result = new ly.pp.justpiano3.protobuf.vo.OnlineRoomVO(this);
      result.roomId_ = roomId_;
      result.roomName_ = roomName_;
      result.color_ = color_;
      result.roomMode_ = roomMode_;
      result.isPlaying_ = isPlaying_;
      result.isEncrypt_ = isEncrypt_;
      result.femaleNum_ = femaleNum_;
      result.maleNum_ = maleNum_;
      result.closeNum_ = closeNum_;
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
      if (other instanceof ly.pp.justpiano3.protobuf.vo.OnlineRoomVO) {
        return mergeFrom((ly.pp.justpiano3.protobuf.vo.OnlineRoomVO)other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(ly.pp.justpiano3.protobuf.vo.OnlineRoomVO other) {
      if (other == ly.pp.justpiano3.protobuf.vo.OnlineRoomVO.getDefaultInstance()) return this;
      if (other.getRoomId() != 0) {
        setRoomId(other.getRoomId());
      }
      if (!other.getRoomName().isEmpty()) {
        roomName_ = other.roomName_;
        onChanged();
      }
      if (other.getColor() != 0) {
        setColor(other.getColor());
      }
      if (other.getRoomMode() != 0) {
        setRoomMode(other.getRoomMode());
      }
      if (other.getIsPlaying() != false) {
        setIsPlaying(other.getIsPlaying());
      }
      if (other.getIsEncrypt() != false) {
        setIsEncrypt(other.getIsEncrypt());
      }
      if (other.getFemaleNum() != 0) {
        setFemaleNum(other.getFemaleNum());
      }
      if (other.getMaleNum() != 0) {
        setMaleNum(other.getMaleNum());
      }
      if (other.getCloseNum() != 0) {
        setCloseNum(other.getCloseNum());
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
      ly.pp.justpiano3.protobuf.vo.OnlineRoomVO parsedMessage = null;
      try {
        parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        parsedMessage = (ly.pp.justpiano3.protobuf.vo.OnlineRoomVO) e.getUnfinishedMessage();
        throw e.unwrapIOException();
      } finally {
        if (parsedMessage != null) {
          mergeFrom(parsedMessage);
        }
      }
      return this;
    }

    private int roomId_ ;
    /**
     * <pre>
     * 房间id
     * </pre>
     *
     * <code>uint32 room_id = 1;</code>
     * @return The roomId.
     */
    @java.lang.Override
    public int getRoomId() {
      return roomId_;
    }
    /**
     * <pre>
     * 房间id
     * </pre>
     *
     * <code>uint32 room_id = 1;</code>
     * @param value The roomId to set.
     * @return This builder for chaining.
     */
    public Builder setRoomId(int value) {
      
      roomId_ = value;
      onChanged();
      return this;
    }
    /**
     * <pre>
     * 房间id
     * </pre>
     *
     * <code>uint32 room_id = 1;</code>
     * @return This builder for chaining.
     */
    public Builder clearRoomId() {
      
      roomId_ = 0;
      onChanged();
      return this;
    }

    private java.lang.Object roomName_ = "";
    /**
     * <pre>
     * 房间名称
     * </pre>
     *
     * <code>string room_name = 2;</code>
     * @return The roomName.
     */
    public java.lang.String getRoomName() {
      java.lang.Object ref = roomName_;
      if (!(ref instanceof java.lang.String)) {
        com.google.protobuf.ByteString bs =
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        roomName_ = s;
        return s;
      } else {
        return (java.lang.String) ref;
      }
    }
    /**
     * <pre>
     * 房间名称
     * </pre>
     *
     * <code>string room_name = 2;</code>
     * @return The bytes for roomName.
     */
    public com.google.protobuf.ByteString
        getRoomNameBytes() {
      java.lang.Object ref = roomName_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        roomName_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }
    /**
     * <pre>
     * 房间名称
     * </pre>
     *
     * <code>string room_name = 2;</code>
     * @param value The roomName to set.
     * @return This builder for chaining.
     */
    public Builder setRoomName(
        java.lang.String value) {
      if (value == null) {
    throw new NullPointerException();
  }
  
      roomName_ = value;
      onChanged();
      return this;
    }
    /**
     * <pre>
     * 房间名称
     * </pre>
     *
     * <code>string room_name = 2;</code>
     * @return This builder for chaining.
     */
    public Builder clearRoomName() {
      
      roomName_ = getDefaultInstance().getRoomName();
      onChanged();
      return this;
    }
    /**
     * <pre>
     * 房间名称
     * </pre>
     *
     * <code>string room_name = 2;</code>
     * @param value The bytes for roomName to set.
     * @return This builder for chaining.
     */
    public Builder setRoomNameBytes(
        com.google.protobuf.ByteString value) {
      if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
      
      roomName_ = value;
      onChanged();
      return this;
    }

    private int color_ ;
    /**
     * <pre>
     * 房名颜色
     * </pre>
     *
     * <code>uint32 color = 3;</code>
     * @return The color.
     */
    @java.lang.Override
    public int getColor() {
      return color_;
    }
    /**
     * <pre>
     * 房名颜色
     * </pre>
     *
     * <code>uint32 color = 3;</code>
     * @param value The color to set.
     * @return This builder for chaining.
     */
    public Builder setColor(int value) {
      
      color_ = value;
      onChanged();
      return this;
    }
    /**
     * <pre>
     * 房名颜色
     * </pre>
     *
     * <code>uint32 color = 3;</code>
     * @return This builder for chaining.
     */
    public Builder clearColor() {
      
      color_ = 0;
      onChanged();
      return this;
    }

    private int roomMode_ ;
    /**
     * <pre>
     * 房间模式
     * </pre>
     *
     * <code>uint32 room_mode = 4;</code>
     * @return The roomMode.
     */
    @java.lang.Override
    public int getRoomMode() {
      return roomMode_;
    }
    /**
     * <pre>
     * 房间模式
     * </pre>
     *
     * <code>uint32 room_mode = 4;</code>
     * @param value The roomMode to set.
     * @return This builder for chaining.
     */
    public Builder setRoomMode(int value) {
      
      roomMode_ = value;
      onChanged();
      return this;
    }
    /**
     * <pre>
     * 房间模式
     * </pre>
     *
     * <code>uint32 room_mode = 4;</code>
     * @return This builder for chaining.
     */
    public Builder clearRoomMode() {
      
      roomMode_ = 0;
      onChanged();
      return this;
    }

    private boolean isPlaying_ ;
    /**
     * <pre>
     * 房间是否处于弹奏中状态
     * </pre>
     *
     * <code>bool is_playing = 5;</code>
     * @return The isPlaying.
     */
    @java.lang.Override
    public boolean getIsPlaying() {
      return isPlaying_;
    }
    /**
     * <pre>
     * 房间是否处于弹奏中状态
     * </pre>
     *
     * <code>bool is_playing = 5;</code>
     * @param value The isPlaying to set.
     * @return This builder for chaining.
     */
    public Builder setIsPlaying(boolean value) {
      
      isPlaying_ = value;
      onChanged();
      return this;
    }
    /**
     * <pre>
     * 房间是否处于弹奏中状态
     * </pre>
     *
     * <code>bool is_playing = 5;</code>
     * @return This builder for chaining.
     */
    public Builder clearIsPlaying() {
      
      isPlaying_ = false;
      onChanged();
      return this;
    }

    private boolean isEncrypt_ ;
    /**
     * <pre>
     * 房间是否有密码
     * </pre>
     *
     * <code>bool is_encrypt = 6;</code>
     * @return The isEncrypt.
     */
    @java.lang.Override
    public boolean getIsEncrypt() {
      return isEncrypt_;
    }
    /**
     * <pre>
     * 房间是否有密码
     * </pre>
     *
     * <code>bool is_encrypt = 6;</code>
     * @param value The isEncrypt to set.
     * @return This builder for chaining.
     */
    public Builder setIsEncrypt(boolean value) {
      
      isEncrypt_ = value;
      onChanged();
      return this;
    }
    /**
     * <pre>
     * 房间是否有密码
     * </pre>
     *
     * <code>bool is_encrypt = 6;</code>
     * @return This builder for chaining.
     */
    public Builder clearIsEncrypt() {
      
      isEncrypt_ = false;
      onChanged();
      return this;
    }

    private int femaleNum_ ;
    /**
     * <pre>
     * 房间女生人数
     * </pre>
     *
     * <code>uint32 female_num = 7;</code>
     * @return The femaleNum.
     */
    @java.lang.Override
    public int getFemaleNum() {
      return femaleNum_;
    }
    /**
     * <pre>
     * 房间女生人数
     * </pre>
     *
     * <code>uint32 female_num = 7;</code>
     * @param value The femaleNum to set.
     * @return This builder for chaining.
     */
    public Builder setFemaleNum(int value) {
      
      femaleNum_ = value;
      onChanged();
      return this;
    }
    /**
     * <pre>
     * 房间女生人数
     * </pre>
     *
     * <code>uint32 female_num = 7;</code>
     * @return This builder for chaining.
     */
    public Builder clearFemaleNum() {
      
      femaleNum_ = 0;
      onChanged();
      return this;
    }

    private int maleNum_ ;
    /**
     * <pre>
     * 房间男生人数
     * </pre>
     *
     * <code>uint32 male_num = 8;</code>
     * @return The maleNum.
     */
    @java.lang.Override
    public int getMaleNum() {
      return maleNum_;
    }
    /**
     * <pre>
     * 房间男生人数
     * </pre>
     *
     * <code>uint32 male_num = 8;</code>
     * @param value The maleNum to set.
     * @return This builder for chaining.
     */
    public Builder setMaleNum(int value) {
      
      maleNum_ = value;
      onChanged();
      return this;
    }
    /**
     * <pre>
     * 房间男生人数
     * </pre>
     *
     * <code>uint32 male_num = 8;</code>
     * @return This builder for chaining.
     */
    public Builder clearMaleNum() {
      
      maleNum_ = 0;
      onChanged();
      return this;
    }

    private int closeNum_ ;
    /**
     * <pre>
     * 房间关闭空位人数
     * </pre>
     *
     * <code>uint32 close_num = 9;</code>
     * @return The closeNum.
     */
    @java.lang.Override
    public int getCloseNum() {
      return closeNum_;
    }
    /**
     * <pre>
     * 房间关闭空位人数
     * </pre>
     *
     * <code>uint32 close_num = 9;</code>
     * @param value The closeNum to set.
     * @return This builder for chaining.
     */
    public Builder setCloseNum(int value) {
      
      closeNum_ = value;
      onChanged();
      return this;
    }
    /**
     * <pre>
     * 房间关闭空位人数
     * </pre>
     *
     * <code>uint32 close_num = 9;</code>
     * @return This builder for chaining.
     */
    public Builder clearCloseNum() {
      
      closeNum_ = 0;
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


    // @@protoc_insertion_point(builder_scope:ly.pp.justpiano3.protobuf.vo.OnlineRoomVO)
  }

  // @@protoc_insertion_point(class_scope:ly.pp.justpiano3.protobuf.vo.OnlineRoomVO)
  private static final ly.pp.justpiano3.protobuf.vo.OnlineRoomVO DEFAULT_INSTANCE;
  static {
    DEFAULT_INSTANCE = new ly.pp.justpiano3.protobuf.vo.OnlineRoomVO();
  }

  public static ly.pp.justpiano3.protobuf.vo.OnlineRoomVO getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final com.google.protobuf.Parser<OnlineRoomVO>
      PARSER = new com.google.protobuf.AbstractParser<OnlineRoomVO>() {
    @java.lang.Override
    public OnlineRoomVO parsePartialFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return new OnlineRoomVO(input, extensionRegistry);
    }
  };

  public static com.google.protobuf.Parser<OnlineRoomVO> parser() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.protobuf.Parser<OnlineRoomVO> getParserForType() {
    return PARSER;
  }

  @java.lang.Override
  public ly.pp.justpiano3.protobuf.vo.OnlineRoomVO getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

}


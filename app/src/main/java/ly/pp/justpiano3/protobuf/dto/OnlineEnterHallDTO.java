// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: OnlineRequestProtocol.proto

package ly.pp.justpiano3.protobuf.dto;

/**
 * <pre>
 * 进入大厅
 * </pre>
 *
 * Protobuf type {@code ly.pp.justpiano3.protobuf.dto.OnlineEnterHallDTO}
 */
public final class OnlineEnterHallDTO extends
    com.google.protobuf.GeneratedMessageV3 implements
    // @@protoc_insertion_point(message_implements:ly.pp.justpiano3.protobuf.dto.OnlineEnterHallDTO)
    OnlineEnterHallDTOOrBuilder {
private static final long serialVersionUID = 0L;
  // Use OnlineEnterHallDTO.newBuilder() to construct.
  private OnlineEnterHallDTO(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
    super(builder);
  }
  private OnlineEnterHallDTO() {
    password_ = "";
  }

  @java.lang.Override
  @SuppressWarnings({"unused"})
  protected java.lang.Object newInstance(
      UnusedPrivateParameter unused) {
    return new OnlineEnterHallDTO();
  }

  @java.lang.Override
  public final com.google.protobuf.UnknownFieldSet
  getUnknownFields() {
    return this.unknownFields;
  }
  private OnlineEnterHallDTO(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    this();
    if (extensionRegistry == null) {
      throw new java.lang.NullPointerException();
    }
    int mutable_bitField0_ = 0;
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

            hallId_ = input.readUInt32();
            break;
          }
          case 18: {
            java.lang.String s = input.readStringRequireUtf8();
            bitField0_ |= 0x00000001;
            password_ = s;
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
    return ly.pp.justpiano3.protobuf.dto.OnlineRequest.internal_static_ly_pp_justpiano3_protobuf_dto_OnlineEnterHallDTO_descriptor;
  }

  @java.lang.Override
  protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internalGetFieldAccessorTable() {
    return ly.pp.justpiano3.protobuf.dto.OnlineRequest.internal_static_ly_pp_justpiano3_protobuf_dto_OnlineEnterHallDTO_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            ly.pp.justpiano3.protobuf.dto.OnlineEnterHallDTO.class, ly.pp.justpiano3.protobuf.dto.OnlineEnterHallDTO.Builder.class);
  }

  private int bitField0_;
  public static final int HALL_ID_FIELD_NUMBER = 1;
  private int hallId_;
  /**
   * <pre>
   * 大厅id
   * </pre>
   *
   * <code>uint32 hall_id = 1;</code>
   * @return The hallId.
   */
  @java.lang.Override
  public int getHallId() {
    return hallId_;
  }

  public static final int PASSWORD_FIELD_NUMBER = 2;
  private volatile java.lang.Object password_;
  /**
   * <pre>
   * 大厅密码（若有）
   * </pre>
   *
   * <code>optional string password = 2;</code>
   * @return Whether the password field is set.
   */
  @java.lang.Override
  public boolean hasPassword() {
    return ((bitField0_ & 0x00000001) != 0);
  }
  /**
   * <pre>
   * 大厅密码（若有）
   * </pre>
   *
   * <code>optional string password = 2;</code>
   * @return The password.
   */
  @java.lang.Override
  public java.lang.String getPassword() {
    java.lang.Object ref = password_;
    if (ref instanceof java.lang.String) {
      return (java.lang.String) ref;
    } else {
      com.google.protobuf.ByteString bs = 
          (com.google.protobuf.ByteString) ref;
      java.lang.String s = bs.toStringUtf8();
      password_ = s;
      return s;
    }
  }
  /**
   * <pre>
   * 大厅密码（若有）
   * </pre>
   *
   * <code>optional string password = 2;</code>
   * @return The bytes for password.
   */
  @java.lang.Override
  public com.google.protobuf.ByteString
      getPasswordBytes() {
    java.lang.Object ref = password_;
    if (ref instanceof java.lang.String) {
      com.google.protobuf.ByteString b = 
          com.google.protobuf.ByteString.copyFromUtf8(
              (java.lang.String) ref);
      password_ = b;
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
    if (hallId_ != 0) {
      output.writeUInt32(1, hallId_);
    }
    if (((bitField0_ & 0x00000001) != 0)) {
      com.google.protobuf.GeneratedMessageV3.writeString(output, 2, password_);
    }
    unknownFields.writeTo(output);
  }

  @java.lang.Override
  public int getSerializedSize() {
    int size = memoizedSize;
    if (size != -1) return size;

    size = 0;
    if (hallId_ != 0) {
      size += com.google.protobuf.CodedOutputStream
        .computeUInt32Size(1, hallId_);
    }
    if (((bitField0_ & 0x00000001) != 0)) {
      size += com.google.protobuf.GeneratedMessageV3.computeStringSize(2, password_);
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
    if (!(obj instanceof ly.pp.justpiano3.protobuf.dto.OnlineEnterHallDTO)) {
      return super.equals(obj);
    }
    ly.pp.justpiano3.protobuf.dto.OnlineEnterHallDTO other = (ly.pp.justpiano3.protobuf.dto.OnlineEnterHallDTO) obj;

    if (getHallId()
        != other.getHallId()) return false;
    if (hasPassword() != other.hasPassword()) return false;
    if (hasPassword()) {
      if (!getPassword()
          .equals(other.getPassword())) return false;
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
    hash = (37 * hash) + HALL_ID_FIELD_NUMBER;
    hash = (53 * hash) + getHallId();
    if (hasPassword()) {
      hash = (37 * hash) + PASSWORD_FIELD_NUMBER;
      hash = (53 * hash) + getPassword().hashCode();
    }
    hash = (29 * hash) + unknownFields.hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static ly.pp.justpiano3.protobuf.dto.OnlineEnterHallDTO parseFrom(
      java.nio.ByteBuffer data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static ly.pp.justpiano3.protobuf.dto.OnlineEnterHallDTO parseFrom(
      java.nio.ByteBuffer data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static ly.pp.justpiano3.protobuf.dto.OnlineEnterHallDTO parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static ly.pp.justpiano3.protobuf.dto.OnlineEnterHallDTO parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static ly.pp.justpiano3.protobuf.dto.OnlineEnterHallDTO parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static ly.pp.justpiano3.protobuf.dto.OnlineEnterHallDTO parseFrom(
      byte[] data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static ly.pp.justpiano3.protobuf.dto.OnlineEnterHallDTO parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static ly.pp.justpiano3.protobuf.dto.OnlineEnterHallDTO parseFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }
  public static ly.pp.justpiano3.protobuf.dto.OnlineEnterHallDTO parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input);
  }
  public static ly.pp.justpiano3.protobuf.dto.OnlineEnterHallDTO parseDelimitedFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
  }
  public static ly.pp.justpiano3.protobuf.dto.OnlineEnterHallDTO parseFrom(
      com.google.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static ly.pp.justpiano3.protobuf.dto.OnlineEnterHallDTO parseFrom(
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
  public static Builder newBuilder(ly.pp.justpiano3.protobuf.dto.OnlineEnterHallDTO prototype) {
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
   * 进入大厅
   * </pre>
   *
   * Protobuf type {@code ly.pp.justpiano3.protobuf.dto.OnlineEnterHallDTO}
   */
  public static final class Builder extends
      com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
      // @@protoc_insertion_point(builder_implements:ly.pp.justpiano3.protobuf.dto.OnlineEnterHallDTO)
      ly.pp.justpiano3.protobuf.dto.OnlineEnterHallDTOOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return ly.pp.justpiano3.protobuf.dto.OnlineRequest.internal_static_ly_pp_justpiano3_protobuf_dto_OnlineEnterHallDTO_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return ly.pp.justpiano3.protobuf.dto.OnlineRequest.internal_static_ly_pp_justpiano3_protobuf_dto_OnlineEnterHallDTO_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              ly.pp.justpiano3.protobuf.dto.OnlineEnterHallDTO.class, ly.pp.justpiano3.protobuf.dto.OnlineEnterHallDTO.Builder.class);
    }

    // Construct using ly.pp.justpiano3.protobuf.dto.OnlineEnterHallDTO.newBuilder()
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
      hallId_ = 0;

      password_ = "";
      bitField0_ = (bitField0_ & ~0x00000001);
      return this;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.Descriptor
        getDescriptorForType() {
      return ly.pp.justpiano3.protobuf.dto.OnlineRequest.internal_static_ly_pp_justpiano3_protobuf_dto_OnlineEnterHallDTO_descriptor;
    }

    @java.lang.Override
    public ly.pp.justpiano3.protobuf.dto.OnlineEnterHallDTO getDefaultInstanceForType() {
      return ly.pp.justpiano3.protobuf.dto.OnlineEnterHallDTO.getDefaultInstance();
    }

    @java.lang.Override
    public ly.pp.justpiano3.protobuf.dto.OnlineEnterHallDTO build() {
      ly.pp.justpiano3.protobuf.dto.OnlineEnterHallDTO result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    @java.lang.Override
    public ly.pp.justpiano3.protobuf.dto.OnlineEnterHallDTO buildPartial() {
      ly.pp.justpiano3.protobuf.dto.OnlineEnterHallDTO result = new ly.pp.justpiano3.protobuf.dto.OnlineEnterHallDTO(this);
      int from_bitField0_ = bitField0_;
      int to_bitField0_ = 0;
      result.hallId_ = hallId_;
      if (((from_bitField0_ & 0x00000001) != 0)) {
        to_bitField0_ |= 0x00000001;
      }
      result.password_ = password_;
      result.bitField0_ = to_bitField0_;
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
      if (other instanceof ly.pp.justpiano3.protobuf.dto.OnlineEnterHallDTO) {
        return mergeFrom((ly.pp.justpiano3.protobuf.dto.OnlineEnterHallDTO)other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(ly.pp.justpiano3.protobuf.dto.OnlineEnterHallDTO other) {
      if (other == ly.pp.justpiano3.protobuf.dto.OnlineEnterHallDTO.getDefaultInstance()) return this;
      if (other.getHallId() != 0) {
        setHallId(other.getHallId());
      }
      if (other.hasPassword()) {
        bitField0_ |= 0x00000001;
        password_ = other.password_;
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
      ly.pp.justpiano3.protobuf.dto.OnlineEnterHallDTO parsedMessage = null;
      try {
        parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        parsedMessage = (ly.pp.justpiano3.protobuf.dto.OnlineEnterHallDTO) e.getUnfinishedMessage();
        throw e.unwrapIOException();
      } finally {
        if (parsedMessage != null) {
          mergeFrom(parsedMessage);
        }
      }
      return this;
    }
    private int bitField0_;

    private int hallId_ ;
    /**
     * <pre>
     * 大厅id
     * </pre>
     *
     * <code>uint32 hall_id = 1;</code>
     * @return The hallId.
     */
    @java.lang.Override
    public int getHallId() {
      return hallId_;
    }
    /**
     * <pre>
     * 大厅id
     * </pre>
     *
     * <code>uint32 hall_id = 1;</code>
     * @param value The hallId to set.
     * @return This builder for chaining.
     */
    public Builder setHallId(int value) {
      
      hallId_ = value;
      onChanged();
      return this;
    }
    /**
     * <pre>
     * 大厅id
     * </pre>
     *
     * <code>uint32 hall_id = 1;</code>
     * @return This builder for chaining.
     */
    public Builder clearHallId() {
      
      hallId_ = 0;
      onChanged();
      return this;
    }

    private java.lang.Object password_ = "";
    /**
     * <pre>
     * 大厅密码（若有）
     * </pre>
     *
     * <code>optional string password = 2;</code>
     * @return Whether the password field is set.
     */
    public boolean hasPassword() {
      return ((bitField0_ & 0x00000001) != 0);
    }
    /**
     * <pre>
     * 大厅密码（若有）
     * </pre>
     *
     * <code>optional string password = 2;</code>
     * @return The password.
     */
    public java.lang.String getPassword() {
      java.lang.Object ref = password_;
      if (!(ref instanceof java.lang.String)) {
        com.google.protobuf.ByteString bs =
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        password_ = s;
        return s;
      } else {
        return (java.lang.String) ref;
      }
    }
    /**
     * <pre>
     * 大厅密码（若有）
     * </pre>
     *
     * <code>optional string password = 2;</code>
     * @return The bytes for password.
     */
    public com.google.protobuf.ByteString
        getPasswordBytes() {
      java.lang.Object ref = password_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        password_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }
    /**
     * <pre>
     * 大厅密码（若有）
     * </pre>
     *
     * <code>optional string password = 2;</code>
     * @param value The password to set.
     * @return This builder for chaining.
     */
    public Builder setPassword(
        java.lang.String value) {
      if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000001;
      password_ = value;
      onChanged();
      return this;
    }
    /**
     * <pre>
     * 大厅密码（若有）
     * </pre>
     *
     * <code>optional string password = 2;</code>
     * @return This builder for chaining.
     */
    public Builder clearPassword() {
      bitField0_ = (bitField0_ & ~0x00000001);
      password_ = getDefaultInstance().getPassword();
      onChanged();
      return this;
    }
    /**
     * <pre>
     * 大厅密码（若有）
     * </pre>
     *
     * <code>optional string password = 2;</code>
     * @param value The bytes for password to set.
     * @return This builder for chaining.
     */
    public Builder setPasswordBytes(
        com.google.protobuf.ByteString value) {
      if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
      bitField0_ |= 0x00000001;
      password_ = value;
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


    // @@protoc_insertion_point(builder_scope:ly.pp.justpiano3.protobuf.dto.OnlineEnterHallDTO)
  }

  // @@protoc_insertion_point(class_scope:ly.pp.justpiano3.protobuf.dto.OnlineEnterHallDTO)
  private static final ly.pp.justpiano3.protobuf.dto.OnlineEnterHallDTO DEFAULT_INSTANCE;
  static {
    DEFAULT_INSTANCE = new ly.pp.justpiano3.protobuf.dto.OnlineEnterHallDTO();
  }

  public static ly.pp.justpiano3.protobuf.dto.OnlineEnterHallDTO getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final com.google.protobuf.Parser<OnlineEnterHallDTO>
      PARSER = new com.google.protobuf.AbstractParser<OnlineEnterHallDTO>() {
    @java.lang.Override
    public OnlineEnterHallDTO parsePartialFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return new OnlineEnterHallDTO(input, extensionRegistry);
    }
  };

  public static com.google.protobuf.Parser<OnlineEnterHallDTO> parser() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.protobuf.Parser<OnlineEnterHallDTO> getParserForType() {
    return PARSER;
  }

  @java.lang.Override
  public ly.pp.justpiano3.protobuf.dto.OnlineEnterHallDTO getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

}


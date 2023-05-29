// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: OnlineRequestProtocol.proto

package protobuf.dto;

/**
 * <pre>
 * 改变左右手
 * </pre>
 *
 * Protobuf type {@code protobuf.dto.OnlineChangeRoomHandDTO}
 */
public final class OnlineChangeRoomHandDTO extends
    com.google.protobuf.GeneratedMessageV3 implements
    // @@protoc_insertion_point(message_implements:protobuf.dto.OnlineChangeRoomHandDTO)
    OnlineChangeRoomHandDTOOrBuilder {
private static final long serialVersionUID = 0L;
  // Use OnlineChangeRoomHandDTO.newBuilder() to construct.
  private OnlineChangeRoomHandDTO(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
    super(builder);
  }
  private OnlineChangeRoomHandDTO() {
  }

  @java.lang.Override
  @SuppressWarnings({"unused"})
  protected java.lang.Object newInstance(
      UnusedPrivateParameter unused) {
    return new OnlineChangeRoomHandDTO();
  }

  @java.lang.Override
  public final com.google.protobuf.UnknownFieldSet
  getUnknownFields() {
    return this.unknownFields;
  }
  private OnlineChangeRoomHandDTO(
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

            hand_ = input.readUInt32();
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
    return protobuf.dto.OnlineRequest.internal_static_protobuf_dto_OnlineChangeRoomHandDTO_descriptor;
  }

  @java.lang.Override
  protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internalGetFieldAccessorTable() {
    return protobuf.dto.OnlineRequest.internal_static_protobuf_dto_OnlineChangeRoomHandDTO_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            protobuf.dto.OnlineChangeRoomHandDTO.class, protobuf.dto.OnlineChangeRoomHandDTO.Builder.class);
  }

  public static final int HAND_FIELD_NUMBER = 1;
  private int hand_;
  /**
   * <pre>
   * 左右手
   * </pre>
   *
   * <code>uint32 hand = 1;</code>
   * @return The hand.
   */
  @java.lang.Override
  public int getHand() {
    return hand_;
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
    if (hand_ != 0) {
      output.writeUInt32(1, hand_);
    }
    unknownFields.writeTo(output);
  }

  @java.lang.Override
  public int getSerializedSize() {
    int size = memoizedSize;
    if (size != -1) return size;

    size = 0;
    if (hand_ != 0) {
      size += com.google.protobuf.CodedOutputStream
        .computeUInt32Size(1, hand_);
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
    if (!(obj instanceof protobuf.dto.OnlineChangeRoomHandDTO)) {
      return super.equals(obj);
    }
    protobuf.dto.OnlineChangeRoomHandDTO other = (protobuf.dto.OnlineChangeRoomHandDTO) obj;

    if (getHand()
        != other.getHand()) return false;
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
    hash = (37 * hash) + HAND_FIELD_NUMBER;
    hash = (53 * hash) + getHand();
    hash = (29 * hash) + unknownFields.hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static protobuf.dto.OnlineChangeRoomHandDTO parseFrom(
      java.nio.ByteBuffer data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static protobuf.dto.OnlineChangeRoomHandDTO parseFrom(
      java.nio.ByteBuffer data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static protobuf.dto.OnlineChangeRoomHandDTO parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static protobuf.dto.OnlineChangeRoomHandDTO parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static protobuf.dto.OnlineChangeRoomHandDTO parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static protobuf.dto.OnlineChangeRoomHandDTO parseFrom(
      byte[] data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static protobuf.dto.OnlineChangeRoomHandDTO parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static protobuf.dto.OnlineChangeRoomHandDTO parseFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }
  public static protobuf.dto.OnlineChangeRoomHandDTO parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input);
  }
  public static protobuf.dto.OnlineChangeRoomHandDTO parseDelimitedFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
  }
  public static protobuf.dto.OnlineChangeRoomHandDTO parseFrom(
      com.google.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static protobuf.dto.OnlineChangeRoomHandDTO parseFrom(
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
  public static Builder newBuilder(protobuf.dto.OnlineChangeRoomHandDTO prototype) {
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
   * 改变左右手
   * </pre>
   *
   * Protobuf type {@code protobuf.dto.OnlineChangeRoomHandDTO}
   */
  public static final class Builder extends
      com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
      // @@protoc_insertion_point(builder_implements:protobuf.dto.OnlineChangeRoomHandDTO)
      protobuf.dto.OnlineChangeRoomHandDTOOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return protobuf.dto.OnlineRequest.internal_static_protobuf_dto_OnlineChangeRoomHandDTO_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return protobuf.dto.OnlineRequest.internal_static_protobuf_dto_OnlineChangeRoomHandDTO_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              protobuf.dto.OnlineChangeRoomHandDTO.class, protobuf.dto.OnlineChangeRoomHandDTO.Builder.class);
    }

    // Construct using protobuf.dto.OnlineChangeRoomHandDTO.newBuilder()
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
      hand_ = 0;

      return this;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.Descriptor
        getDescriptorForType() {
      return protobuf.dto.OnlineRequest.internal_static_protobuf_dto_OnlineChangeRoomHandDTO_descriptor;
    }

    @java.lang.Override
    public protobuf.dto.OnlineChangeRoomHandDTO getDefaultInstanceForType() {
      return protobuf.dto.OnlineChangeRoomHandDTO.getDefaultInstance();
    }

    @java.lang.Override
    public protobuf.dto.OnlineChangeRoomHandDTO build() {
      protobuf.dto.OnlineChangeRoomHandDTO result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    @java.lang.Override
    public protobuf.dto.OnlineChangeRoomHandDTO buildPartial() {
      protobuf.dto.OnlineChangeRoomHandDTO result = new protobuf.dto.OnlineChangeRoomHandDTO(this);
      result.hand_ = hand_;
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
      if (other instanceof protobuf.dto.OnlineChangeRoomHandDTO) {
        return mergeFrom((protobuf.dto.OnlineChangeRoomHandDTO)other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(protobuf.dto.OnlineChangeRoomHandDTO other) {
      if (other == protobuf.dto.OnlineChangeRoomHandDTO.getDefaultInstance()) return this;
      if (other.getHand() != 0) {
        setHand(other.getHand());
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
      protobuf.dto.OnlineChangeRoomHandDTO parsedMessage = null;
      try {
        parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        parsedMessage = (protobuf.dto.OnlineChangeRoomHandDTO) e.getUnfinishedMessage();
        throw e.unwrapIOException();
      } finally {
        if (parsedMessage != null) {
          mergeFrom(parsedMessage);
        }
      }
      return this;
    }

    private int hand_ ;
    /**
     * <pre>
     * 左右手
     * </pre>
     *
     * <code>uint32 hand = 1;</code>
     * @return The hand.
     */
    @java.lang.Override
    public int getHand() {
      return hand_;
    }
    /**
     * <pre>
     * 左右手
     * </pre>
     *
     * <code>uint32 hand = 1;</code>
     * @param value The hand to set.
     * @return This builder for chaining.
     */
    public Builder setHand(int value) {
      
      hand_ = value;
      onChanged();
      return this;
    }
    /**
     * <pre>
     * 左右手
     * </pre>
     *
     * <code>uint32 hand = 1;</code>
     * @return This builder for chaining.
     */
    public Builder clearHand() {
      
      hand_ = 0;
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


    // @@protoc_insertion_point(builder_scope:protobuf.dto.OnlineChangeRoomHandDTO)
  }

  // @@protoc_insertion_point(class_scope:protobuf.dto.OnlineChangeRoomHandDTO)
  private static final protobuf.dto.OnlineChangeRoomHandDTO DEFAULT_INSTANCE;
  static {
    DEFAULT_INSTANCE = new protobuf.dto.OnlineChangeRoomHandDTO();
  }

  public static protobuf.dto.OnlineChangeRoomHandDTO getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final com.google.protobuf.Parser<OnlineChangeRoomHandDTO>
      PARSER = new com.google.protobuf.AbstractParser<OnlineChangeRoomHandDTO>() {
    @java.lang.Override
    public OnlineChangeRoomHandDTO parsePartialFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return new OnlineChangeRoomHandDTO(input, extensionRegistry);
    }
  };

  public static com.google.protobuf.Parser<OnlineChangeRoomHandDTO> parser() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.protobuf.Parser<OnlineChangeRoomHandDTO> getParserForType() {
    return PARSER;
  }

  @java.lang.Override
  public protobuf.dto.OnlineChangeRoomHandDTO getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

}


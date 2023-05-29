// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: OnlineRequestProtocol.proto

package protobuf.dto;

/**
 * <pre>
 * 家族
 * </pre>
 *
 * Protobuf type {@code protobuf.dto.OnlineFamilyDTO}
 */
public final class OnlineFamilyDTO extends
    com.google.protobuf.GeneratedMessageV3 implements
    // @@protoc_insertion_point(message_implements:protobuf.dto.OnlineFamilyDTO)
    OnlineFamilyDTOOrBuilder {
private static final long serialVersionUID = 0L;
  // Use OnlineFamilyDTO.newBuilder() to construct.
  private OnlineFamilyDTO(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
    super(builder);
  }
  private OnlineFamilyDTO() {
    message_ = "";
    familyName_ = "";
    userName_ = "";
  }

  @java.lang.Override
  @SuppressWarnings({"unused"})
  protected java.lang.Object newInstance(
      UnusedPrivateParameter unused) {
    return new OnlineFamilyDTO();
  }

  @java.lang.Override
  public final com.google.protobuf.UnknownFieldSet
  getUnknownFields() {
    return this.unknownFields;
  }
  private OnlineFamilyDTO(
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

            type_ = input.readUInt32();
            break;
          }
          case 16: {
            bitField0_ |= 0x00000001;
            familyId_ = input.readUInt32();
            break;
          }
          case 26: {
            java.lang.String s = input.readStringRequireUtf8();
            bitField0_ |= 0x00000002;
            message_ = s;
            break;
          }
          case 32: {
            bitField0_ |= 0x00000004;
            page_ = input.readUInt32();
            break;
          }
          case 42: {
            java.lang.String s = input.readStringRequireUtf8();
            bitField0_ |= 0x00000008;
            familyName_ = s;
            break;
          }
          case 50: {
            java.lang.String s = input.readStringRequireUtf8();
            bitField0_ |= 0x00000010;
            userName_ = s;
            break;
          }
          case 56: {
            bitField0_ |= 0x00000020;
            status_ = input.readUInt32();
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
    return protobuf.dto.OnlineRequest.internal_static_protobuf_dto_OnlineFamilyDTO_descriptor;
  }

  @java.lang.Override
  protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internalGetFieldAccessorTable() {
    return protobuf.dto.OnlineRequest.internal_static_protobuf_dto_OnlineFamilyDTO_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            protobuf.dto.OnlineFamilyDTO.class, protobuf.dto.OnlineFamilyDTO.Builder.class);
  }

  private int bitField0_;
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

  public static final int FAMILY_ID_FIELD_NUMBER = 2;
  private int familyId_;
  /**
   * <pre>
   * 家族id
   * </pre>
   *
   * <code>optional uint32 family_id = 2;</code>
   * @return Whether the familyId field is set.
   */
  @java.lang.Override
  public boolean hasFamilyId() {
    return ((bitField0_ & 0x00000001) != 0);
  }
  /**
   * <pre>
   * 家族id
   * </pre>
   *
   * <code>optional uint32 family_id = 2;</code>
   * @return The familyId.
   */
  @java.lang.Override
  public int getFamilyId() {
    return familyId_;
  }

  public static final int MESSAGE_FIELD_NUMBER = 3;
  private volatile java.lang.Object message_;
  /**
   * <pre>
   * 家族宣言
   * </pre>
   *
   * <code>optional string message = 3;</code>
   * @return Whether the message field is set.
   */
  @java.lang.Override
  public boolean hasMessage() {
    return ((bitField0_ & 0x00000002) != 0);
  }
  /**
   * <pre>
   * 家族宣言
   * </pre>
   *
   * <code>optional string message = 3;</code>
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
   * 家族宣言
   * </pre>
   *
   * <code>optional string message = 3;</code>
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

  public static final int PAGE_FIELD_NUMBER = 4;
  private int page_;
  /**
   * <pre>
   * 列表当前页数
   * </pre>
   *
   * <code>optional uint32 page = 4;</code>
   * @return Whether the page field is set.
   */
  @java.lang.Override
  public boolean hasPage() {
    return ((bitField0_ & 0x00000004) != 0);
  }
  /**
   * <pre>
   * 列表当前页数
   * </pre>
   *
   * <code>optional uint32 page = 4;</code>
   * @return The page.
   */
  @java.lang.Override
  public int getPage() {
    return page_;
  }

  public static final int FAMILY_NAME_FIELD_NUMBER = 5;
  private volatile java.lang.Object familyName_;
  /**
   * <pre>
   * 家族名称
   * </pre>
   *
   * <code>optional string family_name = 5;</code>
   * @return Whether the familyName field is set.
   */
  @java.lang.Override
  public boolean hasFamilyName() {
    return ((bitField0_ & 0x00000008) != 0);
  }
  /**
   * <pre>
   * 家族名称
   * </pre>
   *
   * <code>optional string family_name = 5;</code>
   * @return The familyName.
   */
  @java.lang.Override
  public java.lang.String getFamilyName() {
    java.lang.Object ref = familyName_;
    if (ref instanceof java.lang.String) {
      return (java.lang.String) ref;
    } else {
      com.google.protobuf.ByteString bs = 
          (com.google.protobuf.ByteString) ref;
      java.lang.String s = bs.toStringUtf8();
      familyName_ = s;
      return s;
    }
  }
  /**
   * <pre>
   * 家族名称
   * </pre>
   *
   * <code>optional string family_name = 5;</code>
   * @return The bytes for familyName.
   */
  @java.lang.Override
  public com.google.protobuf.ByteString
      getFamilyNameBytes() {
    java.lang.Object ref = familyName_;
    if (ref instanceof java.lang.String) {
      com.google.protobuf.ByteString b = 
          com.google.protobuf.ByteString.copyFromUtf8(
              (java.lang.String) ref);
      familyName_ = b;
      return b;
    } else {
      return (com.google.protobuf.ByteString) ref;
    }
  }

  public static final int USER_NAME_FIELD_NUMBER = 6;
  private volatile java.lang.Object userName_;
  /**
   * <pre>
   * 相关用户名称
   * </pre>
   *
   * <code>optional string user_name = 6;</code>
   * @return Whether the userName field is set.
   */
  @java.lang.Override
  public boolean hasUserName() {
    return ((bitField0_ & 0x00000010) != 0);
  }
  /**
   * <pre>
   * 相关用户名称
   * </pre>
   *
   * <code>optional string user_name = 6;</code>
   * @return The userName.
   */
  @java.lang.Override
  public java.lang.String getUserName() {
    java.lang.Object ref = userName_;
    if (ref instanceof java.lang.String) {
      return (java.lang.String) ref;
    } else {
      com.google.protobuf.ByteString bs = 
          (com.google.protobuf.ByteString) ref;
      java.lang.String s = bs.toStringUtf8();
      userName_ = s;
      return s;
    }
  }
  /**
   * <pre>
   * 相关用户名称
   * </pre>
   *
   * <code>optional string user_name = 6;</code>
   * @return The bytes for userName.
   */
  @java.lang.Override
  public com.google.protobuf.ByteString
      getUserNameBytes() {
    java.lang.Object ref = userName_;
    if (ref instanceof java.lang.String) {
      com.google.protobuf.ByteString b = 
          com.google.protobuf.ByteString.copyFromUtf8(
              (java.lang.String) ref);
      userName_ = b;
      return b;
    } else {
      return (com.google.protobuf.ByteString) ref;
    }
  }

  public static final int STATUS_FIELD_NUMBER = 7;
  private int status_;
  /**
   * <pre>
   * 相关状态
   * </pre>
   *
   * <code>optional uint32 status = 7;</code>
   * @return Whether the status field is set.
   */
  @java.lang.Override
  public boolean hasStatus() {
    return ((bitField0_ & 0x00000020) != 0);
  }
  /**
   * <pre>
   * 相关状态
   * </pre>
   *
   * <code>optional uint32 status = 7;</code>
   * @return The status.
   */
  @java.lang.Override
  public int getStatus() {
    return status_;
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
    if (((bitField0_ & 0x00000001) != 0)) {
      output.writeUInt32(2, familyId_);
    }
    if (((bitField0_ & 0x00000002) != 0)) {
      com.google.protobuf.GeneratedMessageV3.writeString(output, 3, message_);
    }
    if (((bitField0_ & 0x00000004) != 0)) {
      output.writeUInt32(4, page_);
    }
    if (((bitField0_ & 0x00000008) != 0)) {
      com.google.protobuf.GeneratedMessageV3.writeString(output, 5, familyName_);
    }
    if (((bitField0_ & 0x00000010) != 0)) {
      com.google.protobuf.GeneratedMessageV3.writeString(output, 6, userName_);
    }
    if (((bitField0_ & 0x00000020) != 0)) {
      output.writeUInt32(7, status_);
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
    if (((bitField0_ & 0x00000001) != 0)) {
      size += com.google.protobuf.CodedOutputStream
        .computeUInt32Size(2, familyId_);
    }
    if (((bitField0_ & 0x00000002) != 0)) {
      size += com.google.protobuf.GeneratedMessageV3.computeStringSize(3, message_);
    }
    if (((bitField0_ & 0x00000004) != 0)) {
      size += com.google.protobuf.CodedOutputStream
        .computeUInt32Size(4, page_);
    }
    if (((bitField0_ & 0x00000008) != 0)) {
      size += com.google.protobuf.GeneratedMessageV3.computeStringSize(5, familyName_);
    }
    if (((bitField0_ & 0x00000010) != 0)) {
      size += com.google.protobuf.GeneratedMessageV3.computeStringSize(6, userName_);
    }
    if (((bitField0_ & 0x00000020) != 0)) {
      size += com.google.protobuf.CodedOutputStream
        .computeUInt32Size(7, status_);
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
    if (!(obj instanceof protobuf.dto.OnlineFamilyDTO)) {
      return super.equals(obj);
    }
    protobuf.dto.OnlineFamilyDTO other = (protobuf.dto.OnlineFamilyDTO) obj;

    if (getType()
        != other.getType()) return false;
    if (hasFamilyId() != other.hasFamilyId()) return false;
    if (hasFamilyId()) {
      if (getFamilyId()
          != other.getFamilyId()) return false;
    }
    if (hasMessage() != other.hasMessage()) return false;
    if (hasMessage()) {
      if (!getMessage()
          .equals(other.getMessage())) return false;
    }
    if (hasPage() != other.hasPage()) return false;
    if (hasPage()) {
      if (getPage()
          != other.getPage()) return false;
    }
    if (hasFamilyName() != other.hasFamilyName()) return false;
    if (hasFamilyName()) {
      if (!getFamilyName()
          .equals(other.getFamilyName())) return false;
    }
    if (hasUserName() != other.hasUserName()) return false;
    if (hasUserName()) {
      if (!getUserName()
          .equals(other.getUserName())) return false;
    }
    if (hasStatus() != other.hasStatus()) return false;
    if (hasStatus()) {
      if (getStatus()
          != other.getStatus()) return false;
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
    if (hasFamilyId()) {
      hash = (37 * hash) + FAMILY_ID_FIELD_NUMBER;
      hash = (53 * hash) + getFamilyId();
    }
    if (hasMessage()) {
      hash = (37 * hash) + MESSAGE_FIELD_NUMBER;
      hash = (53 * hash) + getMessage().hashCode();
    }
    if (hasPage()) {
      hash = (37 * hash) + PAGE_FIELD_NUMBER;
      hash = (53 * hash) + getPage();
    }
    if (hasFamilyName()) {
      hash = (37 * hash) + FAMILY_NAME_FIELD_NUMBER;
      hash = (53 * hash) + getFamilyName().hashCode();
    }
    if (hasUserName()) {
      hash = (37 * hash) + USER_NAME_FIELD_NUMBER;
      hash = (53 * hash) + getUserName().hashCode();
    }
    if (hasStatus()) {
      hash = (37 * hash) + STATUS_FIELD_NUMBER;
      hash = (53 * hash) + getStatus();
    }
    hash = (29 * hash) + unknownFields.hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static protobuf.dto.OnlineFamilyDTO parseFrom(
      java.nio.ByteBuffer data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static protobuf.dto.OnlineFamilyDTO parseFrom(
      java.nio.ByteBuffer data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static protobuf.dto.OnlineFamilyDTO parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static protobuf.dto.OnlineFamilyDTO parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static protobuf.dto.OnlineFamilyDTO parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static protobuf.dto.OnlineFamilyDTO parseFrom(
      byte[] data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static protobuf.dto.OnlineFamilyDTO parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static protobuf.dto.OnlineFamilyDTO parseFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }
  public static protobuf.dto.OnlineFamilyDTO parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input);
  }
  public static protobuf.dto.OnlineFamilyDTO parseDelimitedFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
  }
  public static protobuf.dto.OnlineFamilyDTO parseFrom(
      com.google.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static protobuf.dto.OnlineFamilyDTO parseFrom(
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
  public static Builder newBuilder(protobuf.dto.OnlineFamilyDTO prototype) {
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
   * 家族
   * </pre>
   *
   * Protobuf type {@code protobuf.dto.OnlineFamilyDTO}
   */
  public static final class Builder extends
      com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
      // @@protoc_insertion_point(builder_implements:protobuf.dto.OnlineFamilyDTO)
      protobuf.dto.OnlineFamilyDTOOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return protobuf.dto.OnlineRequest.internal_static_protobuf_dto_OnlineFamilyDTO_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return protobuf.dto.OnlineRequest.internal_static_protobuf_dto_OnlineFamilyDTO_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              protobuf.dto.OnlineFamilyDTO.class, protobuf.dto.OnlineFamilyDTO.Builder.class);
    }

    // Construct using protobuf.dto.OnlineFamilyDTO.newBuilder()
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

      familyId_ = 0;
      bitField0_ = (bitField0_ & ~0x00000001);
      message_ = "";
      bitField0_ = (bitField0_ & ~0x00000002);
      page_ = 0;
      bitField0_ = (bitField0_ & ~0x00000004);
      familyName_ = "";
      bitField0_ = (bitField0_ & ~0x00000008);
      userName_ = "";
      bitField0_ = (bitField0_ & ~0x00000010);
      status_ = 0;
      bitField0_ = (bitField0_ & ~0x00000020);
      return this;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.Descriptor
        getDescriptorForType() {
      return protobuf.dto.OnlineRequest.internal_static_protobuf_dto_OnlineFamilyDTO_descriptor;
    }

    @java.lang.Override
    public protobuf.dto.OnlineFamilyDTO getDefaultInstanceForType() {
      return protobuf.dto.OnlineFamilyDTO.getDefaultInstance();
    }

    @java.lang.Override
    public protobuf.dto.OnlineFamilyDTO build() {
      protobuf.dto.OnlineFamilyDTO result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    @java.lang.Override
    public protobuf.dto.OnlineFamilyDTO buildPartial() {
      protobuf.dto.OnlineFamilyDTO result = new protobuf.dto.OnlineFamilyDTO(this);
      int from_bitField0_ = bitField0_;
      int to_bitField0_ = 0;
      result.type_ = type_;
      if (((from_bitField0_ & 0x00000001) != 0)) {
        result.familyId_ = familyId_;
        to_bitField0_ |= 0x00000001;
      }
      if (((from_bitField0_ & 0x00000002) != 0)) {
        to_bitField0_ |= 0x00000002;
      }
      result.message_ = message_;
      if (((from_bitField0_ & 0x00000004) != 0)) {
        result.page_ = page_;
        to_bitField0_ |= 0x00000004;
      }
      if (((from_bitField0_ & 0x00000008) != 0)) {
        to_bitField0_ |= 0x00000008;
      }
      result.familyName_ = familyName_;
      if (((from_bitField0_ & 0x00000010) != 0)) {
        to_bitField0_ |= 0x00000010;
      }
      result.userName_ = userName_;
      if (((from_bitField0_ & 0x00000020) != 0)) {
        result.status_ = status_;
        to_bitField0_ |= 0x00000020;
      }
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
      if (other instanceof protobuf.dto.OnlineFamilyDTO) {
        return mergeFrom((protobuf.dto.OnlineFamilyDTO)other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(protobuf.dto.OnlineFamilyDTO other) {
      if (other == protobuf.dto.OnlineFamilyDTO.getDefaultInstance()) return this;
      if (other.getType() != 0) {
        setType(other.getType());
      }
      if (other.hasFamilyId()) {
        setFamilyId(other.getFamilyId());
      }
      if (other.hasMessage()) {
        bitField0_ |= 0x00000002;
        message_ = other.message_;
        onChanged();
      }
      if (other.hasPage()) {
        setPage(other.getPage());
      }
      if (other.hasFamilyName()) {
        bitField0_ |= 0x00000008;
        familyName_ = other.familyName_;
        onChanged();
      }
      if (other.hasUserName()) {
        bitField0_ |= 0x00000010;
        userName_ = other.userName_;
        onChanged();
      }
      if (other.hasStatus()) {
        setStatus(other.getStatus());
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
      protobuf.dto.OnlineFamilyDTO parsedMessage = null;
      try {
        parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        parsedMessage = (protobuf.dto.OnlineFamilyDTO) e.getUnfinishedMessage();
        throw e.unwrapIOException();
      } finally {
        if (parsedMessage != null) {
          mergeFrom(parsedMessage);
        }
      }
      return this;
    }
    private int bitField0_;

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

    private int familyId_ ;
    /**
     * <pre>
     * 家族id
     * </pre>
     *
     * <code>optional uint32 family_id = 2;</code>
     * @return Whether the familyId field is set.
     */
    @java.lang.Override
    public boolean hasFamilyId() {
      return ((bitField0_ & 0x00000001) != 0);
    }
    /**
     * <pre>
     * 家族id
     * </pre>
     *
     * <code>optional uint32 family_id = 2;</code>
     * @return The familyId.
     */
    @java.lang.Override
    public int getFamilyId() {
      return familyId_;
    }
    /**
     * <pre>
     * 家族id
     * </pre>
     *
     * <code>optional uint32 family_id = 2;</code>
     * @param value The familyId to set.
     * @return This builder for chaining.
     */
    public Builder setFamilyId(int value) {
      bitField0_ |= 0x00000001;
      familyId_ = value;
      onChanged();
      return this;
    }
    /**
     * <pre>
     * 家族id
     * </pre>
     *
     * <code>optional uint32 family_id = 2;</code>
     * @return This builder for chaining.
     */
    public Builder clearFamilyId() {
      bitField0_ = (bitField0_ & ~0x00000001);
      familyId_ = 0;
      onChanged();
      return this;
    }

    private java.lang.Object message_ = "";
    /**
     * <pre>
     * 家族宣言
     * </pre>
     *
     * <code>optional string message = 3;</code>
     * @return Whether the message field is set.
     */
    public boolean hasMessage() {
      return ((bitField0_ & 0x00000002) != 0);
    }
    /**
     * <pre>
     * 家族宣言
     * </pre>
     *
     * <code>optional string message = 3;</code>
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
     * 家族宣言
     * </pre>
     *
     * <code>optional string message = 3;</code>
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
     * 家族宣言
     * </pre>
     *
     * <code>optional string message = 3;</code>
     * @param value The message to set.
     * @return This builder for chaining.
     */
    public Builder setMessage(
        java.lang.String value) {
      if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000002;
      message_ = value;
      onChanged();
      return this;
    }
    /**
     * <pre>
     * 家族宣言
     * </pre>
     *
     * <code>optional string message = 3;</code>
     * @return This builder for chaining.
     */
    public Builder clearMessage() {
      bitField0_ = (bitField0_ & ~0x00000002);
      message_ = getDefaultInstance().getMessage();
      onChanged();
      return this;
    }
    /**
     * <pre>
     * 家族宣言
     * </pre>
     *
     * <code>optional string message = 3;</code>
     * @param value The bytes for message to set.
     * @return This builder for chaining.
     */
    public Builder setMessageBytes(
        com.google.protobuf.ByteString value) {
      if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
      bitField0_ |= 0x00000002;
      message_ = value;
      onChanged();
      return this;
    }

    private int page_ ;
    /**
     * <pre>
     * 列表当前页数
     * </pre>
     *
     * <code>optional uint32 page = 4;</code>
     * @return Whether the page field is set.
     */
    @java.lang.Override
    public boolean hasPage() {
      return ((bitField0_ & 0x00000004) != 0);
    }
    /**
     * <pre>
     * 列表当前页数
     * </pre>
     *
     * <code>optional uint32 page = 4;</code>
     * @return The page.
     */
    @java.lang.Override
    public int getPage() {
      return page_;
    }
    /**
     * <pre>
     * 列表当前页数
     * </pre>
     *
     * <code>optional uint32 page = 4;</code>
     * @param value The page to set.
     * @return This builder for chaining.
     */
    public Builder setPage(int value) {
      bitField0_ |= 0x00000004;
      page_ = value;
      onChanged();
      return this;
    }
    /**
     * <pre>
     * 列表当前页数
     * </pre>
     *
     * <code>optional uint32 page = 4;</code>
     * @return This builder for chaining.
     */
    public Builder clearPage() {
      bitField0_ = (bitField0_ & ~0x00000004);
      page_ = 0;
      onChanged();
      return this;
    }

    private java.lang.Object familyName_ = "";
    /**
     * <pre>
     * 家族名称
     * </pre>
     *
     * <code>optional string family_name = 5;</code>
     * @return Whether the familyName field is set.
     */
    public boolean hasFamilyName() {
      return ((bitField0_ & 0x00000008) != 0);
    }
    /**
     * <pre>
     * 家族名称
     * </pre>
     *
     * <code>optional string family_name = 5;</code>
     * @return The familyName.
     */
    public java.lang.String getFamilyName() {
      java.lang.Object ref = familyName_;
      if (!(ref instanceof java.lang.String)) {
        com.google.protobuf.ByteString bs =
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        familyName_ = s;
        return s;
      } else {
        return (java.lang.String) ref;
      }
    }
    /**
     * <pre>
     * 家族名称
     * </pre>
     *
     * <code>optional string family_name = 5;</code>
     * @return The bytes for familyName.
     */
    public com.google.protobuf.ByteString
        getFamilyNameBytes() {
      java.lang.Object ref = familyName_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        familyName_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }
    /**
     * <pre>
     * 家族名称
     * </pre>
     *
     * <code>optional string family_name = 5;</code>
     * @param value The familyName to set.
     * @return This builder for chaining.
     */
    public Builder setFamilyName(
        java.lang.String value) {
      if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000008;
      familyName_ = value;
      onChanged();
      return this;
    }
    /**
     * <pre>
     * 家族名称
     * </pre>
     *
     * <code>optional string family_name = 5;</code>
     * @return This builder for chaining.
     */
    public Builder clearFamilyName() {
      bitField0_ = (bitField0_ & ~0x00000008);
      familyName_ = getDefaultInstance().getFamilyName();
      onChanged();
      return this;
    }
    /**
     * <pre>
     * 家族名称
     * </pre>
     *
     * <code>optional string family_name = 5;</code>
     * @param value The bytes for familyName to set.
     * @return This builder for chaining.
     */
    public Builder setFamilyNameBytes(
        com.google.protobuf.ByteString value) {
      if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
      bitField0_ |= 0x00000008;
      familyName_ = value;
      onChanged();
      return this;
    }

    private java.lang.Object userName_ = "";
    /**
     * <pre>
     * 相关用户名称
     * </pre>
     *
     * <code>optional string user_name = 6;</code>
     * @return Whether the userName field is set.
     */
    public boolean hasUserName() {
      return ((bitField0_ & 0x00000010) != 0);
    }
    /**
     * <pre>
     * 相关用户名称
     * </pre>
     *
     * <code>optional string user_name = 6;</code>
     * @return The userName.
     */
    public java.lang.String getUserName() {
      java.lang.Object ref = userName_;
      if (!(ref instanceof java.lang.String)) {
        com.google.protobuf.ByteString bs =
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        userName_ = s;
        return s;
      } else {
        return (java.lang.String) ref;
      }
    }
    /**
     * <pre>
     * 相关用户名称
     * </pre>
     *
     * <code>optional string user_name = 6;</code>
     * @return The bytes for userName.
     */
    public com.google.protobuf.ByteString
        getUserNameBytes() {
      java.lang.Object ref = userName_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        userName_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }
    /**
     * <pre>
     * 相关用户名称
     * </pre>
     *
     * <code>optional string user_name = 6;</code>
     * @param value The userName to set.
     * @return This builder for chaining.
     */
    public Builder setUserName(
        java.lang.String value) {
      if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000010;
      userName_ = value;
      onChanged();
      return this;
    }
    /**
     * <pre>
     * 相关用户名称
     * </pre>
     *
     * <code>optional string user_name = 6;</code>
     * @return This builder for chaining.
     */
    public Builder clearUserName() {
      bitField0_ = (bitField0_ & ~0x00000010);
      userName_ = getDefaultInstance().getUserName();
      onChanged();
      return this;
    }
    /**
     * <pre>
     * 相关用户名称
     * </pre>
     *
     * <code>optional string user_name = 6;</code>
     * @param value The bytes for userName to set.
     * @return This builder for chaining.
     */
    public Builder setUserNameBytes(
        com.google.protobuf.ByteString value) {
      if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
      bitField0_ |= 0x00000010;
      userName_ = value;
      onChanged();
      return this;
    }

    private int status_ ;
    /**
     * <pre>
     * 相关状态
     * </pre>
     *
     * <code>optional uint32 status = 7;</code>
     * @return Whether the status field is set.
     */
    @java.lang.Override
    public boolean hasStatus() {
      return ((bitField0_ & 0x00000020) != 0);
    }
    /**
     * <pre>
     * 相关状态
     * </pre>
     *
     * <code>optional uint32 status = 7;</code>
     * @return The status.
     */
    @java.lang.Override
    public int getStatus() {
      return status_;
    }
    /**
     * <pre>
     * 相关状态
     * </pre>
     *
     * <code>optional uint32 status = 7;</code>
     * @param value The status to set.
     * @return This builder for chaining.
     */
    public Builder setStatus(int value) {
      bitField0_ |= 0x00000020;
      status_ = value;
      onChanged();
      return this;
    }
    /**
     * <pre>
     * 相关状态
     * </pre>
     *
     * <code>optional uint32 status = 7;</code>
     * @return This builder for chaining.
     */
    public Builder clearStatus() {
      bitField0_ = (bitField0_ & ~0x00000020);
      status_ = 0;
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


    // @@protoc_insertion_point(builder_scope:protobuf.dto.OnlineFamilyDTO)
  }

  // @@protoc_insertion_point(class_scope:protobuf.dto.OnlineFamilyDTO)
  private static final protobuf.dto.OnlineFamilyDTO DEFAULT_INSTANCE;
  static {
    DEFAULT_INSTANCE = new protobuf.dto.OnlineFamilyDTO();
  }

  public static protobuf.dto.OnlineFamilyDTO getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final com.google.protobuf.Parser<OnlineFamilyDTO>
      PARSER = new com.google.protobuf.AbstractParser<OnlineFamilyDTO>() {
    @java.lang.Override
    public OnlineFamilyDTO parsePartialFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return new OnlineFamilyDTO(input, extensionRegistry);
    }
  };

  public static com.google.protobuf.Parser<OnlineFamilyDTO> parser() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.protobuf.Parser<OnlineFamilyDTO> getParserForType() {
    return PARSER;
  }

  @java.lang.Override
  public protobuf.dto.OnlineFamilyDTO getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

}


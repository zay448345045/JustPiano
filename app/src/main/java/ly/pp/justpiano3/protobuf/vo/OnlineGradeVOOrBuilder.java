// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: OnlineResponseProtocol.proto

package ly.pp.justpiano3.protobuf.vo;

public interface OnlineGradeVOOrBuilder extends
    // @@protoc_insertion_point(interface_extends:ly.pp.justpiano3.protobuf.vo.OnlineGradeVO)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <pre>
   * 暴击个数
   * </pre>
   *
   * <code>uint32 perfect = 1;</code>
   * @return The perfect.
   */
  int getPerfect();

  /**
   * <pre>
   * 很棒个数
   * </pre>
   *
   * <code>uint32 cool = 2;</code>
   * @return The cool.
   */
  int getCool();

  /**
   * <pre>
   * 完美个数
   * </pre>
   *
   * <code>uint32 great = 3;</code>
   * @return The great.
   */
  int getGreat();

  /**
   * <pre>
   * 一般个数
   * </pre>
   *
   * <code>uint32 bad = 4;</code>
   * @return The bad.
   */
  int getBad();

  /**
   * <pre>
   * 失误个数
   * </pre>
   *
   * <code>uint32 miss = 5;</code>
   * @return The miss.
   */
  int getMiss();

  /**
   * <pre>
   * 连击个数
   * </pre>
   *
   * <code>uint32 combo = 6;</code>
   * @return The combo.
   */
  int getCombo();

  /**
   * <pre>
   * 成绩条颜色（用于组队模式）
   * </pre>
   *
   * <code>uint32 grade_color = 7;</code>
   * @return The gradeColor.
   */
  int getGradeColor();

  /**
   * <pre>
   * 所获经验
   * </pre>
   *
   * <code>uint32 exp = 8;</code>
   * @return The exp.
   */
  int getExp();
}

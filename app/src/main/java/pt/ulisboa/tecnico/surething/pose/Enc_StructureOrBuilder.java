// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: pose.proto

package pt.ulisboa.tecnico.surething.pose;

public interface Enc_StructureOrBuilder extends
    // @@protoc_insertion_point(interface_extends:pt.ulisboa.tecnico.surething.pose.Enc_Structure)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <pre>
   * text string identifying the context of the authenticated data structure
   * </pre>
   *
   * <code>string context = 1;</code>
   * @return The context.
   */
  String getContext();
  /**
   * <pre>
   * text string identifying the context of the authenticated data structure
   * </pre>
   *
   * <code>string context = 1;</code>
   * @return The bytes for context.
   */
  com.google.protobuf.ByteString
      getContextBytes();

  /**
   * <pre>
   *data structure aka body
   * </pre>
   *
   * <code>.pt.ulisboa.tecnico.surething.pose.PoseEncrypt0 body = 2;</code>
   * @return Whether the body field is set.
   */
  boolean hasBody();
  /**
   * <pre>
   *data structure aka body
   * </pre>
   *
   * <code>.pt.ulisboa.tecnico.surething.pose.PoseEncrypt0 body = 2;</code>
   * @return The body.
   */
  PoseEncrypt0 getBody();
  /**
   * <pre>
   *data structure aka body
   * </pre>
   *
   * <code>.pt.ulisboa.tecnico.surething.pose.PoseEncrypt0 body = 2;</code>
   */
  PoseEncrypt0OrBuilder getBodyOrBuilder();

  /**
   * <pre>
   *protected fields of the body structure
   * </pre>
   *
   * <code>bytes protected = 3;</code>
   * @return The protected.
   */
  com.google.protobuf.ByteString getProtected();
}

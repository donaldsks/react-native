/**
 * Copyright (c) 2014-present, Facebook, Inc.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */

package com.facebook.csslayout;

import javax.annotation.Nullable;

import java.util.List;
import java.util.ArrayList;

import com.facebook.proguard.annotations.DoNotStrip;
import com.facebook.soloader.SoLoader;

@DoNotStrip
public class CSSNode implements CSSNodeAPI<CSSNode> {

  static {
    SoLoader.loadLibrary("csslayout");
  }

  /**
   * Get native instance count. Useful for testing only.
   */
  static native int jni_CSSNodeGetInstanceCount();
  static native void jni_CSSLog(int level, String message);

  private static native void jni_CSSLayoutSetLogger(Object logger);
  public static void setLogger(CSSLogger logger) {
    jni_CSSLayoutSetLogger(logger);
  }

  private static native void jni_CSSLayoutSetExperimentalFeatureEnabled(
      int feature,
      boolean enabled);
  public static void setExperimentalFeatureEnabled(
      YogaExperimentalFeature feature,
      boolean enabled) {
    jni_CSSLayoutSetExperimentalFeatureEnabled(feature.intValue(), enabled);
  }

  private static native boolean jni_CSSLayoutIsExperimentalFeatureEnabled(int feature);
  public static boolean isExperimentalFeatureEnabled(YogaExperimentalFeature feature) {
    return jni_CSSLayoutIsExperimentalFeatureEnabled(feature.intValue());
  }

  private CSSNode mParent;
  private List<CSSNode> mChildren;
  private MeasureFunction mMeasureFunction;
  private long mNativePointer;
  private Object mData;

  private boolean mHasSetPadding = false;
  private boolean mHasSetMargin = false;
  private boolean mHasSetBorder = false;
  private boolean mHasSetPosition = false;

  @DoNotStrip
  private float mWidth = YogaConstants.UNDEFINED;
  @DoNotStrip
  private float mHeight = YogaConstants.UNDEFINED;
  @DoNotStrip
  private float mTop = YogaConstants.UNDEFINED;
  @DoNotStrip
  private float mLeft = YogaConstants.UNDEFINED;
  @DoNotStrip
  private int mLayoutDirection = 0;

  private native long jni_CSSNodeNew();
  public CSSNode() {
    mNativePointer = jni_CSSNodeNew();
    if (mNativePointer == 0) {
      throw new IllegalStateException("Failed to allocate native memory");
    }
  }

  private native void jni_CSSNodeFree(long nativePointer);
  @Override
  protected void finalize() throws Throwable {
    try {
      jni_CSSNodeFree(mNativePointer);
    } finally {
      super.finalize();
    }
  }

  private native void jni_CSSNodeReset(long nativePointer);
  @Override
  public void reset() {
    mHasSetPadding = false;
    mHasSetMargin = false;
    mHasSetBorder = false;
    mHasSetPosition = false;

    mWidth = YogaConstants.UNDEFINED;
    mHeight = YogaConstants.UNDEFINED;
    mTop = YogaConstants.UNDEFINED;
    mLeft = YogaConstants.UNDEFINED;
    mLayoutDirection = 0;

    mMeasureFunction = null;
    mData = null;

    jni_CSSNodeReset(mNativePointer);
  }

  @Override
  public int getChildCount() {
    return mChildren == null ? 0 : mChildren.size();
  }

  @Override
  public CSSNode getChildAt(int i) {
    return mChildren.get(i);
  }

  private native void jni_CSSNodeInsertChild(long nativePointer, long childPointer, int index);
  @Override
  public void addChildAt(CSSNode child, int i) {
    if (child.mParent != null) {
      throw new IllegalStateException("Child already has a parent, it must be removed first.");
    }

    if (mChildren == null) {
      mChildren = new ArrayList<>(4);
    }
    mChildren.add(i, child);
    child.mParent = this;
    jni_CSSNodeInsertChild(mNativePointer, child.mNativePointer, i);
  }

  private native void jni_CSSNodeRemoveChild(long nativePointer, long childPointer);
  @Override
  public CSSNode removeChildAt(int i) {

    final CSSNode child = mChildren.remove(i);
    child.mParent = null;
    jni_CSSNodeRemoveChild(mNativePointer, child.mNativePointer);
    return child;
  }

  @Override
  public @Nullable
  CSSNode getParent() {
    return mParent;
  }

  @Override
  public int indexOf(CSSNode child) {
    return mChildren == null ? -1 : mChildren.indexOf(child);
  }

  private native void jni_CSSNodeCalculateLayout(long nativePointer);
  @Override
  public void calculateLayout() {
    jni_CSSNodeCalculateLayout(mNativePointer);
  }

  private native boolean jni_CSSNodeHasNewLayout(long nativePointer);
  @Override
  public boolean hasNewLayout() {
    return jni_CSSNodeHasNewLayout(mNativePointer);
  }

  private native void jni_CSSNodeMarkDirty(long nativePointer);
  @Override
  public void dirty() {
    jni_CSSNodeMarkDirty(mNativePointer);
  }

  private native boolean jni_CSSNodeIsDirty(long nativePointer);
  @Override
  public boolean isDirty() {
    return jni_CSSNodeIsDirty(mNativePointer);
  }

  private native void jni_CSSNodeMarkLayoutSeen(long nativePointer);
  @Override
  public void markLayoutSeen() {
    jni_CSSNodeMarkLayoutSeen(mNativePointer);
  }

  private native void jni_CSSNodeCopyStyle(long dstNativePointer, long srcNativePointer);
  @Override
  public void copyStyle(CSSNode srcNode) {
    jni_CSSNodeCopyStyle(mNativePointer, srcNode.mNativePointer);
  }

  private native int jni_CSSNodeStyleGetDirection(long nativePointer);
  @Override
  public YogaDirection getStyleDirection() {
    return YogaDirection.values()[jni_CSSNodeStyleGetDirection(mNativePointer)];
  }

  private native void jni_CSSNodeStyleSetDirection(long nativePointer, int direction);
  @Override
  public void setDirection(YogaDirection direction) {
    jni_CSSNodeStyleSetDirection(mNativePointer, direction.intValue());
  }

  private native int jni_CSSNodeStyleGetFlexDirection(long nativePointer);
  @Override
  public YogaFlexDirection getFlexDirection() {
    return YogaFlexDirection.values()[jni_CSSNodeStyleGetFlexDirection(mNativePointer)];
  }

  private native void jni_CSSNodeStyleSetFlexDirection(long nativePointer, int flexDirection);
  @Override
  public void setFlexDirection(YogaFlexDirection flexDirection) {
    jni_CSSNodeStyleSetFlexDirection(mNativePointer, flexDirection.intValue());
  }

  private native int jni_CSSNodeStyleGetJustifyContent(long nativePointer);
  @Override
  public YogaJustify getJustifyContent() {
    return YogaJustify.values()[jni_CSSNodeStyleGetJustifyContent(mNativePointer)];
  }

  private native void jni_CSSNodeStyleSetJustifyContent(long nativePointer, int justifyContent);
  @Override
  public void setJustifyContent(YogaJustify justifyContent) {
    jni_CSSNodeStyleSetJustifyContent(mNativePointer, justifyContent.intValue());
  }

  private native int jni_CSSNodeStyleGetAlignItems(long nativePointer);
  @Override
  public YogaAlign getAlignItems() {
    return YogaAlign.values()[jni_CSSNodeStyleGetAlignItems(mNativePointer)];
  }

  private native void jni_CSSNodeStyleSetAlignItems(long nativePointer, int alignItems);
  @Override
  public void setAlignItems(YogaAlign alignItems) {
    jni_CSSNodeStyleSetAlignItems(mNativePointer, alignItems.intValue());
  }

  private native int jni_CSSNodeStyleGetAlignSelf(long nativePointer);
  @Override
  public YogaAlign getAlignSelf() {
    return YogaAlign.values()[jni_CSSNodeStyleGetAlignSelf(mNativePointer)];
  }

  private native void jni_CSSNodeStyleSetAlignSelf(long nativePointer, int alignSelf);
  @Override
  public void setAlignSelf(YogaAlign alignSelf) {
    jni_CSSNodeStyleSetAlignSelf(mNativePointer, alignSelf.intValue());
  }

  private native int jni_CSSNodeStyleGetAlignContent(long nativePointer);
  @Override
  public YogaAlign getAlignContent() {
    return YogaAlign.values()[jni_CSSNodeStyleGetAlignContent(mNativePointer)];
  }

  private native void jni_CSSNodeStyleSetAlignContent(long nativePointer, int alignContent);
  @Override
  public void setAlignContent(YogaAlign alignContent) {
    jni_CSSNodeStyleSetAlignContent(mNativePointer, alignContent.intValue());
  }

  private native int jni_CSSNodeStyleGetPositionType(long nativePointer);
  @Override
  public YogaPositionType getPositionType() {
    return YogaPositionType.values()[jni_CSSNodeStyleGetPositionType(mNativePointer)];
  }

  private native void jni_CSSNodeStyleSetPositionType(long nativePointer, int positionType);
  @Override
  public void setPositionType(YogaPositionType positionType) {
    jni_CSSNodeStyleSetPositionType(mNativePointer, positionType.intValue());
  }

  private native void jni_CSSNodeStyleSetFlexWrap(long nativePointer, int wrapType);
  @Override
  public void setWrap(YogaWrap flexWrap) {
    jni_CSSNodeStyleSetFlexWrap(mNativePointer, flexWrap.intValue());
  }

  private native int jni_CSSNodeStyleGetOverflow(long nativePointer);
  @Override
  public YogaOverflow getOverflow() {
    return YogaOverflow.values()[jni_CSSNodeStyleGetOverflow(mNativePointer)];
  }

  private native void jni_CSSNodeStyleSetOverflow(long nativePointer, int overflow);
  @Override
  public void setOverflow(YogaOverflow overflow) {
    jni_CSSNodeStyleSetOverflow(mNativePointer, overflow.intValue());
  }

  private native void jni_CSSNodeStyleSetFlex(long nativePointer, float flex);
  @Override
  public void setFlex(float flex) {
    jni_CSSNodeStyleSetFlex(mNativePointer, flex);
  }

  private native float jni_CSSNodeStyleGetFlexGrow(long nativePointer);
  @Override
  public float getFlexGrow() {
    return jni_CSSNodeStyleGetFlexGrow(mNativePointer);
  }

  private native void jni_CSSNodeStyleSetFlexGrow(long nativePointer, float flexGrow);
  @Override
  public void setFlexGrow(float flexGrow) {
    jni_CSSNodeStyleSetFlexGrow(mNativePointer, flexGrow);
  }

  private native float jni_CSSNodeStyleGetFlexShrink(long nativePointer);
  @Override
  public float getFlexShrink() {
    return jni_CSSNodeStyleGetFlexShrink(mNativePointer);
  }

  private native void jni_CSSNodeStyleSetFlexShrink(long nativePointer, float flexShrink);
  @Override
  public void setFlexShrink(float flexShrink) {
    jni_CSSNodeStyleSetFlexShrink(mNativePointer, flexShrink);
  }

  private native float jni_CSSNodeStyleGetFlexBasis(long nativePointer);
  @Override
  public float getFlexBasis() {
    return jni_CSSNodeStyleGetFlexBasis(mNativePointer);
  }

  private native void jni_CSSNodeStyleSetFlexBasis(long nativePointer, float flexBasis);
  @Override
  public void setFlexBasis(float flexBasis) {
    jni_CSSNodeStyleSetFlexBasis(mNativePointer, flexBasis);
  }

  private native float jni_CSSNodeStyleGetMargin(long nativePointer, int edge);
  @Override
  public float getMargin(YogaEdge edge) {
    if (!mHasSetMargin) {
      return edge.intValue() < YogaEdge.START.intValue() ? 0 : YogaConstants.UNDEFINED;
    }
    return jni_CSSNodeStyleGetMargin(mNativePointer, edge.intValue());
  }

  private native void jni_CSSNodeStyleSetMargin(long nativePointer, int edge, float margin);
  @Override
  public void setMargin(YogaEdge edge, float margin) {
    mHasSetMargin = true;
    jni_CSSNodeStyleSetMargin(mNativePointer, edge.intValue(), margin);
  }

  private native float jni_CSSNodeStyleGetPadding(long nativePointer, int edge);
  @Override
  public float getPadding(YogaEdge edge) {
    if (!mHasSetPadding) {
      return edge.intValue() < YogaEdge.START.intValue() ? 0 : YogaConstants.UNDEFINED;
    }
    return jni_CSSNodeStyleGetPadding(mNativePointer, edge.intValue());
  }

  private native void jni_CSSNodeStyleSetPadding(long nativePointer, int edge, float padding);
  @Override
  public void setPadding(YogaEdge edge, float padding) {
    mHasSetPadding = true;
    jni_CSSNodeStyleSetPadding(mNativePointer, edge.intValue(), padding);
  }

  private native float jni_CSSNodeStyleGetBorder(long nativePointer, int edge);
  @Override
  public float getBorder(YogaEdge edge) {
    if (!mHasSetBorder) {
      return edge.intValue() < YogaEdge.START.intValue() ? 0 : YogaConstants.UNDEFINED;
    }
    return jni_CSSNodeStyleGetBorder(mNativePointer, edge.intValue());
  }

  private native void jni_CSSNodeStyleSetBorder(long nativePointer, int edge, float border);
  @Override
  public void setBorder(YogaEdge edge, float border) {
    mHasSetBorder = true;
    jni_CSSNodeStyleSetBorder(mNativePointer, edge.intValue(), border);
  }

  private native float jni_CSSNodeStyleGetPosition(long nativePointer, int edge);
  @Override
  public float getPosition(YogaEdge edge) {
    if (!mHasSetPosition) {
      return YogaConstants.UNDEFINED;
    }
    return jni_CSSNodeStyleGetPosition(mNativePointer, edge.intValue());
  }

  private native void jni_CSSNodeStyleSetPosition(long nativePointer, int edge, float position);
  @Override
  public void setPosition(YogaEdge edge, float position) {
    mHasSetPosition = true;
    jni_CSSNodeStyleSetPosition(mNativePointer, edge.intValue(), position);
  }

  private native float jni_CSSNodeStyleGetWidth(long nativePointer);
  @Override
  public float getWidth() {
    return jni_CSSNodeStyleGetWidth(mNativePointer);
  }

  private native void jni_CSSNodeStyleSetWidth(long nativePointer, float width);
  @Override
  public void setWidth(float width) {
    jni_CSSNodeStyleSetWidth(mNativePointer, width);
  }

  private native float jni_CSSNodeStyleGetHeight(long nativePointer);
  @Override
  public float getHeight() {
    return jni_CSSNodeStyleGetHeight(mNativePointer);
  }

  private native void jni_CSSNodeStyleSetHeight(long nativePointer, float height);
  @Override
  public void setHeight(float height) {
    jni_CSSNodeStyleSetHeight(mNativePointer, height);
  }

  private native float jni_CSSNodeStyleGetMinWidth(long nativePointer);
  @Override
  public float getMinWidth() {
    return jni_CSSNodeStyleGetMinWidth(mNativePointer);
  }

  private native void jni_CSSNodeStyleSetMinWidth(long nativePointer, float minWidth);
  @Override
  public void setMinWidth(float minWidth) {
    jni_CSSNodeStyleSetMinWidth(mNativePointer, minWidth);
  }

  private native float jni_CSSNodeStyleGetMinHeight(long nativePointer);
  @Override
  public float getMinHeight() {
    return jni_CSSNodeStyleGetMinHeight(mNativePointer);
  }

  private native void jni_CSSNodeStyleSetMinHeight(long nativePointer, float minHeight);
  @Override
  public void setMinHeight(float minHeight) {
    jni_CSSNodeStyleSetMinHeight(mNativePointer, minHeight);
  }

  private native float jni_CSSNodeStyleGetMaxWidth(long nativePointer);
  @Override
  public float getMaxWidth() {
    return jni_CSSNodeStyleGetMaxWidth(mNativePointer);
  }

  private native void jni_CSSNodeStyleSetMaxWidth(long nativePointer, float maxWidth);
  @Override
  public void setMaxWidth(float maxWidth) {
    jni_CSSNodeStyleSetMaxWidth(mNativePointer, maxWidth);
  }

  private native float jni_CSSNodeStyleGetMaxHeight(long nativePointer);
  @Override
  public float getMaxHeight() {
    return jni_CSSNodeStyleGetMaxHeight(mNativePointer);
  }

  private native void jni_CSSNodeStyleSetMaxHeight(long nativePointer, float maxheight);
  @Override
  public void setMaxHeight(float maxheight) {
    jni_CSSNodeStyleSetMaxHeight(mNativePointer, maxheight);
  }

  private native float jni_CSSNodeStyleGetAspectRatio(long nativePointer);
  public float getAspectRatio() {
    return jni_CSSNodeStyleGetAspectRatio(mNativePointer);
  }

  private native void jni_CSSNodeStyleSetAspectRatio(long nativePointer, float aspectRatio);
  public void setAspectRatio(float aspectRatio) {
    jni_CSSNodeStyleSetAspectRatio(mNativePointer, aspectRatio);
  }

  @Override
  public float getLayoutX() {
    return mLeft;
  }

  @Override
  public float getLayoutY() {
    return mTop;
  }

  @Override
  public float getLayoutWidth() {
    return mWidth;
  }

  @Override
  public float getLayoutHeight() {
    return mHeight;
  }

  @Override
  public YogaDirection getLayoutDirection() {
    return YogaDirection.values()[mLayoutDirection];
  }

  private native void jni_CSSNodeSetHasMeasureFunc(long nativePointer, boolean hasMeasureFunc);
  @Override
  public void setMeasureFunction(MeasureFunction measureFunction) {
    mMeasureFunction = measureFunction;
    jni_CSSNodeSetHasMeasureFunc(mNativePointer, measureFunction != null);
  }

  // Implementation Note: Why this method needs to stay final
  //
  // We cache the jmethodid for this method in CSSLayout code. This means that even if a subclass
  // were to override measure, we'd still call this implementation from layout code since the
  // overriding method will have a different jmethodid. This is final to prevent that mistake.
  @DoNotStrip
  public final long measure(float width, int widthMode, float height, int heightMode) {
    if (!isMeasureDefined()) {
      throw new RuntimeException("Measure function isn't defined!");
    }

    return mMeasureFunction.measure(
          this,
          width,
          YogaMeasureMode.values()[widthMode],
          height,
          YogaMeasureMode.values()[heightMode]);
  }

  @Override
  public boolean isMeasureDefined() {
    return mMeasureFunction != null;
  }

  @Override
  public void setData(Object data) {
    mData = data;
  }

  @Override
  public Object getData() {
    return mData;
  }
}

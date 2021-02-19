package com.mahao.banner;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.os.Build;
import android.renderscript.ScriptIntrinsicYuvToRGB;
import android.text.InputType;
import android.text.LoginFilter;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import java.lang.reflect.UndeclaredThrowableException;
import java.util.ArrayList;

import androidx.annotation.IntegerRes;
import androidx.annotation.NonNull;
import androidx.core.math.MathUtils;
import androidx.core.view.DragStartHelper;
import androidx.core.view.ViewCompat;
import androidx.customview.widget.ViewDragHelper;
import androidx.lifecycle.ViewModelProvider;
import androidx.slidingpanelayout.widget.SlidingPaneLayout;

public class SlidePanlayout extends ViewGroup {


    private static final String TAG = "SlidePanlayout";
    private static final int DEFAULT_OVERHANGE_SIZE = 32;
    private int mOverHangSize;
    private static final int MIN_FLING_VELOCITY = 400;

    private ViewDragHelper mDragHelper;
    private View mSlideView;
    private float mSlideOffset;
    private int mSlideRange;
    private boolean mFirstLayout = true;
    private boolean mCanSlide;
    private boolean mPreservedOpenState;
    private float mParallaxOffset;
    private float mParallaxBy;
    private int mSlideColor = 0xcccccc;
    private ArrayList<DisableLayerRunnable> postRunable = new ArrayList<>();
    private final Rect mRect = new Rect();
    boolean mIsunAbleToDrag;


    public SlidePanlayout(Context context) {
        this(context, null);
    }

    public SlidePanlayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlidePanlayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        float density = context.getResources().getDisplayMetrics().density;
        mOverHangSize = (int) (DEFAULT_OVERHANGE_SIZE * (density + 0.5f));
        setWillNotDraw(false);
        mDragHelper = ViewDragHelper.create(this, 0.5f, new ViewDragHelperCallBack());
        mDragHelper.setMinVelocity(MIN_FLING_VELOCITY * density);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int sizeW = MeasureSpec.getSize(widthMeasureSpec);
        int sizeH = MeasureSpec.getSize(heightMeasureSpec);
        int modeW = MeasureSpec.getMode(widthMeasureSpec);
        int modeH = MeasureSpec.getMode(heightMeasureSpec);
        int childCount = getChildCount();

        Log.d(TAG, "onMeasure: " + sizeW + " h = " + sizeH + " " + modeW + "  " + modeH);
        if (modeW != MeasureSpec.EXACTLY) {
            if (isInEditMode()) {
                if (modeW == MeasureSpec.AT_MOST) {
                    modeW = MeasureSpec.EXACTLY;
                } else if (modeW == MeasureSpec.UNSPECIFIED) {
                    modeW = MeasureSpec.EXACTLY;
                    sizeW = 300;
                }
            } else {
                throw new RuntimeException("mode w error");
            }
        } else if (modeH == MeasureSpec.UNSPECIFIED) {
            if (isInEditMode()) {
                modeH = MeasureSpec.AT_MOST;
                sizeH = 300;
            } else {
                throw new RuntimeException("mode h error");
            }
        }

        int layoutHeight = 0;
        int maxLayoutHeight = 0;
        if (modeH == MeasureSpec.EXACTLY) {
            layoutHeight = maxLayoutHeight = sizeH - getPaddingTop() - getPaddingBottom();
        } else if (modeH == MeasureSpec.AT_MOST) {
            maxLayoutHeight = sizeH - getPaddingBottom() - getPaddingTop();
        }
        int widthSizeAvailable = sizeW - getPaddingLeft() - getPaddingRight();
        int widthRemain = widthSizeAvailable;
        final int count = getChildCount();
        if (count > 2) {
            Log.d(TAG, "onMeasure: more than two child");
        }
        boolean canSlide = false;
        mSlideView = null;
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            SlideLayoutParams layoutParams = (SlideLayoutParams) child.getLayoutParams();
            measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0);
            final int childWidth = child.getMeasuredWidth();
            final int childHeight = child.getMeasuredHeight();
            if (modeH == MeasureSpec.AT_MOST && childHeight > layoutHeight) {
                layoutHeight = Math.min(childHeight, maxLayoutHeight);
            }
            widthRemain = widthRemain - childWidth;
            canSlide |= layoutParams.slideAble = widthRemain < 0;
            if (layoutParams.slideAble) {
                mSlideView = child;
            }
            Log.d(TAG, "onMeasure: " + child.getMeasuredWidth() + "  " + child.getMeasuredHeight());
        }
        //non-sliding panels are smaller than the full screen
        if (canSlide) {
            final int fixedPanelWidthLimit = widthSizeAvailable - mOverHangSize;
            for (int j = 0; j < getChildCount(); j++) {
                final View child = getChildAt(j);
                if (child.getVisibility() == GONE) {
                    continue;
                }
                SlideLayoutParams lp = (SlideLayoutParams) child.getLayoutParams();
                int measuredWidth = child.getMeasuredWidth();
                if (canSlide && child != mSlideView) {
                    if (lp.width < 0 && measuredWidth > fixedPanelWidthLimit) {
                        child.measure(MeasureSpec.makeMeasureSpec(fixedPanelWidthLimit, MeasureSpec.EXACTLY),
                                getChildMeasureSpec(heightMeasureSpec, getPaddingTop() + getPaddingBottom(), sizeW));
                    }
                }
            }
        }
        mCanSlide = canSlide;
        int measureHeight = layoutHeight + getPaddingBottom() + getPaddingTop();
        Log.d(TAG, "onMeasure: " + canSlide + " " + sizeW + " " + measureHeight);
        setMeasuredDimension(sizeW, measureHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final boolean isLayoutRtl = isLayoutRtlSupport();
        if (isLayoutRtl) {
            mDragHelper.setEdgeTrackingEnabled(ViewDragHelper.EDGE_RIGHT);
        } else {
            mDragHelper.setEdgeTrackingEnabled(ViewDragHelper.EDGE_LEFT);
        }
        final int width = r - l;
        final int paddingStart = isLayoutRtl ? getPaddingRight() : getPaddingLeft();
        final int paddingEnd = isLayoutRtl ? getPaddingLeft() : getPaddingRight();
        final int paddingTop = getPaddingTop();

        final int childCount = getChildCount();
        int xStart = paddingStart;
        int nextStart = xStart;
        if (mFirstLayout) {
            mSlideOffset = mCanSlide && mPreservedOpenState ? 1.0f : 0.0f;
        }
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == View.GONE) {
                continue;
            }
            SlideLayoutParams layoutParams = (SlideLayoutParams) child.getLayoutParams();
            final int childWith = child.getMeasuredWidth();
            int offset = 0;
            if (layoutParams.slideAble) {
                final int marginW = layoutParams.leftMargin + layoutParams.rightMargin;
                final int range = Math.min(nextStart, width - paddingEnd - mOverHangSize) - xStart - marginW;
                mSlideRange = range;
                final int leftMargin = isLayoutRtl ? layoutParams.leftMargin : layoutParams.leftMargin;
                layoutParams.dimWhenOffset = xStart + leftMargin + range + width / 2 > childWith - paddingEnd;
                final int pos = (int) (range * mSlideOffset);
                xStart += pos + leftMargin;
                mSlideOffset = (float) pos / mSlideRange;
            } else if (mCanSlide && mParallaxBy != 0) {
                xStart = nextStart;
                offset = (int) ((1 - mSlideOffset) * mParallaxBy);
            } else {
                xStart = nextStart;
            }
            final int childRight;
            final int childLeft;
            childLeft = xStart - offset;
            childRight = childLeft + childWith;
            final int childTop = paddingTop;
            final int childBottom = childTop + child.getMeasuredHeight();
            Log.d(TAG, "onLayout: " + childLeft + " " + childTop + " " + childRight + " " + childBottom);
            child.layout(childLeft, childTop, childRight, childBottom);
            nextStart += child.getWidth();
        }

        if (mFirstLayout) {
            if (mCanSlide) {
                if (mParallaxBy != 0) {
                    parallaxOtherViews(mSlideOffset);
                }
                SlideLayoutParams layoutParams = (SlideLayoutParams) mSlideView.getLayoutParams();
                if (layoutParams.dimWhenOffset) {
                    dimChildView(mSlideView, mSlideOffset, mSlideColor);
                }
            } else {
                for (int i = 0; i < childCount; i++) {
                    dimChildView(getChildAt(i), 0, mSlideColor);
                }
            }
            updateObsuredViewsVisibility(mSlideView);
        }
    }


    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        final SlideLayoutParams layoutParams = (SlideLayoutParams) child.getLayoutParams();
        boolean result;
        final int save = canvas.save();
        if (mCanSlide && !layoutParams.slideAble && mSlideView != null) {
            canvas.getClipBounds(mRect);
            if (isLayoutRtlSupport()) {
                mRect.left = Math.max(mRect.left, mSlideView.getRight());
            } else {
                mRect.right = Math.min(mRect.right, mSlideView.getLeft());
            }
            canvas.clipRect(mRect);
        }
        result = super.drawChild(canvas, child, drawingTime);
        canvas.restoreToCount(save);
        return result;
    }

    /**
     * 如果sliview完全盖住了panel，则panel隐藏了，可能是未了更好的性能吧
     *
     * @param slideView
     */
    private void updateObsuredViewsVisibility(View slideView) {
        final boolean isLayoutRtl = isLayoutRtlSupport();
        final int startBound = isLayoutRtl ? getWidth() - getPaddingRight() : getPaddingLeft();
        final int endBound = isLayoutRtl ? getPaddingLeft() : getWidth() - getPaddingRight();
        final int topBound = getPaddingTop();
        final int bottomBound = getPaddingBottom();
        final int left;
        final int right;
        final int top;
        final int bottom;
        if (slideView != null && viewIsopaque(mSlideView)) {
            left = slideView.getLeft();
            right = slideView.getRight();
            top = slideView.getTop();
            bottom = slideView.getBottom();
        } else {
            left = right = top = bottom = 0;
        }
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child == slideView) {
                break;
            } else if (child.getVisibility() == View.GONE) {
                continue;
            }
            final int clampedChildLeft = Math.max(isLayoutRtl ? endBound : startBound, child.getLeft());
            final int clampedChildRihght = Math.min(isLayoutRtl ? startBound : endBound, child.getRight());
            final int clampedChildTop = Math.max(topBound, child.getTop());
            final int clampedChildBottom = Math.min(bottomBound, child.getBottom());
            if (clampedChildLeft >= left && clampedChildRihght <= right
                    && clampedChildTop >= top && clampedChildBottom <= bottom) {
                child.setVisibility(INVISIBLE);
            } else {
                child.setVisibility(VISIBLE);
            }
        }

    }

    private void dimChildView(View slideView, float slideOffset, int slideColor) {

        final SlideLayoutParams layoutParams = (SlideLayoutParams) slideView.getLayoutParams();
        if (slideOffset > 0 && slideColor != 0) {
            final int baseAlpha = (slideColor & 0xff000000) >>> 24;
            int imag = baseAlpha * slideColor;
            int color = imag << 24 | slideColor & 0xfffff;
            if (layoutParams.dimPaint == null) {
                layoutParams.dimPaint = new Paint();
            }
            layoutParams.dimPaint.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_OVER));
            if (slideView.getLayerType() != View.LAYER_TYPE_HARDWARE) {
                slideView.setLayerType(View.LAYER_TYPE_HARDWARE, layoutParams.dimPaint);
            }
            invalidateChildRegion(slideView);
        } else if (slideView.getLayerType() != View.LAYER_TYPE_NONE) {
            if (layoutParams.dimPaint != null) {
                layoutParams.dimPaint.setColorFilter(null);
            }
            final DisableLayerRunnable dlr = new DisableLayerRunnable(slideView);
            postRunable.add(dlr);
            ViewCompat.postOnAnimation(this, dlr);
        }
    }


    @Override
    public void computeScroll() {
        if (mDragHelper.continueSettling(true)) {
            if (!mCanSlide) {
                mDragHelper.abort();
                return;
            }
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        return mDragHelper.shouldInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mDragHelper.processTouchEvent(event);
        return true;
    }

    private void invalidateChildRegion(View view) {
        if (Build.VERSION.SDK_INT > 17) {
            ViewCompat.setLayerPaint(view, ((SlideLayoutParams) view.getLayoutParams()).dimPaint);
        }
        ViewCompat.postInvalidateOnAnimation(this, view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
    }

    private void parallaxOtherViews(float slideOffset) {
        SlideLayoutParams layoutParams = (SlideLayoutParams) mSlideView.getLayoutParams();
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child == mSlideView) continue;
            final int oldOffset = (int) ((1 - mParallaxOffset) * mParallaxBy);
            mParallaxOffset = slideOffset;
            final int newOffset = (int) ((1 - slideOffset) * mParallaxBy);
            final int dx = oldOffset - newOffset;
            child.offsetLeftAndRight(dx);
        }
    }

    public void setParallaxDistance(int paraDistance) {
        this.mParallaxBy = paraDistance;
    }

    private class ViewDragHelperCallBack extends ViewDragHelper.Callback {

        @Override
        public boolean tryCaptureView(@NonNull View child, int pointerId) {
            if (mIsunAbleToDrag) {
                return false;
            }
            SlideLayoutParams layoutParams = (SlideLayoutParams) child.getLayoutParams();
            Log.d(TAG, "tryCaptureView: " + layoutParams.slideAble);
            return layoutParams.slideAble;
        }

        @Override
        public void onViewDragStateChanged(int state) {
            if (mDragHelper.getViewDragState() == ViewDragHelper.STATE_IDLE) {
                if (mSlideOffset == 0) {
                    updateObsuredViewsVisibility(mSlideView);
                    mPreservedOpenState = false;
                } else {
                    mPreservedOpenState = true;
                }
            }
        }

        @Override
        public void onViewCaptured(@NonNull View capturedChild, int activePointerId) {
            setAllChildVisiable();
        }

        @Override
        public void onViewPositionChanged(@NonNull View changedView, int left, int top, int dx, int dy) {
            Log.d(TAG, "onViewPositionChanged: " + left);
            onPanelDraged(left);
            invalidate();
        }

        @Override
        public void onViewReleased(@NonNull View releasedChild, float xvel, float yvel) {
            SlideLayoutParams layoutParams = (SlideLayoutParams) releasedChild.getLayoutParams();
            int left = getPaddingLeft() + layoutParams.leftMargin;
            Log.d(TAG, "onViewReleased: " + xvel + " mslideOffset " + mSlideOffset);
            if (xvel > 0 || (xvel == 0 && mSlideOffset > 0.5f)) {
                left += mSlideRange;
            }
            mDragHelper.settleCapturedViewAt(left, releasedChild.getTop());
            invalidate();
        }

        @Override
        public int getViewHorizontalDragRange(@NonNull View child) {
            return mSlideRange;
        }

        @Override
        public int clampViewPositionHorizontal(@NonNull View child, int left, int dx) {
            final SlideLayoutParams layoutParams = (SlideLayoutParams) mSlideView.getLayoutParams();

            int startBound = getPaddingLeft() + layoutParams.leftMargin;
            int newLeft = Math.min(Math.max(startBound, left), startBound + mSlideRange);
            return newLeft;
        }

        @Override
        public int clampViewPositionVertical(@NonNull View child, int top, int dy) {

            Log.d(TAG, "clampViewPositionVertical: " + child.getTop() + " " + top);
            return child.getTop();
        }

        @Override
        public void onEdgeDragStarted(int edgeFlags, int pointerId) {
            mDragHelper.captureChildView(mSlideView, pointerId);
        }
    }

    private void onPanelDraged(int left) {
        if (mSlideView == null) {
            mSlideOffset = 0;
            return;
        }
        final boolean isLayoutRtl = isLayoutRtlSupport();
        SlideLayoutParams layoutParams = (SlideLayoutParams) mSlideView.getLayoutParams();
        final int newStart = isLayoutRtl ? getPaddingRight() : getPaddingLeft();
        final int leftMargin = isLayoutRtl ? layoutParams.rightMargin : layoutParams.leftMargin;
        final int startBound = newStart + leftMargin;
        mSlideOffset = (left - startBound) * 1.0f / mSlideRange;
        Log.d(TAG, "onPanelDraged: " + mSlideOffset);
        if (mParallaxBy != 0) {
            parallaxOtherViews(mSlideOffset);
        }
        if (layoutParams.dimWhenOffset) {
            dimChildView(mSlideView, mSlideOffset, mSlideColor);
        }
    }

    private void setAllChildVisiable() {
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == INVISIBLE) {
                child.setVisibility(View.VISIBLE);
            }
        }
    }

    //addview会调用
    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new SlideLayoutParams();
    }

    @Override
    protected boolean checkLayoutParams(LayoutParams p) {
        return p instanceof SlideLayoutParams && super.checkLayoutParams(p);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new SlideLayoutParams(getContext(), attrs);
    }

    @Override
    protected LayoutParams generateLayoutParams(LayoutParams p) {
        return p instanceof MyDrawerLayout.DrawerLayoutParams ? new SlideLayoutParams((MyDrawerLayout.DrawerLayoutParams) p)
                : p instanceof MarginLayoutParams ? new SlideLayoutParams((MarginLayoutParams) p)
                : new SlideLayoutParams(p);
    }

    public static class SlideLayoutParams extends MarginLayoutParams {

        boolean slideAble;

        boolean dimWhenOffset;

        Paint dimPaint;

        public SlideLayoutParams() {
            super(MATCH_PARENT, MATCH_PARENT);
        }


        public SlideLayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            //todo
        }

        public SlideLayoutParams(int width, int height) {
            super(width, height);
        }

        public SlideLayoutParams(MarginLayoutParams source) {
            super(source);
        }

        public SlideLayoutParams(SlideLayoutParams source) {
            super(source);
            this.slideAble = source.slideAble;
        }

        public SlideLayoutParams(ViewGroup.LayoutParams lp) {
            super(lp);
        }
    }

    private class DisableLayerRunnable implements Runnable {

        View childView;

        DisableLayerRunnable(View childView) {
            this.childView = childView;
        }

        @Override
        public void run() {
            if (childView.getParent() == SlidePanlayout.this) {
                childView.setLayerType(View.LAYER_TYPE_NONE, null);
            }
            postRunable.remove(this);
        }
    }

    boolean isLayoutRtlSupport() {
        return ViewCompat.getLayoutDirection(this) == ViewCompat.LAYOUT_DIRECTION_RTL;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mFirstLayout = true;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mFirstLayout = true;
    }

    private static boolean viewIsopaque(View view) {
        if (view.isOpaque()) {
            return true;
        }
        if (Build.VERSION.SDK_INT > 18) {
            return false;
        }
        return false;
    }
}

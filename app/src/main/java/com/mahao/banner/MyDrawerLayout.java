package com.mahao.banner;

import android.content.Context;
import android.content.res.TypedArray;
import android.renderscript.ScriptIntrinsicYuvToRGB;
import android.text.InputType;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import javax.xml.transform.Source;

import androidx.annotation.LongDef;
import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.customview.widget.ViewDragHelper;

public class MyDrawerLayout extends ViewGroup {


    private static final String TAG = "MyDrawerLayout";

    public static final int NO_GRAVITY = -1;
    private boolean mFirstLayout = true;
    public static final int MIN_FLING_VELOCITY = 400;

    public static final int STATE_IDLE = ViewDragHelper.STATE_IDLE;

    public static final int STATE_DRAGGING = ViewDragHelper.STATE_DRAGGING;

    public static final int STATE_SETTLING = ViewDragHelper.STATE_SETTLING;

    ViewDragHelper leftHelper;
    ViewDragHelper rightHelper;
    ViewDragCallBack leftCallback;
    ViewDragCallBack rightCallback;


    public MyDrawerLayout(Context context) {
        this(context, null);
    }

    public MyDrawerLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyDrawerLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        final float density = getResources().getDisplayMetrics().density;
        final float minVel = MIN_FLING_VELOCITY * density;

        leftCallback = new ViewDragCallBack(Gravity.LEFT);
        leftHelper = ViewDragHelper.create(this,1.0f,leftCallback);
        leftHelper.setEdgeTrackingEnabled(ViewDragHelper.EDGE_LEFT);
        leftHelper.setMinVelocity(minVel);
        leftCallback.setDragHelper(leftHelper);

        rightCallback = new ViewDragCallBack(Gravity.RIGHT);
        rightHelper =  ViewDragHelper.create(this,1.0f,rightCallback);
        rightHelper.setEdgeTrackingEnabled(ViewDragHelper.EDGE_RIGHT);
        rightHelper.setMinVelocity(minVel);
        rightCallback.setDragHelper(rightHelper);

        setFocusableInTouchMode(true);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Log.d(TAG, "onMeasure: ");
        int sizeW = MeasureSpec.getSize(widthMeasureSpec);
        int sizeH = MeasureSpec.getSize(heightMeasureSpec);
        int modeW = MeasureSpec.getMode(widthMeasureSpec);
        int modeH = MeasureSpec.getMode(heightMeasureSpec);
        setMeasuredDimension(sizeW, sizeH);

        boolean hasLeftChild = false;
        boolean hasRightChild = false;
        for (int i = 0; i < getChildCount(); i++) {
            View childAt = getChildAt(i);
            if (childAt.getVisibility() == View.GONE) {
                continue;
            }
            DrawerLayoutParams layoutParams = (DrawerLayoutParams) childAt.getLayoutParams();
            if (isContentView(childAt)) {
                int childMeasureSpecW = sizeW - layoutParams.leftMargin - layoutParams.rightMargin - getPaddingLeft() - getPaddingRight();
                int childMeasureSpecH = sizeH - layoutParams.topMargin - layoutParams.bottomMargin - getPaddingBottom() - getPaddingTop();
                childAt.measure(MeasureSpec.makeMeasureSpec(childMeasureSpecW, MeasureSpec.EXACTLY)
                        , MeasureSpec.makeMeasureSpec(childMeasureSpecH, MeasureSpec.EXACTLY));
            } else if (isDrawChild(childAt)) {
                boolean isLeft = checkDrawerViewAbsoluteGravity(childAt, Gravity.LEFT);
                if (isLeft && hasLeftChild || !isLeft && hasRightChild) {
                    throw new RuntimeException("more than one child");
                }
                int drawerMeaspecW = getChildMeasureSpec(widthMeasureSpec, layoutParams.leftMargin + layoutParams.rightMargin,
                        layoutParams.width);
                int drawerMeaspecH = getChildMeasureSpec(heightMeasureSpec, layoutParams.topMargin + layoutParams.bottomMargin,
                        layoutParams.height);
                childAt.measure(drawerMeaspecW, drawerMeaspecH);
            }
        }


    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        Log.d(TAG, "onLayout: " + getChildCount());
        for (int i = 0; i < getChildCount(); i++) {
            View childAt = getChildAt(i);
            if (childAt.getVisibility() == View.GONE) {
                continue;
            }
            DrawerLayoutParams lp = (DrawerLayoutParams) childAt.getLayoutParams();
            if (isContentView(childAt)) {
                childAt.layout(lp.leftMargin, lp.topMargin, lp.leftMargin + childAt.getWidth(), lp.topMargin + getHeight());
            } else if (isDrawChild(childAt)) {
                int childWidth = childAt.getMeasuredWidth();
                int childHeiht = childAt.getMeasuredHeight();
                int childLeft;
                float newOffset;
                if (checkDrawerViewAbsoluteGravity(childAt, Gravity.LEFT)) {
                    childLeft = -childWidth + (int) (childWidth * lp.onscreen);
                    newOffset = (float) (childWidth + childLeft) / childWidth;
                } else {
                    childLeft = getWidth() - (int) (childWidth * lp.onscreen);
                    newOffset = (getWidth() - childLeft) / childWidth;
                }
                boolean changoffset = newOffset == lp.onscreen;
                int argv = lp.gravity & Gravity.VERTICAL_GRAVITY_MASK;
                childAt.layout(childLeft, lp.topMargin, childLeft + childWidth, lp.topMargin + childHeiht);
                if (changoffset) {
                    setDrawerOfferSet(childAt, newOffset);
                }
            }
        }
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if(isDrawChild(child)){
                Log.d(TAG, "onLayout: " + child.getLeft() + " " + child.getTop()
                        + " " + child.getRight() + " " + child.getBottom()
                + " " + ((DrawerLayoutParams)child.getLayoutParams()).gravity);
            }
        }
    }

    @Override
    public void computeScroll() {
        boolean leftDragSetting = leftHelper.continueSettling(true);
        boolean rightDragSetting = rightHelper.continueSettling(true);
        if(leftDragSetting || rightDragSetting){
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    private void setDrawerOfferSet(View childAt, float newOffset) {
        DrawerLayoutParams lp = (DrawerLayoutParams) childAt.getLayoutParams();
        if (newOffset == lp.onscreen) {
            return;
        }
        lp.onscreen = newOffset;
        //todo listener
    }


    public class DrawerLayoutParams extends MarginLayoutParams {

        private int gravity = NO_GRAVITY;
        private float onscreen;

        public DrawerLayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            TypedArray typedArray = c.obtainStyledAttributes(attrs, R.styleable.child_params);
            gravity = typedArray.getInt(0,Gravity.NO_GRAVITY);
            typedArray.recycle();
        }

        public DrawerLayoutParams(int width, int height) {
            super(width, height);
        }

        public DrawerLayoutParams(DrawerLayoutParams source) {
            super(source);
            this.gravity = source.gravity;
        }

        public DrawerLayoutParams(ViewGroup.LayoutParams layoutParams){
            super(layoutParams);
        }

        public DrawerLayoutParams(MarginLayoutParams source){
            super(source);
        }

    }

    public boolean isContentView(View child) {
        DrawerLayoutParams layoutParams = (DrawerLayoutParams) child.getLayoutParams();
        if (layoutParams.gravity == NO_GRAVITY) {
            return true;
        }
        return false;
    }

    public boolean isDrawChild(View child) {
        DrawerLayoutParams layoutParams = (DrawerLayoutParams) child.getLayoutParams();
        int gravity = layoutParams.gravity;
        if (gravity == Gravity.RIGHT || gravity == Gravity.LEFT) {
            return true;
        }
        return false;
    }

    public boolean checkDrawerViewAbsoluteGravity(View view, int gravity) {
        DrawerLayoutParams layoutParams = (DrawerLayoutParams) view.getLayoutParams();
        int absGravity = layoutParams.gravity;
        return (absGravity & gravity) == gravity;
    }

    private class ViewDragCallBack extends ViewDragHelper.Callback {

        private int mAbsGravity;
        private ViewDragHelper mDragHelper;

        private final Runnable mPeekRunnable = new Runnable() {
            @Override
            public void run() {
                peekDrawer();
            }
        };

        private void peekDrawer() {
            final int peekDistance = mDragHelper.getEdgeSize();
            final boolean isLeftChild = mAbsGravity == Gravity.LEFT;
            final View captureView;
            final int childLeft;
            if (isLeftChild) {
                captureView = findDrawerWithGravity(Gravity.LEFT);
                childLeft = (captureView != null ? -captureView.getWidth() : 0) + peekDistance;
            } else {
                captureView = findDrawerWithGravity(Gravity.RIGHT);
                childLeft = getWidth() - peekDistance;
            }
            if (captureView != null && ((isLeftChild && captureView.getLeft() < childLeft)
                    || (!isLeftChild && captureView.getLeft() > childLeft))) {
                DrawerLayoutParams layoutParams = (DrawerLayoutParams) captureView.getLayoutParams();
                mDragHelper.smoothSlideViewTo(captureView, childLeft, captureView.getTop());
                invalidate();
                closeOtherDrawer();
                cancelChildTouch();
            }
        }

        ViewDragCallBack(int gravity) {
            this.mAbsGravity = gravity;
        }

        private void setDragHelper(ViewDragHelper dragHelper) {
            mDragHelper = dragHelper;
        }

        @Override
        public boolean tryCaptureView(@NonNull View child, int pointerId) {
            return isDrawChild(child) && checkDrawerViewAbsoluteGravity(child, mAbsGravity);
        }


        @Override
        public void onViewDragStateChanged(int state) {
            updateDrawerState(mAbsGravity, state, mDragHelper.getCapturedView());
        }

        @Override
        public void onViewPositionChanged(@NonNull View changedView, int left, int top, int dx, int dy) {
            int width = changedView.getWidth();
            float offset;
            Log.d(TAG, "onViewPositionChanged: " + left);
            if (checkDrawerViewAbsoluteGravity(changedView, Gravity.LEFT)) {
                offset = (float) (width + left) / width;
            } else {
                offset = (float) (getWidth() - left) / width;
            }
            setDrawerOfferSet(changedView, offset);
            changedView.setVisibility(offset == 0 ? INVISIBLE : VISIBLE);
            invalidate();
        }

        @Override
        public void onViewCaptured(@NonNull View capturedChild, int activePointerId) {
            //todo
        }

        @Override
        public void onViewReleased(@NonNull View releasedChild, float xvel, float yvel) {
            DrawerLayoutParams layoutParams = (DrawerLayoutParams) releasedChild.getLayoutParams();
            float offset = layoutParams.onscreen;
            int width = releasedChild.getWidth();
            int left = 0;
            Log.d(TAG, "onViewReleased:  offset "  + offset);
            if (checkDrawerViewAbsoluteGravity(releasedChild, Gravity.LEFT)) {
                left = xvel > 0 || (xvel == 0 && offset > 0.5f) ? 0 : -width;
            } else {
                left = xvel < 0 || (xvel == 0 && offset > 0.5f) ? getWidth() - width : getWidth();
            }
            mDragHelper.settleCapturedViewAt(left, releasedChild.getTop());
            invalidate();
        }

        @Override
        public void onEdgeTouched(int edgeFlags, int pointerId) {
            postDelayed(mPeekRunnable,160);
        }


        @Override
        public boolean onEdgeLock(int edgeFlags) {
            View drawerWithGravity = findDrawerWithGravity(mAbsGravity);
            DrawerLayoutParams layoutParams = (DrawerLayoutParams) drawerWithGravity.getLayoutParams();
            if(drawerWithGravity != null && !(layoutParams.onscreen == 1.0f)){
                closeDrawer(drawerWithGravity);
            }
            return false;
        }

        @Override
        public void onEdgeDragStarted(int edgeFlags, int pointerId) {
            final View toCapture;
            if((edgeFlags & ViewDragHelper.EDGE_LEFT) == ViewDragHelper.EDGE_LEFT){
                toCapture = findDrawerWithGravity(Gravity.LEFT);
            }else {
                toCapture = findDrawerWithGravity(Gravity.RIGHT);
            }
            if(toCapture != null){
                mDragHelper.captureChildView(toCapture,pointerId);
            }
        }

        @Override
        public int getViewHorizontalDragRange(@NonNull View child) {
            return isDrawChild(child) ? child.getWidth() : 0;
        }


        @Override
        public int clampViewPositionHorizontal(@NonNull View child, int left, int dx) {
            if(checkDrawerViewAbsoluteGravity(child,Gravity.LEFT)){
                return Math.max(-child.getWidth(),Math.min(left,0));
            }else {
                return Math.max(getWidth() - child.getWidth(),Math.min(getWidth(),left));
            }
        }

        @Override
        public int clampViewPositionVertical(@NonNull View child, int top, int dy) {
            return child.getTop();
        }

        private void closeOtherDrawer() {
            final int otherGrav = mAbsGravity == Gravity.LEFT ? Gravity.RIGHT : Gravity.LEFT;
            View otherView = findDrawerWithGravity(otherGrav);
            if(otherView != null){
               closeDrawer(otherView);
            }
        }

        public void removeCallbakcs(){
            MyDrawerLayout.this.removeCallbacks(mPeekRunnable);
        }

    }


    public void closeDrawer(View drawerView){
        closeDrawer(drawerView,true);
    }

    public void closeDrawer(View drawerView, boolean anim) {
        if(!isDrawChild(drawerView)){
            throw new RuntimeException("is not drawer View");
        }
        DrawerLayoutParams layoutParams = (DrawerLayoutParams) drawerView.getLayoutParams();
        if(mFirstLayout){
            layoutParams.onscreen = 0f;
        }else if(anim){
            if(checkDrawerViewAbsoluteGravity(drawerView,Gravity.LEFT)){
                leftHelper.smoothSlideViewTo(drawerView,-drawerView.getWidth(),drawerView.getTop());
            }else{
                rightHelper.smoothSlideViewTo(drawerView,getWidth(),drawerView.getTop());
            }
        }else {
            moveDrawerToofferSet(drawerView,0);
            updateDrawerState(layoutParams.gravity,STATE_IDLE,drawerView);
            drawerView.setVisibility(INVISIBLE);
        }
        invalidate();
    }

    public void openDrawer(View drawerView,boolean animate){
        if(!isDrawChild(drawerView)){
            throw new RuntimeException("is not sliding view");
        }
        DrawerLayoutParams layoutParams = (DrawerLayoutParams) drawerView.getLayoutParams();
        if(mFirstLayout){
            layoutParams.onscreen = 1.0f;
            updateChildrenImportantAccessibility(drawerView,true);
        }else if(animate){
            if(checkDrawerViewAbsoluteGravity(drawerView,Gravity.LEFT)){
                leftHelper.smoothSlideViewTo(drawerView,0,drawerView.getTop());
            }else {
                rightHelper.smoothSlideViewTo(drawerView,getWidth() - drawerView.getWidth(),drawerView.getTop());
            }
        }else {
            moveDrawerToofferSet(drawerView,1.0f);
            updateDrawerState(layoutParams.gravity,STATE_IDLE,drawerView);
            drawerView.setVisibility(VISIBLE);
        }
        invalidate();
    }


    private void cancelChildTouch() {

    }

    private void updateChildrenImportantAccessibility(View drawerView,boolean isOpen){
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if((!isOpen && !isDrawChild(child)) || (isOpen && child == drawerView)){
                ViewCompat.setImportantForAccessibility(child,ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_YES);
            }else {
                ViewCompat.setImportantForAccessibility(child,ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS);
            }
        }

    }

    void moveDrawerToofferSet(View view,float slideOffset){
        DrawerLayoutParams layoutParams = (DrawerLayoutParams) view.getLayoutParams();
        final float oldOffset = layoutParams.onscreen;
        final int width = view.getWidth();
        final int oldPos = (int) (width * oldOffset);
        final int newPos = (int)(width * slideOffset);
        final int dx = newPos - oldPos;

        view.offsetLeftAndRight(checkDrawerViewAbsoluteGravity(view,Gravity.LEFT) ? dx: -dx);
        setDrawerOfferSet(view,slideOffset);
    }

    private View findDrawerWithGravity(int gravity) {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            DrawerLayoutParams layoutParams = (DrawerLayoutParams) child.getLayoutParams();
            if (layoutParams.gravity == gravity) {
                return child;
            }
        }
        return null;
    }

    private void updateDrawerState(int absGravity, int state, View capturedView) {

    }

    @Override
    protected LayoutParams generateLayoutParams(LayoutParams p) {
        return p instanceof DrawerLayoutParams ? new DrawerLayoutParams((DrawerLayoutParams) p)
                : p instanceof MarginLayoutParams ? new DrawerLayoutParams((MarginLayoutParams)p)
                :new DrawerLayoutParams(p);
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new DrawerLayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
    }

    @Override
    protected boolean checkLayoutParams(LayoutParams p) {
        return p instanceof DrawerLayoutParams && super.checkLayoutParams(p);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new DrawerLayoutParams(getContext(),attrs);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mFirstLayout = true;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mFirstLayout = true;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        //return super.onInterceptTouchEvent(ev);
         final  int action = ev.getActionMasked();
         final boolean interceptForDrag = leftHelper.shouldInterceptTouchEvent(ev)
                 | rightHelper.shouldInterceptTouchEvent(ev);
         return interceptForDrag;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        leftHelper.processTouchEvent(event);
        rightHelper.processTouchEvent(event);
        return true;
    }
}
























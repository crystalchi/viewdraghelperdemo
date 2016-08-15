package com.crystal.viewdraghelperdemo;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Point;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

/**
 * 自定义拖动view，在parent viewgroup中。
 * Created by crystalchi on 2016/8/12 0012.
 */
public class ViewDragHelperLayout extends ViewGroup{

    private static final String TAG = ViewDragHelperLayout.class.getSimpleName();

    private Point originalPoint = new Point(); //保存拖动子view的原始坐标
    private ViewDragHelper mViewDragHelper;
    private View mMainView;
    private View mDragView;
    private View mListView;
    private int currentTop;
    private Context mContext;

    private float mX;
    private float mY;

    private View mScrollableView;
    private int mScrollableViewResId;

    /**
     * Default attributes for layout
     */
    private static final int[] DEFAULT_ATTRS = new int[]{
            android.R.attr.gravity
    };

    public ViewDragHelperLayout(Context context) {
        this(context, null);
    }

    public ViewDragHelperLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ViewDragHelperLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        mViewDragHelper = ViewDragHelper.create(this, 1.0f, new ViewDragHelperCallBack());

        if (attrs != null) {
            TypedArray defAttrs = context.obtainStyledAttributes(attrs, DEFAULT_ATTRS);

            defAttrs.recycle();

            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.SlidingUpPanelLayout);

            if (ta != null) {

                mScrollableViewResId = ta.getResourceId(R.styleable.SlidingUpPanelLayout_umanoScrollableView, -1);

            }

            ta.recycle();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        final int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        final int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int reallyWidth = widthSize - getPaddingLeft() - getPaddingRight();
        int reallyHeight = heightSize - getPaddingTop() - getPaddingBottom();

        mMainView = this.getChildAt(0);
        mDragView = this.getChildAt(1);

        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++){
            final View child = getChildAt(i);
            final LayoutParams lp = (LayoutParams) child.getLayoutParams();

            int width = reallyWidth;
            int height = reallyHeight;
            if(mMainView == child){
                width = width - lp.leftMargin - lp.rightMargin;
                height = height - lp.topMargin - lp.rightMargin;
            }else if(mDragView == child){
                height = height - lp.topMargin;
            }


            int childWidthSpec;
            if(width == MarginLayoutParams.MATCH_PARENT){
                childWidthSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
            }else if(width == MarginLayoutParams.WRAP_CONTENT){
                childWidthSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.AT_MOST);
            }else{
                childWidthSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
            }

            int childHeightSpec;
            if(height == MarginLayoutParams.MATCH_PARENT){
                childHeightSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
            }else if(height == MarginLayoutParams.WRAP_CONTENT){
                childHeightSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.AT_MOST);
            }else{
                childHeightSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
            }

            child.measure(childWidthSpec, childHeightSpec);
        }

        setMeasuredDimension(widthSize, heightSize);

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int paddingLeft = getPaddingLeft();
        final int paddingTop = getPaddingTop();

        final int childCount = getChildCount();


        for (int i = 0; i < childCount; i++) {
            final View child = getChildAt(i);
            final LayoutParams lp = (LayoutParams) child.getLayoutParams();

            final int childHeight = child.getMeasuredHeight();
            int childTop = paddingTop;

            if (child == mDragView) {
                childTop = 1094;
            }


            final int childBottom = childTop + childHeight;
            final int childLeft = paddingLeft + lp.leftMargin;
            final int childRight = childLeft + child.getMeasuredWidth();

            child.layout(childLeft, childTop, childRight, childBottom);
        }


        originalPoint.set(mDragView.getLeft(), mDragView.getTop()); //保存此view的原始坐标
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        final int action = MotionEventCompat.getActionMasked(event);
       /* if(action == MotionEvent.ACTION_DOWN){

        }else if(action == MotionEvent.ACTION_MOVE){
            if(!isViewUnder(mScrollableView, (int)mX, (int)mY)){
                //点击底部上拉时，将move事件传给底部下的listview。这点很重要，需要细细理解。
                //可将此MotionEvent.ACTION_MOVE条件判断中的本代码注释测试效果就知道为什么需要传递此事件了。
                boolean flag = super.dispatchTouchEvent(event);
                return flag;
            }
        }*/
        Log.d(TAG, "dispatchTouchEvent... " + super.dispatchTouchEvent(event));
        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {

        final float x = event.getX();
        final float y = event.getY();

        final int action = MotionEventCompat.getActionMasked(event);
        final float dx = Math.abs(x - mX);
        final float dy = Math.abs(y - mY);
        final int dragTouchSlop = mViewDragHelper.getTouchSlop();
        switch (action){
            case MotionEvent.ACTION_DOWN:
                mX = x;
                mY = y;
                Log.d(TAG, "MotionEvent.ACTION_DOWN...");
                break;
            case MotionEvent.ACTION_MOVE:
                if((mX > mY && mY > dragTouchSlop) || !isViewUnder(mDragView, (int) mX, (int) mY)){ //代表横向滑动，此时取消ViewDragHelper
                    mViewDragHelper.cancel(); //取消
                    Log.d(TAG, "MotionEvent.ACTION_MOVE");
                    return false;
                }
                break;
        }

        boolean interceptFlag = mViewDragHelper.shouldInterceptTouchEvent(event);
        Log.d(TAG, "onInterceptTouchEvent... " + interceptFlag);
        return mViewDragHelper.shouldInterceptTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d(TAG, "onTouchEvent... ");
        /*mViewDragHelper.processTouchEvent(event);
        return true;*/
        if (!isEnabled()) {
            return super.onTouchEvent(event);
        }
        try {
            mViewDragHelper.processTouchEvent(event);
            return true;
        } catch (Exception ex) {
            // Ignore the pointer out of range exception
            return false;
        }
    }

    private class ViewDragHelperCallBack extends ViewDragHelper.Callback{

        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            boolean flag =  mDragView == child;
            Log.d(TAG, "flag is " + flag);
            return flag; //只捕获第一个子view
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            int leftBound = getPaddingLeft();
            int rightBound = getWidth() - child.getWidth() - getPaddingRight();
            if(left < leftBound){
                left = leftBound;
            }else if(left > rightBound){
                left = rightBound;
            }
            Log.d(TAG, "left is " + left);
            return left;
        }

        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            /*int topBound = getPaddingTop();
            int bottomBound = getHeight() - child.getHeight() - getPaddingBottom();
            if(top < topBound){
                top = topBound;
            }else if(top > bottomBound){
                top = bottomBound;
            }
            Log.d(TAG, "top is " + top);
            currentTop = top;
            return top;*/

            final int collapsedTop = 1094;
            final int expandedTop = 0;
            if (true) {
                top = Math.min(Math.max(top, expandedTop), collapsedTop);
            } else {
                top = Math.min(Math.max(top, collapsedTop), expandedTop);
            }
            currentTop = top;
            Log.d(TAG, "top is " + top);
            return top;
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);
            if(mDragView == releasedChild){
                Log.d(TAG, "onViewReleased xvel, yvel is" + xvel + ", " + yvel);
                int reallyTop = currentTop;
                if(reallyTop < getHeight() / 2){
                    mViewDragHelper.settleCapturedViewAt(originalPoint.x, currentTop);
                    invalidate();
                }else{
                    mViewDragHelper.settleCapturedViewAt(originalPoint.x, currentTop);
                    invalidate();
                }
            }
        }
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if(mViewDragHelper.continueSettling(true)){
            Log.d(TAG, "computeScroll...");
            invalidate();
        }
    }

 /*   @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(mContext, attrs);
    }*/

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mMainView = getChildAt(0);
        mDragView = getChildAt(1);
        /*mListView = findViewById(R.id.list);*/
        if (mScrollableViewResId != -1) {
            setScrollableView(findViewById(mScrollableViewResId));
        }
    }

    /**
     * Set the scrollable child of the sliding layout. If set, scrolling will be transfered between
     * the panel and the view when necessary
     *
     * @param scrollableView The scrollable view
     */
    public void setScrollableView(View scrollableView) {
        mScrollableView = scrollableView;
    }

    @Override
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams();
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof MarginLayoutParams
                ? new LayoutParams((MarginLayoutParams) p)
                : new LayoutParams(p);
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams && super.checkLayoutParams(p);
    }

    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    public static class LayoutParams extends MarginLayoutParams {
        private static final int[] ATTRS = new int[]{
                android.R.attr.layout_weight
        };

        public float weight = 0;

        public LayoutParams() {
            super(MATCH_PARENT, MATCH_PARENT);
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(int width, int height, float weight) {
            super(width, height);
            this.weight = weight;
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }

        public LayoutParams(MarginLayoutParams source) {
            super(source);
        }

        public LayoutParams(LayoutParams source) {
            super(source);
        }

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);

            final TypedArray ta = c.obtainStyledAttributes(attrs, ATTRS);
            if (ta != null) {
                this.weight = ta.getFloat(0, 0);
            }

            ta.recycle();
        }
    }

    private boolean isViewUnder(View view, int x, int y) {
        if (view == null) return false;
        int[] viewLocation = new int[2];
        view.getLocationOnScreen(viewLocation);
        int[] parentLocation = new int[2];
        this.getLocationOnScreen(parentLocation);
        int screenX = parentLocation[0] + x;
        int screenY = parentLocation[1] + y;
        return screenX >= viewLocation[0] && screenX < viewLocation[0] + view.getWidth() &&
                screenY >= viewLocation[1] && screenY < viewLocation[1] + view.getHeight();
    }


    /*private ViewDragHelper mDragger;

    private View mDragView;
    private View mAutoBackView;
    private View mEdgeTrackerView;

    private Point mAutoBackOriginPos = new Point();

    public ViewDragHelperLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        mDragger = ViewDragHelper.create(this, 1.0f, new ViewDragHelper.Callback()
        {
            @Override
            public boolean tryCaptureView(View child, int pointerId)
            {
                //mEdgeTrackerView禁止直接移动
                return child == mDragView || child == mAutoBackView;
            }

            @Override
            public int clampViewPositionHorizontal(View child, int left, int dx)
            {
                return left;
            }

            @Override
            public int clampViewPositionVertical(View child, int top, int dy)
            {
                return top;
            }


            //手指释放的时候回调
            @Override
            public void onViewReleased(View releasedChild, float xvel, float yvel)
            {
                //mAutoBackView手指释放时可以自动回去
                if (releasedChild == mAutoBackView)
                {
                    mDragger.settleCapturedViewAt(mAutoBackOriginPos.x, mAutoBackOriginPos.y);
                    invalidate();
                }
            }

            //在边界拖动时回调
            @Override
            public void onEdgeDragStarted(int edgeFlags, int pointerId)
            {
                mDragger.captureChildView(mEdgeTrackerView, pointerId);
            }

            @Override
            public int getViewHorizontalDragRange(View child)
            {
                return getMeasuredWidth()-child.getMeasuredWidth();
            }

            @Override
            public int getViewVerticalDragRange(View child)
            {
                return getMeasuredHeight()-child.getMeasuredHeight();
            }
        });
        mDragger.setEdgeTrackingEnabled(ViewDragHelper.EDGE_LEFT);
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent event)
    {
        return mDragger.shouldInterceptTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        mDragger.processTouchEvent(event);
        return true;
    }

    @Override
    public void computeScroll()
    {
        if(mDragger.continueSettling(true))
        {
            invalidate();
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b)
    {
        super.onLayout(changed, l, t, r, b);

        mAutoBackOriginPos.x = mAutoBackView.getLeft();
        mAutoBackOriginPos.y = mAutoBackView.getTop();
    }

    @Override
    protected void onFinishInflate()
    {
        super.onFinishInflate();

        mDragView = getChildAt(0);
        mAutoBackView = getChildAt(1);
        mEdgeTrackerView = getChildAt(2);
    }*/
}

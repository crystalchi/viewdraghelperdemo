package com.crystal.viewdraghelperdemo;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Administrator on 2016/8/15 0015.
 */
public class YoutubeLayout extends ViewGroup{

    private static final String TAG = YoutubeLayout.class.getSimpleName();

    private final ViewDragHelper mDragHelper;

    private View mListView;
    private View mHeaderView;
    private View mDescView;

    private float mInitialMotionX;
    private float mInitialMotionY;

    private int mDragRange;
    private int mTop;
    private float mDragOffset;

    private boolean isDrag = false;


    public YoutubeLayout(Context context) {
        this(context, null);
    }

    public YoutubeLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public YoutubeLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mDragHelper = ViewDragHelper.create(this, 1f, new DragHelperCallback());
        isDrag = false;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mListView = findViewById(R.id.listView);
        mHeaderView = findViewById(R.id.header);
        mDescView = findViewById(R.id.lv);
    }

    public void maximize() {
        smoothSlideTo(0f);
    }

    public void minimize() {
        smoothSlideTo(1f);
    }

    boolean smoothSlideTo(float slideOffset) {
        final int topBound = getPaddingTop();
        int y = (int) (topBound + slideOffset * mDragRange);

        if (mDragHelper.smoothSlideViewTo(mHeaderView, mHeaderView.getLeft(), y)) {
            ViewCompat.postInvalidateOnAnimation(this);
            return true;
        }
        return false;
    }

    private class DragHelperCallback extends ViewDragHelper.Callback {

        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            if(child == mHeaderView){
                isDrag = true;
                return isDrag;
            }
            return false;
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            mTop = top;
            mDragOffset = (float) top / mDragRange;
            /*Log.d(TAG, "mDragRange ， mTop ，mDragOffset" + mDragRange + " , " + mTop + " , " + mDragOffset);*/

           /* mHeaderView.setPivotX(mHeaderView.getWidth());
            mHeaderView.setPivotY(mHeaderView.getHeight());
            mHeaderView.setScaleX(1 - mDragOffset / 2);
            mHeaderView.setScaleY(1 - mDragOffset / 2);*/

            //mDescView.setAlpha(1 - mDragOffset);

            requestLayout();
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            int top = getPaddingTop();
            Log.d(TAG, "yvel ， mTmDragOffset  is " + yvel + " , " + mDragOffset);
            /*if (yvel > 0 || (yvel == 0 && mDragOffset > 0.5f)) {
                top += mDragRange;
            }*/
            if(mDragOffset > 0.5f && mDragOffset < 0.8f){
                top = getHeight() / 2;
            }
            mDragHelper.settleCapturedViewAt(releasedChild.getLeft(), top);
            invalidate();
        }

        /**
         * 设置滑动面板在垂直方向上的滑动范围值
         * @param child
         * @return
         */
        @Override
        public int getViewVerticalDragRange(View child) {
            return mDragRange;
        }

        /**
         * 限制滑动面板在垂直方向上的滑动范围
         * @param child
         * @param top
         * @param dy
         * @return
         */
        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            /*final int topBound = getPaddingTop();
            final int bottomBound = getHeight() - mHeaderView.getHeight() - mHeaderView.getPaddingBottom();

            final int newTop = Math.min(Math.max(top, topBound), bottomBound);
            return newTop;*/

            final int topBound = getPaddingTop();
            final int bottomBound = getHeight() - mHeaderView.getHeight();
            if(top < topBound){
                top = topBound;
            }
            if(top > bottomBound){
                top = bottomBound;
            }
            return top;
        }

    }

    @Override
    public void computeScroll() {
        if (mDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        final int action = MotionEventCompat.getActionMasked(ev);

        if (( action != MotionEvent.ACTION_DOWN)) {
            mDragHelper.cancel();
            return super.onInterceptTouchEvent(ev);
        }

        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
            mDragHelper.cancel();
            return false;
        }

        final float x = ev.getX();
        final float y = ev.getY();
        boolean interceptTap = false;

        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                mInitialMotionX = x;
                mInitialMotionY = y;
                interceptTap = mDragHelper.isViewUnder(mHeaderView, (int) x, (int) y);
                break;
            }

            case MotionEvent.ACTION_MOVE: {
                final float adx = Math.abs(x - mInitialMotionX);
                final float ady = Math.abs(y - mInitialMotionY);
                final int slop = mDragHelper.getTouchSlop();
                /*useless*/
                if (ady > slop && adx > ady) {
                    mDragHelper.cancel();
                    return false;
                }
            }
        }

        return mDragHelper.shouldInterceptTouchEvent(ev) || interceptTap;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        mDragHelper.processTouchEvent(ev);

        final int action = ev.getAction();
        final float x = ev.getX();
        final float y = ev.getY();

        boolean isHeaderViewUnder = mDragHelper.isViewUnder(mHeaderView, (int) x, (int) y);
        switch (action & MotionEventCompat.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN: {
                mInitialMotionX = x;
                mInitialMotionY = y;
                break;
            }

            case MotionEvent.ACTION_UP: {
                final float dx = x - mInitialMotionX;
                final float dy = y - mInitialMotionY;
                /*final int slop = mDragHelper.getTouchSlop();
                if (dx * dx + dy * dy < slop * slop && isHeaderViewUnder) {
                    if (mDragOffset == 0) {
                        smoothSlideTo(1f);
                    } else {
                        smoothSlideTo(0f);
                    }
                }*/
                break;
            }
        }

        return isHeaderViewUnder && isViewHit(mHeaderView, (int) x, (int) y) || isViewHit(mDescView, (int) x, (int) y);
    }


    private boolean isViewHit(View view, int x, int y) {
        int[] viewLocation = new int[2];
        view.getLocationOnScreen(viewLocation);
        int[] parentLocation = new int[2];
        this.getLocationOnScreen(parentLocation);
        int screenX = parentLocation[0] + x;
        int screenY = parentLocation[1] + y;
        return screenX >= viewLocation[0] && screenX < viewLocation[0] + view.getWidth() &&
                screenY >= viewLocation[1] && screenY < viewLocation[1] + view.getHeight();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        measureChildren(widthMeasureSpec, heightMeasureSpec);

        int maxWidth = MeasureSpec.getSize(widthMeasureSpec);
        int maxHeight = MeasureSpec.getSize(heightMeasureSpec);

        setMeasuredDimension(resolveSizeAndState(maxWidth, widthMeasureSpec, 0),
                resolveSizeAndState(maxHeight, heightMeasureSpec, 0));
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        mDragRange = getHeight() - mHeaderView.getHeight();

        if(mTop == 0 && !isDrag){
            mTop = getHeight() - mHeaderView.getMeasuredHeight();
        }

        mListView.layout(
                0,
                0,
                r,
                getHeight() - mHeaderView.getMeasuredHeight());

        mHeaderView.layout(
                0,
                mTop,
                r,
                mTop + mHeaderView.getMeasuredHeight());

        mDescView.layout(
                0,
                mTop + mHeaderView.getMeasuredHeight(),
                r,
                mTop  + b);

//        Log.d(TAG, "mTop, b is " +  mTop + ", " + b);
    }
}

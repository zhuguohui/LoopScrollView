package com.example.myapplication;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.HorizontalScrollView;
import android.widget.OverScroller;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class LoopScrollView extends HorizontalScrollView {

    private LoopScroller loopScroller;
    private ValueAnimator animator;

    public LoopScrollView(Context context) {
        this(context, null);
    }

    public LoopScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOverScrollMode(OVER_SCROLL_ALWAYS);
        try {
            @SuppressLint("DiscouragedPrivateApi")
            Field field =HorizontalScrollView.class.getDeclaredField("mScroller");
            field.setAccessible(true);
            loopScroller = new LoopScroller(getContext());
            field.set(this, loopScroller);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if(changed||animator==null){
            buildAnimation();
        }
    }

    private void buildAnimation() {
        if(animator!=null){
            animator.cancel();
            animator=null;
        }
        animator = ValueAnimator.ofInt(getWidth() - getPaddingRight() - getPaddingLeft());
        animator.setDuration(5*1000);
        animator.setRepeatCount(-1);
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            int lastValue;
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value= (int) animation.getAnimatedValue();
                int scrollByX=value-lastValue;
          //      Log.i("zzz","scroll by x="+scrollByX);
                scrollByX=Math.max(0,scrollByX);
                if(userUp) {
                    scrollBy(scrollByX, 0);
                }
                lastValue=value;
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
            }

        });
        animator.start();
    }

    static   class LoopScroller extends OverScroller{
        public LoopScroller(Context context) {
            super(context);
        }

      @Override
      public void fling(int startX, int startY, int velocityX, int velocityY, int minX, int maxX, int minY, int maxY, int overX, int overY) {
          super.fling(startX, startY, velocityX, velocityY, Integer.MIN_VALUE,Integer.MAX_VALUE, minY, maxY, 0, overY);
      }
  }




    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if(animator!=null){
            animator.cancel();
            animator.removeAllListeners();
            animator = null;
        }
    }

    @Override
    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
        if (userUp) {
            //scroller再滚动
            scrollX=loopScroller.getCurrX();
            int detailX = scrollX - lastScrollX;
            lastScrollX = scrollX;
            if(detailX==0){
                return;
            }
            scrollX = detailX + getScrollX();

        }
        int moveTo = moveItem(scrollX,clampedX);

        super.onOverScrolled(moveTo, scrollY, false, clampedY);
    }

    boolean userUp = true;
    int lastScrollX = 0;

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_UP) {
            userUp = true;
            lastScrollX = getScrollX();
        } else {
            userUp = false;
        }
        return super.onTouchEvent(ev);
    }
    @Override
    public void scrollTo(int x, int y) {
        int scrollTo = moveItem(x, false);
        super.scrollTo(scrollTo, y);
    }


    private int moveItem(int scrollX, boolean clampedX) {

        int toScrollX = scrollX;

        if (getChildCount() > 0) {
            if (!canScroll(scrollX,clampedX)) {
                boolean toLeft=scrollX<=0;
                int mWidth=getWidth()-getPaddingLeft()-getPaddingRight();
                //无法向右滚动了，将屏幕外的item，移动到后面
                List<View> needRemoveViewList = new ArrayList<>();
                LoopLinearLayout group = (LoopLinearLayout) getChildAt(0);
                int removeItemsWidth = 0;
                boolean needRemove = false;
                for (int i = group.getChildCount() - 1; i >= 0; i--) {
                    View itemView = group.getChildAt(i);
                    MarginLayoutParams params = (MarginLayoutParams) itemView.getLayoutParams();
                    if(toLeft){
                        int itemLeft = itemView.getLeft() - params.leftMargin;
                        if (itemLeft >= mWidth) {
                            //表示之后的控件都需要移除
                            needRemove = true;
                        }
                    }else{
                        int itemRight = itemView.getRight() + params.rightMargin;
                        if (itemRight <= scrollX) {
                            //表示之后的控件都需要移除
                            needRemove = true;
                        }
                    }

                    if (needRemove) {
                        int itemWidth = itemView.getWidth() + params.rightMargin + params.leftMargin;
                        removeItemsWidth += itemWidth;
                        needRemoveViewList.add(0,itemView);
                    }
                    needRemove=false;
                }
                if(!toLeft){
                    group.changeItemsToRight(needRemoveViewList,removeItemsWidth);
                    toScrollX -=removeItemsWidth;
                }else{
                    group.changeItemsToLeft(needRemoveViewList,removeItemsWidth);
                    toScrollX +=removeItemsWidth;
                }

            }

        }
        return Math.max(0, toScrollX);
    }

    private boolean canScroll(int scrollX, boolean clampedX) {
        if(scrollX<0){
            return false;
        }
        if(scrollX==0&&clampedX){
            //表示向左划不动了
            return  false;
        }
        View child = getChildAt(0);
        if (child != null) {
            int childWidth = child.getWidth();
            return getWidth() + scrollX < childWidth + getPaddingLeft() + getPaddingRight();
        }
        return false;
    }
}

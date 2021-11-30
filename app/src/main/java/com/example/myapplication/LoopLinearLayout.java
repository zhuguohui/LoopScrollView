package com.example.myapplication;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import java.util.List;

/**
 * Created by zhuguohui
 * Date: 2021/11/30
 * Time: 10:46
 * Desc:
 */
public class LoopLinearLayout extends LinearLayout {
    public LoopLinearLayout(Context context) {
        this(context, null);
    }

    public LoopLinearLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void detachViewFromParent(View child) {
        super.detachViewFromParent(child);
    }

    @Override
    public void attachViewToParent(View child, int index, ViewGroup.LayoutParams params) {
        super.attachViewToParent(child, index, params);
    }



    public void changeItemsToRight(List<View> moveItems, int offset) {

        int offset2 = 0;
        for (int i = 0; i < getChildCount(); i++) {
            View childAt = getChildAt(i);
            if (!moveItems.contains(childAt)) {
                MarginLayoutParams layoutParams = (MarginLayoutParams) childAt.getLayoutParams();
                offset2 += childAt.getWidth() + layoutParams.leftMargin + layoutParams.rightMargin;
                childAt.layout(childAt.getLeft() - offset, childAt.getTop(), childAt.getRight() - offset, childAt.getBottom());
            }
        }
        for(View view:moveItems){
            view.layout(view.getLeft()+offset2,view.getTop(),view.getRight()+offset2,view.getBottom());
        }
    }
    public void changeItemsToLeft(List<View> moveItems, int offset) {

        int offset2 = 0;
        for (int i = 0; i < getChildCount(); i++) {
            View childAt = getChildAt(i);
            if (!moveItems.contains(childAt)) {
                MarginLayoutParams layoutParams = (MarginLayoutParams) childAt.getLayoutParams();
                offset2 += childAt.getWidth() + layoutParams.leftMargin + layoutParams.rightMargin;
                childAt.layout(childAt.getLeft() + offset, childAt.getTop(), childAt.getRight() + offset, childAt.getBottom());
            }
        }
        for(View view:moveItems){
            view.layout(view.getLeft()-offset2,view.getTop(),view.getRight()-offset2,view.getBottom());
        }
    }


}

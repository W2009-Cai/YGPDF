package com.yg.pdf.weight;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

public class NoScrollViewPager extends ViewPager {
    // false 禁止ViewPager左右滑动。
    // true 普通ViewPager
    private boolean isScroll = false;

    public NoScrollViewPager(@NonNull Context context) {
        this(context, null);
    }
    public NoScrollViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }
    public void setScrollable(boolean isScroll) {
        this.isScroll = isScroll;
    }
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (!isScroll){
            // 不允许拦截事件就返回false
            return isScroll;
        }else {
            // 正常ViewPager处理拦截事件就请求父类普通ViewPager中的onInterceptTouchEvent()
            return super.onInterceptTouchEvent(ev);
        }
    }
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (!isScroll){
            // 不允许消费事件就返回false.
            return isScroll;
        }else {
            // 正常ViewPager消耗事件就请求父类普通ViewPager中的onTouchEvent.
            return super.onTouchEvent(ev);
        }
    }
}
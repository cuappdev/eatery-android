package com.cornellappdev.android.eatery.components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;

public class ControlledScrollView extends NestedScrollView {
    private boolean scrollable;

    public ControlledScrollView(@NonNull Context context) {
        super(context);
        scrollable = true;
    }

    public ControlledScrollView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        scrollable = true;
    }

    public ControlledScrollView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        scrollable = true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return scrollable && super.onTouchEvent(ev);
    }

    @Override
    public boolean performClick() {
        return scrollable && super.performClick();
    }

    public void setScrollable(boolean b) {
        this.scrollable = b;
    }
}
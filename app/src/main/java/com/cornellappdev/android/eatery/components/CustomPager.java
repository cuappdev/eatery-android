package com.cornellappdev.android.eatery.components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.viewpager.widget.ViewPager;

public class CustomPager extends ViewPager {
    private View mCurrentView;

    public CustomPager(Context context) {
        super(context);
    }

    public CustomPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Note(lesley): Code is either from StackOverflow or official docs, allows for ViewPager to be
     * visible inside of MenuActivity. TBH not sure how it works, but it has something to do with
     * resizing the Viewpager window
     * https://stackoverflow.com/questions/8394681/android-i-am-unable-to-have-viewpager-wrap-content/18167273
     */
    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mCurrentView == null) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }
        int height = 0;
        mCurrentView.measure(widthMeasureSpec,
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
        int h = mCurrentView.getMeasuredHeight();
        if (h > height) height = h;
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void measureCurrentView(View currentView) {
        mCurrentView = currentView;
        requestLayout();
    }
}

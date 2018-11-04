package com.cornellappdev.android.eatery;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

/**
 * from https://stackoverflow.com/questions/37605545/
 * android-nestedscrollview-which-contains-expandablelistview-doesnt-scroll-when*
 */
public class NonScrollExpandableListView extends ExpandableListView {

  public NonScrollExpandableListView(Context context) {
    super(context);
  }

  public NonScrollExpandableListView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public NonScrollExpandableListView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
  }

  @Override
  public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    int heightMeasureSpec_custom =
        MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
    super.onMeasure(widthMeasureSpec, heightMeasureSpec_custom);
    ViewGroup.LayoutParams params = getLayoutParams();
    params.height = getMeasuredHeight();
  }
}

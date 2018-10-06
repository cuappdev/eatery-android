package com.cornellappdev.android.eatery.component;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.chip.ChipGroup.OnCheckedChangeListener;

public class HideUnselectedChipGroup extends ChipGroup implements OnCheckedChangeListener {
  private OnCheckedChangeListener mListener;

  public HideUnselectedChipGroup(Context context) {
    super(context);
  }

  public HideUnselectedChipGroup(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public HideUnselectedChipGroup(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  @Override
  public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
    super.setOnCheckedChangeListener(this);
    mListener = listener;
  }

  @Override
  public void onCheckedChanged(ChipGroup chipGroup, int id) {
    if (mListener != null) {
      mListener.onCheckedChanged(chipGroup, id);
    }

    for (int i = 0; i < getChildCount(); i++) {
      View child = getChildAt(i);

      int checkedId = getCheckedChipId();
      if (checkedId != View.NO_ID && checkedId != child.getId()) {
        child.setVisibility(GONE);
      } else {
        child.setVisibility(VISIBLE);
      }
    }
  }
}

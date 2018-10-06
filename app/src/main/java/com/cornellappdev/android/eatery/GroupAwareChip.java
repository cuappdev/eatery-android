package com.cornellappdev.android.eatery;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup.OnHierarchyChangeListener;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipDrawable;
import com.google.android.material.chip.ChipGroup;

public class GroupAwareChip extends Chip {
  private ChipGroup mChipGroup;
  private float mUnmodifiedChipCornerRadius;

  public GroupAwareChip(Context context) {
    super(context);
  }

  public GroupAwareChip(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public GroupAwareChip(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  public void setGroup(ChipGroup group) {
    mChipGroup = group;
    modify();

    mChipGroup.setOnHierarchyChangeListener(new OnHierarchyChangeListener() {
      @Override
      public void onChildViewAdded(View parent, View child) {
        if (child instanceof GroupAwareChip) {
          ((GroupAwareChip) child).modify();
        }
      }

      @Override
      public void onChildViewRemoved(View parent, View child) {
        if (child instanceof GroupAwareChip) {
          ((GroupAwareChip) child).modify();
        }
      }
    });
  }

  private void modify() {
   /* if (mChipGroup != null) {
      int index = mChipGroup.indexOfChild(this);
      ChipDrawable drawable = (ChipDrawable) getChipDrawable();
      if (index != -1) {
        mUnmodifiedChipCornerRadius = drawable.getChipCornerRadius();
        drawable.setChipCornerRadius(0);
      } else {
        drawable.setChipCornerRadius(mUnmodifiedChipCornerRadius);
      }

      mChipGroup.setBackgroundResource(R.drawable.left_round_outline);
      mChipGroup.setClipToOutline(true);
    }*/
  }
}

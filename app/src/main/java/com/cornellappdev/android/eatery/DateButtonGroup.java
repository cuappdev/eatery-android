package com.cornellappdev.android.eatery;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.OnHierarchyChangeListener;
import android.widget.LinearLayout;
import androidx.annotation.IdRes;
import androidx.annotation.Nullable;
import com.cornellappdev.android.eatery.DateButton.OnCheckChangedListener;

public class DateButtonGroup extends LinearLayout implements OnHierarchyChangeListener,
    OnCheckChangedListener {

  @IdRes
  private int checkedViewId = View.NO_ID;
  @Nullable
  private OnCheckedChangeListener mOnCheckedChangeListener;
  @Nullable
  private OnHierarchyChangeListener mOnHierarchyChangeListener;
  private boolean preventChanges = false;

  public DateButtonGroup(Context context) {
    super(context);
  }

  public DateButtonGroup(Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
  }

  public DateButtonGroup(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  public DateButtonGroup(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
  }

  @Override
  public void setOnHierarchyChangeListener(OnHierarchyChangeListener listener) {
    mOnHierarchyChangeListener = listener;
  }

  @IdRes
  public int getCheckedDateButtonId() {
    return checkedViewId;
  }

  public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
    mOnCheckedChangeListener = listener;
  }

  @Override
  public void addView(View child, int index, ViewGroup.LayoutParams params) {
    if (child instanceof DateButton) {
      final DateButton dateButton = (DateButton) child;
      if (checkedViewId == View.NO_ID) {
        dateButton.setChecked(true);
      }
      if (dateButton.isChecked()) {
        setCurrentCheckedViewId(dateButton.getId());
      }
      dateButton.setOnCheckChangedListener(this);
    }
    super.addView(child, index, params);
  }

  public void check(@IdRes int id) {
    if (id == checkedViewId) {
      return;
    }
    if (checkedViewId != View.NO_ID) {
      setChecked(checkedViewId, false);
    }
    if (id != View.NO_ID) {
      setChecked(id, true);
    }
    setCurrentCheckedViewId(id);
  }

  private void setChecked(@IdRes int viewId, boolean checked) {
    View checkedView = findViewById(viewId);
    if (checkedView instanceof DateButton) {
      preventChanges = true;
      ((DateButton) checkedView).setChecked(checked);
      preventChanges = false;
    }
  }

  private void setCurrentCheckedViewId(@IdRes int viewId) {
    this.checkedViewId = viewId;
    if (mOnCheckedChangeListener != null) {
      mOnCheckedChangeListener.onCheckedChanged(this, viewId);
    }
  }

  @Override
  public void onChildViewAdded(View parent, View child) {
    if (child instanceof DateButton && parent == DateButtonGroup.this) {
      int id = child.getId();
      if (id == View.NO_ID) {
        child.setId(View.generateViewId());
      }
      ((DateButton) child).setOnCheckChangedListener(this);
    }

    if (mOnHierarchyChangeListener != null) {
      this.onChildViewAdded(parent, child);
    }
  }

  @Override
  public void onChildViewRemoved(View parent, View child) {
    if (child instanceof DateButton && parent == DateButtonGroup.this) {
      ((DateButton) child).setOnCheckChangedListener(null);
    }

    if (mOnHierarchyChangeListener != null) {
      mOnHierarchyChangeListener.onChildViewRemoved(parent, child);
    }
  }

  @Override
  public void onCheckChanged(DateButton dateButton, boolean isChecked) {
    if (preventChanges) {
      return;
    }
    int id = dateButton.getId();
    if (isChecked) {
      if (checkedViewId != View.NO_ID && checkedViewId != id) {
        setChecked(checkedViewId, false);
      }
      setCurrentCheckedViewId(id);
    } else if (checkedViewId == id) {
      setChecked(id, true);
    }
  }

  public interface OnCheckedChangeListener {

    void onCheckedChanged(DateButtonGroup group, @IdRes int checkedId);
  }

}

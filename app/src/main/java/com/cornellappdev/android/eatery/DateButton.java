package com.cornellappdev.android.eatery;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Checkable;
import android.widget.CheckedTextView;
import android.widget.LinearLayout;
import androidx.annotation.Nullable;

public class DateButton extends LinearLayout implements Checkable {

  private static final int[] CheckedStateSet = {
      android.R.attr.state_checked
  };
  private CheckedTextView dayTextView, dateTextView;
  private OnCheckChangedListener mCheckChangedListener;
  private boolean mChecked;
  private OnClickListener mListener;

  public DateButton(Context context) {
    super(context);
    init(context);
  }

  public DateButton(Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
    init(context);
  }

  public DateButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init(context);
  }

  public DateButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
    init(context);
  }

  private void init(Context context) {
    View v = View.inflate(context, R.layout.button_date, this);
    this.dayTextView = v.findViewById(R.id.dayText);
    this.dateTextView = v.findViewById(R.id.dateText);

    this.dayTextView.setOnClickListener(view -> {
      toggle();
      if (mListener != null) {
        mListener.onClick(this);
      }
    });
    this.dateTextView.setOnClickListener(view -> {
      toggle();
      if (mListener != null) {
        mListener.onClick(this);
      }
    });
    super.setOnClickListener(view -> {
      toggle();
      if (mListener != null) {
        mListener.onClick(view);
      }
    });

  }

  public void setDateText(String dayText, String dateText) {
    this.dayTextView.setText(dayText);
    this.dateTextView.setText(dateText);
  }

  @Override
  public boolean isChecked() {
    return mChecked;
  }

  @Override
  public void setChecked(boolean checked) {
    boolean oldState = mChecked;
    mChecked = checked;
    dayTextView.setChecked(mChecked);
    dateTextView.setChecked(mChecked);
    if (oldState != mChecked && mCheckChangedListener != null) {
      mCheckChangedListener.onCheckChanged(this, mChecked);
    }
  }

  @Override
  public void toggle() {
    setChecked(!mChecked);
  }

  @Override
  public void onFinishInflate() {
    super.onFinishInflate();
  }

  @Override
  public void setOnClickListener(OnClickListener listener) {
    mListener = listener;
  }

  @Override
  protected int[] onCreateDrawableState(int extraSpace) {
    final int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
    if (isChecked()) {
      mergeDrawableStates(drawableState, CheckedStateSet);
    }
    return drawableState;
  }

  public void setOnCheckChangedListener(OnCheckChangedListener checkChangedListener) {
    mCheckChangedListener = checkChangedListener;
  }

  public interface OnCheckChangedListener {
    void onCheckChanged(DateButton b, boolean isChecked);
  }
}

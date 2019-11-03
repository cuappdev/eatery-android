package com.cornellappdev.android.eatery.components;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cornellappdev.android.eatery.R;
import com.cornellappdev.android.eatery.model.Swipe;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;

public class WaitTimesComponent {
    private WaitTimesChart mWaitTimesChart;
    private TextView mWaitTimesButton;
    private WaitTimesMarkerView mWaitTimesMarkerView;
    private View mWaitTimesXAxisLine;
    private LinearLayout mWaitTimesXAxisTicks;
    private LinearLayout mWaitTimesXAxisLabels;

    /**
     * mSwipeData -
     * Should have size of 21 upon construction within CampusMenuActivity.
     * Swipe s: mSwipeData -
     * Hour determined by index, with 0 representing start = 6am, end = 7am.
     * start, end = null.
     * swipeDensity, waitTimeLow, waitTimeHigh are the maximums collected from backend data.
     */
    private List<Swipe> mSwipeData;
    private Entry mLastEntry;
    private boolean mShowWaitTimes;
    private float mLastY;
    private float mLastX;
    private boolean mVerticalScrolling;
    private boolean mFingerJustTapped;

    public WaitTimesComponent(List<Swipe> swipeData) {
        mSwipeData = swipeData;
        mLastY = 0.0f;
        mLastX = 0.0f;
        mVerticalScrolling = false;
        mFingerJustTapped = false;
    }

    // Inflates the wait times component into the passed in holder
    public void inflateView(Context context, FrameLayout holder, NestedScrollView scrollView) {

        View view = View.inflate(context, R.layout.wait_times, holder);

        mShowWaitTimes = true;
        mLastEntry = null;

        mWaitTimesButton = view.findViewById(R.id.wait_time_show_button);
        mWaitTimesButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mShowWaitTimes = !mShowWaitTimes;
                toggleShowWaitTimesChart();
            }
        });
        mWaitTimesChart = view.findViewById(R.id.wait_time_chart);
        mWaitTimesChart.setNoDataText("");

        mWaitTimesChart.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mFingerJustTapped) {
                    // Measure the yDiff directly after the tap, high y diff means motion was a
                    // vertical swipe
                    float yDiff = Math.abs(event.getY() - mLastY);
                    float xDiff = Math.abs(event.getX() - mLastX);
                    // If it was not a vertical swipe
                    if (xDiff > yDiff) {
                        scrollView.requestDisallowInterceptTouchEvent(true);
                        mVerticalScrolling = false;
                    } else {
                        // This variable (when true) is used below in onValueSelected
                        // to prevent interaction with the mWaitTimesChart. I couldn't find
                        // a cleaner way of disabling interaction with the chart
                        mVerticalScrolling = true;
                    }
                    mFingerJustTapped = false;
                }

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    mFingerJustTapped = true;
                    mLastY = event.getY();
                    mLastX = event.getX();
                    v.performClick();
                } else if (event.getAction() == MotionEvent.ACTION_UP
                        || event.getAction() == MotionEvent.ACTION_CANCEL) {
                    mFingerJustTapped = false;
                    mVerticalScrolling = false;
                }
                return false;
            }
        });

        mWaitTimesXAxisLine = view.findViewById(R.id.wait_time_x_axis_line);
        mWaitTimesXAxisTicks = view.findViewById(R.id.wait_time_x_axis_ticks);
        mWaitTimesXAxisLabels = view.findViewById(R.id.wait_time_x_axis_labels);

        if (mSwipeData.size() == 0) {
            mShowWaitTimes = false;
            toggleShowWaitTimesChart();
        } else {
            this.setupWaitTimesChart(context);
        }
    }

    private void toggleShowWaitTimesChart() {
        if (mShowWaitTimes) {
            mWaitTimesButton.setText("Hide");
            mWaitTimesChart.setVisibility(View.VISIBLE);
            mWaitTimesXAxisLine.setVisibility(View.VISIBLE);
            mWaitTimesXAxisTicks.setVisibility(View.VISIBLE);
            mWaitTimesXAxisLabels.setVisibility(View.VISIBLE);
        } else {
            mWaitTimesButton.setText("Show");
            mWaitTimesChart.setVisibility(View.GONE);
            mWaitTimesXAxisLine.setVisibility(View.GONE);
            mWaitTimesXAxisTicks.setVisibility(View.GONE);
            mWaitTimesXAxisLabels.setVisibility(View.GONE);
        }
    }

    private void highlightCurrentHour() {
        mWaitTimesChart.highlightValue(LocalTime.now().getHour() - 6, 0);
    }

    private void setupWaitTimesChart(Context context) {
        mWaitTimesChart.setNoDataText("No wait times available.");
        mWaitTimesChart.setNoDataTextColor(context.getResources().getColor(R.color.secondary));
        mWaitTimesChart.setNoDataTextTypeface(Typeface.SANS_SERIF);
        mWaitTimesChart.setDescription(null);
        mWaitTimesChart.getLegend().setEnabled(false);
        mWaitTimesChart.setDoubleTapToZoomEnabled(false);
        mWaitTimesChart.setPinchZoom(false);
        mWaitTimesChart.getRendererXAxis().getPaintAxisLabels().setTextAlign(Paint.Align.LEFT);
        mWaitTimesChart.setViewPortOffsets(0, 100f, 0, 0);

        this.setupWaitTimesChartAxis();
        this.setupWaitTimesData(context);

        // Set up wait times marker label.
        mWaitTimesMarkerView = new WaitTimesMarkerView(context, R.layout.wait_times_marker_view);
        mWaitTimesChart.setMarker(mWaitTimesMarkerView);
        highlightCurrentHour();
    }

    private void updateMarkerAtEntry(Entry e) {
        Swipe s = mSwipeData.get((int) e.getX());
        mWaitTimesMarkerView.updateMarkerLabel(e, s);
    }

    private void setupWaitTimesData(Context context) {
        mWaitTimesChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            // This method gets called after a bar is clicked after it has been highlighted
            public void onValueSelected(Entry e, Highlight h) {
                // We want to make sure that if the user is scrolling vertically, we remove
                // any interactions with the bar chart
                if (mVerticalScrolling && e != mLastEntry) {
                    // If scrolling - reset the highlight and update the highlight with the
                    // highlight from before
                    mWaitTimesChart.highlightValue(null);
                    if (mLastEntry == null) {
                        mWaitTimesChart.highlightValue(LocalTime.now().getHour() - 6, 0);
                    } else {
                        mWaitTimesChart.highlightValue(mLastEntry.getX(), 0);
                    }
                } else {
                    updateMarkerAtEntry(e);
                    mLastEntry = e;
                }
            }

            @Override
            public void onNothingSelected() {
                if (mLastEntry == null) {
                    highlightCurrentHour();
                } else {
                    updateMarkerAtEntry(mLastEntry);
                    mWaitTimesChart.highlightValue(mLastEntry.getX(), 0);
                }
            }
        });

        // Use wait times data from backend.
        List<BarEntry> entries = new ArrayList<>();
        for (int i = 0; i < mSwipeData.size(); i++) {
            entries.add(new BarEntry(i, (float) mSwipeData.get(i).swipeDensity));
        }

        // Set up wait times bar graph colors.
        BarDataSet set = new BarDataSet(entries, "BarDataSet");
        set.setColors(ContextCompat.getColor(context, R.color.lightBlue));
        set.setValueTypeface(Typeface.SANS_SERIF);
        set.setHighLightColor(context.getResources().getColor(R.color.blue));
        set.setHighLightAlpha(255);
        BarData barData = new BarData(set);
        barData.setDrawValues(false);
        mWaitTimesChart.setData(barData);
    }

    private void setupWaitTimesChartAxis() {
        // Remove bar chart Y axis - left side.
        YAxis yAxisLeft = mWaitTimesChart.getAxisLeft();
        yAxisLeft.setEnabled(false);
        yAxisLeft.setAxisMinimum(0);

        // Remove bar chart Y axis - right side.
        YAxis yAxisRight = mWaitTimesChart.getAxisRight();
        yAxisRight.setEnabled(false);

        // Customize bar chart X axis.
        XAxis xAxis = mWaitTimesChart.getXAxis();
        xAxis.setEnabled(false);
        xAxis.setAxisMaximum(21);
    }
}

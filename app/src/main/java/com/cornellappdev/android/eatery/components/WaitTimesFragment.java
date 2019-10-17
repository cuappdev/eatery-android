package com.cornellappdev.android.eatery.components;

import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.cornellappdev.android.eatery.R;
import com.cornellappdev.android.eatery.model.Swipe;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.ArrayList;
import java.util.List;

public class WaitTimesFragment extends Fragment {
    private BarChart mWaitTimesChart;
    private WaitTimesMarkerView mWaitTimesMarkerView;
    /**
     * mSwipeData -
     *  Should have size of 21 upon construction within CampusMenuActivity.
     * Swipe s: mSwipeData -
     *  Hour determined by index, with 0 representing start = 6am, end = 7am.
     *  start, end = null.
     *  swipeDensity, waitTimeLow, waitTimeHigh are the maximums collected from backend data.
     */
    private List<Swipe> mSwipeData;

    public WaitTimesFragment(List<Swipe> swipeData) {
        mSwipeData = swipeData;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_wait_times, container, false);

        mWaitTimesChart = (BarChart) view.findViewById(R.id.wait_time_chart);
        this.setupWaitTimesChart();
        return view;
    }

    private void setupWaitTimesChart() {
        mWaitTimesChart.setDescription(null);
        mWaitTimesChart.getLegend().setEnabled(false);
        mWaitTimesChart.setDoubleTapToZoomEnabled(false);
        mWaitTimesChart.setPinchZoom(false);
        mWaitTimesChart.animateY(2000);
        mWaitTimesChart.setExtraBottomOffset(24f);
        mWaitTimesChart.setExtraTopOffset(48f);
        mWaitTimesChart.getRendererXAxis().getPaintAxisLabels().setTextAlign(Paint.Align.LEFT);

        this.setupWaitTimesChartAxis();
        this.setupWaitTimesData();
        // Set up wait times marker label.
        mWaitTimesMarkerView = new WaitTimesMarkerView(getContext(), R.layout.wait_times_marker_view);
        mWaitTimesChart.setMarker(mWaitTimesMarkerView);
    }

    private void setupWaitTimesData() {
        mWaitTimesChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                Swipe s = mSwipeData.get((int) e.getX());
                if (s.waitTimeLow == 0 && s.waitTimeHigh == 0) {
                    // Prevent wait times marker from showing for hours without backend swipe data.
                    mWaitTimesChart.highlightValue(null);
                } else {
                    // Update waitTimeLow and waitTimeHigh on wait times marker.
                    mWaitTimesMarkerView.updateMarkerLabel(e, s);
                }
            }
            // onNothingSelected must be overridden for onChartValueSelectedListener.
            @Override
            public void onNothingSelected() {

            }
        });

        // Use wait times data from backend.
        List<BarEntry> entries = new ArrayList<>();
        for (int i = 0; i < mSwipeData.size(); i++) {
            entries.add(new BarEntry(i, (float) mSwipeData.get(i).swipeDensity));
        }

        // Set up wait times bar graph colors.
        BarDataSet set = new BarDataSet(entries, "BarDataSet");
        set.setColors(ContextCompat.getColor(getContext(), R.color.lightBlue));
        set.setValueTypeface(Typeface.SANS_SERIF);
        set.setHighLightColor(getResources().getColor(R.color.blue));
        set.setHighLightAlpha(255);
        BarData barData = new BarData(set);
        barData.setDrawValues(false);
        mWaitTimesChart.setData(barData);
    }

    private void setupWaitTimesChartAxis() {
        // Remove bar chart Y axis - left side.
        YAxis yAxisLeft = mWaitTimesChart.getAxisLeft();
        yAxisLeft.setDrawGridLines(false);
        yAxisLeft.setDrawLabels(false);
        yAxisLeft.setEnabled(false);
        yAxisLeft.setAxisMinimum(0);

        // Remove bar chart Y axis - right side.
        YAxis yAxisRight = mWaitTimesChart.getAxisRight();
        yAxisRight.setDrawGridLines(false);
        yAxisRight.setDrawLabels(false);
        yAxisRight.setEnabled(false);
        yAxisLeft.setAxisMinimum(0);

        // Customize bar chart X axis.
        XAxis xAxis = mWaitTimesChart.getXAxis();
        xAxis.setDrawGridLines(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        // Set X axis colors and fonts.
        // "xAxis.set[_]Color(R.color.[_])" will use Android's default colors, rather than our custom defined.
        xAxis.setAxisLineColor(getResources().getColor(R.color.inactive));
        xAxis.setTextColor(getResources().getColor(R.color.secondary));
        xAxis.setTextSize(14f);
        xAxis.setAxisLineWidth(2f);
        // Setup X axis labels.
        xAxis.setAxisMaximum(21);
        xAxis.setLabelCount(8);
        xAxis.setTypeface(Typeface.SANS_SERIF);
        xAxis.setYOffset(8f);
        ArrayList<String> xAxisLabels = new ArrayList<String>();
        for(int i = 0; i < 22; i++) {
            int hourNum = (i + 6) % 12;
            if (hourNum == 0) {
                hourNum = 12;
            }
            String ap = i <= 5 || i >= 18 ? "a" : "p";
            xAxisLabels.add(hourNum + ap);
        }
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xAxisLabels));
    }
}

package com.cornellappdev.android.eatery.components;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;

import androidx.core.content.ContextCompat;

import com.cornellappdev.android.eatery.R;
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

public class WaitTimesBarChart extends BarChart {
    public WaitTimesBarChart(Context context) {
        super(context);
    }

    private void setupAxis(BarChart b) {
        // Remove bar chart Y axis - left side.
        YAxis yAxisLeft = b.getAxisLeft();
        yAxisLeft.setDrawGridLines(false);
        yAxisLeft.setDrawLabels(false);
        yAxisLeft.setEnabled(false);
        yAxisLeft.setAxisMinimum(-0.5f);

        // Remove bar chart Y axis - right side.
        YAxis yAxisRight = b.getAxisRight();
        yAxisRight.setDrawGridLines(false);
        yAxisRight.setDrawLabels(false);
        yAxisRight.setEnabled(false);

        // Customize bar chart X axis.
        XAxis xAxis = b.getXAxis();
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

    private void setupWaitTimesChart() {
        this.setDescription(null);
        this.getLegend().setEnabled(false);
        this.setDoubleTapToZoomEnabled(false);
        this.setPinchZoom(false);
        this.animateY(2000);
        this.setExtraBottomOffset(24f);
        this.setExtraTopOffset(48f);
        this.getRendererXAxis().getPaintAxisLabels().setTextAlign(Paint.Align.LEFT);
        this.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            //          TODO (yanlam): update with wait times from backend
            @Override
            public void onValueSelected(Entry e, Highlight h) {

            }

            @Override
            public void onNothingSelected() {

            }
        });

        setupAxis(this);

        List<BarEntry> entries = new ArrayList<>();
//        TODO (yanlam): pull wait times from backend and make bar entries dynamic.
        entries.add(new BarEntry(0, 2f));
        entries.add(new BarEntry(1f, 30f));
        entries.add(new BarEntry(2f, 10f));
        entries.add(new BarEntry(3f, 20f));
        entries.add(new BarEntry(4f, 30f));
        entries.add(new BarEntry(5f, 40f));
        entries.add(new BarEntry(6f, 40f));
        entries.add(new BarEntry(7f, 35f));
        entries.add(new BarEntry(8f, 40f));
        entries.add(new BarEntry(9f, 30f));
        entries.add(new BarEntry(10f, 2f));
        entries.add(new BarEntry(11f, 5f));
        entries.add(new BarEntry(12f, 10f));
        entries.add(new BarEntry(13f, 40f));
        entries.add(new BarEntry(14f, 40f));
        entries.add(new BarEntry(15f, 20f));
        entries.add(new BarEntry(16f, 2f));
        entries.add(new BarEntry(17f, 2f));
        entries.add(new BarEntry(18f, 10f));
        entries.add(new BarEntry(19f, 2f));
        entries.add(new BarEntry(20f, 2f));

        BarDataSet set = new BarDataSet(entries, "BarDataSet");
        set.setColors(ContextCompat.getColor(getContext(), R.color.lightBlue));
        set.setValueTypeface(Typeface.SANS_SERIF);
        set.setHighLightColor(getResources().getColor(R.color.blue));
        set.setHighLightAlpha(255);
        BarData barData = new BarData(set);
        barData.setDrawValues(false);

        this.setData(barData);
    }
}

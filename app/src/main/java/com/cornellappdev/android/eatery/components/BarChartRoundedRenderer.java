package com.cornellappdev.android.eatery.components;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.buffer.BarBuffer;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.highlight.Range;
import com.github.mikephil.charting.interfaces.dataprovider.BarDataProvider;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.renderer.BarChartRenderer;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

/*
 * This class is an extension of MikePhil's BarChartRenderer allowing us to curve edges of the bar
 * columns
 *
 * We extended the two methods where rectangles (and highlights) are drawn, more information on this
 * class can be found at:
 * https://github.com/PhilJay/MPAndroidChart/blob/master/MPChartLib/src/main/java/com/github
 * /mikephil/charting/renderer/BarChartRenderer.java
 *
 * More information on the open-sourced repo used here as a whole can be found at:
 * https://github.com/PhilJay/MPAndroidChart
 */
public class BarChartRoundedRenderer extends BarChartRenderer {
    private static final float CORNER_RADIUS = 7;

    BarChartRoundedRenderer(BarDataProvider chart, ChartAnimator animator,
            ViewPortHandler viewPortHandler) {
        super(chart, animator, viewPortHandler);
    }

    private void drawRectHelper(Canvas c, float left, float top, float right, float bottom,
            Paint p) {
        if (bottom - top >= CORNER_RADIUS) {
            c.drawRoundRect(left, top, right, bottom, CORNER_RADIUS, CORNER_RADIUS, p);
            c.drawRect(left, top + CORNER_RADIUS, right, bottom, p);
        } else {
            c.drawRoundRect(left, top, right, bottom, CORNER_RADIUS, CORNER_RADIUS, p);
            c.drawRect(left, top + (bottom - top) * 3 / 4, right, bottom, p);
        }
    }

    private void drawRectHelper(Canvas c, RectF r, Paint p) {
        drawRectHelper(c, r.left, r.top, r.right, r.bottom, p);
    }

    @Override
    public void drawDataSet(Canvas c, IBarDataSet dataSet, int index) {

        Transformer trans = mChart.getTransformer(dataSet.getAxisDependency());
        mBarBorderPaint.setColor(dataSet.getBarBorderColor());
        mBarBorderPaint.setStrokeWidth(Utils.convertDpToPixel(dataSet.getBarBorderWidth()));
        final boolean drawBorder = dataSet.getBarBorderWidth() > 0.f;
        float phaseX = mAnimator.getPhaseX();
        float phaseY = mAnimator.getPhaseY();

        // initialize the buffer
        BarBuffer buffer = mBarBuffers[index];
        buffer.setPhases(phaseX, phaseY);
        buffer.setDataSet(index);
        buffer.setInverted(mChart.isInverted(dataSet.getAxisDependency()));
        buffer.setBarWidth(mChart.getBarData().getBarWidth());

        buffer.feed(dataSet);
        trans.pointValuesToPixel(buffer.buffer);
        final boolean isSingleColor = dataSet.getColors().size() == 1;
        if (isSingleColor) {
            mRenderPaint.setColor(dataSet.getColor());
        }
        // Buffer is an array where index j, j+1, j+2, j+3 where j is a multiple of 4, represents
        // the position of the left, top, right, and bottom edge respectively
        // Iterate over all the barchart data and draw bars with correct location and paint
        for (int j = 0; j < buffer.size(); j += 4) {
            if (!mViewPortHandler.isInBoundsLeft(buffer.buffer[j + 2])) {
                continue;
            }
            if (!mViewPortHandler.isInBoundsRight(buffer.buffer[j])) {
                break;
            }
            if (!isSingleColor) {
                // Set the color for the currently drawn value. If the index
                // is out of bounds, reuse colors.
                mRenderPaint.setColor(dataSet.getColor(j / 4));
            }
            drawRectHelper(c, buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2],
                    buffer.buffer[j + 3], mRenderPaint);
            if (drawBorder) {
                drawRectHelper(c, buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2],
                        buffer.buffer[j + 3], mBarBorderPaint);
            }
        }
    }

    @Override
    public void drawValue(Canvas c, String valueText, float x, float y, int color) {
        mValuePaint.setColor(color);
        c.drawText(valueText, x, y, mValuePaint);
    }

    @Override
    public void drawHighlighted(Canvas c, Highlight[] indices) {
        BarData barData = mChart.getBarData();
        for (Highlight high : indices) {
            IBarDataSet set = barData.getDataSetByIndex(high.getDataSetIndex());
            if (set == null || !set.isHighlightEnabled()) {
                continue;
            }
            BarEntry e = set.getEntryForXValue(high.getX(), high.getY());
            if (!isInBoundsX(e, set)) {
                continue;
            }

            Transformer trans = mChart.getTransformer(set.getAxisDependency());
            mHighlightPaint.setColor(set.getHighLightColor());
            mHighlightPaint.setAlpha(set.getHighLightAlpha());
            boolean isStack = (high.getStackIndex() >= 0 && e.isStacked());
            final float y1;
            final float y2;

            if (isStack) {
                if (mChart.isHighlightFullBarEnabled()) {
                    y1 = e.getPositiveSum();
                    y2 = -e.getNegativeSum();
                } else {
                    Range range = e.getRanges()[high.getStackIndex()];
                    y1 = range.from;
                    y2 = range.to;
                }
            } else {
                y1 = e.getY();
                y2 = 0.f;
            }
            prepareBarHighlight(e.getX(), y1, y2, barData.getBarWidth() / 2f, trans);
            setHighlightDrawPos(high, mBarRect);
            drawRectHelper(c, mBarRect, mHighlightPaint);
        }
    }
}
package com.cornellappdev.android.eatery.components;
import android.content.Context;
import android.widget.TextView;

import com.cornellappdev.android.eatery.R;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;

public class WaitTimesMarkerView extends MarkerView {

    private TextView timeLabel;

    public WaitTimesMarkerView (Context context, int layoutResource) {
        super(context, layoutResource);
        timeLabel = (TextView) findViewById(R.id.timeLabel);
    }

    @Override
    public void refreshContent(Entry e, Highlight h) {
        timeLabel.setText("Happy" + e.getY());
    }

    public int getXOffset(float xpos) {
        // this will center the marker-view horizontally
        return -(getWidth() / 2);
    }

    public int getYOffset(float ypos) {
        // this will cause the marker-view to be above the selected value
        return -getHeight();
    }
}

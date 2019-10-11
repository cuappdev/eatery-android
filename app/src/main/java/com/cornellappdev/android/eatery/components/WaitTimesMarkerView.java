package com.cornellappdev.android.eatery.components;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;

import android.widget.LinearLayout;
import android.widget.TextView;

import com.cornellappdev.android.eatery.R;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;

public class WaitTimesMarkerView extends MarkerView {

    private TextView timeLabel;
    private LinearLayout markerLayout;
    private Entry entry;

    public WaitTimesMarkerView (Context context, int layoutResource) {
        super(context, layoutResource);
        timeLabel = (TextView) findViewById(R.id.timeLabel);
        markerLayout = (LinearLayout) findViewById(R.id.waitTimesLayout);
        entry = null;
    }

    @Override
    public void draw(Canvas canvas, float posX, float posY) {
        int canvasSave = canvas.save();
        float eX = entry.getX();
        float eY = 0f;

        Paint paint = new Paint();
        paint.setColor(getResources().getColor(R.color.inactive));
        paint.setStrokeWidth(4f);

        // Draw vertical line from label to bar.
        canvas.drawLine(posX, eY, posX, posY, paint);

        // Move label to match bar position.
        canvas.translate((float)(posX + getXOffset(eX)), eY);

        draw(canvas);
        canvas.restoreToCount(canvasSave);
    }

    @Override
    public void refreshContent(Entry e, Highlight h) {
        entry = e;
        float eX = e.getX();
        float eY = e.getY();

        String waitTime = "5-10m";
//        TODO: update wait time.
        timeLabel.setText(waitTime);
    }

    public double getXOffset(float xpos) {
        // Adjust label position to keep it from going offscreen.
        double d = xpos <= 2 ? (0.96 * Math.pow((xpos - 2), 2) + 2.2)
                : xpos >= 18 ? (0.08 * Math.pow((xpos - 21), 2) + 1.11)
                : 2.0;
        return -(getWidth() / d);
    }
}

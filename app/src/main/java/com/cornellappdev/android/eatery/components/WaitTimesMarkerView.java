package com.cornellappdev.android.eatery.components;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.Html;
import android.text.format.DateFormat;
import android.view.View;
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
        canvas.drawLine(posX, eY, posX, posY, paint);
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
        double k = 2.0;
        if (xpos <= 2) {
            k = 0.96 * Math.pow((xpos - 2), 2.0) + 2.2;
        } else if (xpos >= 18) {
            k = 0.08 * Math.pow((xpos - 21), 2.0) + 1.11;
        }
        return -(getWidth() / k);
    }

    public double getYOffset(float ypos) {
        return 0;
    }
}

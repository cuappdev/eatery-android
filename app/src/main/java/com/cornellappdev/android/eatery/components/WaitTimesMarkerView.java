package com.cornellappdev.android.eatery.components;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;

import android.text.Html;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cornellappdev.android.eatery.R;
import com.cornellappdev.android.eatery.model.Swipe;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;

import java.time.LocalTime;

public class WaitTimesMarkerView extends MarkerView {

    private TextView waitTimeLabel;
    private LinearLayout markerLayout;
    private Entry entry;

    public WaitTimesMarkerView (Context context, int layoutResource) {
        super(context, layoutResource);
        waitTimeLabel = findViewById(R.id.waitTimeLabel);
        markerLayout = findViewById(R.id.waitTimesLayout);
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

    // formatTime is "Now: " if current time, otherwise, format of "12p" for noon.
    private String formatTime(float eX) {
        int time = (int)((eX + 6) % 12);
        time = time == 0 ? 12 : time;
        boolean isCurrentTime = LocalTime.now().getHour() - 6 == eX;
        return (isCurrentTime ? "Now" : (time + (eX <= 5 || eX >= 18 ? "a" : "p"))) + ": ";
    }

    // formatWaitTime is "?" if no data available, otherwise, format of "0-3m" for 0 to 3 minute wait.
    private String formatWaitTime(Swipe s, boolean hasNoData) {
        return hasNoData ? "?" : (s.waitTimeLow + "-" + s.waitTimeHigh + "m");
    }

    // formatMarkerLabel is the HTML string for the marker label.
    private String formatMarkerLabel(float eX, Swipe s) {
        boolean hasNoData = s.waitTimeLow == 0 && s.waitTimeHigh == 0;
        String timeString = formatTime(eX);
        String waitTime = formatWaitTime(s, hasNoData);
        return timeString
                + "<font color=\"" + getResources().getColor(R.color.blue) + "\" face=\"sans-serif-medium\">"
                + waitTime
                + (hasNoData ? "</font>" : "</font> wait");
    }

    // updateMarkerLabel is called when a bar is selected in the corresponding bar chart.
    public void updateMarkerLabel(Entry e, Swipe s) {
        entry = e;
        String waitTimeHtml = formatMarkerLabel(e.getX(), s);
        waitTimeLabel.setText(Html.fromHtml(waitTimeHtml));
    }

    public double getXOffset(float xpos) {
        // Adjust label position to keep it from going offscreen.
        double d = xpos <= 4 ? (0.96 * Math.pow((xpos - 4), 2) + 3.2)
                : xpos >= 16 ? (0.08 * Math.pow((xpos - 19), 2) + 1.11)
                : 2.0;
        return -(getWidth() / d);
    }
}

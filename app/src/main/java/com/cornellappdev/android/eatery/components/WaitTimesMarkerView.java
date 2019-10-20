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

    public void updateMarkerLabel(Entry e, Swipe s) {
        entry = e;
        float eX = e.getX();
        int t = (int)((eX + 6) % 12);
        t = t == 0 ? 12 : t;
        boolean hasNoData = s.waitTimeLow == 0 && s.waitTimeHigh == 0;
        String time = (LocalTime.now().getHour() - 6 == eX ? "Now" : (t + (eX <= 5 || eX >= 18 ? "a" : "p"))) + ": ";
        String waitTime = hasNoData ? "?" : (s.waitTimeLow + "-" + s.waitTimeHigh + "m");
        String waitTimeHtml = time
                + "<font color=\"" + getResources().getColor(R.color.blue) + "\" face=\"sans-serif-medium\">"
                + waitTime
                + (hasNoData ? "</font>" : "</font> wait");
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

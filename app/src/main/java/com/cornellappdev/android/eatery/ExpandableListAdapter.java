package com.cornellappdev.android.eatery;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.cornellappdev.android.eatery.model.DiningHallModel;
import com.cornellappdev.android.eatery.model.Interval;
import com.cornellappdev.android.eatery.model.enums.MealType;
import com.cornellappdev.android.eatery.util.TimeUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;

import java.util.ArrayList;

/**
 * This class is used in WeeklyMenuActivity, where it displays the
 * corresponding dining halls for each meal period and the menu for that particular day
 */
public class ExpandableListAdapter extends BaseExpandableListAdapter {
    private Context mContext;
    private ArrayList<DiningHallModel> mDiningList;
    private LocalDate mDate;
    private MealType mMealType;

    public ExpandableListAdapter(
            Context context,
            LocalDate date,
            MealType mealType,
            ArrayList<DiningHallModel> diningList) {
        this.mContext = context;
        this.mMealType = mealType;
        this.mDate = date;
        this.mDiningList = diningList;
    }

    @Override
    public int getGroupCount() {
        return mDiningList.size();
    }

    @Override
    public int getChildrenCount(int diningHallNumber) {
        if (getGroupCount() == 0) return 0;
        return ((DiningHallModel) getGroup(diningHallNumber)).getMealByDateAndType(mDate,
                mMealType).getMenuAsList().size();
    }

    @Override
    public Object getGroup(int diningHallNumber) {
        return mDiningList.get(diningHallNumber);
    }

    @Override
    public Object getChild(int diningHallNumber, int menuItemNumber) {
        return ((DiningHallModel) getGroup(diningHallNumber))
                .getMealByDateAndType(mDate, mMealType).getMenuAsList().get(menuItemNumber);
    }

    @Override
    public long getGroupId(int diningHallNumber) {
        return diningHallNumber;
    }

    @Override
    public long getChildId(int diningHallNumber, int menuItemNumber) {
        return menuItemNumber;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int diningHallNumber, boolean isExpanded, View view,
            ViewGroup viewGroup) {
        // Displays header for explistview
        if (view == null) {
            // Inflate layout if it does not exist already
            LayoutInflater viewInflater = (LayoutInflater) this.mContext.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            view = viewInflater.inflate(R.layout.list_view_header, viewGroup, false);
        }

        DiningHallModel dm = (DiningHallModel) getGroup(diningHallNumber);
        String diningName = (dm).getNickName();
        TextView headerText = view.findViewById(R.id.header);
        headerText.setText(diningName);
        headerText.setTypeface(null, Typeface.BOLD);
        TextView openText = view.findViewById(R.id.weekly_open);
        TextView timeText = view.findViewById(R.id.weekly_time);

        LocalDateTime currentTime = LocalDateTime.now();
        Interval interval = dm.getIntervalByDateAndType(mDate, mMealType);
        if (interval == null) {
            return view;
        }
        openText.setText("");
        openText.setPadding(0, 0, 0, 0);
        if (interval.getStart().toLocalDate().equals(LocalDate.now())) {
            openText.setPadding(0, 0, 8, 0);
            openText.setText(R.string.closed);
            openText.setTextColor(ContextCompat.getColor(mContext, R.color.red));
            if (interval.containsTime(currentTime)) {
                openText.setText(R.string.open);
                openText.setTextColor(ContextCompat.getColor(mContext, R.color.green));
            }
        }
        timeText.setText((TimeUtil.format(dm.getCurrentStatus(),
                dm.getIntervalByDateAndType(mDate, mMealType), dm.getChangeTime())));
        return view;
    }

    @Override
    public View getChildView(int diningHallNumber, int menuItemNumber, boolean isLastChild,
            View view, ViewGroup viewGroup) {
        // Inflate layout if it does not exist already
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) this.mContext.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_view_body, viewGroup, false);
        }

        // Horizontal line that separates each eatery entry
        View horizontal_line = view.findViewById(R.id.horiline);
        horizontal_line.setVisibility(View.GONE);
        String str = (String) getChild(diningHallNumber, menuItemNumber);
        TextView tv = view.findViewById(R.id.menu_title);

        if (str == null) {
            tv.setText("No menu information");
            tv.setTextColor(ContextCompat.getColor(mContext, R.color.primary));
            tv.setTextSize(18);
            tv.setGravity(Gravity.CENTER_HORIZONTAL);
            tv.setPadding(0, 96, 0, 0);
        } else if (((DiningHallModel) getGroup(diningHallNumber))
                .getMealByDateAndType(mDate, mMealType).containsCategory(str)) {
            SpannableString sstr = new SpannableString(str);
            tv.setText(sstr);
            tv.setTypeface(null, Typeface.NORMAL);
            tv.setTextColor(ContextCompat.getColor(mContext, R.color.primary));
            tv.setTextSize(18);
            tv.setPadding(32, 24, 0, 0);
        } else {
            SpannableString sstr = new SpannableString(str);
            tv.setText(sstr);
            tv.setTextColor(ContextCompat.getColor(mContext, R.color.secondary));
            tv.setTextSize(14);
            tv.setTypeface(null, Typeface.NORMAL);
            tv.setPadding(32, 0, 0, 0);
        }
        if (menuItemNumber == ((DiningHallModel) getGroup(diningHallNumber))
                .getMealByDateAndType(mDate, mMealType).getMenuAsList().size() - 1) {
            tv.setPadding(32, 0, 0, 24);
        }
        return view;
    }

    @Override
    public boolean isChildSelectable(int diningHallNumber, int menuItemNumber) {
        return false;
    }
}

package com.cornellappdev.android.eatery;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cornellappdev.android.eatery.model.DiningHallModel;
import com.cornellappdev.android.eatery.model.enums.MealType;
import com.cornellappdev.android.eatery.presenter.WeeklyPresenter;

import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 */
public class WeeklyMenuFragment extends Fragment implements View.OnClickListener {
    private ExpandableListAdapter mListAdapterWest;
    private ExpandableListAdapter mListAdapterNorth;
    private ExpandableListAdapter mListAdapterCentral;
    private NonScrollExpandableListView mExpListViewWest;
    private NonScrollExpandableListView mExpListViewNorth;
    private NonScrollExpandableListView mExpListViewCentral;
    private TextView mWestText;
    private TextView mNorthText;
    private TextView mCentralText;
    private MealType mMealType = MealType.BREAKFAST;
    private TextView mBreakfastText;
    private TextView mLunchText;
    private TextView mDinnerText;
    private LinearLayout mLinDate;
    private ArrayList<TextView> dateTvList;
    private WeeklyPresenter mPresenter;
    private int mLastExpandedPosition;
    private NonScrollExpandableListView mLastClickedListView;
    private Context mAppContext;

    public WeeklyMenuFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_weekly_menu, container, false);
        mPresenter = new WeeklyPresenter(rootView);
        mAppContext = getActivity().getApplicationContext();
        mBreakfastText = rootView.findViewById(R.id.breakfast);
        mLunchText = rootView.findViewById(R.id.lunch);
        mDinnerText = rootView.findViewById(R.id.dinner);
        mLinDate = rootView.findViewById(R.id.lin_date);
        mExpListViewWest = rootView.findViewById(R.id.expandablelistview_west);
        mExpListViewNorth = rootView.findViewById(R.id.expandablelistview_north);
        mExpListViewCentral = rootView.findViewById(R.id.expandablelistview_central);
        mWestText = rootView.findViewById(R.id.west_header);
        mNorthText = rootView.findViewById(R.id.north_header);
        mCentralText = rootView.findViewById(R.id.central_header);

        //Set onClickListeners for date text
        int dateArr[] = {R.id.date0, R.id.date1, R.id.date2, R.id.date3, R.id.date4, R.id.date5,
                R.id.date6};

        dateTvList = new ArrayList<>();
        for (int dateId : dateArr) {
            rootView.findViewById(dateId).setOnClickListener(this);
            dateTvList.add((TextView) rootView.findViewById(dateId));
        }

        getActivity().setTitle("Upcoming Menus");
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        int offsetPx = 64 * (dm.densityDpi
                / DisplayMetrics.DENSITY_DEFAULT); // 64 is an arbitrary dp offset
        mExpListViewWest.setIndicatorBounds(dm.widthPixels - offsetPx, dm.widthPixels);
        mExpListViewNorth.setIndicatorBounds(dm.widthPixels - offsetPx, dm.widthPixels);
        mExpListViewCentral.setIndicatorBounds(dm.widthPixels - offsetPx, dm.widthPixels);

        mLastExpandedPosition = -1;
        mLastClickedListView = null;

        mExpListViewCentral.setOnGroupExpandListener(
                new NonScrollExpandableListView.OnGroupExpandListener() {
                    @Override
                    public void onGroupExpand(int index) {
                        if (mLastClickedListView != null
                                && mLastExpandedPosition != -1
                                && index != mLastExpandedPosition) {
                            mLastClickedListView.collapseGroup(mLastExpandedPosition);
                        }
                        mLastExpandedPosition = index;
                        mLastClickedListView = mExpListViewCentral;
                    }
                });

        mExpListViewWest.setOnGroupExpandListener(
                new NonScrollExpandableListView.OnGroupExpandListener() {
                    @Override
                    public void onGroupExpand(int index) {
                        if (mLastClickedListView != null
                                && mLastExpandedPosition != -1
                                && index != mLastExpandedPosition) {
                            mLastClickedListView.collapseGroup(mLastExpandedPosition);
                        }
                        mLastExpandedPosition = index;
                        mLastClickedListView = mExpListViewWest;
                    }
                });

        mExpListViewNorth.setOnGroupExpandListener(
                new NonScrollExpandableListView.OnGroupExpandListener() {
                    @Override
                    public void onGroupExpand(int index) {
                        if (mLastClickedListView != null
                                && mLastExpandedPosition != -1
                                && index != mLastExpandedPosition) {
                            mLastClickedListView.collapseGroup(mLastExpandedPosition);
                        }
                        mLastExpandedPosition = index;
                        mLastClickedListView = mExpListViewNorth;
                    }
                });

        // When changing date, highlight Breakfast textview
        mLinDate.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mBreakfastText.setTextColor(
                                ContextCompat.getColor(mAppContext, R.color.blue));
                        mLunchText.setTextColor(
                                ContextCompat.getColor(mAppContext, R.color.secondary));
                        mDinnerText.setTextColor(
                                ContextCompat.getColor(mAppContext, R.color.secondary));
                    }
                });
        Date now = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(now);
        for (TextView textView : dateTvList) {
            // Formatting for each day
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEE");
            dateFormat.setCalendar(cal);
            String sDay = dateFormat.format(cal.getTime());

            // Formatting for each date
            int date = cal.get(Calendar.DAY_OF_MONTH);
            String sDate = Integer.toString(date);
            SpannableString ssDate = new SpannableString(sDay + '\n' + sDate);
            ssDate.setSpan(new RelativeSizeSpan(0.8f), 0, 3, 0);
            ssDate.setSpan(new RelativeSizeSpan(2f), 4, ssDate.length(), 0);
            TextView tv = textView;
            tv.setText(ssDate);
            cal.add(Calendar.DAY_OF_YEAR, 1);
        }

        mBreakfastText.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mBreakfastText.setTextColor(
                                ContextCompat.getColor(mAppContext, R.color.blue));
                        mLunchText.setTextColor(
                                ContextCompat.getColor(mAppContext, R.color.secondary));
                        mDinnerText.setTextColor(
                                ContextCompat.getColor(mAppContext, R.color.secondary));

                        mMealType = MealType.BREAKFAST;
                        changeListAdapter(mMealType, mPresenter.getSelectedDate());
                    }
                });

        mLunchText.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mBreakfastText.setTextColor(
                                ContextCompat.getColor(mAppContext, R.color.secondary));
                        mLunchText.setTextColor(ContextCompat.getColor(mAppContext, R.color.blue));
                        mDinnerText.setTextColor(
                                ContextCompat.getColor(mAppContext, R.color.secondary));

                        mMealType = MealType.LUNCH;
                        changeListAdapter(mMealType, mPresenter.getSelectedDate());
                    }
                });

        mDinnerText.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mBreakfastText.setTextColor(
                                ContextCompat.getColor(mAppContext, R.color.secondary));
                        mLunchText.setTextColor(
                                ContextCompat.getColor(mAppContext, R.color.secondary));
                        mDinnerText.setTextColor(ContextCompat.getColor(mAppContext, R.color.blue));

                        mMealType = MealType.DINNER;
                        changeListAdapter(mMealType, mPresenter.getSelectedDate());
                    }
                });

        LocalDateTime currentTime = LocalDateTime.now();

        // Display lunch menu if current time is between 11:00am - 4:00pm
        if (currentTime.getHour() >= 11 && currentTime.getHour() < 16) {
            changeListAdapter(MealType.LUNCH, mPresenter.getSelectedDate());
            mMealType = MealType.LUNCH;
            mBreakfastText.setTextColor(ContextCompat.getColor(mAppContext, R.color.secondary));
            mLunchText.setTextColor(ContextCompat.getColor(mAppContext, R.color.blue));
            mDinnerText.setTextColor(ContextCompat.getColor(mAppContext, R.color.secondary));
        }
        // Display dinner menu if current time is between 4:00pm - 10:00pm
        else if (currentTime.getHour() >= 16 && currentTime.getHour() < 22) {
            changeListAdapter(MealType.DINNER, mPresenter.getSelectedDate());
            mMealType = MealType.DINNER;
            mBreakfastText.setTextColor(ContextCompat.getColor(mAppContext, R.color.secondary));
            mLunchText.setTextColor(ContextCompat.getColor(mAppContext, R.color.secondary));
            mDinnerText.setTextColor(ContextCompat.getColor(mAppContext, R.color.blue));
        }
        // Display today's or tomorrow's breakfast menu
        else {
            if (currentTime.getHour() >= 22) {
                mPresenter.getDayInWeek(1);
            }
            changeListAdapter(MealType.BREAKFAST, mPresenter.getSelectedDate());
            mMealType = MealType.BREAKFAST;
            mBreakfastText.setTextColor(ContextCompat.getColor(mAppContext, R.color.blue));
            mLunchText.setTextColor(ContextCompat.getColor(mAppContext, R.color.secondary));
            mDinnerText.setTextColor(ContextCompat.getColor(mAppContext, R.color.secondary));
        }
        return rootView;
    }

    /**
     * Changes the text color to black when selected
     */
    @Override
    public void onClick(View view) {
        TextView textView = (TextView) view;

        textView.setTextColor(ContextCompat.getColor(mAppContext, R.color.primary));
        changeDateColor(textView);

        LocalDate selectedDate = mPresenter.getDayInWeek(dateTvList.indexOf(textView));
        mPresenter.setSelectedDate(selectedDate);
        changeListAdapter(mMealType, mPresenter.getSelectedDate());
    }

    /**
     * Changes the text color to grey if the date is not selected
     */
    public void changeDateColor(TextView selectedDate) {
        for (TextView textView : dateTvList) {
            if (!textView.equals(selectedDate)) {
                textView.setTextColor(ContextCompat.getColor(mAppContext, R.color.secondary));
            }
        }
    }

    /**
     * Updates the list of dining halls and menus that is displayed
     */
    public void changeListAdapter(MealType mMealType, LocalDate date) {
        HashMap<String, ArrayList<DiningHallModel>> finalList = mPresenter.generateAreaLists(
                mMealType, date);
        ArrayList<DiningHallModel> westList = finalList.get("West");
        ArrayList<DiningHallModel> northList = finalList.get("North");
        ArrayList<DiningHallModel> centralList = finalList.get("Central");

        // Hides layout elements if nothing in list for a given CafeteriaArea
        if (westList != null && westList.size() == 0) {
            mWestText.setVisibility(View.GONE);
            mExpListViewWest.setVisibility(View.GONE);
        } else {
            mWestText.setVisibility(View.VISIBLE);
            mExpListViewWest.setVisibility(View.VISIBLE);
            mListAdapterWest =
                    new ExpandableListAdapter(getContext(), date, mMealType, westList);
            mExpListViewWest.setAdapter(mListAdapterWest);
        }
        if (northList != null && northList.size() == 0) {
            mNorthText.setVisibility(View.GONE);
            mExpListViewNorth.setVisibility(View.GONE);
        } else {
            mNorthText.setVisibility(View.VISIBLE);
            mExpListViewNorth.setVisibility(View.VISIBLE);
            mListAdapterNorth =
                    new ExpandableListAdapter(getContext(), date, mMealType, northList);
            mExpListViewNorth.setAdapter(mListAdapterNorth);
        }
        if (centralList != null && centralList.size() == 0) {
            mCentralText.setVisibility(View.GONE);
            mExpListViewCentral.setVisibility(View.GONE);
        } else {
            mCentralText.setVisibility(View.VISIBLE);
            mExpListViewCentral.setVisibility(View.VISIBLE);
            mListAdapterCentral =
                    new ExpandableListAdapter(getContext(), date, mMealType, centralList);
            mExpListViewCentral.setAdapter(mListAdapterCentral);
        }
    }
}

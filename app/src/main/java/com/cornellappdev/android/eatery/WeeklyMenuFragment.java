package com.cornellappdev.android.eatery;


import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cornellappdev.android.eatery.model.DiningHallModel;
import com.cornellappdev.android.eatery.model.MealModel;
import com.cornellappdev.android.eatery.model.enums.MealType;
import com.cornellappdev.android.eatery.presenter.MainListPresenter;
import com.cornellappdev.android.eatery.presenter.WeeklyPresenter;

import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.TreeMap;


/**
 * A simple {@link Fragment} subclass.
 */
public class WeeklyMenuFragment extends Fragment implements View.OnClickListener{
	private TextView mDate0;
	private TextView mDate1;
	private TextView mDate2;
	private TextView mDate3;
	private TextView mDate4;
	private TextView mDate5;
	private TextView mDate6;
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
	private ArrayList<TextView> mDateList = new ArrayList<>();
	private WeeklyPresenter mPresenter;
	private int mLastExpandedPosition;
	private NonScrollExpandableListView mLastClickedListView;

	public WeeklyMenuFragment() {
		// Required empty public constructor
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View rootView = inflater.inflate(R.layout.fragment_weekly_menu, container, false);
		mPresenter = new WeeklyPresenter(rootView);
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

		// Set OnClickListener for dates
		mDate0 = rootView.findViewById(R.id.date0);
		mDate1 = rootView.findViewById(R.id.date1);
		mDate2 = rootView.findViewById(R.id.date2);
		mDate3 = rootView.findViewById(R.id.date3);
		mDate4 = rootView.findViewById(R.id.date4);
		mDate5 = rootView.findViewById(R.id.date5);
		mDate6 = rootView.findViewById(R.id.date6);

		mDate0.setOnClickListener(this);
		mDate1.setOnClickListener(this);
		mDate2.setOnClickListener(this);
		mDate3.setOnClickListener(this);
		mDate4.setOnClickListener(this);
		mDate5.setOnClickListener(this);
		mDate6.setOnClickListener(this);

		// Layout for menu list
		getActivity().setTitle("Upcoming Menus");
		DisplayMetrics dm = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
		int width = dm.widthPixels;
		mExpListViewWest.setIndicatorBounds(width - 250, width);
		mExpListViewNorth.setIndicatorBounds(width - 250, width);
		mExpListViewCentral.setIndicatorBounds(width - 250, width);

		mLastExpandedPosition = -1;
		mLastClickedListView = null;

		mExpListViewCentral.setOnGroupExpandListener(
				new NonScrollExpandableListView.OnGroupExpandListener() {
					@Override
					public void onGroupExpand(int i) {
						if (mLastClickedListView != null
								&& mLastExpandedPosition != -1
								&& i != mLastExpandedPosition) {
							mLastClickedListView.collapseGroup(mLastExpandedPosition);
						}
						mLastExpandedPosition = i;
						mLastClickedListView = mExpListViewCentral;
					}
				});

		mExpListViewWest.setOnGroupExpandListener(
				new NonScrollExpandableListView.OnGroupExpandListener() {
					@Override
					public void onGroupExpand(int i) {
						if (mLastClickedListView != null
								&& mLastExpandedPosition != -1
								&& i != mLastExpandedPosition) {
							mLastClickedListView.collapseGroup(mLastExpandedPosition);
						}
						mLastExpandedPosition = i;
						mLastClickedListView = mExpListViewWest;
					}
				});
		mExpListViewNorth.setOnGroupExpandListener(
				new NonScrollExpandableListView.OnGroupExpandListener() {
					@Override
					public void onGroupExpand(int i) {
						if (mLastClickedListView != null
								&& mLastExpandedPosition != -1
								&& i != mLastExpandedPosition) {
							mLastClickedListView.collapseGroup(mLastExpandedPosition);
						}
						mLastExpandedPosition = i;
						mLastClickedListView = mExpListViewNorth;
					}
				});

		// Populate list of date TextViews on header
		mDateList.add((TextView) rootView.findViewById(R.id.date0));
		mDateList.add((TextView) rootView.findViewById(R.id.date1));
		mDateList.add((TextView) rootView.findViewById(R.id.date2));
		mDateList.add((TextView) rootView.findViewById(R.id.date3));
		mDateList.add((TextView) rootView.findViewById(R.id.date4));
		mDateList.add((TextView) rootView.findViewById(R.id.date5));
		mDateList.add((TextView) rootView.findViewById(R.id.date6));


		// When changing date, highlight Breakfast textview
		mLinDate.setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						mBreakfastText.setTextColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.blue));
						mLunchText.setTextColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.secondary));
						mDinnerText.setTextColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.secondary));
					}
				});

		Date now = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(now);
		for (int i = 0; i < 7; i++) {
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
			TextView tv = mDateList.get(i);
			tv.setText(ssDate);

			cal.add(Calendar.DAY_OF_YEAR, 1);
		}

		mBreakfastText.setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						mBreakfastText.setTextColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.blue));
						mLunchText.setTextColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.secondary));
						mDinnerText.setTextColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.secondary));

						mMealType = MealType.BREAKFAST;
						changeListAdapter(mMealType, mPresenter.getSelectedDate());
					}
				});

		mLunchText.setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						mBreakfastText.setTextColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.secondary));
						mLunchText.setTextColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.blue));
						mDinnerText.setTextColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.secondary));

						mMealType = MealType.LUNCH;
						changeListAdapter(mMealType, mPresenter.getSelectedDate());
					}
				});

		mDinnerText.setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						mBreakfastText.setTextColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.secondary));
						mLunchText.setTextColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.secondary));
						mDinnerText.setTextColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.blue));

						mMealType = MealType.DINNER;
						changeListAdapter(mMealType, mPresenter.getSelectedDate());
					}
				});
		LocalDateTime currentTime = LocalDateTime.now();

		if (currentTime.getHour() < 11) {
			changeListAdapter(MealType.BREAKFAST, mPresenter.getSelectedDate());
			mMealType = MealType.BREAKFAST;
			mBreakfastText.setTextColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.blue));
			mLunchText.setTextColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.secondary));
			mDinnerText.setTextColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.secondary));
		} else if (currentTime.getHour() < 16) {
			changeListAdapter(MealType.LUNCH, mPresenter.getSelectedDate());
			mMealType = MealType.LUNCH;
			mBreakfastText.setTextColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.secondary));
			mLunchText.setTextColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.blue));
			mDinnerText.setTextColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.secondary));
		} else if (currentTime.getHour() < 22) {
			changeListAdapter(MealType.DINNER, mPresenter.getSelectedDate());
			mMealType = MealType.DINNER;
			mBreakfastText.setTextColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.secondary));
			mLunchText.setTextColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.secondary));
			mDinnerText.setTextColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.blue));
		} else {
			mPresenter.getDayInWeek(1);
			changeListAdapter(MealType.BREAKFAST, mPresenter.getSelectedDate());
			mMealType = MealType.BREAKFAST;
			mBreakfastText.setTextColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.blue));
			mLunchText.setTextColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.secondary));
			mDinnerText.setTextColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.secondary));
		}
		return rootView;
	}

	/**
	 * Changes the text color to black when selected
	 */
	@Override
	public void onClick(View v) {
		TextView tv = (TextView) v;

		tv.setTextColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.primary));
		changeDateColor(tv);

		int id = tv.getId();
		LocalDate currentDate = mPresenter.getSelectedDate();

		switch (id) {
			case R.id.date0:
				currentDate = mPresenter.getDayInWeek(0);
				break;
			case R.id.date1:
				currentDate = mPresenter.getDayInWeek(1);
				break;
			case R.id.date2:
				currentDate = mPresenter.getDayInWeek(2);
				break;
			case R.id.date3:
				currentDate = mPresenter.getDayInWeek(3);
				break;
			case R.id.date4:
				currentDate = mPresenter.getDayInWeek(4);
				break;
			case R.id.date5:
				currentDate = mPresenter.getDayInWeek(5);
				break;
			case R.id.date6:
				currentDate = mPresenter.getDayInWeek(6);
				break;
		}
		changeListAdapter(mMealType, currentDate);
	}

	/**
	 * Changes the text color to grey if the date is not selected
	 */
	public void changeDateColor(TextView v) {
		for (int i = 0; i < 7; i++) {
			if (!mDateList.get(i).equals(v)) {
				mDateList.get(i).setTextColor(ContextCompat.getColor(
						getActivity().getApplicationContext(), R.color.secondary));
			}
		}
	}

	/**
	 * Updates the list of dining halls and menus that is displayed
	 */
	public void changeListAdapter(MealType mealType, LocalDate date) {
		HashMap<String, ArrayList<DiningHallModel>> finalList = mPresenter.generateAreaLists(mealType, date);
		ArrayList<DiningHallModel> westList = finalList.get("West");
		ArrayList<DiningHallModel> northList = finalList.get("North");
		ArrayList<DiningHallModel> centralList = finalList.get("Central");

		// Hides layout elements if there is nothing in the list corresponding to a certain
		// CafeteriaArea
		if (westList != null && westList.size() == 0) {
			mWestText.setVisibility(View.GONE);
			mExpListViewWest.setVisibility(View.GONE);
		} else {
			mWestText.setVisibility(View.VISIBLE);
			mExpListViewWest.setVisibility(View.VISIBLE);

			mListAdapterWest =
					new ExpandableListAdapter(getContext(), date, mealType, westList);
			mExpListViewWest.setAdapter(mListAdapterWest);
		}
		if (northList != null && northList.size() == 0) {
			mNorthText.setVisibility(View.GONE);
			mExpListViewNorth.setVisibility(View.GONE);
		} else {
			mNorthText.setVisibility(View.VISIBLE);
			mExpListViewNorth.setVisibility(View.VISIBLE);

			mListAdapterNorth =
					new ExpandableListAdapter(getContext(), date, mealType, northList);
			mExpListViewNorth.setAdapter(mListAdapterNorth);
		}
		if (centralList != null && centralList.size() == 0) {
			mCentralText.setVisibility(View.GONE);
			mExpListViewCentral.setVisibility(View.GONE);
		} else {
			mCentralText.setVisibility(View.VISIBLE);
			mExpListViewCentral.setVisibility(View.VISIBLE);

			mListAdapterCentral =
					new ExpandableListAdapter(getContext(), date, mealType, centralList);
			mExpListViewCentral.setAdapter(mListAdapterCentral);
		}
	}
}

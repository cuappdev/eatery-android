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
	private ExpandableListAdapter listAdapterWest;
	private ExpandableListAdapter listAdapterNorth;
	private ExpandableListAdapter listAdapterCentral;
	private NonScrollExpandableListView expListViewWest;
	private NonScrollExpandableListView expListViewNorth;
	private NonScrollExpandableListView expListViewCentral;
	private TextView westText;
	private TextView northText;
	private TextView centralText;
	private MealType mealType = MealType.BREAKFAST;
	private TextView breakfastText;
	private TextView lunchText;
	private TextView dinnerText;
	private LinearLayout linDate;
	private ArrayList<TextView> dateTvList = new ArrayList<>();
	private WeeklyPresenter presenter;
	private int lastExpandedPosition;
	private NonScrollExpandableListView lastClickedListView;

	public WeeklyMenuFragment() {
		// Required empty public constructor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View rootView = inflater.inflate(R.layout.fragment_weekly_menu, container, false);
		presenter = new WeeklyPresenter(rootView);
		breakfastText = rootView.findViewById(R.id.breakfast);
		lunchText = rootView.findViewById(R.id.lunch);
		dinnerText = rootView.findViewById(R.id.dinner);
		linDate = rootView.findViewById(R.id.lin_date);
		expListViewWest = rootView.findViewById(R.id.expandablelistview_west);
		expListViewNorth = rootView.findViewById(R.id.expandablelistview_north);
		expListViewCentral = rootView.findViewById(R.id.expandablelistview_central);
		westText = rootView.findViewById(R.id.west_header);
		northText = rootView.findViewById(R.id.north_header);
		centralText = rootView.findViewById(R.id.central_header);

		//Set onClickListeners for date text
		int dateArr[] = {R.id.date0, R.id.date1, R.id.date2, R.id.date3, R.id.date4, R.id.date5, R.id.date6};

		for (int dateId : dateArr) {
			rootView.findViewById(dateId).setOnClickListener(this);
			dateTvList.add((TextView) rootView.findViewById(dateId));
		}

		// Layout for expandable menu list
		getActivity().setTitle("Upcoming Menus");
		DisplayMetrics dm = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
		int offsetPx = 64 * (dm.densityDpi / DisplayMetrics.DENSITY_DEFAULT); // 64 is an arbitrary dp offset
		expListViewWest.setIndicatorBounds(dm.widthPixels - offsetPx, dm.widthPixels);
		expListViewNorth.setIndicatorBounds(dm.widthPixels - offsetPx, dm.widthPixels);
		expListViewCentral.setIndicatorBounds(dm.widthPixels - offsetPx, dm.widthPixels);

		lastExpandedPosition = -1;
		lastClickedListView = null;

		expListViewCentral.setOnGroupExpandListener(
				new NonScrollExpandableListView.OnGroupExpandListener() {
					@Override
					public void onGroupExpand(int index) {
						if (lastClickedListView != null
								&& lastExpandedPosition != -1
								&& index != lastExpandedPosition) {
							lastClickedListView.collapseGroup(lastExpandedPosition);
						}
						lastExpandedPosition = index;
						lastClickedListView = expListViewCentral;
					}
				});
		expListViewWest.setOnGroupExpandListener(
				new NonScrollExpandableListView.OnGroupExpandListener() {
					@Override
					public void onGroupExpand(int index) {
						if (lastClickedListView != null
								&& lastExpandedPosition != -1
								&& index != lastExpandedPosition) {
							lastClickedListView.collapseGroup(lastExpandedPosition);
						}
						lastExpandedPosition = index;
						lastClickedListView = expListViewWest;
					}
				});
		expListViewNorth.setOnGroupExpandListener(
				new NonScrollExpandableListView.OnGroupExpandListener() {
					@Override
					public void onGroupExpand(int index) {
						if (lastClickedListView != null
								&& lastExpandedPosition != -1
								&& index != lastExpandedPosition) {
							lastClickedListView.collapseGroup(lastExpandedPosition);
						}
						lastExpandedPosition = index;
						lastClickedListView = expListViewNorth;
					}
				});

		// When changing date, highlight Breakfast textview
		linDate.setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						breakfastText.setTextColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.blue));
						lunchText.setTextColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.secondary));
						dinnerText.setTextColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.secondary));
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

		breakfastText.setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						breakfastText.setTextColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.blue));
						lunchText.setTextColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.secondary));
						dinnerText.setTextColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.secondary));

						mealType = MealType.BREAKFAST;
						changeListAdapter(mealType, presenter.getSelectedDate());
					}
				});

		lunchText.setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						breakfastText.setTextColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.secondary));
						lunchText.setTextColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.blue));
						dinnerText.setTextColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.secondary));

						mealType = MealType.LUNCH;
						changeListAdapter(mealType, presenter.getSelectedDate());
					}
				});

		dinnerText.setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						breakfastText.setTextColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.secondary));
						lunchText.setTextColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.secondary));
						dinnerText.setTextColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.blue));

						mealType = MealType.DINNER;
						changeListAdapter(mealType, presenter.getSelectedDate());
					}
				});

		LocalDateTime currentTime = LocalDateTime.now();

		if (currentTime.getHour() < 11) {
			changeListAdapter(MealType.BREAKFAST, presenter.getSelectedDate());
			mealType = MealType.BREAKFAST;
			breakfastText.setTextColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.blue));
			lunchText.setTextColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.secondary));
			dinnerText.setTextColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.secondary));
		} else if (currentTime.getHour() < 16) {
			changeListAdapter(MealType.LUNCH, presenter.getSelectedDate());
			mealType = MealType.LUNCH;
			breakfastText.setTextColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.secondary));
			lunchText.setTextColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.blue));
			dinnerText.setTextColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.secondary));
		} else if (currentTime.getHour() < 22) {
			changeListAdapter(MealType.DINNER, presenter.getSelectedDate());
			mealType = MealType.DINNER;
			breakfastText.setTextColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.secondary));
			lunchText.setTextColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.secondary));
			dinnerText.setTextColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.blue));
		} else {
			presenter.getDayInWeek(1);
			changeListAdapter(MealType.BREAKFAST, presenter.getSelectedDate());
			mealType = MealType.BREAKFAST;
			breakfastText.setTextColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.blue));
			lunchText.setTextColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.secondary));
			dinnerText.setTextColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.secondary));
		}
		return rootView;
	}

	/**
	 * Changes the text color to black when selected
	 */
	@Override
	public void onClick(View view) {
		TextView textView = (TextView) view;

		textView.setTextColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.primary));
		changeDateColor(textView);

		int id = textView.getId();
		LocalDate currentDate = presenter.getSelectedDate();

		switch (id) {
			case R.id.date0:
				currentDate = presenter.getDayInWeek(0);
				break;
			case R.id.date1:
				currentDate = presenter.getDayInWeek(1);
				break;
			case R.id.date2:
				currentDate = presenter.getDayInWeek(2);
				break;
			case R.id.date3:
				currentDate = presenter.getDayInWeek(3);
				break;
			case R.id.date4:
				currentDate = presenter.getDayInWeek(4);
				break;
			case R.id.date5:
				currentDate = presenter.getDayInWeek(5);
				break;
			case R.id.date6:
				currentDate = presenter.getDayInWeek(6);
				break;
		}
		changeListAdapter(mealType, currentDate);
	}

	/**
	 * Changes the text color to grey if the date is not selected
	 */
	public void changeDateColor(TextView selectedDate) {
		for (TextView textView : dateTvList) {
			if (!textView.equals(selectedDate)) {
				textView.setTextColor(ContextCompat.getColor(
						getActivity().getApplicationContext(), R.color.secondary));
			}
		}
	}

	/**
	 * Updates the list of dining halls and menus that is displayed
	 */
	public void changeListAdapter(MealType mealType, LocalDate date) {
		HashMap<String, ArrayList<DiningHallModel>> finalList = presenter.generateAreaLists(mealType, date);
		ArrayList<DiningHallModel> westList = finalList.get("West");
		ArrayList<DiningHallModel> northList = finalList.get("North");
		ArrayList<DiningHallModel> centralList = finalList.get("Central");

		// Hides layout elements if there is nothing in the list corresponding to a certain
		// CafeteriaArea
		if (westList != null && westList.size() == 0) {
			westText.setVisibility(View.GONE);
			expListViewWest.setVisibility(View.GONE);
		} else {
			westText.setVisibility(View.VISIBLE);
			expListViewWest.setVisibility(View.VISIBLE);

			listAdapterWest =
					new ExpandableListAdapter(getContext(), date, mealType, westList);
			expListViewWest.setAdapter(listAdapterWest);
		}
		if (northList != null && northList.size() == 0) {
			northText.setVisibility(View.GONE);
			expListViewNorth.setVisibility(View.GONE);
		} else {
			northText.setVisibility(View.VISIBLE);
			expListViewNorth.setVisibility(View.VISIBLE);

			listAdapterNorth =
					new ExpandableListAdapter(getContext(), date, mealType, northList);
			expListViewNorth.setAdapter(listAdapterNorth);
		}
		if (centralList != null && centralList.size() == 0) {
			centralText.setVisibility(View.GONE);
			expListViewCentral.setVisibility(View.GONE);
		} else {
			centralText.setVisibility(View.VISIBLE);
			expListViewCentral.setVisibility(View.VISIBLE);

			listAdapterCentral =
					new ExpandableListAdapter(getContext(), date, mealType, centralList);
			expListViewCentral.setAdapter(listAdapterCentral);
		}
	}
}

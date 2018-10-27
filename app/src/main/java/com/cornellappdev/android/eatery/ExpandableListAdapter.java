package com.cornellappdev.android.eatery;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.threeten.bp.LocalDate;

import com.cornellappdev.android.eatery.model.DiningHallModel;
import com.cornellappdev.android.eatery.model.EateryBaseModel;
import com.cornellappdev.android.eatery.model.MealModel;
import com.cornellappdev.android.eatery.model.enums.MealType;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Lesley on 4/20/2018. This class is used in WeeklyMenuActivity, where it displays the
 * corresponding dining halls for each meal period and the menu for that particular day
 */
public class ExpandableListAdapter extends BaseExpandableListAdapter {
  private Context context;
  private ArrayList<DiningHallModel> diningList;
  View line;
  private LocalDate date;
  private MealType mealType;

  public ExpandableListAdapter(
      Context context,
      LocalDate date,
      MealType mealType,
      ArrayList<DiningHallModel> diningList) {
    this.context = context;
    this.mealType = mealType;
    this.date = date;
    this.diningList = diningList;
  }

  @Override
  public int getGroupCount() {
    return diningList.size();
  }

  @Override
  public int getChildrenCount(int i) {
    if (getGroupCount() == 0) {
      return 0;
    }
    return ((DiningHallModel) getGroup(i))
        .getMealByDateAndType(date, mealType).getNumberOfCategories();
  }

  @Override
  public Object getGroup(int i) {

    return diningList.get(i);
  }

  @Override
  public Object getChild(int i, int i1) {
    Log.d("log-explistview", ((DiningHallModel) getGroup(i)).getNickName());
    return ((DiningHallModel) getGroup(i))
        .getMealByDateAndType(date, mealType).getCategory(i1);
  }

  @Override
  public long getGroupId(int i) {
    return i;
  }

  @Override
  public long getChildId(int i, int i1) {
    return i1;
  }

  @Override
  public boolean hasStableIds() {
    return false;
  }

  @Override
  /** Displays header for explistview**/
  public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
    // Inflate layout if it does not exist already
    if (view == null) {
      LayoutInflater infalInflater =
          (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      view = infalInflater.inflate(R.layout.list_view_header, viewGroup, false);
    }

    String m = ((DiningHallModel) getGroup(i)).getNickName();
    TextView headerText = view.findViewById(R.id.header);
    headerText.setText(m);
    headerText.setTypeface(null, Typeface.NORMAL);

    TextView timetext1 = view.findViewById(R.id.time1);

//    try {
//      CafeteriaModel myCafe = null;
//      int count = 0;
//      while (count < cafeData.size()) {
//        if (m.equals(cafeData.get(count).getNickName())) {
//          myCafe = cafeData.get(count);
//        }
//        count++;
//      }
//
//      ArrayList<MealModel> day = myCafe.getWeeklyMenu().get(dateOffset + 1);
//      int length = day.size() - 1;
//
//      // finding correct idx to get desired meal model from day array
//      MealModel meal;
//      if (length == 1) {
//        meal = day.get(mealIndex / 2);
//      } else if (length == 2) {
//        meal = day.get(mealIndex);
//      } else if (length == 3) {
//        meal = day.get(mealIndex - 2 + length);
//      } else {
//        meal = day.get(0);
//      }
//
//      SimpleDateFormat localDateFormat = new SimpleDateFormat("h:mm a");
//      Date date = new Date();
//
//      String endTime = localDateFormat.format(meal.getEnd());
//      String startTime = localDateFormat.format(meal.getStart());
//
//      // meal date seems to be off by one day
//      if ((date.getTime() > meal.getStart().getTime() && date.getTime() < meal.getEnd().getTime())
//          || date.getDay() != meal.getStart().getDay()
//          || date.getTime() < meal.getStart().getTime()) {
//        timetext1.setText("Open from " + startTime + " to " + endTime);
//        timetext1.setTextColor(Color.parseColor("#1a84db"));
//      } else {
//        String mealString = "";
//        if (mealIndex == 0) {
//          mealString = "Breakfast";
//        } else if (meal.getType().equals("Brunch")) {
//          mealString = "Brunch";
//        } else if (mealIndex == 1) {
//          mealString = "Lunch";
//        } else {
//          mealString = "Dinner";
//        }
//        timetext1.setText("Closed for " + mealString);
//        timetext1.setTextColor(Color.parseColor("#989898"));
//      }
//
//    } catch (Exception e) {
//      e.printStackTrace();
//    }
    return view;
  }

  @Override
  public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
    // Inflate layout if it does not exist already
    if (view == null) {
      LayoutInflater infalInflater =
          (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      view = infalInflater.inflate(R.layout.list_view_body, viewGroup, false);
    }

    // Horizontal line that separates each eatery entry
    line = view.findViewById(R.id.horiline);
    line.setVisibility(View.GONE);
    String str = (String) getChild(i, i1);

    TextView tv = view.findViewById(R.id.menu_title);
    SpannableStringBuilder finalStr = new SpannableStringBuilder();
    if (str == "") {
      tv.setText("No menu available");
    }
    else {
      // Formatting for category
      DisplayMetrics dm = new DisplayMetrics();
      ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
      float logicalDensity = dm.density;

      SpannableString sstr = new SpannableString(str);
      sstr.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, sstr.length(),Spannable.SPAN_INCLUSIVE_INCLUSIVE);
      sstr.setSpan(new ForegroundColorSpan(Color.parseColor("#000000")), 0, sstr.length(), 0);
      sstr.setSpan(new AbsoluteSizeSpan((int) (20*logicalDensity)), 0, sstr.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
      finalStr.append(sstr);
      for (String item : ((DiningHallModel) getGroup(i))
          .getMealByDateAndType(date, mealType).getItems(str)) {
        SpannableString itemStr = new SpannableString(item);
        itemStr.setSpan(new ForegroundColorSpan(Color.parseColor("#7d8288")), 0, itemStr.length(), 0);
        itemStr.setSpan(new StyleSpan(Typeface.NORMAL), 0, itemStr.length(),Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        itemStr.setSpan(new AbsoluteSizeSpan((int) (14*logicalDensity)), 0, itemStr.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        finalStr.append("\n");
        finalStr.append(itemStr);
      }
    }
    tv.setText(finalStr, TextView.BufferType.SPANNABLE);
    tv.setPadding(15, 24, 0, 0);
    return view;
  }

  @Override
  public boolean isChildSelectable(int i, int i1) {
    return false;
  }
}

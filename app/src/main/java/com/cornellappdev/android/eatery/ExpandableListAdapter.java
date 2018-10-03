package com.cornellappdev.android.eatery;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;
import com.cornellappdev.android.eatery.WeeklyMenuActivity.MenuListItem;
import com.cornellappdev.android.eatery.model.DiningHallModel;
import com.cornellappdev.android.eatery.model.EateryModel;
import com.cornellappdev.android.eatery.model.MealModel;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.threeten.bp.ZoneId;
import org.threeten.bp.ZonedDateTime;
import org.threeten.bp.format.DateTimeFormatter;

/**
 * Created by Lesley on 4/20/2018. This class is used in WeeklyMenuActivity, where it displays the
 * corresponding dining halls for each meal period and the menu for that particular day
 */

public class ExpandableListAdapter extends BaseExpandableListAdapter {

  private Context context;
  private List<EateryModel> cafeData;
  private List<String> cafeKeys = new ArrayList<>();

  private Map<String, List<MenuListItem>> mealMap;
  private int mealIndex;
  private int dateOffset;

  ExpandableListAdapter(Context context, Map<String, List<MenuListItem>> mealMap,
      int dateOffset, int mealIndex, List<EateryModel> cafeData) {
    this.context = context;
    this.mealMap = mealMap;
    this.mealIndex = mealIndex;
    this.dateOffset = dateOffset;
    this.cafeData = cafeData;

    if (mealMap != null) {
      cafeKeys.addAll(mealMap.keySet());
    }
  }

  @Override
  public int getGroupCount() {
    if (mealMap == null) {
      return 0;
    }
    return mealMap.size();
  }

  @Override
  public int getChildrenCount(int i) {
    if (mealMap == null) {
      return 0;
    }
    String m = cafeKeys.get(i);
    return mealMap.get(m).size();
  }

  @Override
  public Object getGroup(int i) {
    return cafeKeys.get(i);
  }

  @Override
  public Object getChild(int i, int i1) {
    String m = (String) getGroup(i);
    return mealMap.get(m).get(i1).getItem();
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
  public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
    // Inflate layout if it does not exist already
    if (view == null) {
      LayoutInflater infalInflater = (LayoutInflater) this.context
          .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      view = infalInflater.inflate(R.layout.list_view_header, viewGroup, false);
    }

    String m = (String) getGroup(i);
    TextView headerText = view.findViewById(R.id.header);
    headerText.setText(m);
    headerText.setTypeface(null, Typeface.NORMAL);

    TextView timetext1 = view.findViewById(R.id.time1);

    try {
      EateryModel eatery = null;
      int count = 0;
      while (count < cafeData.size()) {
        if (m.equals(cafeData.get(count).getNickName())) {
          eatery = cafeData.get(count);
        }
        count++;
      }
      if (eatery instanceof DiningHallModel) {
        List<MealModel> day = ((DiningHallModel) eatery).getWeeklyMenu().get(dateOffset + 1);
        int length = day.size() - 1;

        //finding correct idx to get desired meal model from day array
        MealModel meal;
        if (length == 1) {
          meal = day.get(mealIndex / 2);
        } else if (length == 2) {
          meal = day.get(mealIndex);
        } else if (length == 3) {
          meal = day.get(mealIndex - 2 + length);
        } else {
          meal = day.get(0);
        }

        DateTimeFormatter localDateFormat = DateTimeFormatter.ofPattern("h:mma");
        ZonedDateTime now = ZonedDateTime.now();
        Resources res = context.getResources();

        String formattedEndTime = localDateFormat.format(meal.getEnd());
        String formattedStartTime = localDateFormat.format(meal.getStart());

        ZoneId cornell = TimeUtil.getInstance().getCornellTimeZone();
        ZonedDateTime startTime = meal.getStart().atZone(cornell);
        ZonedDateTime endTime = meal.getStart().atZone(cornell);

        //meal date seems to be off by one day
        // TODO
        if ((now.isAfter(startTime) && now.isBefore(endTime)) || now.isBefore(startTime) || now
            .isAfter(startTime)) {
          timetext1.setText(
              res.getString(R.string.open_from_a_to_b, formattedStartTime, formattedEndTime));
          timetext1.setTextColor(ContextCompat.getColor(context, R.color.eateryOpenColor));
        } else {
          String mealString = "";
          if (mealIndex == 0) {
            mealString = "Breakfast";
          } else if (meal.getType().equals("Brunch")) {
            mealString = "Brunch";
          } else if (mealIndex == 1) {
            mealString = "Lunch";
          } else {
            mealString = "Dinner";
          }

          timetext1.setText(res.getString(R.string.closed_for_a, mealString));
          timetext1.setTextColor(Color.parseColor("#989898"));
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return view;
  }

  @Override
  public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
    // Inflate layout if it does not exist already
    if (view == null) {
      LayoutInflater infalInflater = (LayoutInflater) this.context
          .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      view = infalInflater.inflate(R.layout.list_view_body, viewGroup, false);
    }

    // Horizontal line that separates each eatery entry
    View line = view.findViewById(R.id.horiline);
    line.setVisibility(View.GONE);
    String str = (String) getChild(i, i1);

    TextView tv = view.findViewById(R.id.menu_title);

    if (str == null) {
      System.out.println("dingdongditch");
      tv.setText(R.string.no_menu_available);
    }
    // If str == 3, then string is a category
    if (str != null && str.charAt(0) == '3') {
      str = str.substring(1);
      SpannableString sstr = new SpannableString(str);
      tv.setText(sstr);
      tv.setTextColor(Color.parseColor("#000000"));
      tv.setTextSize(18);
      tv.setPadding(0, 70, 0, 0);
    }
    // If str != 3, then string is a meal item
    else {
      SpannableString sstr = new SpannableString(str);
      tv.setText(sstr);
      tv.setTypeface(null, Typeface.NORMAL);
      tv.setTextColor(Color.parseColor("#808080"));
      tv.setTextSize(14);
      tv.setPadding(0, 0, 0, 0);
    }

    return view;
  }

  @Override
  public boolean isChildSelectable(int i, int i1) {
    if (getChildrenCount(i) == 1) {
      return true;
    }
    return false;
  }
}

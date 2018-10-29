package com.cornellappdev.android.eatery;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;

import android.widget.TextView;
import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;

import com.cornellappdev.android.eatery.model.DiningHallModel;
import com.cornellappdev.android.eatery.model.enums.MealType;
import com.cornellappdev.android.eatery.util.TimeUtil;

import java.util.ArrayList;

import static com.cornellappdev.android.eatery.model.EateryBaseModel.Status.CLOSED;
import static com.cornellappdev.android.eatery.model.EateryBaseModel.Status.OPEN;

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
    try {
      return ((DiningHallModel) getGroup(i))
          .getMealByDateAndType(date, mealType).getMenuAsList().size();
    } catch (Exception e){
      this.mealType = MealType.BRUNCH;
      return ((DiningHallModel) getGroup(i))
          .getMealByDateAndType(date, MealType.BRUNCH).getMenuAsList().size();
    }
  }

  @Override
  public Object getGroup(int i) {
    return diningList.get(i);
  }

  @Override
  public Object getChild(int i, int i1) {
    try {
      return ((DiningHallModel) getGroup(i))
          .getMealByDateAndType(date, mealType).getMenuAsList().get(i1);
    } catch (Exception e){
      this.mealType = MealType.BRUNCH;
      return ((DiningHallModel) getGroup(i))
          .getMealByDateAndType(date, MealType.BRUNCH).getMenuAsList().get(i1);
    }
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

    DiningHallModel dm = (DiningHallModel) getGroup(i);
    String diningName = (dm).getNickName();
    TextView headerText = view.findViewById(R.id.header);
    headerText.setText(diningName);
    headerText.setTypeface(null, Typeface.BOLD);

    TextView openText = view.findViewById(R.id.weekly_open);
    TextView timeText = view.findViewById(R.id.weekly_time);

    LocalDateTime currentTime = LocalDateTime.now();
    if (date.getDayOfMonth() != currentTime.getDayOfMonth() ||
        TimeUtil.getMealType(currentTime) != mealType) {
      openText.setText(R.string.closed);
      openText.setTextColor(ContextCompat.getColor(context, R.color.red));
      timeText.setText("");
    } else {
      switch (dm.getCurrentStatus()) {
      case OPEN:
        openText.setText(R.string.open);
        timeText.setText((TimeUtil.format(dm.getCurrentStatus(),dm.getChangeTime())));
        openText.setTextColor(ContextCompat.getColor(context, R.color.green));
        break;
      case CLOSED:
        openText.setText(R.string.closed);
        timeText.setText("");
        openText.setTextColor(ContextCompat.getColor(context, R.color.red));
        break;
      case CLOSINGSOON:
        openText.setText(R.string.closing_soon);
        timeText.setText((TimeUtil.format(dm.getCurrentStatus(),dm.getChangeTime())));
        openText.setTextColor(ContextCompat.getColor(context, R.color.red));
        break;
      }
    }
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

    if (str == null) {
      tv.setText("No menu information");
      tv.setTextColor(ContextCompat.getColor(context, R.color.primary));
      tv.setTextSize(18);
      tv.setGravity(Gravity.CENTER_HORIZONTAL);
      tv.setPadding(0, 96, 0, 0);
    }
    else if (((DiningHallModel) getGroup(i))
        .getMealByDateAndType(date, mealType).containsCategory(str)) {
      SpannableString sstr = new SpannableString(str);
      tv.setText(sstr);
      tv.setTypeface(null, Typeface.NORMAL);
      tv.setTextColor(ContextCompat.getColor(context, R.color.primary));
      tv.setTextSize(18);
      tv.setPadding(32, 24, 0, 0);
    }
    else {
      SpannableString sstr = new SpannableString(str);
      tv.setText(sstr);
      tv.setTextColor(Color.parseColor("#7d8288"));
      tv.setTextSize(14);
      tv.setTypeface(null, Typeface.NORMAL);
      tv.setPadding(32, 0, 0, 0);
    }

    if (i1 == ((DiningHallModel) getGroup(i))
        .getMealByDateAndType(date, mealType).getMenuAsList().size() - 1) {
      tv.setPadding(32, 0, 0, 24);
    }
    return view;
  }

  @Override
  public boolean isChildSelectable(int i, int i1) {
    return false;
  }
}

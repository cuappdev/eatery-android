package com.cornellappdev.android.eatery;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cornellappdev.android.eatery.model.MealMenuModel;
import com.cornellappdev.android.eatery.model.MealModel;
import com.cornellappdev.android.eatery.model.enums.MealType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** A simple {@link Fragment} subclass. */
public class MenuFragment extends Fragment {
  int position;
  MealMenuModel menu;
  LinearLayout linear;

  @Override
  public View onCreateView(
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    menu = ((MealModel) getArguments().getSerializable("cafeData")).getMenu();
    Log.d("log-frag", menu.toString());
    View view = inflater.inflate(R.layout.fragment_menu, container, false);
    linear = view.findViewById(R.id.linearFragment);

    try {
      position = getArguments().getInt("position");
      Log.d("log-frag", Integer.toString(position));
    } catch (Exception e) {
      e.printStackTrace();
    }

    float scale = getResources().getDisplayMetrics().density;
    // Checks for unavailable or missing menus
//    if (menus.get(position).getMenu().entrySet().isEmpty()) {
//      TextView missingMenuText = new TextView(getContext());
//      missingMenuText.setText("No menu available");
//      missingMenuText.setTextColor(Color.parseColor("#de000000"));
//      missingMenuText.setPadding((int) (16 * scale + 0.5f), 0, 0, (int) (12 * scale + 0.5f));
//      missingMenuText.setTextSize(14);
//      linear.addView(missingMenuText);
//    }

    int counter = 0;
    ArrayList<String> categories = (ArrayList<String>) menu.getCategories();
    for (int i = 0; i < categories.size(); i++) {
      // Add subheading for category of food
      TextView categoryText = new TextView(getContext());
      categoryText.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
      categoryText.setText(categories.get(i));
      categoryText.setTextSize(16);
      categoryText.setTextColor(Color.parseColor("#de000000"));
      linear.addView(categoryText);

      // Note(lesley): there's extra padding added to the first category somewhere and I
      // don't know how to fix it
      if (counter == 0) {
        categoryText.setPadding((int) (16 * scale + 0.5f), 0, 0, (int) (12 * scale + 0.5f));
      } else {
        categoryText.setPadding(
            (int) (16 * scale + 0.5f), (int) (12 * scale + 0.5f), 0, (int) (12 * scale + 0.5f));
      }

      View blank = new View(getContext());
      blank.setLayoutParams(
          new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1));
      blank.setBackgroundColor(Color.parseColor("#ccd0d5"));
      blank.setElevation(-1);
      linear.addView(blank);

      ArrayList<String> items = (ArrayList<String>) menu.getItems(categories.get(i));
      for (int j = 0; j < items.size(); j++) {
        TextView mealItemText = new TextView(getContext());
        mealItemText.setText(items.get(j));
        mealItemText.setTextSize(14);
        mealItemText.setTextColor(Color.parseColor("#de000000"));
        mealItemText.setPadding(
            (int) (16 * scale + 0.5f), (int) (8 * scale + 0.5f), 0, (int) (8 * scale + 0.5f));
        linear.addView(mealItemText);

        if (j != items.size() - 1) {
          View divider = new View(getContext());
          divider.setBackgroundColor(Color.parseColor("#ccd0d5"));
          LinearLayout.LayoutParams dividerParams =
              new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1);
          dividerParams.setMargins((int) (15.8 * scale + 0.5f), 0, 0, 0);
          divider.setElevation(-1);
          divider.setLayoutParams(dividerParams);
          linear.addView(divider);
        }
      }

      // Add horizontal line that separates each category
      if (i < categories.size() - 2) {
        View divider = new View(getContext());
        divider.setBackgroundColor(Color.parseColor("#ccd0d5"));
        divider.setLayoutParams(
            new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1));
        divider.setElevation(-1);
        linear.addView(divider);

        View grey = new View(getContext());
        grey.setBackgroundColor(Color.parseColor("#f2f2f2"));
        grey.setLayoutParams(
            new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 16));
        linear.addView(grey);
      }
    }
    // Inflate the layout for this fragment
    return view;
  }
}

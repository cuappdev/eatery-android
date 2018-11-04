package com.cornellappdev.android.eatery;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cornellappdev.android.eatery.model.MealMenuModel;
import com.cornellappdev.android.eatery.model.MealModel;

import java.util.ArrayList;

public class MenuFragment extends Fragment {
  int position;
  MealMenuModel menu;
  LinearLayout linear;

  @Override
  public View onCreateView(
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    menu = ((MealModel) getArguments().getSerializable("cafeData")).getMenu();
    View view = inflater.inflate(R.layout.fragment_menu, container, false);
    linear = view.findViewById(R.id.linearFragment);

    try {
      position = getArguments().getInt("position");
    } catch (Exception e) {
      e.printStackTrace();
    }

    float scale = getResources().getDisplayMetrics().density;
    // Checks for unavailable or missing menus

    if (menu.getNumberOfCategories() == 0) {
      TextView missingMenuText = new TextView(getContext());
      missingMenuText.setText("Nothing on the menu 😮");
      missingMenuText.setTextColor(ContextCompat.getColor(getContext(), R.color.primary));
      missingMenuText.setPadding(0, (int) (96 * scale + 0.5f), 0, 0);

      missingMenuText.setTextSize(32);
      linear.addView(missingMenuText);
    }

    int counter = 0;
    ArrayList<String> categories = (ArrayList<String>) menu.getCategories();
    for (int i = 0; i < categories.size(); i++) {
      // Add subheading for category of food
      TextView categoryText = new TextView(getContext());
      categoryText.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
      categoryText.setText(categories.get(i));
      categoryText.setTextSize(16);
      categoryText.setTextColor(ContextCompat.getColor(getContext(), R.color.primary));
      linear.addView(categoryText);

      categoryText.setPadding(
          (int) (16 * scale + 0.5f), (int) (12 * scale + 0.5f), 0, (int) (12 * scale + 0.5f));

      View blank = new View(getContext());
      blank.setLayoutParams(
          new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1));
      blank.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.inactive));
      blank.setElevation(-1);
      linear.addView(blank);

      ArrayList<String> items = (ArrayList<String>) menu.getItems(categories.get(i));
      for (int j = 0; j < items.size(); j++) {
        TextView mealItemText = new TextView(getContext());
        mealItemText.setText(items.get(j));
        mealItemText.setTextSize(14);
        mealItemText.setTextColor(ContextCompat.getColor(getContext(), R.color.primary));
        mealItemText.setPadding(
            (int) (16 * scale + 0.5f), (int) (8 * scale + 0.5f), 0, (int) (8 * scale + 0.5f));
        linear.addView(mealItemText);

        if (j != items.size() - 1) {
          View divider = new View(getContext());
          divider.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.inactive));
          LinearLayout.LayoutParams dividerParams =
              new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1);
          dividerParams.setMargins((int) (15.8 * scale + 0.5f), 0, 0, 0);
          divider.setElevation(-1);
          divider.setLayoutParams(dividerParams);
          linear.addView(divider);
        }
      }

      // Add horizontal line that separates each category
      if (i < categories.size() - 1) {
        View divider = new View(getContext());
        divider.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.inactive));
        divider.setLayoutParams(
            new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1));
        divider.setElevation(-1);
        linear.addView(divider);

        View grey = new View(getContext());
        grey.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.wash));
        grey.setLayoutParams(
            new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 16));
        linear.addView(grey);
      }
    }
    // Inflate the layout for this fragment
    return view;
  }
}
